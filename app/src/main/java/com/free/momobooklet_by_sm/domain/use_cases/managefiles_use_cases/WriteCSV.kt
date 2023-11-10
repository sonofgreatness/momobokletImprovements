package com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.net.toUri
import com.free.momobooklet_by_sm.common.util.classes.WRITETO
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.ExportState
import com.free.momobooklet_by_sm.domain.managefiles.csv.models.DailyCommissionModelCSV
import com.free.momobooklet_by_sm.domain.managefiles.csv.models.TransactionModelCSV
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.CsvConfigImpl
import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.lang.Exception

class WriteCSV(
    private val csvConfig: CsvConfigImpl,
    val content: List<TransactionModelCSV>,
    private val content2: List<DailyCommissionModelCSV>)


{
      @SuppressLint("LogNotTimber")
      @WorkerThread
      fun  writeCSV() = flow  {
          with(csvConfig) {


              val myhostPath : String = if(location.ordinal == WRITETO.EXTERNAL.ordinal)
                  hostPath
              else
                  internalhostPath

              reportConfigRepository.setUpInternalDirectories()

              //Unconditional File Write To Internal Memory
              try{
                  val myhostPath2   = internalhostPath
                  val hostDirectory2 = File(myhostPath2)
                  // 👇 create csv file
                  val csvFile2 = File("${hostDirectory2.path}/files/${fileName}")
                  val csvWriter2: CSVWriter
                  csvWriter2 = CSVWriter(FileWriter(csvFile2))

                  // 👇 write csv file
                  StatefulBeanToCsvBuilder<TransactionModelCSV>(csvWriter2)
                      .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                      .build()
                      .write(content)

                  StatefulBeanToCsvBuilder<DailyCommissionModelCSV>(csvWriter2)
                      .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                      .build()
                      .write(content2)

                  csvWriter2.close()
              }
              catch (ex: Exception)
              {
                  Timber.d("internal csv write fail")
              }


              Log.d("HostPath Value : ", myhostPath)
              myhostPath.ifEmpty { throw IllegalStateException("Wrong Path") }

              val hostDirectory = File(myhostPath)
              if (!hostDirectory.exists()) {
                  reportConfigRepository.setUpExternalDirectories()
                  //hostDirectory.mkdir() // 👈 create directory
              }

              // 👇 create csv file
              val csvFile = File("${hostDirectory.path}/${fileName}")
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