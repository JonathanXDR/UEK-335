import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.jonathan_russ.expense_tracker.R
import com.jonathan_russ.expense_tracker.ui.editexpense.ExpenseTextField
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun LocationOption(
    location: Location?,
    onLocationSelected: (String) -> Unit,
    onLocationChanged: (TextFieldValue) -> Unit,
    locationInputError: Boolean,
    onNext: KeyboardActionScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val fusedLocationClient = rememberFusedLocationProviderClient(context)
    var locationText by remember { mutableStateOf("") }
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                getCurrentLocation(context, fusedLocationClient) { location ->
                    locationText = location
                    onLocationSelected(location)
                }
            } else {
                showPermissionDeniedDialog = true
            }
        }
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = modifier.padding(top = 16.dp)) {
            Text(
                text = stringResource(R.string.edit_expense_location),
                style = MaterialTheme.typography.bodyLarge,
            )
            ExpenseTextField(
                value = TextFieldValue(locationText),
                onValueChange = onLocationChanged,
                placeholder = stringResource(R.string.edit_expense_location_placeholder),
                keyboardActions =
                KeyboardActions(onNext = onNext),
                singleLine = true,
                isError = locationInputError,
                modifier =
                Modifier
                    .padding(vertical = 8.dp),
            )
        }

        IconButton(onClick = {
            scope.launch {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }) {
            Icon(
                Icons.Filled.MyLocation,
                contentDescription = stringResource(R.string.edit_expense_location_get_current)
            )
        }

        if (showPermissionDeniedDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDeniedDialog = false },
                title = { Text(text = stringResource(R.string.edit_expense_location_permission_required)) },
                text = { Text(text = stringResource(R.string.edit_expense_location_permission_explanation)) },
                confirmButton = {
                    Button(onClick = {
                        showPermissionDeniedDialog = false
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }) {
                        Text(text = stringResource(R.string.dialog_try_again))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showPermissionDeniedDialog = false
                        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        })
                    }) {
                        Text(text = stringResource(R.string.dialog_open_settings))
                    }
                }
            )
        }
    }
}


@Composable
fun rememberFusedLocationProviderClient(context: Context): FusedLocationProviderClient {
    return remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationFound: (String) -> Unit
) {
    val locationResult: Task<Location> = fusedLocationClient.lastLocation
    locationResult.addOnSuccessListener { location: Location? ->
        location?.let {
            getCityNameAsync(context, it.latitude, it.longitude, onLocationFound)
        }
    }
}

private fun getCityNameAsync(
    context: Context,
    latitude: Double,
    longitude: Double,
    onCityNameFound: (String) -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())
    geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
        override fun onGeocode(addresses: List<Address>) {
            if (addresses.isNotEmpty()) {
                val cityName = addresses[0].locality ?: "Unknown Location"
                onCityNameFound(cityName)
            } else {
                onCityNameFound("Location Not Found")
            }
        }
    })
}
