package com.example.skite.ui.components.students

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.skite.data.entities.student.Student

@Composable
fun StudentCard(
    student: Student,
    onStudentClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onStudentClick(student.id) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = student.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Number: ${student.number}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
