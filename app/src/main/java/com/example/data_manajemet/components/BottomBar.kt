package com.example.data_manajemet.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data_manajemet.navigation.BottomNavItem

@Composable
fun BottomBar(
    items: List<BottomNavItem>,
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit
) {
    NavigationBar {
        items.forEachIndexed { index, item ->
            if (item.route == "upload") {
                // FAB-style tombol upload di tengah
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = -20.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    FloatingActionButton(
                        onClick = { onItemSelected(item) },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(item.icon, contentDescription = item.title)
                    }
                }
            } else {
                NavigationBarItem(
                    selected = selectedItem == item,
                    onClick = { onItemSelected(item) },
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title) }
                )
            }
        }
    }
}
