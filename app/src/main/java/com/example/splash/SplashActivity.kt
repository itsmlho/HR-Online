package com.example.splash

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.splash.ui.theme.SplashTheme
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import java.util.Locale

enum class SplashScreenState {
    Logo, FindingLocation, GovLogo, Welcome, UserInfo
}

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashTheme {
                var location by remember { mutableStateOf("Unknown") }
                var screenState by remember { mutableStateOf(SplashScreenState.Logo) }
                val context = LocalContext.current

                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                fun updateLocationName(loc: Location?) {
                    if (loc != null) {
                        try {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                            if (addresses != null && addresses.isNotEmpty()) {
                                val address = addresses[0]
                                val city = address.locality ?: address.subAdminArea
                                val state = address.adminArea
                                val country = address.countryName
                                val fullAddress = listOfNotNull(city, state, country).joinToString(", ")
                                location = if (fullAddress.isBlank()) "Unknown" else fullAddress
                            }
                        } catch (e: Exception) { /* Failed */ }
                    }
                    screenState = SplashScreenState.GovLogo
                }

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        fusedLocationClient.removeLocationUpdates(this)
                        updateLocationName(locationResult.lastLocation)
                    }
                }

                @SuppressLint("MissingPermission")
                fun requestLocationUpdates() {
                    val locationRequest = LocationRequest.create().apply { priority = LocationRequest.PRIORITY_HIGH_ACCURACY }
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                }

                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
                    if (it) requestLocationUpdates() else updateLocationName(null)
                }

                LaunchedEffect(screenState) {
                    when (screenState) {
                        SplashScreenState.Logo -> {
                            delay(2500)
                            screenState = SplashScreenState.FindingLocation
                        }
                        SplashScreenState.FindingLocation -> {
                            delay(3000) // Finding animation duration
                            val playServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
                            if (playServicesAvailable == ConnectionResult.SUCCESS) {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    requestLocationUpdates()
                                } else {
                                    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            } else {
                                updateLocationName(null)
                            }
                        }
                        SplashScreenState.GovLogo -> {
                            delay(2500)
                            screenState = SplashScreenState.Welcome
                        }
                        SplashScreenState.Welcome -> {
                            delay(2500)
                            screenState = SplashScreenState.UserInfo
                        }
                        SplashScreenState.UserInfo -> {
                            delay(3000)
                            startActivity(Intent(context, MainActivity::class.java))
                            finish()
                        }
                    }
                }

                SplashScreenContent(state = screenState, location = location)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SplashScreenContent(state: SplashScreenState, location: String) {
    val baseColor = Color(0xFFFF6568)
    val gradient = Brush.verticalGradient(listOf(baseColor, Color.White), startY = 800f)

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.background(gradient)) {
            AnimatedContent(
                targetState = state,
                transitionSpec = { fadeIn(animationSpec = tween(1200)) with fadeOut(animationSpec = tween(1200)) },
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { targetState ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    when (targetState) {
                        SplashScreenState.Logo -> LogoStage()
                        SplashScreenState.FindingLocation -> FindingLocationStage()
                        SplashScreenState.GovLogo -> GovLogoStage(location = location)
                        SplashScreenState.Welcome -> WelcomeStage()
                        SplashScreenState.UserInfo -> UserInfoStage()
                    }
                }
            }
        }
    }
}

@Composable
private fun LogoStage() {
    var scale by remember { mutableStateOf(0.5f) }
    var textAlpha by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        scale = 1f
        delay(1000)
        textAlpha = 1f
    }

    val animatedScale by animateFloatAsState(targetValue = scale, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    val animatedAlpha by animateFloatAsState(targetValue = textAlpha, animationSpec = tween(500))

    // IMPORTANT: Replace with your actual logo (e.g., R.drawable.ic_hr_logo)
    Image(painter = painterResource(id = R.drawable.ic_app_logo), "", modifier = Modifier.size(120.dp).scale(animatedScale))
    Spacer(modifier = Modifier.height(16.dp))
    Text("HR ONLINE", fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.alpha(animatedAlpha))
}

@Composable
private fun FindingLocationStage() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse)
    )
    Icon(Icons.Default.LocationOn, "", tint = Color(0xFF3F51B5), modifier = Modifier.size(80.dp).scale(scale))
    Spacer(modifier = Modifier.height(8.dp))
    Text("mencari lokasi saat ini", fontSize = 14.sp, color = Color.Gray)
}

@Composable
private fun GovLogoStage(location: String) {
    // IMPORTANT: Replace with your actual government logo (e.g., R.drawable.ic_gov_logo_jabar)
    Image(painter = painterResource(id = R.drawable.ic_launcher_background), "", modifier = Modifier.size(150.dp))
    Spacer(modifier = Modifier.height(8.dp))
    val displayText = if (location == "Unknown") "Location not found" else location
    Text(displayText, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
}

@Composable
private fun WelcomeStage() {
    Text("SELAMAT DATANG", fontSize = 32.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
}

@Composable
private fun UserInfoStage() {
    Column(horizontalAlignment = Alignment.Start) {
        Text("NIM : 3124104096")
        Text("NAMA : ARIES ADITYANTO")
        Text("KELAS : TI.24.B1")
    }
}

@Preview(showBackground = true, name = "1. Logo") @Composable fun P1() { SplashTheme { SplashScreenContent(SplashScreenState.Logo, "Unknown") } }
@Preview(showBackground = true, name = "2. Finding") @Composable fun P2() { SplashTheme { SplashScreenContent(SplashScreenState.FindingLocation, "Unknown") } }
@Preview(showBackground = true, name = "3. Gov Logo") @Composable fun P3() { SplashTheme { SplashScreenContent(SplashScreenState.GovLogo, "Bekasi, Jawa Barat, Indonesia") } }
@Preview(showBackground = true, name = "4. Welcome") @Composable fun P4() { SplashTheme { SplashScreenContent(SplashScreenState.Welcome, "Bekasi, Jawa Barat, Indonesia") } }
@Preview(showBackground = true, name = "5. User Info") @Composable fun P5() { SplashTheme { SplashScreenContent(SplashScreenState.UserInfo, "Bekasi, Jawa Barat, Indonesia") } }
