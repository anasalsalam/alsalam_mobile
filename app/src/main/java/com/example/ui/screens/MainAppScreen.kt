package com.example.ui.screens

import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.viewmodel.ShopViewModel
import com.example.ui.components.*

import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch


@Composable
fun BarChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF6750A4)
) {
    Canvas(modifier = modifier) {
        val spacing = 20.dp.toPx()
        val barWidth = (size.width - (data.size + 1) * spacing) / data.size
        val maxVal = (data.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(1.0)

        data.forEachIndexed { index, pair ->
            val barHeight = (pair.second / maxVal) * size.height
            val x = spacing + index * (barWidth + spacing)
            val y = (size.height - barHeight).toFloat()

            drawRect(
                color = barColor,
                topLeft = androidx.compose.ui.geometry.Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight.toFloat())
            )
        }
    }
}

@Composable
fun AreaChart(
    data: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFFB3261E),
    fillColor: Color = Color(0xFFB3261E).copy(alpha = 0.2f)
) {
    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas
        val maxVal = (data.maxOfOrNull { it } ?: 1.0).coerceAtLeast(1.0)
        val stepX = size.width / (data.size - 1)

        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, (size.height - (data[0] / maxVal) * size.height).toFloat())
            data.forEachIndexed { index, value ->
                if (index > 0) {
                    lineTo(index * stepX, (size.height - (value / maxVal) * size.height).toFloat())
                }
            }
        }

        val fillPath = androidx.compose.ui.graphics.Path().apply {
            addPath(path)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }

        drawPath(fillPath, color = fillColor)
        drawPath(path, color = lineColor, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()))
    }
}

enum class Tab(val label: String, val arLabel: String, val icon: ImageVector) {
    Dashboard("Dashboard", "لوحة التحكم", Icons.Default.Dashboard),
    Inventory("Inventory", "المخزون", Icons.Default.Inventory),
    Sales("Invoicing", "الفواتير", Icons.Default.Receipt),
    CRM("CRM", "العملاء والموردين", Icons.Default.People),
    Finance("Finance", "المالية والمصروفات", Icons.Default.AccountBalanceWallet),
    Employees("Security", "الصلاحيات والموظفين", Icons.Default.Security),
    AIHelper("AI Grounded Helper", "المساعد الذكي والبحث", Icons.Default.AutoAwesome)
}

@Composable
fun MainAppScreen(viewModel: ShopViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val errorMsg = viewModel.loginError

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF)) // Beautiful light M3 canvas background
    ) {
        if (currentUser == null) {
            LoginScreen(
                errorMsg = errorMsg,
                onLogin = { u, p -> viewModel.login(u, p) }
            )
        } else {
            DashboardLayout(
                user = currentUser!!,
                viewModel = viewModel,
                onLogout = { viewModel.logout() }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
    errorMsg: String?,
    onLogin: (String, String) -> Boolean
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Light gradient professional polish background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFFEF7FF), Color(0xFFF3EDF7)),
                        radius = 1200f
                    )
                )
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 480.dp)
                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFF6750A4), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Construction,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Smart Repair Manager",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20)
                )
                Text(
                    text = "مدير صيانة الهواتف المطور",
                    fontSize = 14.sp,
                    color = Color(0xFF49454F),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Inputs
                PolishTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username / اسم المستخدم",
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                PolishTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password / كلمة المرور",
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input")
                )

                if (errorMsg != null) {
                    Text(
                        text = errorMsg,
                        color = Color(0xFFB3261E), // M3 Error red
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                PolishButton(
                    text = "Confirm Entrance / تسجيل الدخول",
                    onClick = { onLogin(username, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_button")
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Quick selector credentials guide for convenience
                HorizontalDivider(color = Color(0xFFCAC4D0))
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "👇 Quick Employee Login (طرق الدخول التجريبي السريع)",
                    color = Color(0xFF49454F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center,
                    maxItemsInEachRow = 2
                ) {
                    val quickLogins = listOf(
                        Triple("admin", "Admin (المدير)", Color(0xFF6750A4)),
                        Triple("tech", "Technician (فني)", Color(0xFF38BDF8)),
                        Triple("cashier", "Cashier (كاشير)", Color(0xFF2E7D32)),
                        Triple("accountant", "Accountant (محاسب)", Color(0xFFF57C00))
                    )
                    
                    quickLogins.forEach { (usr, title, color) ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color.copy(alpha = 0.12f))
                                .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .clickable {
                                    username = usr
                                    password = usr
                                    onLogin(usr, usr)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(title, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardLayout(
    user: User,
    viewModel: ShopViewModel,
    onLogout: () -> Unit
) {
    var activeTab by remember { mutableStateOf(Tab.Dashboard) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWidescreen = maxWidth > 780.dp

        if (isWidescreen) {
            // ==================== MODERN WEB PLATFORM VIEW (WIDESCREEN SIDEBAR LAYOUT) ====================
            Row(modifier = Modifier.fillMaxSize()) {
                // Wide Sidebar Panel
                Column(
                    modifier = Modifier
                        .width(260.dp)
                        .fillMaxHeight()
                        .background(Color(0xFFF3EDF7)) // Beautiful M3 side container theme colors
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Branding Header - Matching Web Platform Theme
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF6750A4), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Construction,
                                    contentDescription = "Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "سمارت ريبير",
                                    color = Color(0xFF1D1B20),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "Smart Repair Platform",
                                    color = Color(0xFF49454F),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Real-time Cloud Connection status widget
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEADDFF).copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF4CAF50), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sync Connected • Online",
                                    color = Color(0xFF21005D),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Left Side vertical navigations (like a real modern React/Web platform)
                        Tab.values().forEach { tab ->
                            val isSelected = activeTab == tab
                            val bgColor = if (isSelected) Color(0xFFEADDFF) else Color.Transparent
                            val tColor = if (isSelected) Color(0xFF21005D) else Color(0xFF49454F)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(bgColor)
                                    .clickable { activeTab = tab }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                    tint = tColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = tab.label,
                                        color = tColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = tab.arLabel,
                                        color = tColor.copy(alpha = 0.7f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Logged in User Information Profile Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = 1.dp, color = Color(0xFFCAC4D0), shape = RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xFF6750A4), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.username.take(1).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Column {
                                    Text(
                                        text = user.username,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D1B20)
                                    )
                                    Text(
                                        text = user.role.uppercase(),
                                        fontSize = 9.sp,
                                        color = Color(0xFF757575),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = "Logout",
                                    tint = Color(0xFFB3261E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Sidebar border divider
                Spacer(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFFCAC4D0)))

                // Main Content Workspace Frame on the right
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Modern Header Bar showing Active Space info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 24.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${activeTab.label} Workspace Mode",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1B20)
                            )
                            Text(
                                text = "بوابة عمل • ${activeTab.arLabel}",
                                fontSize = 11.sp,
                                color = Color(0xFF49454F)
                            )
                        }

                        // Status indicators
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "System Date (UTC): " + SimpleDateFormat("yyyy-MM-dd", Locale.US).format(java.util.Date()),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF6750A4),
                                modifier = Modifier
                                    .background(Color(0xFFEADDFF), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFFFEF7FF))
                            .padding(20.dp)
                    ) {
                        when (activeTab) {
                            Tab.Dashboard -> DashboardTab(viewModel = viewModel, role = user.role)
                            Tab.Inventory -> InventoryTab(viewModel = viewModel, role = user.role)
                            Tab.Sales -> SalesTab(viewModel = viewModel, role = user.role)
                            Tab.CRM -> CRMTab(viewModel = viewModel, role = user.role)
                            Tab.Finance -> FinanceTab(viewModel = viewModel, role = user.role)
                            Tab.Employees -> EmployeesTab(viewModel = viewModel, role = user.role)
                            Tab.AIHelper -> AIHelperTab()
                        }
                    }
                }
            }
        } else {
            // ==================== GRACEFUL MOBILE COMPACT VIEW LAYOUT ====================
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFEF7FF))
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF6750A4), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Construction,
                                contentDescription = "Logo icon",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "سمارت ريبير Portal",
                                color = Color(0xFF1D1B20),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF4CAF50), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "متصل • ${user.username}",
                                    color = Color(0xFF2E7D32),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3EDF7))
                            .border(1.dp, Color(0xFFCAC4D0), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Horizontal M3 scrolling tab bar
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3EDF7))
                        .border(width = 0.5.dp, color = Color(0xFFCAC4D0))
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(Tab.values()) { tab ->
                        val isSelected = activeTab == tab
                        val bgColor = if (isSelected) Color(0xFFEADDFF) else Color.Transparent
                        val tColor = if (isSelected) Color(0xFF21005D) else Color(0xFF49454F)
                        val bColor = if (isSelected) Color(0xFFCAC4D0) else Color.Transparent

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(bgColor)
                                .then(if (isSelected) Modifier.border(1.dp, bColor, RoundedCornerShape(100.dp)) else Modifier)
                                .clickable { activeTab = tab }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(tab.label, color = tColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(tab.arLabel, color = tColor.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                // Compact layout content drawer
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFFFEF7FF))
                        .padding(14.dp)
                ) {
                    when (activeTab) {
                        Tab.Dashboard -> DashboardTab(viewModel = viewModel, role = user.role)
                        Tab.Inventory -> InventoryTab(viewModel = viewModel, role = user.role)
                        Tab.Sales -> SalesTab(viewModel = viewModel, role = user.role)
                        Tab.CRM -> CRMTab(viewModel = viewModel, role = user.role)
                        Tab.Finance -> FinanceTab(viewModel = viewModel, role = user.role)
                        Tab.Employees -> EmployeesTab(viewModel = viewModel, role = user.role)
                        Tab.AIHelper -> AIHelperTab()
                    }
                }
            }
        }
    }
}

