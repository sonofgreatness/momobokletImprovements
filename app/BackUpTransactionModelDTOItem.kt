data class BackUpTransactionModelDTOItem(
    val AgentPhoneNumber: String,
    val Amount: Double,
    val C_ID: String,
    val C_Name: String,
    val C_PHONE: String,
    val Date: String,
    val Signature: List<Int>,
    val Time: String,
    val Transaction_ID: String,
    val Transaction_type: Boolean
)