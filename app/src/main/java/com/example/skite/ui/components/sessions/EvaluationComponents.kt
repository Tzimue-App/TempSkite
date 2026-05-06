package com.example.skite.ui.components.sessions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChronoEvaluationComponent(
    jsonData: String,
    onDataUpdated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Basic Chrono UI stub. Will expand with elapsed time later.
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Stopwatch Tool", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { /* Start */ }) { Text("Play") }
            Button(onClick = { /* Stop */ }) { Text("Pause") }
            Button(onClick = { /* Reset */ }) { Text("Reset") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onDataUpdated("{\"laps\": [1000, 2000]}") }) {
            Text("Lap")
        }
    }
}

@Composable
fun DefaultEvaluationComponent(
    jsonData: String,
    onDataUpdated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Default dynamic skills layout stub.
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp)
    ) {
        Text("Skills Evaluation", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("jsonData: $jsonData", style = MaterialTheme.typography.bodySmall)
        
        Spacer(modifier = Modifier.height(16.dp))
        // Simulated sliders for testing JSON mutations.
        var skillScore by remember { mutableStateOf(0.5f) }
        
        Text("Skill 1 Score: ${(skillScore * 100).toInt()}%")
        Slider(
            value = skillScore,
            onValueChange = { 
                skillScore = it
                onDataUpdated("{\"skill1\": $it}") 
            }
        )
    }
}
