package codes.carl.streamsprites

import android.os.Bundle
import android.Manifest
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import codes.carl.streamsprites.sensors.PhoneSensor
import codes.carl.streamsprites.ui.theme.StreamSpritesTheme
import codes.carl.streamsprites.viewmodels.ConnectionViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity(), PhoneSensor.PermissionRequester {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all {
            it.value
        }
        if (granted) {
            SpriteApplication.initializePhoneSensor(this)
        } else {
            Log.e("MainActivity", "Location permissions are required to use this app.")
        }
    }

    override fun requestLocationPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            StreamSpritesTheme {
                val connectionViewModel: ConnectionViewModel = koinViewModel()
                val isConnected by connectionViewModel.connectionStatus.observeAsState(true)

                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    if (!isConnected) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(8.dp)
                        ) {
                            Text(text = "Bad Connection. Requests failing...", color = Color.White, modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    Scaffold(modifier = Modifier.fillMaxSize().padding(8.dp)) { innerPadding ->
                        Home(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }

        requestLocationPermissions()
    }
}

@Composable
fun Home(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Stream Sprites",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = stringResource(R.string.home))

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Data",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        var isSpeedChecked by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Speed")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isSpeedChecked,
                onCheckedChange = {
                    isSpeedChecked = it
                    Log.d("MainActivity", "Switch state: $it")
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        var isBatteryChecked by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Battery Status")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isBatteryChecked,
                onCheckedChange = {
                    isBatteryChecked = it
                    Log.d("MainActivity", "Switch state: $it")
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StreamSpritesTheme {
        Home()
    }
}