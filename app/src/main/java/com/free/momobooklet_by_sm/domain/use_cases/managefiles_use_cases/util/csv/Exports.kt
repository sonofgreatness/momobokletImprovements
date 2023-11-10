package com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv

import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.pdf.PdfConfigImpl

// List of supported export functionality
sealed class Exports {
    data class CSV(val csvConfig: CsvConfigImpl) : Exports()
    data class PDF(val pdfConfig: PdfConfigImpl) : Exports()
}
