package com.example.skite.ui.components.sessions

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skite.data.entities.enums.SessionAttendance
import com.example.skite.data.entities.student.Student

@Composable
fun StudentAttendanceCard(
    student: Student,
    currentAttendance: SessionAttendance,
    onAttendanceChanged: (SessionAttendance) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = student.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SessionAttendance.entries.forEach { status ->
                    val isSelected = currentAttendance == status
                    val backgroundColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            when (status) {
                                SessionAttendance.PRESENT -> MaterialTheme.colorScheme.primary
                                SessionAttendance.MISSING -> MaterialTheme.colorScheme.error
                                SessionAttendance.MISSING_MC -> MaterialTheme.colorScheme.tertiary
                                SessionAttendance.MISSING_PARENT -> MaterialTheme.colorScheme.secondary
                                SessionAttendance.OTHER -> MaterialTheme.colorScheme.outline
                            }
                        } else {
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        },
                        label = "bgColorAnim"
                    )

                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            when (status) {
                                SessionAttendance.PRESENT -> MaterialTheme.colorScheme.onPrimary
                                SessionAttendance.MISSING -> MaterialTheme.colorScheme.onError
                                SessionAttendance.MISSING_MC -> MaterialTheme.colorScheme.onTertiary
                                SessionAttendance.MISSING_PARENT -> MaterialTheme.colorScheme.onSecondary
                                SessionAttendance.OTHER -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        },
                        label = "textColorAnim"
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .clickable { onAttendanceChanged(status) }
                    ) {
                        Text(
                            text = status.short,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            ),
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}
