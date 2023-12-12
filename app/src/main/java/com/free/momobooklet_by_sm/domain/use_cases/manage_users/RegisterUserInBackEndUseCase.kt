package com.free.momobooklet_by_sm.domain.use_cases.manage_users

import com.free.momobooklet_by_sm.common.util.Constants.Companion.BACKEND_REG_FAIL
import com.free.momobooklet_by_sm.common.util.Constants.Companion.BACKEND_REG_OK
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.Resource
import com.free.momobooklet_by_sm.data.dto.user.UserRegistrationRequest
import com.free.momobooklet_by_sm.domain.repositories.user.BackEndUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class RegisterUserInBackEndUseCase@Inject constructor(val repository:BackEndUserRepository) {

    operator fun invoke(request : UserRegistrationRequest) : Flow<Resource<String>> = flow{

        try{
            emit(Resource.Loading("Registration Loading"))
            val response = repository.addUser(request)
            if (response.isSuccessful)
                emit(Resource.Success(BACKEND_REG_OK))
            else
                emit(Resource.Error(BACKEND_REG_FAIL))
        }
        catch(ex: Exception){
            emit(Resource.Error(BACKEND_REG_FAIL))
            Timber.d(BACKEND_REG_FAIL +ex.message)
        }


    }



}