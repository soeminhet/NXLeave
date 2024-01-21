package com.smh.nxleave.viewmodel

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smh.nxleave.domain.mapper.toUiModels
import com.smh.nxleave.domain.model.LeaveTypeModel
import com.smh.nxleave.domain.model.ProjectModel
import com.smh.nxleave.domain.model.RoleModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.domain.repository.FireStoreRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import com.smh.nxleave.screen.model.LeaveRequestModel
import com.smh.nxleave.screen.model.LeaveRequestUiModel
import com.smh.nxleave.utility.DATE_TIME_PATTERN_ONE
import com.smh.nxleave.utility.NotificationUtil
import com.smh.nxleave.utility.getCurrentMonthStartAndEndOffsetDate
import com.smh.nxleave.utility.toFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val realTimeDataRepository: RealTimeDataRepository,
    private val fireStoreRepository: FireStoreRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState = _uiState.asStateFlow()

    private var cacheLeaveRequestModels = emptyList<LeaveRequestModel>()

    init {
        with(realTimeDataRepository) {
            val currentMonth = getCurrentMonthStartAndEndOffsetDate()
            _uiState.update {
                it.copy(
                    staves = staves.value.add(value = StaffModel.allStaff),
                    roles = roles.value.add(value = RoleModel.allRole),
                    projects = projects.value.add(value = ProjectModel.allProject),
                    selectedStartDate = currentMonth.first,
                    selectedEndDate = currentMonth.second,
                    leaveTypes = leaveTypes.value,
                )
            }
            onFilterApply(
                staff = StaffModel.allStaff,
                role = RoleModel.allRole,
                project = ProjectModel.allProject,
                startDate = currentMonth.first,
                endDate = currentMonth.second
            )
        }
    }

    fun onFilterApply(staff: StaffModel, role: RoleModel, project: ProjectModel, startDate: OffsetDateTime, endDate: OffsetDateTime) {
        setLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val staffIds = when{
                staff.id.isBlank() && role.id.isBlank() && project.id.isBlank() -> { uiState.value.staves.map { it.id } }
                staff.id.isNotBlank() -> { listOf(staff.id) }
                role.id.isNotBlank() && project.id.isNotBlank() -> {
                    uiState.value.staves
                        .filter { it.roleId == role.id && it.currentProjectIds.contains(project.id)}
                        .map { it.id }
                }
                role.id.isNotBlank() -> {
                    uiState.value.staves
                        .filter { it.roleId == role.id }
                        .map { it.id }
                }
                else -> {
                    uiState.value.staves
                        .filter { it.currentProjectIds.contains(project.id) }
                        .map { it.id }
                }
            }
            val leaveRequests = if (staffIds.isNotEmpty()) fireStoreRepository.getLeaveRequestBy(staffIds, startDate, endDate)
            else emptyList()
            cacheLeaveRequestModels = leaveRequests
            _uiState.update {
                it.copy(
                    selectedStaff = staff,
                    selectedRole = role,
                    selectedProject = project,
                    selectedStartDate = startDate,
                    selectedEndDate = endDate,
                    leaveRequests = leaveRequests.toUiModels(
                        projects = uiState.value.projects,
                        roles = uiState.value.roles,
                        staves = uiState.value.staves,
                        leaveTypes = uiState.value.leaveTypes
                    )
                )
            }
            setLoading(false)
        }
    }

    private fun setLoading(value: Boolean) {
        _uiState.update {
            it.copy(
                loading = value
            )
        }
    }

    fun generateExcelAndPushLocalNotification(context: Context) {
        setLoading(true)
        generateExcel(
            context = context,
            onCompleted = {
                setLoading(false)
                val file = it.toFile(context)
                NotificationUtil.pushLocalNotification(context, file)
            },
            onException = {
                setLoading(false)
            }
        )
    }

    private fun generateExcel(
        context: Context,
        onCompleted: (Uri) -> Unit,
        onException: (Exception) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val dates = getDateRangeList(
                startDate = uiState.value.selectedStartDate,
                endDate = uiState.value.selectedEndDate
            )

            val leaveTypes = uiState.value.leaveTypes
            val staves = uiState.value.staves
            val staffLeaves = cacheLeaveRequestModels
                .groupBy { it.staffId }
                .toList()

            try {
                val workbook = XSSFWorkbook()
                val sheet = workbook.createSheet("NXLeaveReport")

                val dateStyle = workbook.createCellStyle()
                dateStyle.font.bold = true
                dateStyle.alignment = HorizontalAlignment.CENTER

                val nameStyle = workbook.createCellStyle()
                nameStyle.font.bold = true

                val approveStyle = workbook.createCellStyle()
                approveStyle.alignment = HorizontalAlignment.CENTER
                approveStyle.font.bold = false
                approveStyle.fillForegroundColor = IndexedColors.GREEN.index
                approveStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

                val pendingStyle = workbook.createCellStyle()
                pendingStyle.alignment = HorizontalAlignment.CENTER
                pendingStyle.font.bold = false
                pendingStyle.fillForegroundColor = IndexedColors.YELLOW.index
                pendingStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

                val rejectStyle = workbook.createCellStyle()
                rejectStyle.alignment = HorizontalAlignment.CENTER
                rejectStyle.font.bold = false
                rejectStyle.fillForegroundColor = IndexedColors.RED.index
                rejectStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

                val normalStyleStyle = workbook.createCellStyle()
                normalStyleStyle.alignment = HorizontalAlignment.CENTER
                normalStyleStyle.font.bold = false
                normalStyleStyle.fillPattern = FillPatternType.NO_FILL

                val dateRow = sheet.createRow(0)
                dates.forEachIndexed { colIndex, date ->
                    val dateCell = dateRow.createCell(colIndex + 2)
                    dateCell.setCellValue(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    dateCell.cellStyle = dateStyle
                }

                var currentRowIndex = 0
                staffLeaves.forEach { pair ->
                    val leaveTypeGroup = pair.second.groupBy { it.leaveTypeId }.toList()
                    leaveTypeGroup.forEachIndexed { secondIndex, typePair ->
                        currentRowIndex += 1
                        val row = sheet.createRow(currentRowIndex)

                        if (secondIndex == 0) {
                            val staffName = staves.firstOrNull { it.id == pair.first }?.name ?: "Unknown"
                            val nameCell = row.createCell(0)
                            nameCell.setCellValue(staffName)
                            nameCell.cellStyle = nameStyle
                        }

                        val typeCell = row.createCell(1)
                        val typeName = leaveTypes.firstOrNull { type -> type.id == typePair.first }?.name ?: "Unknown"
                        typeCell.setCellValue(typeName)
                        typeCell.cellStyle = nameStyle

                        dates.forEachIndexed { colIndex, date ->
                            val cell = row.createCell(colIndex + 2)
                            val model= typePair.second.firstOrNull { model ->
                                (model.startDate..(model.endDate ?: model.startDate)).contains(date)
                            }
                            val status = model?.leaveStatus.orEmpty()

                            cell.setCellValue(status)
                            cell.cellStyle = when (status) {
                                "Approved" -> approveStyle
                                "Pending" -> pendingStyle
                                "Rejected" -> rejectStyle
                                else -> normalStyleStyle
                            }
                        }
                    }
                }

                invokeWithMediaStore(
                    context = context,
                    filename = "NXLeaveReport_${LocalDateTime.now().format(DATE_TIME_PATTERN_ONE)}.xlsx",
                    onWrite = { outputStream ->
                        workbook.write(outputStream)
                        workbook.close()
                    },
                    onCompleted = onCompleted,
                    onException = onException
                )
            } catch (e: Exception) {
                onException(e)
            }
        }
    }

    private fun invokeWithMediaStore(
        context: Context,
        filename: String,
        onWrite: (OutputStream?) -> Unit,
        onCompleted: (Uri) -> Unit,
        onException: (Exception) -> Unit
    ) {
        val resolver: ContentResolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, context.contentResolver.getType(Uri.parse(filename)))
        }

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val fileUri: Uri? = resolver.insert(collection, contentValues)

        try {
            val output = resolver.openOutputStream(fileUri!!)
            onWrite(output)
            output?.close()
            onCompleted(fileUri)
        } catch (e: Exception) {
            onException(e)
        }
    }

    private fun getDateRangeList(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): List<OffsetDateTime> {
        val dates = mutableListOf<OffsetDateTime>()
        var currentDate = startDate
        while (currentDate <= endDate ) {
            dates.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }
        return dates.toList()
    }
}

data class ReportUiState(
    val loading: Boolean = false,
    val selectedStaff: StaffModel = StaffModel.allStaff,
    val selectedRole: RoleModel = RoleModel.allRole,
    val selectedProject: ProjectModel = ProjectModel.allProject,
    val selectedStartDate: OffsetDateTime = getCurrentMonthStartAndEndOffsetDate().first,
    val selectedEndDate: OffsetDateTime = getCurrentMonthStartAndEndOffsetDate().second,
    val leaveTypes: List<LeaveTypeModel> = emptyList(),
    val staves: List<StaffModel> = emptyList(),
    val roles: List<RoleModel> = emptyList(),
    val projects: List<ProjectModel> = emptyList(),
    val leaveRequests: List<LeaveRequestUiModel> = emptyList()
)


/** Private Extensions */
private fun <T> List<T>.add(value: T): List<T> {
    val mList = this.toMutableList()
    mList.add(0, value)
    return mList.toList()
}