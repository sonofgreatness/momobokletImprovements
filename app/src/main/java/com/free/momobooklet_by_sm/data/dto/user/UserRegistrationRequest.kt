package com.free.momobooklet_by_sm.data.dto.user

import com.free.momobooklet_by_sm.common.util.classes.Role

data class UserRegistrationRequest(

    private val userName: String? = null,
    private val momoName: String? = null,
    private val password: String? = null,
    private val email: String? = null,
    private val role: Role? = Role.USER
)



