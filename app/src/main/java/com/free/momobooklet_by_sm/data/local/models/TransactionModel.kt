package com.free.momobooklet_by_sm.data.local.models




import android.os.Parcelable
import androidx.room.*
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.util.csv.Exportable
import kotlinx.parcelize.Parcelize



@Parcelize
@Entity(tableName = "RECORDS_SHEET"
  /*  foreignKeys = arrayOf(
        ForeignKey(entity = UserModel::class,
            parentColumns = arrayOf("MoMoNumber"),
            childColumns = arrayOf("AgentPhoneNumber"),
            onDelete = CASCADE))
  */
)
@TypeConverters(Converters::class)
data class TransactionModel(
    @PrimaryKey
    val Transaction_ID:String,
    val Date: String,//dd-mm-yyyy
    val C_Name:String,
    val C_ID: String,
    val C_PHONE:String,
    val Transaction_type: Boolean,
    val Amount :Float,
    val timestamp : Long,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB,name="Signature")
    val Signature:ByteArray,
    val Time :String,
    @ColumnInfo(name = "AgentPhoneNumber", index = true)
    val AgentPhoneNumber: String,
) : Parcelable, Exportable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransactionModel

        if (Transaction_ID != other.Transaction_ID) return false
        if (Date != other.Date) return false
        if (C_Name != other.C_Name) return false
        if (C_ID != other.C_ID) return false
        if (C_PHONE != other.C_PHONE) return false
        if (Transaction_type != other.Transaction_type) return false
        if (Amount != other.Amount) return false
        if (timestamp != other.timestamp) return false
        if (!Signature.contentEquals(other.Signature)) return false
        if (Time != other.Time) return false
        if (AgentPhoneNumber != other.AgentPhoneNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Transaction_ID.hashCode()
        result = 31 * result + Date.hashCode()
        result = 31 * result + C_Name.hashCode()
        result = 31 * result + C_ID.hashCode()
        result = 31 * result + C_PHONE.hashCode()
        result = 31 * result + Transaction_type.hashCode()
        result = 31 * result + Amount.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + Signature.contentHashCode()
        result = 31 * result + Time.hashCode()
        result = 31 * result + AgentPhoneNumber.hashCode()
        return result
    }
}