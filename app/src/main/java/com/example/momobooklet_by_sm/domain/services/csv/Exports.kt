package com.example.momobooklet_by_sm.domain.services.csv

import com.example.momobooklet_by_sm.domain.services.pdf.PdfConfigImpl

// List of supported export functionality
sealed class Exports {
    data class CSV(val csvConfig: CsvConfigImpl) : Exports()
    data class PDF(val pdfConfig: PdfConfigImpl ) : Exports()
}
