package com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.net.toUri
import com.example.momobooklet_by_sm.common.util.classes.WRITETO
import com.example.momobooklet_by_sm.common.util.classes.operationalStates.ExportState
import com.example.momobooklet_by_sm.domain.managefiles.csv.models.DailyCommissionModelCSV
import com.example.momobooklet_by_sm.domain.managefiles.csv.models.TransactionModelCSV
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.CsvConfigImpl
import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.OutputStreamWriter

class WriteCSV(val csvConfig: CsvConfigImpl,
                val content: List<TransactionModelCSV>,
               val content2: List<DailyCommissionModelCSV>)


{
      @WorkerThread
      fun  writeCSV() = flow  {
          with(csvConfig) {

              val myhostPath : String = if(location.ordinal == WRITETO.EXTERNAL.ordinal)
                  hostPath
              else
                  internalhostPath
              reportConfigRepository.setUpInternalDirectories()

              myhostPath.ifEmpty { throw IllegalStateException("Wrong Path") }

              val hostDirectory = File(myhostPath)
              if (!hostDirectory.exists()) {
                  reportConfigRepository.setUpExternalDirectories()
                  //hostDirectory.mkdir() // 👈 create directory
              }

              // 👇 create csv file
              val csvFile = File("${hostDirectory.path}/files/${fileName}")
              val csvWriter: CSVWriter

              emit(ExportState.Loading)
              try {
                  Log.d("Create File Status (Exists) :  csv_catch", "${csvFile.exists()}")
                  csvWriter = CSVWriter(FileWriter(csvFile))

                  // 👇 write csv file
                  StatefulBeanToCsvBuilder<TransactionModelCSV>(csvWriter)
                      .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                      .build()
                      .write(content)

                  StatefulBeanToCsvBuilder<DailyCommissionModelCSV>(csvWriter)
                      .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                      .build()
                      .write(content2)

                  csvWriter.close()
                  // 👇 emit success
                  emit(ExportState.Success(csvFile.toUri()))
              }
              catch (i_o: IOException) {
                  Log.d("Create File Exception sneak Peek :    csv_catch", i_o.message.toString())
                  CSVWriter(OutputStreamWriter(otherHostPath))
                  emit(ExportState.Error(i_o.message.plus("csv_catch")))
              }
              /*otherHostPath?.flush()
              otherHostPath?.fd?.sync()
              otherHostPath?.close()
              */
          }

      }
}