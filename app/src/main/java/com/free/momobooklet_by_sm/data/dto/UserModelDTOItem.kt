package com.free.momobooklet_by_sm.data.dto


import com.free.momobooklet_by_sm.data.local.models.UserModel
import com.google.gson.annotations.SerializedName

data class UserModelDTOItem(
    @SerializedName("AgentEmail")
    val agentEmail: String,
    @SerializedName("AgentPassword")
    val agentPassword: String,
    @SerializedName("IsIncontrol")
    val isIncontrol: Boolean,
    @SerializedName("IsRemoteRegistered")
    val isRemoteRegistered: Boolean,
    @SerializedName("MoMoName")
    val moMoName: String,
    @SerializedName("MoMoNumber")
    val moMoNumber: String
)
{

    fun makeUserModel(): UserModel{
        return UserModel(
            moMoName.trim(),
            moMoNumber.trim(),
            agentEmail.trim(),
            agentPassword.trim(),
            IsIncontrol = false,
            isRemoteRegistered,
           FireBaseVerificationId =  null
        )
    }
}