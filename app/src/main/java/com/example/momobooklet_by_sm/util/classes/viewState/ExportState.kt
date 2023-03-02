package com.example.momobooklet_by_sm.util.classes.viewState



import android.net.Uri

sealed class ExportState {
    object Loading : ExportState()
    object Empty : ExportState()
    data class Success(val fileUri: Uri) : ExportState()
    data class Error(val exception: Throwable) : ExportState()
}