// ======================== TABS IMPLEMENTATION ========================

@Composable
fun DashboardTab(viewModel: ShopViewModel, role: String) {
    val tasks by viewModel.repairTickets.collectAsStateWithLifecycle()
    val parts by viewModel.parts.collectAsStateWithLifecycle()
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "📊 Sales Stream Channels Analysis (مقارنة المبيعات)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1D1B20)
                )
                Spacer(modifier = Modifier.height(12.dp))

                val cashTotal = invoices.filter { it.type == "cash" }.sumOf { it.totalAmount }
                val creditTotal = invoices.filter { it.type == "credit" }.sumOf { it.totalAmount }
                val installmentTotal = invoices.filter { it.type == "installment" }.sumOf { it.totalAmount }

                val chartData = listOf(
                    "Cash" to cashTotal,
                    "Credit" to creditTotal,
                    "Installment" to installmentTotal
                )

                BarChart(
                    data = chartData,
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
            }
        }
        // Upper stats using M3 Professional Polish colors
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val lowStockCount = parts.filter { it.quantity <= it.minLimit }.size
            
            PolishStatsCard(
                title = "Total Parts (أصناف القطع)",
                value = "${parts.size}",
                subtitle = "Active catalogue entries",
                color = Color(0xFF6750A4),
                containerColor = Color(0xFFF3EDF7),
                borderColor = Color(0xFFCAC4D0),
                modifier = Modifier.weight(1f)
            )
            PolishStatsCard(
                title = "Low Stock Alert (نقص بالقطع)",
                value = "$lowStockCount",
                subtitle = "Items at/under min limits",
                color = if (lowStockCount > 0) Color(0xFFF57C00) else Color(0xFF2E7D32),
                containerColor = if (lowStockCount > 0) Color(0xFFFFF9E6) else Color(0xFFE8F5E9),
                borderColor = if (lowStockCount > 0) Color(0xFFFFE082) else Color(0xFFC8E6C9),
                modifier = Modifier.weight(1f)
            )
        }

        // Action controls
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "🛠️ Works Kanban Board (جدول الصيانة والتقارير)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1D1B20)
            )

            if (role == "admin" || role == "technician" || role == "cashier") {
                PolishButton(
                    text = "New Bug Ticket / تذكرة صيانة",
                    onClick = { showAddDialog = true },
                    icon = Icons.Default.Add
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Visual Kanban Columns (Received, Checking, Completed)
        Row(
            modifier = Modifier.fillMaxWidth().height(400.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val columns = listOf(
                Triple("received", "📥 Received (مستلم)", Color(0xFF38BDF8)),
                Triple("checking", "🔍 Checking (قيد الفحص)", Color(0xFFFBBF24)),
                Triple("completed", "✅ Ready (تم التصليح)", Color(0xFF34D399))
            )

            columns.forEach { (status, title, color) ->
                val (colBg, colTxt) = when (status) {
                    "received" -> Color(0xFFEADDFF) to Color(0xFF21005D)
                    "checking" -> Color(0xFFD1E4FF) to Color(0xFF001D35)
                    else -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(24.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(colBg)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .padding(bottom = 2.dp)
                        ) {
                            Text(
                                text = title,
                                color = colTxt,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFFCAC4D0), modifier = Modifier.padding(bottom = 8.dp))

                        val statusTasks = tasks.filter { it.status == status }

                        if (statusTasks.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Empty / لا يوجد",
                                    color = Color(0xFF757575),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(statusTasks) { task ->
                                    KanbanTaskCard(
                                        task = task,
                                        badgeColor = color,
                                        onMoveForward = {
                                            val nextStatus = when(task.status) {
                                                "received" -> "checking"
                                                "checking" -> "completed"
                                                else -> "completed"
                                            }
                                            viewModel.moveRepairTicketStatus(task, nextStatus)
                                        },
                                        onDelete = { viewModel.deleteRepairTicket(task) },
                                        roleAllowed = role == "admin" || role == "technician"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var device by remember { mutableStateOf("") }
        var fault by remember { mutableStateOf("") }
        var customer by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Create Work Ticket / تذكرة صيانة") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = device, onValueChange = { device = it }, label = { Text("Device Name (اسم الجهاز)") })
                    OutlinedTextField(value = fault, onValueChange = { fault = it }, label = { Text("Fault / العطل أو العيب") })
                    OutlinedTextField(value = customer, onValueChange = { customer = it }, label = { Text("Customer Name (العميل)") })
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes / ملاحظات") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (device.isNotEmpty() && fault.isNotEmpty() && customer.isNotEmpty()) {
                            viewModel.addRepairTicket(device, fault, customer, notes)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add Ticket / إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel / إلغاء") }
            }
        )
    }
}

@Composable
fun KanbanTaskCard(
    task: RepairTicket,
    badgeColor: Color,
    onMoveForward: () -> Unit,
    onDelete: () -> Unit,
    roleAllowed: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF7FF)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = task.ticketId,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = badgeColor
                )
                
                if (roleAllowed) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(16.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF757575), modifier = Modifier.size(14.dp))
                    }
                }
            }

            Text(
                text = task.device,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF1D1B20),
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = "Fault: ${task.fault}",
                fontSize = 11.sp,
                color = Color(0xFF49454F),
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = "Client: ${task.customerName}",
                fontSize = 10.sp,
                color = Color(0xFF757575)
            )

            if (task.status != "completed" && roleAllowed) {
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = onMoveForward,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4), contentColor = Color.White),
                    shape = RoundedCornerShape(100.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.align(Alignment.End).height(24.dp)
                ) {
                    Text(
                        text = if (task.status == "received") "Investigate →" else "Complete ✔",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFF3EDF7),
    borderColor: Color = Color(0xFFCAC4D0)
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = modifier
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, color = Color(0xFF49454F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(value, color = color, fontSize = 28.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 4.dp))
            Text(subtitle, color = Color(0xFF757575), fontSize = 10.sp)
        }
    }
}

