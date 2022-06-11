package com.example.momobooklet_by_sm.database.model


import android.os.Parcelable
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import kotlinx.android.parcel.Parcelize
import java.sql.Blob

@Parcelize


@Entity(tableName = "RECORDS_SHEET",
    foreignKeys = arrayOf(
        ForeignKey(entity = UserModel::class,
            parentColumns = arrayOf("MoMoNumber"),
            childColumns = arrayOf("AgentPhoneNumber"),
            onDelete = CASCADE)
    )
    )

data class TransactionModel(
    @PrimaryKey(autoGenerate = true)
    val Transaction_ID: Int,
    val Date: String,
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
//so you can update database.
): Parcelable {
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
        if (!Signature.contentEquals(other.Signature)) return false
        if (AgentPhoneNumber != other.AgentPhoneNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Transaction_ID
        result = 31 * result + Date.hashCode()
        result = 31 * result + C_Name.hashCode()
        result = 31 * result + C_ID.hashCode()
        result = 31 * result + C_PHONE.hashCode()
        result = 31 * result + Transaction_type.hashCode()
        result = 31 * result + Amount.hashCode()
        result = 31 * result + Signature.contentHashCode()
        result = 31 * result + AgentPhoneNumber.hashCode()
        return result
    }
}





