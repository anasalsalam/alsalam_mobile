package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.screens.MainAppScreen
import com.example.ui.viewmodel.ShopViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import androidx.test.core.app.ApplicationProvider
import android.app.Application
import java.io.File

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class AppFlowScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun capture_app_screens() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = ShopViewModel(application)

    composeTestRule.setContent {
      MyApplicationTheme {
        MainAppScreen(viewModel = viewModel)
      }
    }

    val screenshotsDir = File("build/outputs/roborazzi")
    if (!screenshotsDir.exists()) screenshotsDir.mkdirs()

    // 1. Login Screen
    composeTestRule.onRoot().captureRoboImage(filePath = "build/outputs/roborazzi/01_login.png")

    // 2. Perform Login
    composeTestRule.onNodeWithTag("username_input").performTextInput("admin")
    composeTestRule.onNodeWithTag("password_input").performTextInput("admin")
    composeTestRule.onNodeWithTag("login_button").performClick()

    // 3. Dashboard
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "build/outputs/roborazzi/02_dashboard.png")

    // Capture just a few more screens simply
    try {
        composeTestRule.onNodeWithText("Inventory", substring = true).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "build/outputs/roborazzi/03_inventory.png")
    } catch(e: Throwable) {
        println("Could not click Inventory: ${e.message}")
    }
  }
}
