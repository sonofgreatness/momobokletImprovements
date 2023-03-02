package com.example.momobooklet_by_sm.services.csv

import com.example.momobooklet_by_sm.services.pdf.PdfConfig

// List of supported export functionality
sealed class Exports {
    data class CSV(val csvConfig: CsvConfig) : Exports()
    data class PDF(val pdfConfig: PdfConfig ) : Exports()
}