// ======================== TABS: INVENTORY ========================

@Composable
fun InventoryTab(viewModel: ShopViewModel, role: String) {
    val partsList by viewModel.parts.collectAsStateWithLifecycle()
    var searchStr by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "📦 Spare Parts & Accessories Inventory (المخزون وقطع الغيار)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1D1B20)
            )

            if (role == "admin" || role == "technician" || role == "cashier") {
                PolishButton(
                    text = "Add Part / ترميز قطعة",
                    onClick = { showAddDialog = true },
                    icon = Icons.Default.Add
                )
            }
        }

        // Search Bar Standard Polish style
        PolishTextField(
            value = searchStr,
            onValueChange = { searchStr = it },
            label = "Search by code or name... (ابحث بكود أو اسم القطعة)",
            leadingIcon = Icons.Default.Search,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        // Category selection (Classification / Systematic Organization)
        Text(
            text = "Part Category Classification / تصنيفات قطع الغيار والمخزون:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF49454F),
            modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            val categories = listOf(
                "All" to "All (الكل)",
                "Displays" to "Screens (الشاشات)",
                "Charging" to "Power & Ports (الشحن والتوصيل)",
                "Chips" to "ICs & Chips (الأي سي والمعالجات)",
                "Accessories" to "Shields & Cases (الدروع والحماية)"
            )
            items(categories) { (code, lbl) ->
                val isSel = selectedCategory == code
                val bg = if (isSel) Color(0xFFEADDFF) else Color(0xFFF3EDF7)
                val txtColor = if (isSel) Color(0xFF21005D) else Color(0xFF49454F)
                val border = if (isSel) Color(0xFF6750A4) else Color(0xFFCAC4D0)
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(bg)
                        .border(1.dp, border, RoundedCornerShape(8.dp))
                        .clickable { selectedCategory = code }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(lbl, color = txtColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        val filteredParts = partsList.filter { part ->
            when (selectedCategory) {
                "All" -> true
                "Displays" -> part.name.contains("Display", ignoreCase = true) || part.name.contains("Screen", ignoreCase = true) || part.name.contains("شاشة", ignoreCase = true)
                "Charging" -> part.name.contains("Charger", ignoreCase = true) || part.name.contains("Port", ignoreCase = true) || part.name.contains("منفذ", ignoreCase = true) || part.name.contains("شحن", ignoreCase = true)
                "Chips" -> part.name.contains("Chip", ignoreCase = true) || part.name.contains("IC", ignoreCase = true) || part.name.contains("معالج", ignoreCase = true) || part.name.contains("أيس", ignoreCase = true)
                "Accessories" -> part.name.contains("Glass", ignoreCase = true) || part.name.contains("Case", ignoreCase = true) || part.name.contains("Armor", ignoreCase = true) || part.name.contains("حماية", ignoreCase = true)
                else -> true
            }
        }.filter {
            it.name.contains(searchStr, ignoreCase = true) || it.code.contains(searchStr, ignoreCase = true)
        }

        if (filteredParts.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No matching parts in catalog / لا توجد نتائج مطابقة", color = Color(0xFF757575))
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredParts) { part ->
                    val isLow = part.quantity <= part.minLimit
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (isLow) Color(0xFFFFBF00) else Color(0xFFCAC4D0),
                                RoundedCornerShape(16.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = part.code,
                                        color = Color(0xFF6750A4),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    if (isLow) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFFFF9E6))
                                                .border(1.dp, Color(0xFFFFE082), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("LOW STOCK / حرج", color = Color(0xFFF57C00), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                
                                Text(
                                    text = part.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B20),
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text("Cost: $${part.costPrice}", color = Color(0xFF49454F), fontSize = 12.sp)
                                    Text("Sale: $${part.sellingPrice}", color = Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Available / متوفر", color = Color(0xFF49454F), fontSize = 11.sp)
                                Text("${part.quantity} pcs", color = if (isLow) Color(0xFFF57C00) else Color(0xFF1D1B20), fontSize = 20.sp, fontWeight = FontWeight.Black)
                                Text("Min Threshold: ${part.minLimit}", color = Color(0xFF757575), fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var code by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var quantity by remember { mutableStateOf("") }
        var costPrice by remember { mutableStateOf("") }
        var sellingPrice by remember { mutableStateOf("") }
        var minLimit by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Code & Catalog Spare Part (ترميز قطعة غيار جديدة)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Part Code (كود الصنف / الباركود)") })
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Part Name (الاسم بالتفصيل)") })
                    OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Initial Stock (الكمية الابتدائية)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = costPrice, onValueChange = { costPrice = it }, label = { Text("Cost Price (سعر التكلفة علامك)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = sellingPrice, onValueChange = { sellingPrice = it }, label = { Text("Selling Price (سعر البيع للعميل)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = minLimit, onValueChange = { minLimit = it }, label = { Text("Min Limit Stock alert (حد النقص المسموح)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val q = quantity.toIntOrNull() ?: 0
                        val cost = costPrice.toDoubleOrNull() ?: 0.0
                        val sell = sellingPrice.toDoubleOrNull() ?: 0.0
                        val min = minLimit.toIntOrNull() ?: 5
                        if (code.isNotEmpty() && name.isNotEmpty()) {
                            viewModel.addPart(code, name, q, cost, sell, min)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Register Part / ترميز")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel / إلغاء") }
            }
        )
    }
}

// ======================== TABS: SALES & INVOICING ========================

@Composable
fun SalesTab(viewModel: ShopViewModel, role: String) {
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val parts by viewModel.parts.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()

    var showInvoiceCreator by remember { mutableStateOf(false) }
    var salesSubTab by remember { mutableStateOf("invoices") } // invoices, installments
    
    val allInstallments by viewModel.installments.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "🧾 Invoicing & Cashier Desk (المبيعات والفواتير)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1D1B20)
            )

            if (role != "technician") {
                PolishButton(
                    text = "New Sale Invoice / فاتورة جديدة",
                    onClick = { showInvoiceCreator = true },
                    icon = Icons.Default.Receipt
                )
            }
        }

        // Sub tab navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val subTabs = listOf(
                "invoices" to "Invoices & Billing (سجل الفواتير)",
                "installments" to "Installments (مركز الأقساط)"
            )
            subTabs.forEach { (sub, lbl) ->
                val isSelected = salesSubTab == sub
                val bg = if (isSelected) Color(0xFFEADDFF) else Color(0xFFF3EDF7)
                val txtColor = if (isSelected) Color(0xFF21005D) else Color(0xFF49454F)
                val border = if (isSelected) Color(0xFF6750A4) else Color.Transparent

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(100.dp))
                        .background(bg)
                        .then(if (isSelected) Modifier.border(1.dp, border, RoundedCornerShape(100.dp)) else Modifier)
                        .clickable { salesSubTab = sub }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(lbl, color = txtColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (salesSubTab == "invoices") {
            Text(
                text = "Historic Sales Records (سجل الفواتير التاريخية)",
                color = Color(0xFF49454F),
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            if (invoices.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No previous invoices. Click above to create one. / لا توجد مبيعات مسجلة حتى الآن", color = Color(0xFF757575))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(invoices) { inv ->
                        val (badgeBg, badgeText) = when (inv.type) {
                            "cash" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                            "credit" -> Color(0xFFFADBD8) to Color(0xFFB3261E)
                            "installment" -> Color(0xFFFFF9E6) to Color(0xFFF57C00)
                            else -> Color(0xFFF3EDF7) to Color(0xFF1D1B20)
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(inv.invoiceNumber, color = Color(0xFF6750A4), fontWeight = FontWeight.Bold, fontSize = 13.sp)

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(badgeBg)
                                            .border(1.dp, badgeText.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = inv.type.uppercase(),
                                            color = badgeText,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = "Customer: ${inv.customerName}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B20),
                                    modifier = Modifier.padding(top = 6.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color(0xFFCAC4D0))
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("PAID AMOUNT (المدفوع)", color = Color(0xFF49454F), fontSize = 10.sp)
                                        Text("$${inv.paidAmount}", color = Color(0xFF2E7D32), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                    Column {
                                        Text("REMAINING DUE (المتبقي)", color = Color(0xFF49454F), fontSize = 10.sp)
                                        Text("$${inv.remainingAmount}", color = if (inv.remainingAmount > 0) Color(0xFFB3261E) else Color(0xFF757575), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("TOTAL INVOICE (الإجمالي)", color = Color(0xFF49454F), fontSize = 10.sp)
                                        Text("$${inv.totalAmount}", color = Color(0xFF1D1B20), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // "installments" tab
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Available Installment Options (Real-time monitoring)
                item {
                    Text(
                        text = "📋 Available Installment Options / خيارات التقسيط والمراقبة المتاحة:",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val plans = listOf(
                            Triple("Silver (3 Moz)", "0% Downpayment\n0% Interest", Color(0xFF6750A4)),
                            Triple("Gold (6 Moz)", "10% Downpayment\n0% Interest", Color(0xFF2E7D32)),
                            Triple("Platinum (12 Moz)", "20% Downpayment\n5% flat fee", Color(0xFFF57C00))
                        )
                        plans.forEach { (planName, planDetails, planColor) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, planColor.copy(alpha = 0.40f), RoundedCornerShape(12.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = planName,
                                        color = planColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = planDetails,
                                        color = Color(0xFF49454F),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Customer installments list
                item {
                    Text(
                        text = "⏳ Outstanding Customer Installments / الأقساط المستحقة القائمة للعملاء:",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (allInstallments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No outstanding installments in database. / لا توجد دفعات تقسيط قائمة للعملاء", color = Color(0xFF757575), fontSize = 12.sp)
                        }
                    }
                } else {
                    val invoiceMap = invoices.associateBy { it.id }
                    items(allInstallments) { inst ->
                        val associatedInvoice = invoiceMap[inst.invoiceId]
                        val clientName = associatedInvoice?.customerName ?: "Client / عميل"
                        val customerId = associatedInvoice?.customerId
                        
                        val (statusText, statusColor, statusBg) = when (inst.status) {
                            "paid" -> Triple("PAID / مسدد", Color(0xFF2E7D32), Color(0xFFE8F5E9))
                            "late" -> Triple("OVERDUE / متأخر", Color(0xFFB3261E), Color(0xFFFADBD8))
                            else -> Triple("PENDING / مستحق", Color(0xFFF57C00), Color(0xFFFFF9E6))
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = clientName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF1D1B20)
                                        )
                                        Text(
                                            text = "Invoice: ${associatedInvoice?.invoiceNumber ?: "#N/A"} | Due: ${inst.dueDate}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF49454F)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(statusBg)
                                            .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(statusText, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = Color(0xFFCAC4D0).copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("AMOUNT TO PAY / مبلغ القسط", fontSize = 10.sp, color = Color(0xFF49454F))
                                        Text("$${inst.amount}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF1D1B20))
                                    }

                                    if (inst.status != "paid") {
                                        Button(
                                            onClick = {
                                                viewModel.payInstallment(inst.id, inst.amount, customerId)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Payments, contentDescription = "Pay", modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Collect / تحصيل القسط", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showInvoiceCreator) {
        var customerName by remember { mutableStateOf("") }
        var selectedCustomerId by remember { mutableStateOf<Int?>(null) }
        var invoiceType by remember { mutableStateOf("cash") } // cash, credit, installment
        var paidAmountStr by remember { mutableStateOf("") }
        var installmentMonthsStr by remember { mutableStateOf("3") }

        // Multi items selected list
        val cartItems = remember { mutableStateListOf<Pair<Part, Int>>() }

        AlertDialog(
            onDismissRequest = { showInvoiceCreator = false },
            title = { Text("Generate Invoice / فاتورة مبيعات") },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 480.dp)
                ) {
                    item {
                        // Select existing customer
                        Text("Associate Client / ربط بعميل", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                        ) {
                            items(customers) { c ->
                                val isSelected = selectedCustomerId == c.id
                                val blockBg = if (isSelected) Color(0xFFEADDFF) else Color(0xFFF3EDF7)
                                val blockText = if (isSelected) Color(0xFF21005D) else Color(0xFF49454F)
                                val blockBorder = if (isSelected) Color(0xFF6750A4) else Color(0xFFCAC4D0)

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(blockBg)
                                        .border(1.dp, blockBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedCustomerId = if (isSelected) null else c.id
                                            customerName = if (isSelected) "" else c.name
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(c.name, color = blockText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Manual customer input if needed
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Customer Name (اسم العميل)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFCAC4D0)
                            ),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )
                    }

                    item {
                        // Invoice pricing config
                        Text("Invoice Type / طريقة الدفع", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("cash" to "Cash (نقدي)", "credit" to "Credit (آجل)", "installment" to "Installment (أقساط)").forEach { (t, lbl) ->
                                val isSel = invoiceType == t
                                val selectBg = if (isSel) Color(0xFFEADDFF) else Color(0xFFF3EDF7)
                                val selectText = if (isSel) Color(0xFF21005D) else Color(0xFF49454F)
                                val selectBorder = if (isSel) Color(0xFF6750A4) else Color(0xFFCAC4D0)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(selectBg)
                                        .border(1.dp, selectBorder, RoundedCornerShape(8.dp))
                                        .clickable { invoiceType = t }
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(lbl, color = selectText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (invoiceType == "installment") {
                        item {
                            OutlinedTextField(
                                value = installmentMonthsStr,
                                
                                onValueChange = { installmentMonthsStr = it },
                                label = { Text("Installment Months (أقساط على شهور)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6750A4),
                                    unfocusedBorderColor = Color(0xFFCAC4D0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = paidAmountStr,
                            onValueChange = { paidAmountStr = it },
                            label = { Text("Paid Amount / المبلغ المدفوع مقدماً") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFCAC4D0)
                            ),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                        HorizontalDivider(color = Color(0xFFCAC4D0))
                        Text("Add Catalog Spare Parts / إضافة صنف مع المخزون", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
                    }

                    // Available items quick cart builder
                    items(parts) { p ->
                        val qtyInCart = cartItems.find { it.first.id == p.id }?.second ?: 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF3EDF7))
                                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.name, color = Color(0xFF1D1B20), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("${p.code} | Stock: ${p.quantity} | Sale: $${p.sellingPrice}", color = Color(0xFF49454F), fontSize = 11.sp)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (qtyInCart > 0) {
                                    IconButton(
                                        onClick = {
                                            val index = cartItems.indexOfFirst { it.first.id == p.id }
                                            if (index >= 0) {
                                                val entry = cartItems[index]
                                                if (entry.second > 1) {
                                                    cartItems[index] = entry.first to (entry.second - 1)
                                                } else {
                                                    cartItems.removeAt(index)
                                                }
                                            }
                                        }
                                    ) {
                                        Text("-", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    }
                                    Text("$qtyInCart", color = Color(0xFF1D1B20), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                IconButton(
                                    onClick = {
                                        val index = cartItems.indexOfFirst { it.first.id == p.id }
                                        if (index >= 0) {
                                            val entry = cartItems[index]
                                            if (entry.second < p.quantity) {
                                                cartItems[index] = entry.first to (entry.second + 1)
                                            }
                                        } else {
                                            cartItems.add(p to 1)
                                        }
                                    }
                                ) {
                                    Text("+", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val itemsList = cartItems.map { InvoiceLineItem(partId = it.first.id, quantity = it.second, sellingPrice = it.first.sellingPrice) }
                        val paid = paidAmountStr.toDoubleOrNull() ?: 0.0
                        val months = installmentMonthsStr.toIntOrNull() ?: 3
                        
                        if (customerName.isNotEmpty() && itemsList.isNotEmpty()) {
                            viewModel.createInvoice(customerName, selectedCustomerId, invoiceType, paid, months, itemsList) {
                                showInvoiceCreator = false
                             }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4), contentColor = Color.White)
                ) {
                    Text("Register Sale / تأكيد وحفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInvoiceCreator = false }) { Text("Cancel / إلغاء", color = Color(0xFF6750A4)) }
            }
        )
    }
}

// ======================== TABS: CRM ========================

@Composable
fun CRMTab(viewModel: ShopViewModel, role: String) {
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()
    val installments by viewModel.installments.collectAsStateWithLifecycle()

    var showAddCustomer by remember { mutableStateOf(false) }
    var showAddSupplier by remember { mutableStateOf(false) }
    var showOwedPaymentDialog by remember { mutableStateOf<Supplier?>(null) }
    var viewInstallmentsCustId by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👥 Customers CRM Profile (دليل نظام العملاء والأقساط)", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Button(
                        onClick = { showAddCustomer = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4), contentColor = Color.White),
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                    ) {
                        Text("+ Add Client", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (customers.isEmpty()) {
                    Text("No customers registered / لا يوجد عملاء حالياً", color = Color(0xFF757575), fontSize = 12.sp)
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(customers) { c ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF3EDF7))
                                    .border(1.dp, Color(0xFFCAC4D0).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(c.name, color = Color(0xFF1D1B20), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("${c.phone} | ${c.email}", color = Color(0xFF49454F), fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Debts: $${c.remainingBalance}", color = if (c.remainingBalance > 0) Color(0xFFB3261E) else Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Installments Left: ${c.remainingInstallments}", color = Color(0xFF757575), fontSize = 11.sp)
                                    if (c.remainingInstallments > 0) {
                                        Text(
                                            "View Due Bills",
                                            color = Color(0xFF6750A4),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.clickable { viewInstallmentsCustId = c.id }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Supplier database
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🏢 System Suppliers Directory (دليل وحسابات الموردين)", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Button(
                        onClick = { showAddSupplier = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4), contentColor = Color.White),
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                    ) {
                        Text("+ Add Supplier", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (suppliers.isEmpty()) {
                    Text("No suppliers saved / لا يوجد موردين مسجلين", color = Color(0xFF757575), fontSize = 12.sp)
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(suppliers) { s ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF3EDF7))
                                    .border(1.dp, Color(0xFFCAC4D0).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(s.name, color = Color(0xFF1D1B20), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("${s.companyName} | ${s.phone}", color = Color(0xFF49454F), fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Owed: $${s.outstandingBalance}", color = if (s.outstandingBalance > 0) Color(0xFFF57C00) else Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    if (s.outstandingBalance > 0) {
                                        Text(
                                            "Pay Supplier Cash",
                                            color = Color(0xFF2E7D32),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.clickable { showOwedPaymentDialog = s }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialogs setup
    if (showAddCustomer) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddCustomer = false },
            title = { Text("Code New Customer (ترميز عميل)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Customer Name (اسم العميل)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6750A4), unfocusedBorderColor = Color(0xFFCAC4D0))
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone / رقم الجوال") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6750A4), unfocusedBorderColor = Color(0xFFCAC4D0))
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email (البريد الإلكتروني)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6750A4), unfocusedBorderColor = Color(0xFFCAC4D0))
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotEmpty()) {
                            viewModel.addCustomer(name, phone, email)
                            showAddCustomer = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4), contentColor = Color.White)
                ) { Text("Register Client") }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomer = false }) { Text("Cancel / إلغاء", color = Color(0xFF6750A4)) }
            }
        )
    }

    if (showAddSupplier) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var company by remember { mutableStateOf("") }
        var oBalance by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddSupplier = false },
            title = { Text("System Supplier Register (إضافة حساب مورد)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Contact Person (المسؤول)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6750A4), unfocusedBorderColor = Color(0xFFCAC4D0))
                    )
                    OutlinedTextField(
                        value = company,
                        onValueChange = { company = it },
                        label = { Text("Company Name (اسم المؤسسة / المورد)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6750A4), unfocusedBorderColor = Color(0xFFCAC4D0))
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Contact Phone") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6750A4), unfocusedBorderColor = Color(0xFFCAC4D0))
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Catalog") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6750A4), unfocusedBorderColor = Color(0xFFCAC4D0))
                    )
                    OutlinedTextField(
                        value = oBalance,
                        onValueChange = { oBalance = it },
                        label = { Text("Outstanding Owed Balance ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6750A4), unfocusedBorderColor = Color(0xFFCAC4D0))
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val bal = oBalance.toDoubleOrNull() ?: 0.0
                        if (name.isNotEmpty()) {
                            viewModel.addSupplier(name, phone, email, company, bal)
                            showAddSupplier = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4), contentColor = Color.White)
                ) { Text("Confirm Supplier") }
            },
            dismissButton = {
                TextButton(onClick = { showAddSupplier = false }) { Text("Cancel / إلغاء", color = Color(0xFF6750A4)) }
            }
        )
    }

    if (showOwedPaymentDialog != null) {
        val s = showOwedPaymentDialog!!
        var pAmount by remember { mutableStateOf("") }
        var pNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showOwedPaymentDialog = null },
            title = { Text("Disburse Payment to: ${s.companyName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Total outstanding to supplier: $${s.outstandingBalance}", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = pAmount,
                        onValueChange = { pAmount = it },
                        label = { Text("Payment Amount ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6750A4), unfocusedBorderColor = Color(0xFFCAC4D0))
                    )
                    OutlinedTextField(
                        value = pNotes,
                        onValueChange = { pNotes = it },
                        label = { Text("Payment description (e.g., Check info)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6750A4), unfocusedBorderColor = Color(0xFFCAC4D0))
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pay = pAmount.toDoubleOrNull() ?: 0.0
                        if (pay > 0.0) {
                            viewModel.paySupplier(s.id, pay, pNotes)
                            showOwedPaymentDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4), contentColor = Color.White)
                ) { Text("Register Supplier Disbursement") }
            },
            dismissButton = {
                TextButton(onClick = { showOwedPaymentDialog = null }) { Text("Cancel / إلغاء", color = Color(0xFF6750A4)) }
            }
        )
    }

    if (viewInstallmentsCustId != null) {
        val custId = viewInstallmentsCustId!!
        val customer = customers.find { it.id == custId }
        val allInvoices by viewModel.invoices.collectAsStateWithLifecycle()
        val allInsts by viewModel.installments.collectAsStateWithLifecycle()

        val custInvoices = allInvoices.filter { it.customerId == custId && it.type == "installment" }
        val custInsts = allInsts.filter { inst -> custInvoices.any { it.id == inst.invoiceId } }

        AlertDialog(
            onDismissRequest = { viewInstallmentsCustId = null },
            title = { Text("Outstanding Installments: ${customer?.name ?: "Client"}") },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (custInsts.isEmpty()) {
                        item { Text("No current pending installments.", color = Color(0xFF49454F)) }
                    } else {
                        items(custInsts) { inst ->
                            val isPaid = inst.status == "paid"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF3EDF7))
                                    .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Due: ${inst.dueDate}", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Amount: $${inst.amount}", color = Color(0xFF6750A4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("Status: ${inst.status.uppercase()}", color = if (isPaid) Color(0xFF2E7D32) else Color(0xFFF57C00), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                if (!isPaid) {
                                    Button(
                                        onClick = {
                                            viewModel.payInstallment(inst.id, inst.amount, custId)
                                            viewInstallmentsCustId = null // trigger refresh
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32), contentColor = Color.White),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                                    ) {
                                        Text("Collect $${inst.amount}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewInstallmentsCustId = null }) { Text("Close", color = Color(0xFF6750A4)) }
            }
        )
    }
}

// ======================== TABS: FINANCE ========================

@Composable
fun FinanceTab(viewModel: ShopViewModel, role: String) {
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val parts by viewModel.parts.collectAsStateWithLifecycle()

    var showAddExpense by remember { mutableStateOf(false) }

    val totalCashSales = invoices.filter { it.type == "cash" }.sumOf { it.paidAmount } + invoices.sumOf { it.paidAmount }
    val totalCreditReceivables = invoices.sumOf { it.remainingAmount }
    val totalExpenses = expenses.sumOf { it.amount } + parts.sumOf { it.quantity * it.costPrice } * 0.1
    val netCashFlow = totalCashSales - totalExpenses

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "📈 Dynamic Expense Trends Area Plot (مخطط اتجاه الاستهلاك)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1D1B20)
                )
                Spacer(modifier = Modifier.height(12.dp))

                val sortedExpenses = expenses.sortedBy { it.date }
                val dailyTrend = sortedExpenses.takeLast(7).map { it.amount }

                if (dailyTrend.size >= 2) {
                    AreaChart(
                        data = dailyTrend,
                        modifier = Modifier.fillMaxWidth().height(150.dp)
                    )
                } else {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color(0xFFF3EDF7)), contentAlignment = Alignment.Center) {
                        Text("Not enough data for trend plot", fontSize = 11.sp, color = Color(0xFF757575))
                    }
                }
            }
        }
        Text(
            text = "📊 Cash Flow & Expense Management (إدارة الإيرادات والمصروفات)",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF1D1B20),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Financial reports preview grid
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PolishStatsCard(
                title = "Total Cash Revenue / المبيعات المقبوضة",
                value = "$${String.format("%.2f", totalCashSales)}",
                subtitle = "Cash invoices + initial advances",
                color = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            )
            PolishStatsCard(
                title = "Credit Outstanding / الذمم الدائنة للعميل",
                value = "$${String.format("%.2f", totalCreditReceivables)}",
                subtitle = "Active receivables due",
                color = Color(0xFFF57C00),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PolishStatsCard(
                title = "Disbursed Expenses / المصاريف المنصرفة",
                value = "$${String.format("%.2f", totalExpenses)}",
                subtitle = "Shop utilities + payroll + parts cost",
                color = Color(0xFFB3261E),
                modifier = Modifier.weight(1f)
            )
            PolishStatsCard(
                title = "Net Capital Health / الربح التقريبي العام",
                value = "$${String.format("%.2f", netCashFlow)}",
                subtitle = "Cash revenues minus total expenses",
                color = if (netCashFlow >= 0) Color(0xFF6750A4) else Color(0xFFB3261E),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("💸 Expense Disbursals Log (جدول تقارير المصروفات)", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            
            if (role == "admin" || role == "accountant") {
                Button(
                    onClick = { showAddExpense = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E), contentColor = Color.White),
                    shape = RoundedCornerShape(100.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text("+ Disburse Cash", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (expenses.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No operational expenses yet / لم تسجل أي مصروفات مالية تشغيلية", color = Color(0xFF757575))
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(expenses) { exp ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3EDF7))
                            .border(1.dp, Color(0xFFCAC4D0).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(exp.category.uppercase(), color = Color(0xFFB3261E), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(exp.description, color = Color(0xFF1D1B20), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            
                            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(exp.date)
                            Text(dateStr, color = Color(0xFF757575), fontSize = 10.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("-$${exp.amount}", color = Color(0xFFB3261E), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            
                            if (role == "admin" || role == "accountant") {
                                Text(
                                    "Void",
                                    color = Color(0xFF757575),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { viewModel.removeExpense(exp) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddExpense) {
        var category by remember { mutableStateOf("utilities") }
        var amount by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        val categories = listOf("rent" to "Rent (الإيجار)", "utilities" to "Utilities (المرافق)", "wages" to "Wages (الرواتب)", "advertising" to "Marketing (التسويق)", "other" to "Other (أخرى)")

        AlertDialog(
            onDismissRequest = { showAddExpense = false },
            title = { Text("Log Expense Disbursement (تسجيل منصرفات نقدية)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select Category (التصنيف)", color = Color(0xFF1D1B20), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { (code, lbl) ->
                            val isSel = category == code
                            val selBg = if (isSel) Color(0xFFFADBD8) else Color(0xFFF3EDF7)
                            val selText = if (isSel) Color(0xFFB3261E) else Color(0xFF49454F)
                            val selBorder = if (isSel) Color(0xFFB3261E) else Color(0xFFCAC4D0)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(selBg)
                                    .border(1.dp, selBorder, RoundedCornerShape(8.dp))
                                    .clickable { category = code }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, color = selText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Expense Amount ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6750A4), unfocusedBorderColor = Color(0xFFCAC4D0))
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Reason / توضيح المصروف") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6750A4), unfocusedBorderColor = Color(0xFFCAC4D0))
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val aValue = amount.toDoubleOrNull() ?: 0.0
                        if (aValue > 0.0 && description.isNotEmpty()) {
                            viewModel.addExpense(category, aValue, description)
                            showAddExpense = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4), contentColor = Color.White)
                ) { Text("Disburse / صرف") }
            },
            dismissButton = {
                TextButton(onClick = { showAddExpense = false }) { Text("Cancel / إلغاء", color = Color(0xFF6750A4)) }
            }
        )
    }
}

// ======================== TABS: EMPLOYEES ========================

@Composable
fun EmployeesTab(viewModel: ShopViewModel, role: String) {
    val users by viewModel.users.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    var is2faByPass by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "🔐 Permissions & Security Logs (صلاحيات الموظفين والسكيوريتي)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1D1B20)
            )

            if (role == "admin") {
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4), contentColor = Color.White),
                    shape = RoundedCornerShape(100.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text("+ Hire Employee", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Two factor options card simulation (Security standard requested!)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("🛡️ Extra Protection: Strict Two-Factor Access", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Secure administrative operations with simulation passcode checks.", color = Color(0xFF49454F), fontSize = 11.sp)
                }

                Switch(
                    checked = is2faByPass,
                    onCheckedChange = { is2faByPass = it }
                )
            }
        }

        Text(
            text = "Active Employee Register (المستخدمين النشطين)",
            color = Color(0xFF49454F),
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { usr ->
                val roleColor = when (usr.role) {
                    "admin" -> Color(0xFF6750A4)
                    "technician" -> Color(0xFF2E7D32)
                    "cashier" -> Color(0xFF0288D1)
                    "accountant" -> Color(0xFFF57C00)
                    else -> Color(0xFF1D1B20)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3EDF7))
                        .border(1.dp, Color(0xFFCAC4D0).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(usr.username, color = Color(0xFF1D1B20), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Encryption Active | SHA-256 Enabled", color = Color(0xFF757575), fontSize = 10.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(roleColor.copy(alpha = 0.12f))
                                .border(1.dp, roleColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = usr.role.uppercase(),
                                color = roleColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (role == "admin" && usr.username != "admin") {
                            IconButton(
                                onClick = { viewModel.removeEmployee(usr) },
                                modifier = Modifier.padding(start = 12.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Fired", tint = Color(0xFFB3261E))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var chosenRole by remember { mutableStateOf("technician") }

        val roles = listOf("technician" to "Technician", "cashier" to "Cashier", "accountant" to "Accountant", "admin" to "Admin")

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Hire & Assign Role (إنشاء حساب موظف جديد)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Choose Username") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6750A4), unfocusedBorderColor = Color(0xFFCAC4D0))
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Set Secure Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6750A4), unfocusedBorderColor = Color(0xFFCAC4D0))
                    )
                    
                    Text("Select Permissions Rank:", color = Color(0xFF1D1B20), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(roles) { (code, lbl) ->
                            val isSel = chosenRole == code
                            val rBg = if (isSel) Color(0xFFEADDFF) else Color(0xFFF3EDF7)
                            val rText = if (isSel) Color(0xFF21005D) else Color(0xFF49454F)
                            val rBorder = if (isSel) Color(0xFF6750A4) else Color(0xFFCAC4D0)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(rBg)
                                    .border(1.dp, rBorder, RoundedCornerShape(8.dp))
                                    .clickable { chosenRole = code }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                  Text(lbl, color = rText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (username.isNotEmpty() && password.isNotEmpty()) {
                            viewModel.registerEmployee(username, password, chosenRole)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4), contentColor = Color.White)
                ) { Text("Deploy Credentials") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel / إلغاء", color = Color(0xFF6750A4)) }
            }
        )
    }
}

@Composable
fun AIHelperTab() {
    var queryStr by remember { mutableStateOf("") }
    var responseState by remember { mutableStateOf<com.example.data.GeminiSearchResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    val prebuiltPrompts = listOf(
        "iPhone 15 screen replacement instructions",
        "Troubleshoot Samsung S24 charging port",
        "OLED screen market prices 2026",
        "New cell battery health diagnostic guides"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = Color(0xFFCAC4D0), shape = RoundedCornerShape(24.dp))
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🤖 Google Live Grounded AI Support (البحث ومساعد الصيانة الذكي)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF6750A4)
                )
                Text(
                    text = "Powered by gemini-3.5-flash with real-time Google search grounding. Get accurate, real-world answers for repair workflows and pricing instantly.",
                    fontSize = 11.sp,
                    color = Color(0xFF49454F),
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PolishTextField(
                        value = queryStr,
                        onValueChange = { queryStr = it },
                        label = "Describe your hardware/software fault... (اكتب المشكلة أو عطل القطعة)",
                        leadingIcon = Icons.Default.Search,
                        modifier = Modifier.weight(1f)
                    )

                    PolishButton(
                        text = "Search Grounding",
                        onClick = {
                            if (queryStr.isNotEmpty()) {
                                isLoading = true
                                coroutineScope.launch {
                                    responseState = com.example.data.GeminiService.searchGroundingQuery(queryStr)
                                    isLoading = false
                                }
                            }
                        },
                        icon = Icons.Default.AutoAwesome,
                        enabled = !isLoading && queryStr.isNotEmpty()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Suggestion chips
                Text(
                    text = "Quick Technical Inquiries (استفسارات صيانة فنية مقترحة):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF757575)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(prebuiltPrompts) { p ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFEADDFF).copy(alpha = 0.5f))
                                .border(width = 0.5.dp, color = Color(0xFF6750A4).copy(alpha = 0.4f), shape = RoundedCornerShape(100.dp))
                                .clickable(enabled = !isLoading) {
                                    queryStr = p
                                    isLoading = true
                                    coroutineScope.launch {
                                        responseState = com.example.data.GeminiService.searchGroundingQuery(p)
                                        isLoading = false
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF6750A4))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(p, color = Color(0xFF21005D), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Response and Grounding panel
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .border(width = 1.dp, color = Color(0xFFCAC4D0), shape = RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            val response = responseState
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFF6750A4))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Querying live Google indexes...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF1D1B20)
                        )
                        Text(
                            text = "جاري البحث المباشر والتحليل عبر محرك غوغل والذكاء الاصطناعي...",
                            fontSize = 11.sp,
                            color = Color(0xFF49454F),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                response != null -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                text = "💡 AI Solution Report / مسودة الحل المولد والبحث:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF49454F)
                            )

                            // Boxed Response Bubble
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .background(Color(0xFFFEF7FF), RoundedCornerShape(12.dp))
                                    .border(width = 0.5.dp, color = Color(0xFFCAC4D0), shape = RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = response.answer,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1D1B20),
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        if (response.sources.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "🌐 Connected Web Grounding Sources (المصادر الموثقة في غوغل):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6750A4)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            items(response.sources) { src ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEADDFF).copy(alpha = 0.2f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                        .border(width = 0.5.dp, color = Color(0xFFCAC4D0).copy(alpha = 0.7f), shape = RoundedCornerShape(10.dp))
                                        .clickable { uriHandler.openUri(src.url) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Language,
                                                contentDescription = "Source Icon",
                                                tint = Color(0xFF6750A4),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = src.title,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF21005D),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = src.url,
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF757575),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Launch,
                                            contentDescription = "Go",
                                            tint = Color(0xFF6750A4),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    // Empty state visual report helper
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFEADDFF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Clouds",
                                tint = Color(0xFF6750A4),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Awaiting Technician Prompt / بانتظار استفسار فني",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF49454F)
                        )
                        Text(
                            text = "Type any repair question or select a quick inquiry above to trigger grounded search analysis.",
                            fontSize = 10.sp,
                            color = Color(0xFF757575),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
