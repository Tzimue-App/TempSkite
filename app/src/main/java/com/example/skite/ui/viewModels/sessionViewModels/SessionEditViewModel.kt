package com.example.skite.ui.viewModels.sessionViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skite.data.entities.session.Session
import com.example.skite.data.result.DataResult
import com.example.skite.data.repositories.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SessionEditViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _session = MutableLiveData<Session?>()
    val session: LiveData<Session?> = _session

    fun loadSession(id: Int) {
        viewModelScope.launch {
            sessionRepository.findByIdFlow(id).collect { result ->
                if (result is DataResult.Success) {
                    _session.value = result.data
                }
            }
        }
    }

    fun initSession(groupId: Int?) {
        _session.value = Session(
            name = "",
            sportType = com.example.skite.data.entities.enums.SessionSportType.RUNNING,
            state = com.example.skite.data.entities.enums.SessionState.PLANNED,
            date = Date(),
            groupId = groupId ?: 0,
            // snapshotsJson = null,
            id = 0
        )
    }

    fun saveSession(session: Session) {
        viewModelScope.launch {
            if (session.id == null || session.id == 0) {
                sessionRepository.add(session)
            } else {
                sessionRepository.update(session)
            }
        }
    }
}
