package app.chat_m25.ui.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class LocationItem(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

val presetLocations = listOf(
    LocationItem("天安门广场", "北京市东城区天安门广场", 39.9042, 116.4074),
    LocationItem("上海外滩", "上海市黄浦区外滩", 31.2400, 121.4900),
    LocationItem("广州塔", "广州市海珠区广州塔", 23.1141, 113.3189),
    LocationItem("深圳京基100", "深圳市罗湖区京基100", 22.5431, 114.0579),
    LocationItem("杭州西湖", "杭州市西湖区西湖", 30.2465, 120.1489),
    LocationItem("成都宽窄巷子", "成都市青羊区宽窄巷子", 30.6587, 104.0544),
    LocationItem("重庆解放碑", "重庆市渝中区解放碑", 29.5587, 106.5784),
    LocationItem("武汉黄鹤楼", "武汉市武昌区黄鹤楼", 30.5482, 114.3162),
    LocationItem("西安钟楼", "西安市碑林区钟楼", 34.2601, 108.9402),
    LocationItem("南京夫子庙", "南京市秦淮区夫子庙", 32.0259, 118.7840)
)

@Composable
fun LocationPickerDialog(
    onDismiss: () -> Unit,
    onLocationSelected: (Double, Double, String) -> Unit
) {
    var selectedLocation by remember { mutableStateOf<LocationItem?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("位置") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "选择位置",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(presetLocations) { location ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedLocation = location }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (selectedLocation == location)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = location.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selectedLocation == location)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = location.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedLocation?.let {
                        onLocationSelected(it.latitude, it.longitude, it.address)
                    }
                },
                enabled = selectedLocation != null
            ) {
                Text("发送")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
