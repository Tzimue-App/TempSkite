package com.example.skite.ui.components.students

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.skite.ui.common.listComponent.EntityListComponent
import com.example.skite.ui.viewModels.studentViewModels.StudentListViewModel.UiState

@Composable
fun StudentListContent(
    uiState: UiState.Success,
    onStudentClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    EntityListComponent(
        entities = uiState.students,
        modifier = modifier
    ) { student ->
        StudentCard(
            student = student,
            onStudentClick = { onStudentClick(student.id) }
        )
    }
}
