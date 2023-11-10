package com.free.momobooklet_by_sm.common.util.classes.operationalStates



import android.net.Uri

/********************************************
 * This class handles the state of exporting
 *               Room DataBase to file
 ********************************************/
sealed class ExportState {
    object Loading : ExportState()
    object Empty : ExportState()
    data class Success(val fileUri: Uri? = null) : ExportState()
    data class Error(val exception_message: String? = null) : ExportState()
}
