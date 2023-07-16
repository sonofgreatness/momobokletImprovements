package com.example.momobooklet_by_sm.data.local.models




import android.os.Parcelable
import androidx.room.*
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.Exportable
import kotlinx.android.parcel.Parcelize

@Parcelize


@Entity(tableName = "RECORDS_SHEET"
  /*  foreignKeys = arrayOf(
        ForeignKey(entity = UserModel::class,
            parentColumns = arrayOf("MoMoNumber"),
            childColumns = arrayOf("AgentPhoneNumber"),
            onDelete = CASCADE))
  */
)

data class TransactionModel(
    @PrimaryKey
    val Transaction_ID:String,
    val Date: String,//dd-mm-yyyy
    val C_Name:String,
    val C_ID: String,
    val C_PHONE:String,
    val Transaction_type: Boolean,
    val Amount :Float,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB,name="Signature")
    val Signature:ByteArray,
    val Time :String,
    @ColumnInfo(name = "AgentPhoneNumber", index = true)
    val AgentPhoneNumber: String,
) : Parcelable, Exportable