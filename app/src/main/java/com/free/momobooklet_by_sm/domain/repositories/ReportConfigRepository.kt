package com.free.momobooklet_by_sm.domain.repositories


/**
 * Manages External Folder where Reports are Saved
 */
interface ReportConfigRepository{

    fun setUpExternalDirectories()
    fun setUpInternalDirectories()
}