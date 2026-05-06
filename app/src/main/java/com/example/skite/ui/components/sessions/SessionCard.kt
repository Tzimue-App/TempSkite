package com.example.skite.ui.components.sessions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.skite.data.entities.attendance.Attendance
import com.example.skite.data.entities.session.Session
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SessionCard(
    session: Session,
    onSessionClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    attendance: Attendance? = null,
    attendanceDisplay: Boolean? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onSessionClick(session.id) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = session.name,
                style = MaterialTheme.typography.titleMedium
            )
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            session.date?.let { date ->
                Text(
                    text = dateFormat.format(date),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "State: ${session.state.name}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (attendanceDisplay != null && attendanceDisplay) {
                if (attendance != null) {
                    Text(
                        text = "Attendance: ${attendance.attendance.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Attendance: TBD",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
