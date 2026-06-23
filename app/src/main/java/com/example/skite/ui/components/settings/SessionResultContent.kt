package com.example.skite.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.skite.R
import com.example.skite.data.entities.resultType.ResultType
import com.example.skite.data.entities.sessionType.SessionType

private enum class SessionResultTab { SESSION_TYPES, RESULT_TYPES }

@Composable
fun SessionResultContent(
    sessionTypes: List<SessionType>,
    resultTypes: List<ResultType>,
    onSaveSessionType: (SessionType) -> Unit,
    onSaveResultType: (ResultType) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(SessionResultTab.SESSION_TYPES.ordinal) }

    Column(modifier = modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            SessionResultTab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = when (tab) {
                                SessionResultTab.SESSION_TYPES ->
                                    stringResource(R.string.settings_tab_session_types)
                                SessionResultTab.RESULT_TYPES ->
                                    stringResource(R.string.settings_tab_result_types)
                            }
                        )
                    }
                )
            }
        }

        when (SessionResultTab.entries[selectedTab]) {
            SessionResultTab.SESSION_TYPES ->
                SessionTypeContent(
                    sessionTypes = sessionTypes,
                    resultTypes = resultTypes,
                    onSave = onSaveSessionType,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

            SessionResultTab.RESULT_TYPES ->
                ResultTypeContent(
                    resultTypes = resultTypes,
                    onSave = onSaveResultType,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
        }
    }
}