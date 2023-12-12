package com.free.momobooklet_by_sm.common.util

class Constants {
  companion object {



      const val  BUY_IDENTIFIER = "buy"
      const val  SELL_IDENTIFIER = "sell"
      const val  DATE_DATE_PATTERN = "dd-MM-yyyy"
      const val  TIME_DATE_PATTERN = "dd-MM-yyyy hh:mm:ss a"
      const val CHARACTER_COUNT_PIN = 13
      const  val CHARACTER_COUNT_PHONE = 8
      const  val CHARACTER_COUNT_OTP = 6

      const val COUNTRY_CODE = "+268"
      const val MYNUMBER = "+26876911464"
      const val PHONE_NUMBER_KEY = "phone_key"
      const val MOMO_NAME_KEY = "momo_name_key"
      const val MOMO_EMAIL_KEY = "momo_email_key"
      const val MOMO_PASSWORD_KEY = "momo_password_key"
      const val MOMO_CONTROL_STATUS_KEY = "momo_control_status_key"
      const val MOMO_REGISTRATION_STATUS_KEY = "momo_registration_status_key"
      const val MOMO_FIREBASEVERIFICATION_KEY = "momo_firebaseverification_key"

      const val REGISTRATION_HOME_KEY = "home_fragment_key"
      const val  REQUEST_FILE_PERMISSION = 43
      const val  PDF_TABLE_TEXT_COLOR = "#000000"
      const val PDF_TABLE_TEXT_SIZE = 8

      const val  PDF_TABLE_HEADER_TEXT_COLOR = "#009688"
      const val PDF_TABLE_TEXT_HEADER_SIZE = 11
      const val  BASE_URL ="https://script.google.com/macros/s/AKfycbw9kMBVQD6xWXpSEvy6S9jU-GRPkoZHzTakoL2stByXp5Va6aZaJWeqPGXoHOON59IYeQ/"
      const val  BASE_URL3 ="http://172.22.48.1:8080/"

      const val  BASE_URL2 ="https://script.google.com/macros/s/AKfycbyALy6Zn2Co8zr5eyVGoKpAIXRLxhCkG6s5ZH8MVI40wk--C6nWrVyAmpFFMaRNzavnwQ/exec"
      const val  MIXPANEL_TOKEN = "a34dd4774bd7d4f78b8285e889ebdab6"


      const val SMS_CONDITION_TEXT = "is your verification code for"
      const val MONTHLY_STARTDATE_FILENAME  = "ManageMonthlyStartDate.txt"
      const val DEFAULT_START_DATE = "3"
      const val  SUMMARY_FLAG ="\n\n\n\n\n COMMISSION SUMMARY  \n\n\n\n\n"
      const val  DAILYCOMMISSIONCOLUMNTITLE_1 ="DATE"
      const val  DAILYCOMMISSIONCOLUMNTITLE_2 ="NUMBER OF TRANSACTIONS"
      const val  DAILYCOMMISSIONCOLUMNTITLE_3 ="COMMISSION AMOUNT"

      const val  MONTHLYCOMMISSIONCOLUMNTITLE_1 ="START-DATE"
      const val  MONTHLYCOMMISSIONCOLUMNTITLE_2 ="END-DATE"
      const val  MONTHLYCOMMISSIONCOLUMNTITLE_3 ="NUMBER OF TRANSACTIONS"
      const val  MONTHLYCOMMISSIONCOLUMNTITLE_4 ="COMMISSION AMOUNT"
      const val  TRANSACTIONCOLUMNTITLE_1 ="ID"

      const val  TRANSACTIONCOLUMNTITLE_2 ="DATE"
      const val  TRANSACTIONCOLUMNTITLE_3 ="CUSTOMER NAME"
      const val  TRANSACTIONCOLUMNTITLE_4 ="CUSTOMER PHONE"
      const val  TRANSACTIONCOLUMNTITLE_5 ="PIN"
      const val  TRANSACTIONCOLUMNTITLE_6 ="BUY/SELL"
      const val  TRANSACTIONCOLUMNTITLE_7 ="AMOUNT"
      const val  TRANSACTIONCOLUMNTITLE_8 ="SIGNATURE"

      const val  TRANSACTIONDATA_IMPORT_WORK_NAME = "transactiondata_import_work"
      const val   VERBOSE_NOTIFICATION_CHANNEL_NAME = "Verbose WorkManager Notifications"
      const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows Notifications Whenever work starts"
      const val NOTIFICATION_TITLE = "WorkRequest Starting"
      const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
      const val NOTIFICATION_ID = 1
      const val OUTPUT_PATH = "json_outputs"
      const val TAG_OUTPUT = "OUTPUT"

      const val FIREBASE_TAG = "Firebase TAG"
      const val  AGENT_PHONENUMBER_KEY = "agentPhoneNumber"
      const val FIREBASE_REGISTRATION_KEY ="firebase registration status"
      const val OTP_KEY = "OTP KEY"
      const val FIREBASE_REGISTRATION_ERROR_MESSAGE_KEY=  "firebase registration error message"
      const val FIREBASE_CODESENT = "CodeSent"
      const val FIREBASE_VERIFIED = "VerificationCompleted"
      const val ZIP_PREFIX_FILES = "files/"
      const val ZIP_PREFIX_DATABASES = "databases/"
      const val ZIP_PREFIX_DATABASEJSONFILES = "database_data/"
      const val ZIP_PREFIX_BACKUPS = "BACKUPS"


      const val BACKUP_SUCCESS_MESSAGE  = "back up successful"
      const val BACKUP_LOADING_MESSAGE  = "loading backup  please wait ..."
      const val BACKUP_ERROR_MESSAGE  = "back up failed ,error message  ="
      const val RECOVERY_LOADING_MESSAGE = "loading  recovery"
      const val RECOVERY_SUCCESS_MESSAGE = "loading  recovery"
      const val EXTERRNALBACKUP_SUCCESS_MESSAGE  = "back up to external folder successful"
      const val EXTERRNALBACKUP_ERROR_MESSAGE  = "back up up to external folder failed ,error message  ="
      const val BACKUP_FILENAME = "backup_file.zip"
      const val RECOVERY_FILENAME = "recovery_file.zip"

      const val ZIP_PREFIX_PREFS = "shared_prefs/"
      const val BACKUP_TRANSACTIONS_FILENAME = "transactionsBackUp.json"
      const val BACKUP_USERS_FILENAME = "usersBackUp.json"
      const val BACKEND_REG_OK = "User Registered Succesfully"
      const val BACKEND_REG_FAIL = "User registration failed check server connection ..."
      const val BACKEND_AUTH_OK = "User authenticated Succesfully"
      const val BACKEND_AUTH_FAIL = "User  authentication  failed check server connection ..."
      const val BACKEND_REG_FLAG = "Boolean flag for when backed registration is called"
      const val AUTH_PREFS_NAME = "User Authentication Prefs"
      const val BACKEND_TRANSACT_ADD_OK = "Succcessfully Added  transaction (s)"
      const val BACKEND_TRANSACT_ADD_FAIL = "Failed to add transaction(s)"

  }

}