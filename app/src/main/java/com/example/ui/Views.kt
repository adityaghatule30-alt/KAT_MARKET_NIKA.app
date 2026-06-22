package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: MarketViewModel) {
    val activeSection by viewModel.currentSection.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val listings by viewModel.filteredListings.collectAsState(initial = emptyList())
    val businesses by viewModel.allBusinesses.collectAsState()
    val families by viewModel.allFamilies.collectAsState()
    val rcdDeals by viewModel.allRcdDeals.collectAsState()
    val ads by viewModel.allAds.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val richList by viewModel.richList.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val fixedDeposits by viewModel.fixedDeposits.collectAsState()
    val myInventory by viewModel.myInventory.collectAsState()
    val myVouches by viewModel.myVouches.collectAsState()

    val alertMsg by viewModel.alertMessage.collectAsState()
    val selectedListing by viewModel.selectedListing.collectAsState()

    var showCreateListingDialog by remember { mutableStateOf(false) }
    var showAiAssistantMenu by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Dismiss dialogues
    if (alertMsg != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearAlert() },
            title = { Text("App Notice", color = GoldAccent, fontWeight = FontWeight.Bold) },
            text = { Text(alertMsg ?: "", color = Color.White) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearAlert() }) {
                    Text("OK", color = GoldAccent)
                }
            },
            containerColor = CardSlateBg
        )
    }

    // AI Assistant expanded menu
    if (showAiAssistantMenu) {
        Dialog(onDismissRequest = { showAiAssistantMenu = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                border = BorderStroke(1.dp, GoldAccent)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🤖 KAT AI Companion Advisor", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select a specialized prompt task below:", color = SoftGrayText, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    val aiShortcuts = listOf(
                        "🔍 Price Check" to "Can you do a price check on a BMW M5 in the current market?",
                        "🏢 Business Advice" to "Give me business advice on how to maximize profits with 24/7 stores.",
                        "📝 Generate Listing" to "Write an appealing marketplace template description for selling a property.",
                        "📈 Market Analysis" to "Analyze the current trading assets and give me a full market analysis report."
                    )

                    aiShortcuts.forEach { (label, prompt) ->
                        Button(
                            onClick = {
                                showAiAssistantMenu = false
                                viewModel.selectSection(MainSection.AI_ADVISOR)
                                viewModel.sendAiChat(prompt)
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate)
                        ) {
                            Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { showAiAssistantMenu = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CardSlateBg,
                drawerContentColor = Color.White,
                modifier = Modifier.width(280.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CardSlateBg)
                        .padding(16.dp)
                ) {
                    // Drawer Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(GoldAccent, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("K", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "KAT_MARKET",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }

                    // Profile Summary Widget in Sidebar
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(GoldAccent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        (userProfile?.username ?: "A").take(1).uppercase(),
                                        color = DeepSlateBg,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        userProfile?.username ?: "Aditya",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🛡 Verified Trader", color = GreenVerify, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("💎 VIP", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Reputation Index", color = SoftGrayText, fontSize = 10.sp)
                                Text("⭐ 95/100", color = GreenVerify, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Drawer Menu Items
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        val itemsList = listOf(
                            "🏠 Dashboard" to {
                                viewModel.selectSection(MainSection.DASHBOARD)
                            },
                            "🛒 Marketplace" to {
                                viewModel.selectedCategoryFilter.value = "ALL"
                                viewModel.selectSection(MainSection.MARKETPLACE)
                            },
                            "🚗 Vehicles" to {
                                viewModel.selectedCategoryFilter.value = "Vehicle"
                                viewModel.selectSection(MainSection.MARKETPLACE)
                            },
                            "🏠 Properties" to {
                                viewModel.selectedCategoryFilter.value = "Property"
                                viewModel.selectSection(MainSection.MARKETPLACE)
                            },
                            "🏢 Businesses" to {
                                viewModel.selectSection(MainSection.BUSINESS_NETWORK)
                            },
                            "❤️ Wishlist [RCD]" to {
                                viewModel.selectSection(MainSection.RCD_CENTER)
                            },
                            "💰 Coins Store" to {
                                viewModel.selectSection(MainSection.INVENTORY)
                            },
                            "🎁 Rewards & Crates" to {
                                viewModel.selectSection(MainSection.INVENTORY)
                            },
                            "👨‍👩‍👧‍👦 Families Alliance" to {
                                viewModel.selectSection(MainSection.PROFILE_FAMILY)
                            },
                            "🎫 Support Chat" to {
                                viewModel.selectSection(MainSection.CHATS)
                            },
                            "👤 Profile Passport" to {
                                viewModel.selectSection(MainSection.PROFILE_FAMILY)
                            },
                            "⚙ Administration" to {
                                viewModel.selectSection(MainSection.ADMINISTRATION)
                            },
                            "🛡️ Scammer Shield" to {
                                viewModel.selectSection(MainSection.SCAMMER_SHIELD)
                            },
                            "🎯 Bounty Board" to {
                                viewModel.selectSection(MainSection.BOUNTY_SYSTEM)
                            }
                        )

                        items(itemsList) { (label, action) ->
                            val isActive = when {
                                label.contains("Dashboard") -> activeSection == MainSection.DASHBOARD
                                label.contains("Marketplace") && viewModel.selectedCategoryFilter.value == "ALL" -> activeSection == MainSection.MARKETPLACE
                                label.contains("Vehicles") && viewModel.selectedCategoryFilter.value == "Vehicle" -> activeSection == MainSection.MARKETPLACE
                                label.contains("Properties") && viewModel.selectedCategoryFilter.value == "Property" -> activeSection == MainSection.MARKETPLACE
                                label.contains("Businesses") -> activeSection == MainSection.BUSINESS_NETWORK
                                label.contains("Wishlist") -> activeSection == MainSection.RCD_CENTER
                                label.contains("Coins Store") -> activeSection == MainSection.INVENTORY
                                label.contains("Rewards") -> activeSection == MainSection.INVENTORY
                                label.contains("Families") -> activeSection == MainSection.PROFILE_FAMILY
                                label.contains("Support") -> activeSection == MainSection.CHATS
                                label.contains("Profile") -> activeSection == MainSection.PROFILE_FAMILY
                                label.contains("Administration") -> activeSection == MainSection.ADMINISTRATION
                                label.contains("Scammer") -> activeSection == MainSection.SCAMMER_SHIELD
                                label.contains("Bounty") -> activeSection == MainSection.BOUNTY_SYSTEM
                                else -> false
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isActive) GoldAccent.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable {
                                        action()
                                        scope.launch { drawerState.close() }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    label,
                                    color = if (isActive) GoldAccent else Color.White,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isActive) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(GoldAccent, CircleShape)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Secure Cloud Core v3.5", color = MutedText, fontSize = 9.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open Drawer", tint = Color.White)
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                when (activeSection) {
                                    MainSection.DASHBOARD -> "🏠 Dashboard"
                                    MainSection.MARKETPLACE -> "🛒 Marketplace"
                                    MainSection.BUSINESS_NETWORK -> "🏢 Businesses"
                                    MainSection.AI_ADVISOR -> "🤖 AI Advisor"
                                    MainSection.PROFILE_FAMILY -> "👤 Profiles & Families"
                                    MainSection.RCD_CENTER -> "❤️ Real Currency Deals"
                                    MainSection.ADVERTISING -> "📢 Campaigns"
                                    MainSection.INVENTORY -> "🎁 Inventory & Coins"
                                    MainSection.ADMINISTRATION -> "⚙ Settings & Admin"
                                    MainSection.CHATS -> "💬 Support Inbox"
                                    MainSection.SCAMMER_SHIELD -> "🛡️ Scammer Shield"
                                    MainSection.BOUNTY_SYSTEM -> "🎯 Bounty Board"
                                },
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    actions = {
                        Row(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(ElevatedSlate, RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = "Coins", tint = CoinGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "${userProfile?.coinBalance ?: 0} KC",
                                color = CoinGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DeepSlateBg,
                        titleContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Floating Robot AI Assistant Button
                    FloatingActionButton(
                        onClick = { showAiAssistantMenu = true },
                        containerColor = GoldAccent,
                        shape = CircleShape,
                        modifier = Modifier.testTag("floating_ai_assistant_fab")
                    ) {
                        Text("🤖", fontSize = 22.sp)
                    }

                    if (activeSection == MainSection.MARKETPLACE) {
                        FloatingActionButton(
                            onClick = { showCreateListingDialog = true },
                            containerColor = GreenVerify,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("create_listing_fab")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Item", tint = DeepSlateBg)
                        }
                    }
                }
            },
            containerColor = DeepSlateBg
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Symmetrical top search input for dashboard experience
                val searchVal by viewModel.searchQuery.collectAsState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchVal,
                        onValueChange = { viewModel.searchQuery.value = it },
                        placeholder = { Text("Search Everything...", color = MutedText, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f).height(46.dp),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = GoldAccent, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchVal.isNotBlank()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardSlateBg,
                            unfocusedContainerColor = CardSlateBg,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = ElevatedSlate
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Interactive horizontal categories shortcut row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardSlateBg)
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SubSectionChip("Main Dashboard", activeSection == MainSection.DASHBOARD) { viewModel.selectSection(MainSection.DASHBOARD) }
                    SubSectionChip("Market Board", activeSection == MainSection.MARKETPLACE) { viewModel.selectSection(MainSection.MARKETPLACE) }
                    SubSectionChip("💬 Support Inbox", activeSection == MainSection.CHATS) { viewModel.selectSection(MainSection.CHATS) }
                    SubSectionChip("🛡️ Scammer Shield", activeSection == MainSection.SCAMMER_SHIELD) { viewModel.selectSection(MainSection.SCAMMER_SHIELD) }
                    SubSectionChip("🎯 Bounty Board", activeSection == MainSection.BOUNTY_SYSTEM) { viewModel.selectSection(MainSection.BOUNTY_SYSTEM) }
                    SubSectionChip("Businesses Net", activeSection == MainSection.BUSINESS_NETWORK) { viewModel.selectSection(MainSection.BUSINESS_NETWORK) }
                    SubSectionChip("AI Companion", activeSection == MainSection.AI_ADVISOR) { viewModel.selectSection(MainSection.AI_ADVISOR) }
                    SubSectionChip("Profiles Network", activeSection == MainSection.PROFILE_FAMILY) { viewModel.selectSection(MainSection.PROFILE_FAMILY) }
                }

                AnimatedContent(
                    targetState = activeSection,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "ScreenTransition"
                ) { section ->
                    when (section) {
                        MainSection.DASHBOARD -> SaaSDashboardView(viewModel = viewModel)
                        MainSection.MARKETPLACE -> MarketplaceView(viewModel, listings)
                        MainSection.BUSINESS_NETWORK -> BusinessNetworkView(viewModel, businesses)
                        MainSection.AI_ADVISOR -> AiAdvisorView(viewModel)
                        MainSection.PROFILE_FAMILY -> ProfilesFamilyView(viewModel, userProfile, families, myVouches)
                        MainSection.RCD_CENTER -> RcdCenterView(viewModel, rcdDeals)
                        MainSection.ADVERTISING -> AdvertisingView(viewModel, ads)
                        MainSection.INVENTORY -> InventoryView(viewModel, myInventory)
                        MainSection.ADMINISTRATION -> AdministrationView(viewModel, auditLogs, rcdDeals)
                        MainSection.CHATS -> ChatInboxView(viewModel)
                        MainSection.SCAMMER_SHIELD -> ScammerShieldView(viewModel)
                        MainSection.BOUNTY_SYSTEM -> BountyBoardView(viewModel)
                    }
                }
            }
        }
    }

    // Detail display modal
    if (selectedListing != null) {
        ListingDetailsDialog(listing = selectedListing!!, viewModel = viewModel) {
            viewModel.selectedListing.value = null
        }
    }

    // Creating listings dialog form
    if (showCreateListingDialog) {
        CreateListingDialog(viewModel = viewModel) {
            showCreateListingDialog = false
        }
    }
}

@Composable
fun SaaSDashboardView(viewModel: MarketViewModel) {
    val listings by viewModel.filteredListings.collectAsState(initial = emptyList())
    val userProfile by viewModel.userProfile.collectAsState()
    var showNetWorthDialog by remember { mutableStateOf(false) }

    if (showNetWorthDialog) {
        RequestNetWorthVerificationDialog(
            viewModel = viewModel,
            userProfile = userProfile,
            onDismiss = { showNetWorthDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Greeting Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ElevatedSlate)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "👋 Good Morning, ${userProfile?.username ?: "Aditya"}",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Welcome back to KAT Market! Ready to trade?",
                            color = SoftGrayText,
                            fontSize = 12.sp
                        )
                    }
                    // Profile Image placeholder
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (userProfile?.username ?: "A").take(1).uppercase(),
                            color = DeepSlateBg,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }

        // Certified Wealth Portfolio Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (userProfile?.isNetWorthVerified == true) Color(0xFF0F172A) else CardSlateBg
            ),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, if (userProfile?.isNetWorthVerified == true) GreenVerify else ElevatedSlate)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "💰 CERTIFIED WEALTH PORTFOLIO",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                    
                    // Verification badge status
                    if (userProfile?.isNetWorthVerified == true) {
                        Surface(
                            color = GreenVerify.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(0.5.dp, GreenVerify)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🛡", fontSize = 10.sp)
                                Text("VERIFIED", color = GreenVerify, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                        }
                    } else {
                        Surface(
                            color = RedUrgent.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(0.5.dp, RedUrgent)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚠", fontSize = 10.sp)
                                Text("UNVERIFIED", color = RedUrgent, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Net Worth amount display
                val displayedWealth = if (userProfile?.isNetWorthVerified == true) {
                    userProfile?.verifiedNetWorth ?: 0L
                } else {
                    (userProfile?.bankBalance ?: 0L) + (userProfile?.walletBalance ?: 0L)
                }

                Text(
                    formatCurrency(displayedWealth),
                    color = if (userProfile?.isNetWorthVerified == true) CoinGold else Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                
                if (userProfile?.isNetWorthVerified == true) {
                    Text(
                        "Verified By: ${userProfile?.netWorthVerifiedBy.orEmpty().ifBlank { "Staff_Alisa" }} • Last Updated: ${userProfile?.netWorthLastUpdated.orEmpty().ifBlank { "17 June 2026" }}",
                        color = SoftGrayText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    if (userProfile?.netWorthRejectionReason?.isNotBlank() == true) {
                        Text(
                            "Rejection Logs: ${userProfile?.netWorthRejectionReason}",
                            color = Color(0xFFFCA5A5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        Text(
                            "Net worth wealth estimation unverified by Grand staff audit reviews.",
                            color = SoftGrayText,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Auto Recalculation Alerts
                if (userProfile?.isNetWorthVerified == true && userProfile?.netWorthNeedsReverification == true) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFF78350F),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚠", fontSize = 14.sp)
                            Text(
                                "Net Worth Requires Reverification (Asset sold, property bought or new listing added)",
                                color = Color(0xFFFDE68A),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Divider(
                    color = ElevatedSlate,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Breakdown section
                Text(
                    "WEALTH PORTFOLIO BREAKDOWN",
                    color = SoftGrayText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                val bBank = if (userProfile?.isNetWorthVerified == true) userProfile?.verifiedBankCodeBalance ?: 0L else userProfile?.bankBalance ?: 0L
                val bVehicles = if (userProfile?.isNetWorthVerified == true) userProfile?.verifiedVehiclesWorth ?: 0L else 0L
                val bProperties = if (userProfile?.isNetWorthVerified == true) userProfile?.verifiedPropertiesWorth ?: 0L else 0L
                val bBusinesses = if (userProfile?.isNetWorthVerified == true) userProfile?.verifiedBusinessesWorth ?: 0L else 0L
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("🏦 Verified Bank Vault:", color = SoftGrayText, fontSize = 11.sp)
                        Text("🚗 Verified Vehicles:", color = SoftGrayText, fontSize = 11.sp)
                        Text("🏠 Verified Properties:", color = SoftGrayText, fontSize = 11.sp)
                        Text("🏢 Verified Businesses:", color = SoftGrayText, fontSize = 11.sp)
                        Text("📈 Total Portfolio Value:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(formatCurrency(bBank), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(formatCurrency(bVehicles), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(formatCurrency(bProperties), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(formatCurrency(bBusinesses), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(formatCurrency(displayedWealth), color = CoinGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Request verification button
                Button(
                    onClick = { showNetWorthDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("request_net_worth_verification_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text(
                        if (userProfile?.isNetWorthVerified == true) "Re-Verify Portfolio Evidence" else "Request Net Worth Verification",
                        color = DeepSlateBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Quick Stats Cards - Net Worth, Coins, Active Listings, Reputation
        Text(
            "Portfolio Quick Stats",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Cash Wallet Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ElevatedSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("💳 Pocket Cash", color = SoftGrayText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            formatCurrency(userProfile?.walletBalance ?: 0L),
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Active Listings Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ElevatedSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("📦 Active Listings", color = SoftGrayText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${listings.size} Listings",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // KAT Coins Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ElevatedSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("🪙 KAT Coins", color = SoftGrayText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${userProfile?.coinBalance ?: 0} KC",
                            color = CoinGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Reputation Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ElevatedSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("🏆 Reputation", color = SoftGrayText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "95/100 (Excellent)",
                            color = GreenVerify,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Featured Listings Carousel/List
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Starred VIP Showcase",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                "See All",
                color = GoldAccent,
                fontSize = 12.sp,
                modifier = Modifier.clickable {
                    viewModel.showOnlyFeatured.value = true
                    viewModel.selectSection(MainSection.MARKETPLACE)
                }
            )
        }

        val featuredItems = listings.filter { it.isFeatured }
        if (featuredItems.isEmpty()) {
            val fallbackItems = listings.take(3)
            if (fallbackItems.isEmpty()) {
                Text("No available listings to show.", color = MutedText, fontSize = 12.sp)
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    fallbackItems.forEach { item ->
                        DashboardCard(item) {
                            viewModel.selectedListing.value = item
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                featuredItems.forEach { item ->
                    DashboardCard(item) {
                        viewModel.selectedListing.value = item
                    }
                }
            }
        }

        // Trending Assets List
        Text(
            "Trending Assets right now 🔥",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val trendingList = listOf(
            Triple("🔥 BMW M5", "520,000,000", "Vehicle"),
            Triple("🔥 G63 AMG", "450,000,000", "Vehicle"),
            Triple("🔥 House #20", "120,000,000", "Property"),
            Triple("🔥 Gas Station", "850,000,000", "Business")
        )

        trendingList.forEach { (asset, price, category) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        viewModel.searchQuery.value = asset.replace("🔥 ", "")
                        viewModel.selectedCategoryFilter.value = if (category == "Vehicle") "Vehicle" else "ALL"
                        viewModel.selectSection(MainSection.MARKETPLACE)
                    },
                colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(0.5.dp, ElevatedSlate)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(ElevatedSlate, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (category == "Vehicle") "🚗" else "🏠", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(asset, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(category, color = SoftGrayText, fontSize = 10.sp)
                        }
                    }
                    Text("$$price", color = GreenVerify, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

data class ClientVerifiedVehicle(val name: String, val value: Long, val owners: Int)
data class ClientVerifiedProperty(val num: Int, val location: String, val value: Long)
data class ClientVerifiedBusiness(val name: String, val value: Long, val profit: Long)

@Composable
fun RequestNetWorthVerificationDialog(
    viewModel: MarketViewModel,
    userProfile: UserProfile?,
    onDismiss: () -> Unit
) {
    var bankBalanceText by remember { mutableStateOf((userProfile?.bankBalance ?: 0L).toString()) }
    var selectedBankScreenshot by remember { mutableStateOf("bank_statement_proof_01.png") }
    
    // Vehicles builder state
    var vehicleInputName by remember { mutableStateOf("") }
    var vehicleInputValue by remember { mutableStateOf("") }
    var vehicleInputOwners by remember { mutableStateOf("1") }
    var vehicleScreenshotListOk by remember { mutableStateOf(false) }
    var vehicleScreenshotStorageOk by remember { mutableStateOf(false) }
    var vehicleScreenshotGarageOk by remember { mutableStateOf(false) }
    val vehiclesAdded = remember { mutableStateListOf<ClientVerifiedVehicle>() }

    // Properties builder state
    var propertyInputNumber by remember { mutableStateOf("") }
    var propertyInputLocation by remember { mutableStateOf("") }
    var propertyInputValue by remember { mutableStateOf("") }
    var propertyScreenshotOk by remember { mutableStateOf(false) }
    val propertiesAdded = remember { mutableStateListOf<ClientVerifiedProperty>() }

    // Businesses builder state
    var businessInputName by remember { mutableStateOf("") }
    var businessInputValue by remember { mutableStateOf("") }
    var businessInputProfit by remember { mutableStateOf("") }
    var businessScreenshotOwnershipOk by remember { mutableStateOf(false) }
    var businessScreenshotProfitOk by remember { mutableStateOf(false) }
    val businessesAdded = remember { mutableStateListOf<ClientVerifiedBusiness>() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg),
            border = BorderStroke(1.dp, GoldAccent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🏦 Wealth Audit Verification",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Text("✕", color = SoftGrayText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                
                Text(
                    "Submit screenshots and asset details. Grand Market staff will review and certify your net worth parameters. Once approved, unverified values will change to official verified stamps.",
                    color = SoftGrayText,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Bank Balance Proof
                Text("🏦 1. BANK BALANCE VERIFICATION", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = bankBalanceText,
                    onValueChange = { bankBalanceText = it },
                    label = { Text("Bank Vault Balance ($)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = ElevatedSlate,
                        focusedContainerColor = DeepSlateBg,
                        unfocusedContainerColor = DeepSlateBg
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))
                
                Text("Select Bank Balance Screenshot Evidence File:", color = SoftGrayText, fontSize = 10.sp)
                val bankFiles = listOf("shot_vault_balances_current.png", "personal_bank_v2.png", "statement_vouch_audit.png")
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    bankFiles.forEach { file ->
                        val isSel = selectedBankScreenshot == file
                        Box(
                            modifier = Modifier
                                .background(if (isSel) GoldAccent.copy(alpha = 0.2f) else ElevatedSlate, RoundedCornerShape(6.dp))
                                .border(1.dp, if (isSel) GoldAccent else Color.Transparent, RoundedCornerShape(6.dp))
                                .clickable { selectedBankScreenshot = file }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(file, color = if (isSel) GoldAccent else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Vehicles Owned Verification
                Text("🚗 2. VEHICLE ASSETS COMPILER", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepSlateBg),
                    border = BorderStroke(0.5.dp, ElevatedSlate)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        OutlinedTextField(
                            value = vehicleInputName,
                            onValueChange = { vehicleInputName = it },
                            placeholder = { Text("Vehicle Name (e.g. BMW M5)", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = ElevatedSlate
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = vehicleInputValue,
                                onValueChange = { vehicleInputValue = it },
                                placeholder = { Text("Est. Value ($)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = GoldAccent,
                                    unfocusedBorderColor = ElevatedSlate
                                )
                            )
                            OutlinedTextField(
                                value = vehicleInputOwners,
                                onValueChange = { vehicleInputOwners = it },
                                placeholder = { Text("Owners Count", fontSize = 11.sp) },
                                modifier = Modifier.weight(0.8f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = GoldAccent,
                                    unfocusedBorderColor = ElevatedSlate
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Required screenshots Checklist:", color = SoftGrayText, fontSize = 10.sp)
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = vehicleScreenshotListOk, onCheckedChange = { vehicleScreenshotListOk = it })
                            Text("Vehicle List Photo (.png)", color = Color.White, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = vehicleScreenshotStorageOk, onCheckedChange = { vehicleScreenshotStorageOk = it })
                            Text("Vehicle Storage Photo (.png)", color = Color.White, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = vehicleScreenshotGarageOk, onCheckedChange = { vehicleScreenshotGarageOk = it })
                            Text("Garage Screenshot (.png)", color = Color.White, fontSize = 10.sp)
                        }

                        Button(
                            onClick = {
                                val valueParsed = vehicleInputValue.toLongOrNull()
                                if (vehicleInputName.isNotBlank() && valueParsed != null && valueParsed > 0) {
                                    if (!vehicleScreenshotListOk || !vehicleScreenshotStorageOk || !vehicleScreenshotGarageOk) {
                                        viewModel.alertMessage.value = "All 3 vehicle screenshots must be verified!"
                                    } else {
                                        vehiclesAdded.add(
                                            ClientVerifiedVehicle(
                                                vehicleInputName,
                                                valueParsed,
                                                vehicleInputOwners.toIntOrNull() ?: 1
                                            )
                                        )
                                        vehicleInputName = ""
                                        vehicleInputValue = ""
                                        vehicleInputOwners = "1"
                                        vehicleScreenshotListOk = false
                                        vehicleScreenshotStorageOk = false
                                        vehicleScreenshotGarageOk = false
                                    }
                                } else {
                                    viewModel.alertMessage.value = "Specify valid vehicle name and value amount!"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("+ Add Vehicle parameters", color = Color.White, fontSize = 10.sp)
                        }
                    }
                }

                if (vehiclesAdded.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Compiled Vehicles List:", color = Color.White, fontSize = 11.sp)
                    vehiclesAdded.forEachIndexed { idx, veh ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("• ${veh.name} [Worth: ${formatCurrency(veh.value)}] (Owners: ${veh.owners})", color = SoftGrayText, fontSize = 11.sp)
                            IconButton(onClick = { vehiclesAdded.removeAt(idx) }, modifier = Modifier.size(24.dp)) {
                                Text("✕", color = RedUrgent, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Properties Owned Verification
                Text("🏠 3. PROPERTY ASSETS COMPILER", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepSlateBg),
                    border = BorderStroke(0.5.dp, ElevatedSlate)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = propertyInputNumber,
                                onValueChange = { propertyInputNumber = it },
                                placeholder = { Text("Property Num", fontSize = 11.sp) },
                                modifier = Modifier.weight(0.8f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = GoldAccent,
                                    unfocusedBorderColor = ElevatedSlate
                                )
                            )
                            OutlinedTextField(
                                value = propertyInputLocation,
                                onValueChange = { propertyInputLocation = it },
                                placeholder = { Text("Location (e.g. Elite)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1.2f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = GoldAccent,
                                    unfocusedBorderColor = ElevatedSlate
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = propertyInputValue,
                            onValueChange = { propertyInputValue = it },
                            placeholder = { Text("State Estimated Value ($)", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = GoldAccent,
                                    unfocusedBorderColor = ElevatedSlate
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = propertyScreenshotOk, onCheckedChange = { propertyScreenshotOk = it })
                            Text("Property Ownership Proof Photo attached", color = Color.White, fontSize = 10.sp)
                        }

                        Button(
                            onClick = {
                                val valParsed = propertyInputValue.toLongOrNull()
                                val numParsed = propertyInputNumber.toIntOrNull()
                                if (numParsed != null && propertyInputLocation.isNotBlank() && valParsed != null && valParsed > 0) {
                                    if (!propertyScreenshotOk) {
                                        viewModel.alertMessage.value = "Property Ownership screenshot is required!"
                                    } else {
                                        propertiesAdded.add(
                                            ClientVerifiedProperty(
                                                numParsed,
                                                propertyInputLocation,
                                                valParsed
                                            )
                                        )
                                        propertyInputNumber = ""
                                        propertyInputLocation = ""
                                        propertyInputValue = ""
                                        propertyScreenshotOk = false
                                    }
                                } else {
                                    viewModel.alertMessage.value = "Specify valid property parameters first!"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("+ Add Property parameters", color = Color.White, fontSize = 10.sp)
                        }
                    }
                }

                if (propertiesAdded.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Compiled Properties List:", color = Color.White, fontSize = 11.sp)
                    propertiesAdded.forEachIndexed { idx, prop ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("• House #${prop.num} at ${prop.location} [Worth: ${formatCurrency(prop.value)}]", color = SoftGrayText, fontSize = 11.sp)
                            IconButton(onClick = { propertiesAdded.removeAt(idx) }, modifier = Modifier.size(24.dp)) {
                                Text("✕", color = RedUrgent, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 4: Businesses Owned Verification
                Text("🏢 4. BUSINESS ASSETS COMPILER", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepSlateBg),
                    border = BorderStroke(0.5.dp, ElevatedSlate)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        OutlinedTextField(
                            value = businessInputName,
                            onValueChange = { businessInputName = it },
                            placeholder = { Text("Business Name or ID", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = GoldAccent,
                                    unfocusedBorderColor = ElevatedSlate
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = businessInputValue,
                                onValueChange = { businessInputValue = it },
                                placeholder = { Text("Est. Value ($)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = GoldAccent,
                                    unfocusedBorderColor = ElevatedSlate
                                )
                            )
                            OutlinedTextField(
                                value = businessInputProfit,
                                onValueChange = { businessInputProfit = it },
                                placeholder = { Text("Profit Stats / day", fontSize = 11.sp) },
                                modifier = Modifier.weight(1.1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = GoldAccent,
                                    unfocusedBorderColor = ElevatedSlate
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = businessScreenshotOwnershipOk, onCheckedChange = { businessScreenshotOwnershipOk = it })
                            Text("Business Ownership Screenshot attached", color = Color.White, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = businessScreenshotProfitOk, onCheckedChange = { businessScreenshotProfitOk = it })
                            Text("Revenue Statistics proof attached", color = Color.White, fontSize = 10.sp)
                        }

                        Button(
                            onClick = {
                                val valParsed = businessInputValue.toLongOrNull()
                                val profParsed = businessInputProfit.toLongOrNull()
                                if (businessInputName.isNotBlank() && valParsed != null && profParsed != null) {
                                    if (!businessScreenshotOwnershipOk || !businessScreenshotProfitOk) {
                                        viewModel.alertMessage.value = "Business and Profit screenshots are required!"
                                    } else {
                                        businessesAdded.add(
                                            ClientVerifiedBusiness(
                                                businessInputName,
                                                valParsed,
                                                profParsed
                                            )
                                        )
                                        businessInputName = ""
                                        businessInputValue = ""
                                        businessInputProfit = ""
                                        businessScreenshotOwnershipOk = false
                                        businessScreenshotProfitOk = false
                                    }
                                } else {
                                    viewModel.alertMessage.value = "Specify business name and metrics amounts!"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("+ Add Business parameters", color = Color.White, fontSize = 10.sp)
                        }
                    }
                }

                if (businessesAdded.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Compiled Businesses List:", color = Color.White, fontSize = 11.sp)
                    businessesAdded.forEachIndexed { idx, bus ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("• ${bus.name} [Worth: ${formatCurrency(bus.value)}] (Profit: ${formatCurrency(bus.profit)}/day)", color = SoftGrayText, fontSize = 11.sp)
                            IconButton(onClick = { businessesAdded.removeAt(idx) }, modifier = Modifier.size(24.dp)) {
                                Text("✕", color = RedUrgent, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Button(
                    onClick = {
                        val bankBal = bankBalanceText.toLongOrNull()
                        if (bankBal == null || bankBal < 0L) {
                            viewModel.alertMessage.value = "Type a valid bank balance parameter!"
                        } else {
                            val vJson = vehiclesAdded.map { "{\"name\":\"${it.name}\",\"value\":${it.value},\"owners\":${it.owners}}" }.toString()
                            val pJson = propertiesAdded.map { "{\"num\":${it.num},\"location\":\"${it.location}\",\"value\":${it.value}}" }.toString()
                            val bJson = businessesAdded.map { "{\"name\":\"${it.name}\",\"value\":${it.value},\"profit\":${it.profit}}" }.toString()
                            
                            viewModel.submitNetWorthRequest(
                                bankBalance = bankBal,
                                bankScreenshot = selectedBankScreenshot,
                                vehiclesJson = vJson,
                                propertiesJson = pJson,
                                businessesJson = bJson
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("🚀 SUBMIT FOR STAFF AUDIT", color = DeepSlateBg, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, ElevatedSlate)
                ) {
                    Text("Cancel Submission", color = Color.White, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun DashboardCard(listing: MarketListing, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .clickable { onClick() }
            .border(1.dp, ElevatedSlate, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSlateBg)
    ) {
        Column {
            // Image block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Brush.verticalGradient(listOf(ElevatedSlate, DeepSlateBg))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    when (listing.category) {
                        "Vehicle" -> "🚗"
                        "Property" -> "🏠"
                        "Business" -> "🏢"
                        "Skin" -> "🎨"
                        else -> "📦"
                    },
                    fontSize = 24.sp
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    listing.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    formatCurrency(listing.askingPrice),
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("95/100", color = SoftGrayText, fontSize = 9.sp)
                    }
                    if (listing.isVerifiedSeller) {
                        Box(
                            modifier = Modifier
                                .background(GreenVerify.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("VERIFIED", color = GreenVerify, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// Sub-header tabs
@Composable
fun SubSectionChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) GoldAccent else ElevatedSlate)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) DeepSlateBg else Color.White
        )
    }
}

// ----------------------------------------------------
// SECTION 1: MARKETPLACE BOARD
// ----------------------------------------------------
@Composable
fun MarketplaceView(viewModel: MarketViewModel, listings: List<MarketListing>) {
    val search by viewModel.searchQuery.collectAsState()
    val filterCat by viewModel.selectedCategoryFilter.collectAsState()
    val verifiedOnly by viewModel.showOnlyVerifiedSellers.collectAsState()
    val featuredOnly by viewModel.showOnlyFeatured.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Advanced search
        OutlinedTextField(
            value = search,
            onValueChange = { viewModel.searchQuery.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_anywhere_input"),
            placeholder = { Text("Search plate, location, name, type (Search Everywhere)", color = MutedText) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = GoldAccent) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = GoldAccent,
                unfocusedBorderColor = ElevatedSlate,
                focusedContainerColor = CardSlateBg,
                unfocusedContainerColor = CardSlateBg
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category pill chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val categories = listOf("ALL", "Vehicle", "Property", "Business", "Skin", "Item")
            categories.forEach { cat ->
                val isSelected = filterCat == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) GoldAccent else CardSlateBg)
                        .clickable { viewModel.selectedCategoryFilter.value = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        cat,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) DeepSlateBg else SoftGrayText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Switches
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = verifiedOnly,
                    onCheckedChange = { viewModel.showOnlyVerifiedSellers.value = it },
                    colors = CheckboxDefaults.colors(checkedColor = GreenVerify)
                )
                Text("Verified", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = featuredOnly,
                    onCheckedChange = { viewModel.showOnlyFeatured.value = it },
                    colors = CheckboxDefaults.colors(checkedColor = GoldAccent)
                )
                Text("VIP Featured", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = {
                viewModel.searchQuery.value = ""
                viewModel.selectedCategoryFilter.value = "ALL"
                viewModel.showOnlyVerifiedSellers.value = false
                viewModel.showOnlyFeatured.value = false
            }) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (listings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Warning, contentDescription = "None", tint = MutedText, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No active matching listings on board.", color = SoftGrayText, textAlign = TextAlign.Center)
                    Text("Tip: Click '+' below to publish item!", color = GoldAccent, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listings) { item ->
                    ListingCard(item, viewModel) {
                        viewModel.selectedListing.value = item
                    }
                }
            }
        }
    }
}

@Composable
fun ListingCard(listing: MarketListing, viewModel: MarketViewModel, onClick: () -> Unit) {
    var showOfferDialog by remember { mutableStateOf(false) }
    var offerAmountInput by remember { mutableStateOf("") }
    var offerMessageInput by remember { mutableStateOf("") }

    var showReportDialog by remember { mutableStateOf(false) }
    var reportNotesInput by remember { mutableStateOf("") }

    var isFavorited by remember { mutableStateOf(false) }
    var isInterested by remember { mutableStateOf(false) }

    // Helper parser for Grand RP style numeric abbreviations (M, K, B)
    fun parseToAmount(input: String): Long {
        val clean = input.trim().uppercase()
        if (clean.endsWith("B")) {
            return ((clean.removeSuffix("B").toDoubleOrNull() ?: 0.0) * 1_000_000_000).toLong()
        }
        if (clean.endsWith("M")) {
            return ((clean.removeSuffix("M").toDoubleOrNull() ?: 0.0) * 1_000_000).toLong()
        }
        if (clean.endsWith("K")) {
            return ((clean.removeSuffix("K").toDoubleOrNull() ?: 0.0) * 1_000).toLong()
        }
        return clean.replace(",", "").replace("\$", "").toLongOrNull() ?: 0L
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                border = BorderStroke(
                    1.dp,
                    if (listing.isFeatured) GoldAccent else if (listing.isUrgent) RedUrgent else ElevatedSlate
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = CardSlateBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Main image block / Low visibility placeholder
            if (listing.images.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1E293B), Color(0xFF0F1115))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (listing.category) {
                        "Vehicle" -> "🚗"
                        "Property" -> "🏠"
                        "Business" -> "🏢"
                        "Skin" -> "🎨"
                        else -> "📦"
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(icon, fontSize = 28.sp)
                        Text(listing.images.first(), color = SoftGrayText, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }

                    if (listing.watermarked) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Text("🛡️ KAT_MARKET_NIKA • SECURE", color = GoldAccent, fontSize = 6.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xE60F1115), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("📸 ${listing.images.size}", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(Color(0xFF292524)), // warm brown alert
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(10.dp)) {
                        Text("⚠ Low Visibility", color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Buyers trust listings with proof screenshots", color = Color(0xFFD6D3D1), fontSize = 8.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(ElevatedSlate, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(listing.category.uppercase(), fontSize = 8.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(listing.subType, fontSize = 10.sp, color = SoftGrayText)
                    }

                    Row {
                        if (listing.isFeatured) {
                            Box(
                                modifier = Modifier
                                    .background(GoldAccent, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text("VIP", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        if (listing.isUrgent) {
                            Box(
                                modifier = Modifier
                                    .background(RedUrgent, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text("URGENT", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    listing.title,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(formatCurrency(listing.askingPrice), color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Black)
                    Text("State: ${formatCurrency(listing.statePrice)}", color = MutedText, fontSize = 9.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Divider(color = ElevatedSlate)
                Spacer(modifier = Modifier.height(6.dp))

                // Footer analytics and seller ratings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (listing.isVerifiedSeller) Icons.Default.CheckCircle else Icons.Default.AccountCircle,
                            contentDescription = "Seller",
                            tint = if (listing.isVerifiedSeller) GreenVerify else SoftGrayText,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(listing.sellerName, color = SoftGrayText, fontSize = 9.sp, maxLines = 1)
                        Text(" (⭐ ${listing.sellerReputation})", color = GreenVerify, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        UserScamBadge(listing.sellerName, viewModel)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👁️", fontSize = 8.sp)
                            Spacer(modifier = Modifier.width(1.dp))
                            Text("${listing.views}", color = MutedText, fontSize = 8.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("❤️", fontSize = 8.sp)
                            Spacer(modifier = Modifier.width(1.dp))
                            Text("${listing.favoritesCount}", color = MutedText, fontSize = 8.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = ElevatedSlate.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                // 🔥 Premium Actions Board (Grid Layout under every list item)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // ❤️ Favorite Button
                        Button(
                            onClick = {
                                viewModel.toggleFavorite(listing, isFavorited)
                                isFavorited = !isFavorited
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .testTag("btn_favorite_${listing.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFavorited) Color(0xFFF43F5E).copy(alpha = 0.2f) else ElevatedSlate
                            ),
                            border = BorderStroke(1.dp, if (isFavorited) Color(0xFFF43F5E) else ElevatedSlate),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(if (isFavorited) "💖" else "❤️", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (isFavorited) "Favorited" else "Favorite",
                                    color = if (isFavorited) Color(0xFFF43F5E) else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // 👀 Interested Button
                        Button(
                            onClick = {
                                if (!isInterested) {
                                    viewModel.notifyInterest(listing)
                                    isInterested = true
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .testTag("btn_interested_${listing.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isInterested) Color(0xFF10B981).copy(alpha = 0.2f) else ElevatedSlate
                            ),
                            border = BorderStroke(1.dp, if (isInterested) Color(0xFF10B981) else ElevatedSlate),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("👀", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (isInterested) "Interested!" else "Interested",
                                    color = if (isInterested) Color(0xFF10B981) else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // 💸 Make Offer Button
                        Button(
                            onClick = { showOfferDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .testTag("btn_make_offer_${listing.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                            border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💸", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Make Offer", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // 💬 Contact Seller Button
                        Button(
                            onClick = {
                                viewModel.sendChatMessage(listing, "👋 [DIRECT CHAT] Hello! I'd like to talk details about your asset/listing.")
                                viewModel.selectSection(MainSection.CHATS)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .testTag("btn_contact_seller_${listing.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                            border = BorderStroke(1.dp, CyanInfo.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💬", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chat Seller", color = CyanInfo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 🚨 Report Button
                    Button(
                        onClick = { showReportDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .testTag("btn_report_${listing.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x33EF4444)),
                        border = BorderStroke(1.dp, RedUrgent.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🚨", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Report Listing", color = RedUrgent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    // --- Modal : Make Offer Dialog ---
    if (showOfferDialog) {
        AlertDialog(
            onDismissRequest = { showOfferDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💸", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DIRECT DEAL NEGOTIATION", color = GoldAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Offer direct settlement terms for '${listing.title}'. The system will establish a private in-character offer channel.",
                        color = SoftGrayText,
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = offerAmountInput,
                        onValueChange = { offerAmountInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("offer_amount_input"),
                        label = { Text("Offer Amount (e.g. 500K, 45M, 1.2B)", fontSize = 11.sp) },
                        placeholder = { Text("Enter RP sum...", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = ElevatedSlate
                        ),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = offerMessageInput,
                        onValueChange = { offerMessageInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("offer_message_input"),
                        label = { Text("Message to Seller (Optional)", fontSize = 11.sp) },
                        placeholder = { Text("Ready to buy right now today...", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = ElevatedSlate
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val numericAmount = parseToAmount(offerAmountInput)
                        if (numericAmount > 0) {
                            val msg = offerMessageInput.ifBlank { "Ready to transact immediate cash!" }
                            viewModel.submitOffer(listing, numericAmount, msg)
                            showOfferDialog = false
                            // Bring to chat workspace to begin negotiation!
                            viewModel.selectSection(MainSection.CHATS)
                        } else {
                            viewModel.alertMessage.value = "Enter a valid sum! e.g., 450M or 1.2B"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("Submit Offer", color = DeepSlateBg, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showOfferDialog = false }) {
                    Text("Cancel", color = SoftGrayText, fontSize = 12.sp)
                }
            },
            containerColor = DeepSlateBg,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // --- Modal : Report Dialog ---
    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🚨", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("REPORT LISTING BOARD", color = RedUrgent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Admins monitor reported listings for scam or lowballing attempts on the server.",
                        color = SoftGrayText,
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = reportNotesInput,
                        onValueChange = { reportNotesInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("report_notes_input"),
                        label = { Text("Reason for Report", fontSize = 11.sp) },
                        placeholder = { Text("e.g. Inaccurate details, scam attempt, toxic notes", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = RedUrgent,
                            unfocusedBorderColor = ElevatedSlate
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val reason = reportNotesInput.trim()
                        if (reason.isNotEmpty()) {
                            viewModel.submitReportListing(listing, reason)
                            showReportDialog = false
                        } else {
                            viewModel.alertMessage.value = "Please state a reason for flag!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedUrgent)
                ) {
                    Text("Send Report", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel", color = SoftGrayText, fontSize = 12.sp)
                }
            },
            containerColor = DeepSlateBg,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

// ----------------------------------------------------
// SECTION 2: BUSINESS NETWORK DIRECTORY
// ----------------------------------------------------
@Composable
fun BusinessNetworkView(viewModel: MarketViewModel, businesses: List<RegisteredBusiness>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero business spotlight
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, GoldAccent), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("🌟 BUSINESS SPOTLIGHT OF THE WEEK", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Arzamas Center 24/7 Store", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("Location: Central Square • Owned by Alisa_Petrova", color = SoftGrayText, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Average Daily Profit", color = MutedText, fontSize = 10.sp)
                        Text("+8,500,000 Grand RP", color = GreenVerify, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Estimated Capital Value", color = MutedText, fontSize = 10.sp)
                        Text("540,000,000 Grand RP", color = CyanInfo, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Registered Business Network", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        businesses.forEach { biz ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlateBg)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(biz.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("${biz.type} • ${biz.location}", color = MutedText, fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ElevatedSlate)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Rating ${biz.rating} ★", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Daily Profit", color = MutedText, fontSize = 9.sp)
                            Text("+${formatCurrency(biz.dailyProfit)}", color = GreenVerify, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("State Value", color = MutedText, fontSize = 9.sp)
                            Text(formatCurrency(biz.statePrice), color = SoftGrayText, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Staff Count", color = MutedText, fontSize = 9.sp)
                            Text("${biz.employeesCount} Employees", color = CyanInfo, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Investment ROI calculations panel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ElevatedSlate, RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val paybackRatio = (biz.estimatedValue / if (biz.dailyProfit > 0) biz.dailyProfit else 1)
                        Text("ROI Payout Ratio: Approx $paybackRatio Days", color = SoftGrayText, fontSize = 10.sp)
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GoldAccent)
                                .clickable {
                                    viewModel.sendAiChat("Evaluate investment capitalization for business direct ${biz.name} in details.")
                                    viewModel.selectSection(MainSection.AI_ADVISOR)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("AI Audit", color = DeepSlateBg, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// SECTION 3: AI ADVISOR CHATBOT TERMINAL
// ----------------------------------------------------
@Composable
fun AiAdvisorView(viewModel: MarketViewModel) {
    val chatHistory by viewModel.aiChatHistory.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    var textInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // AI Header card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("🤖 KAT_MARKET_NIKA INTEL CORE", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Powered by server-side Gemini 3.5, providing unconstrained real-time appraisal checks and pricing evaluation indexes.",
                    color = SoftGrayText,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Preset command shortcuts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PresetAdviserChip("🚗 Appraisal BMW M5") { textInput = "Check price for BMW M5 f90 with 2 owners, state is 80M" }
            PresetAdviserChip("🏢 Payout: Gas Station") { textInput = "Is investing 500M in a Gas Station yielding 10M daily profit worth it? Calculate ROI." }
            PresetAdviserChip("📢 Generator Ad flyer") { textInput = "Generate a luxury, catching recruitment ad pitch flyer for family 'Nika Syndicate'" }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chat text container
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(ElevatedSlate, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatHistory) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = if (isUser) 12.dp else 0.dp,
                                    bottomEnd = if (isUser) 0.dp else 12.dp
                                )
                            )
                            .background(if (isUser) GoldAccent else CardSlateBg)
                            .padding(12.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        Text(
                            msg.text,
                            color = if (isUser) DeepSlateBg else Color.White,
                            fontSize = 13.sp,
                            fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }

            if (isAiLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = GoldAccent, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gemini reasoning active...", color = SoftGrayText, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chat input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_advisor_chat_input"),
                placeholder = { Text("Ask about valuations, payouts, pitches...", color = MutedText, fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = ElevatedSlate,
                    focusedContainerColor = CardSlateBg,
                    unfocusedContainerColor = CardSlateBg
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendAiChat(textInput)
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(GoldAccent, CircleShape)
                    .testTag("ai_advisor_send_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Send", tint = DeepSlateBg)
            }
        }
    }
}

val CircleShape = RoundedCornerShape(100.dp)

@Composable
fun PresetAdviserChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(BorderStroke(1.dp, ElevatedSlate), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(label, color = SoftGrayText, fontSize = 10.sp)
    }
}

// ----------------------------------------------------
// SECTION 4: WALLET & BANK VAULT (BANKING)
// ----------------------------------------------------
@Composable
fun WalletBankingView(
    viewModel: MarketViewModel,
    fixedDeposits: List<FixedDeposit>,
    richList: List<UserProfile>
) {
    val profile by viewModel.userProfile.collectAsState()
    var transactionTab by remember { mutableStateOf(0) } // 0: Wallet Actions, 1: Savings, 2: Richlist Leaderboards

    var depositInput by remember { mutableStateOf("") }
    var withdrawInput by remember { mutableStateOf("") }
    var transferInput by remember { mutableStateOf("") }
    var transferRecipient by remember { mutableStateOf("") }

    var lockAmountInput by remember { mutableStateOf("") }
    var lockPlanDays by remember { mutableStateOf(30) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Balances Panel
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("🏦 SECURE FINANCIAL VAULT", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Pocket Wallet Cash", color = MutedText, fontSize = 10.sp)
                        Text(formatCurrency(profile?.walletBalance ?: 0L), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Bank Safe Deposits", color = MutedText, fontSize = 10.sp)
                        Text(formatCurrency(profile?.bankBalance ?: 0L), color = CyanInfo, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = ElevatedSlate)
                Spacer(modifier = Modifier.height(10.dp))

                // Calculated Net Worth
                val assetNetWorthVal = (profile?.bankBalance ?: 0L) + (profile?.walletBalance ?: 0L)
                Text(
                    "Total Estimated Capital worth: ${formatCurrency(assetNetWorthVal)} Grand RP",
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Subtabs
        TabRow(
            selectedTabIndex = transactionTab,
            containerColor = CardSlateBg,
            contentColor = GoldAccent
        ) {
            Tab(selected = transactionTab == 0, onClick = { transactionTab = 0 }) {
                Text("Transactions", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = transactionTab == 1, onClick = { transactionTab = 1 }) {
                Text("Fixed Savings", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = transactionTab == 2, onClick = { transactionTab = 2 }) {
                Text("Wealth Ranking", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (transactionTab) {
            0 -> {
                // Wallet actions
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Deposit Cash to Bank Safe", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = depositInput,
                                onValueChange = { depositInput = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                label = { Text("Amount", color = SoftGrayText) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(onClick = {
                                viewModel.depositAmount(depositInput.toLongOrNull() ?: 0L)
                                depositInput = ""
                            }, colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)) {
                                Text("Deposit", color = DeepSlateBg)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Withdraw Cash from Bank Safe", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = withdrawInput,
                                onValueChange = { withdrawInput = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                label = { Text("Amount", color = SoftGrayText) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = CyanInfo)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(onClick = {
                                viewModel.withdrawAmount(withdrawInput.toLongOrNull() ?: 0L)
                                withdrawInput = ""
                            }, colors = ButtonDefaults.buttonColors(containerColor = CyanInfo)) {
                                Text("Withdraw", color = DeepSlateBg)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Secure Peer Transfer to User", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        OutlinedTextField(
                            value = transferRecipient,
                            onValueChange = { transferRecipient = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Recipient Name (e.g., Aditya_Grand)", color = SoftGrayText) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = transferInput,
                                onValueChange = { transferInput = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                label = { Text("Transfer Amount", color = SoftGrayText) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(onClick = {
                                viewModel.transferToPeer(transferInput.toLongOrNull() ?: 0L, transferRecipient)
                                transferInput = ""
                                transferRecipient = ""
                            }, colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)) {
                                Text("Transfer", color = DeepSlateBg)
                            }
                        }
                    }
                }
            }

            1 -> {
                // Fixed Deposits Interest locker list
                Column(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Lock Savings Interest Portfolio", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Secure your capital inside growing vaults to lock multipliers.", color = SoftGrayText, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = lockAmountInput,
                                onValueChange = { lockAmountInput = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Investment Deposit Amount", color = SoftGrayText) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(" Latching Multiplier Plan Duration Options:", color = SoftGrayText, fontSize = 11.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val plans = listOf(7 to 5, 30 to 12, 90 to 45) // duration to interest %
                                plans.forEach { plan ->
                                    val isSelected = lockPlanDays == plan.first
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) GoldAccent else ElevatedSlate)
                                            .clickable { lockPlanDays = plan.first }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "${plan.first} Days\n+${plan.second}% ROI",
                                            color = if (isSelected) DeepSlateBg else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    val amt = lockAmountInput.toLongOrNull() ?: 0L
                                    val interest = if (lockPlanDays == 7) 5 else if (lockPlanDays == 30) 12 else 45
                                    viewModel.createSavingsFD(amt, lockPlanDays, interest)
                                    lockAmountInput = ""
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                            ) {
                                Text("Authorize Fixed Lock Savings", color = DeepSlateBg, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Active Savings Depots", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    if (fixedDeposits.isEmpty()) {
                        Text("No active locks currently yield interest.", color = MutedText, fontSize = 11.sp)
                    } else {
                        fixedDeposits.forEach { fd ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Locked Base: ${formatCurrency(fd.amount)}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Plan: ${fd.durationDays} Days / ROI +${fd.interestPercent}%", color = SoftGrayText, fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { viewModel.redeemMaturedFD(fd) },
                                        colors = ButtonDefaults.buttonColors(containerColor = GreenVerify)
                                    ) {
                                        Text("Claim Yield", color = DeepSlateBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Richest Leaderboards Players
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("💰 GRAND RP RICHLIST INDEX", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    richList.take(10).forEachIndexed { index, user ->
                        val medal = when (index) {
                            0 -> "🥇"
                            1 -> "🥈"
                            2 -> "🥉"
                            else -> "#${index + 1}"
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(medal, fontSize = 14.sp, modifier = Modifier.width(36.dp), fontWeight = FontWeight.Bold)
                                    Column {
                                        Text(user.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(user.role, color = MutedText, fontSize = 11.sp)
                                    }
                                }
                                Text(
                                    formatCurrency(user.bankBalance + user.walletBalance),
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// SECTION 5: COMMUNITY PROFILES & FAMILY NETWORK
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfilesFamilyView(
    viewModel: MarketViewModel,
    profile: UserProfile?,
    families: List<Family>,
    vouches: List<UserVouch>
) {
    var profileTab by remember { mutableStateOf(0) } // 0: Profile passport & Vouches, 1: Families Syndicate
    var proposeNameInput by remember { mutableStateOf("") }
    var newFamilyNameInput by remember { mutableStateOf("") }

    var feedbackVouchText by remember { mutableStateOf("") }
    var feedbackVouchRating by remember { mutableStateOf(5) }

    // Discord configuration states
    var showDiscordOAuthDialog by remember { mutableStateOf(false) }
    var showRolesSyncDialog by remember { mutableStateOf(false) }
    var selectedMockRoles by remember { mutableStateOf(setOf<String>()) }
    
    // Google & Email simulators
    var showGoogleConnectDialog by remember { mutableStateOf(false) }
    var isGoogleConnected by remember { mutableStateOf(false) }
    var googleConnectedEmail by remember { mutableStateOf("adityaghatule30@gmail.com") }
    
    var showEmailConnectDialog by remember { mutableStateOf(false) }
    var isEmailConnected by remember { mutableStateOf(false) }
    var emailInputText by remember { mutableStateOf("") }
    var passwordInputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // SubTabs Selection
        TabRow(
            selectedTabIndex = profileTab,
            containerColor = CardSlateBg,
            contentColor = GoldAccent
        ) {
            Tab(selected = profileTab == 0, onClick = { profileTab = 0 }) {
                Text("Reputation Passport", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = profileTab == 1, onClick = { profileTab = 1 }) {
                Text("Families syndicate", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = profileTab == 2, onClick = { profileTab = 2 }) {
                Text("⚙️ Connected Accounts", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (profileTab == 0) {
            // Profile display passport Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, GoldAccent), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSlateBg)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    profile?.username ?: "NIKA_BOSS_RP",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (profile?.isVerified == true) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Verified",
                                        tint = GreenVerify,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                UserScamBadge(profile?.username ?: "NIKA_BOSS_RP", viewModel)
                            }
                            Text(profile?.title ?: "Novice Trader", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            
                            // Discord Connection Indicators
                            if (profile?.discordUsername != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF5865F2).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                            .border(0.5.dp, Color(0xFF5865F2), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("⚫ Discord Connected", color = Color(0xFF9EA6FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    if (profile.role == "Verified Trader" || profile.isVerified) {
                                        Box(
                                            modifier = Modifier
                                                .background(GreenVerify.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                .border(0.5.dp, GreenVerify, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("🛡️ Verified Trader", color = GreenVerify, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    if (profile.role == "VIP" || profile.hasVip) {
                                        Box(
                                            modifier = Modifier
                                                .background(CoinGold.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                .border(0.5.dp, CoinGold, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("💎 VIP Status", color = CoinGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Reputation circular score
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(ElevatedSlate, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${profile?.reputation ?: 100}", color = GreenVerify, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Rep", color = MutedText, fontSize = 8.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = ElevatedSlate)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoLabelValue("Role Class", profile?.role ?: "Normal User")
                        InfoLabelValue("Family Association", if (profile?.familyName.isNullOrBlank()) "None" else profile?.familyName!!)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoLabelValue("Trade Deals Complete", "${profile?.completedDeals ?: 0} Successful")
                        InfoLabelValue("Registered Vouches", "${profile?.vouchesCount ?: 0} Players Approved")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = ElevatedSlate)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Couple marriage card
                    if (profile?.partnerName.isNullOrBlank()) {
                        Text("Register Marriage Couple Profile", color = SoftGrayText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = proposeNameInput,
                                onValueChange = { proposeNameInput = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Partner character name...", color = MutedText, fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(onClick = {
                                viewModel.createMarriage(proposeNameInput)
                                proposeNameInput = ""
                            }, colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)) {
                                Text("Get Married", color = DeepSlateBg, fontSize = 11.sp)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(listOf(ElevatedSlate, CardSlateBg)),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("❤️", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Married to: ${profile?.partnerName}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Official Grand RP Couple Profile active.", color = SoftGrayText, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vouch writing feedback form
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSlateBg)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Add Vouch Recommendation", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Help other players verify trustworthiness under our security audit criteria.", color = SoftGrayText, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = feedbackVouchText,
                        onValueChange = { feedbackVouchText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        placeholder = { Text("Write feedback comment...", color = MutedText, fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Rating Stars: ", color = SoftGrayText, fontSize = 11.sp)
                            val ratingOpts = listOf(1, 2, 3, 4, 5)
                            ratingOpts.forEach { opt ->
                                val active = feedbackVouchRating >= opt
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "$opt",
                                    tint = if (active) GoldAccent else Color.Gray,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { feedbackVouchRating = opt }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (feedbackVouchText.isNotBlank()) {
                                    viewModel.addVouch("me", feedbackVouchText, feedbackVouchRating)
                                    feedbackVouchText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                        ) {
                            Text("Submit Vouch", color = DeepSlateBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Vouch feedback logs", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))

            vouches.forEach { vouch ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("By: ${vouch.authorName}", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row {
                                repeat(vouch.rating) {
                                    Icon(Icons.Default.Star, contentDescription = "S", tint = GoldAccent, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(vouch.comment, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        } else if (profileTab == 1) {
            // Family subtabs list
            Column(modifier = Modifier.fillMaxWidth()) {
                // Discord server-like profile banner for Nice Guy Alliance
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, GoldAccent),
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                ) {
                    Column {
                        // Banner background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(Brush.horizontalGradient(listOf(Color(0xFF4F7CFF), Color(0xFF1B1F28)))),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(2.dp, GoldAccent, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("N", color = Color(0xFF4F7CFF), fontWeight = FontWeight.Bold, fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Nice Guy Alliance 👑",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        "Official Elite Syndicate Network",
                                        color = SoftGrayText,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // Alliance metrics grid
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ElevatedSlate)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("MEMBERS", color = MutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("85", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TOTAL WEALTH", color = MutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("$15,000,000,000", color = GreenVerify, fontSize = 14.sp, fontWeight = FontWeight.Black)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("BUSINESSES", color = MutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("12", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Syndicate Creed:",
                                color = SoftGrayText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                "Our creed is ultimate compliance, complete synergy, zero trace transactions.",
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                if (profile?.familyName.isNullOrBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Form Family Syndicate Alliance", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Create a shared vault, recruit followers, and rise on leaderboard. Costs 500 KC.", color = SoftGrayText, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = newFamilyNameInput,
                                onValueChange = { newFamilyNameInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Alliance Name (e.g. Nika Syndicate)", color = MutedText, fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    viewModel.createFamily(newFamilyNameInput, 500)
                                    newFamilyNameInput = ""
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                            ) {
                                Text("Found Family Syndicate Alliance (500 KC)", color = DeepSlateBg, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardSlateBg, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text("🛡️ ACTIVE ALLIANCE: ${profile?.familyName}", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("You are an official elite member of the alliance network.", color = SoftGrayText, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Syndicate Vault Leaderboards", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                families.forEach { fam ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(fam.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Leader: ${fam.leaderName} • Members: ${fam.memberCount}", color = SoftGrayText, fontSize = 11.sp)
                                    Text("Vault: ${formatCurrency(fam.vaultBalance)}", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.fundFamilyBank(fam, 50000000L) }, // Donate 50M
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanInfo)
                                ) {
                                    Text("+50M Donate", color = DeepSlateBg, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // SECTION 3: ⚙️ ACCOUNT SETTINGS & CONNECTED ACCOUNTS
            Column(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("⚙️ DISCORD & IDENTITY INTEGRATION HUB", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Establish a single secure identity between the Discord Server, Discord Bot, and KAT_MARKET_NIKA App.", color = SoftGrayText, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Discord Integration Segment
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, if (profile?.discordUsername != null) Color(0xFF5865F2) else Color.Transparent),
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⚫", color = Color(0xFF5865F2), fontSize = 18.sp, fontWeight = FontWeight.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Discord Platform Pairing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        if (profile?.discordUsername != null) "Status: INTEGRATED" else "Status: UNPAIRED",
                                        color = if (profile?.discordUsername != null) GreenVerify else SoftGrayText,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (profile?.discordUsername == null) {
                                Button(
                                    onClick = { showDiscordOAuthDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5865F2))
                                ) {
                                    Text("Connect Discord", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.disconnectDiscordAccount() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
                                ) {
                                    Text("Disconnect Link", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }

                        if (profile?.discordUsername != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = ElevatedSlate)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Display details of the mapped account
                            Text("CONNECTED DISCORD ACCOUNT INFO", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ElevatedSlate, RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color(0xFF5865F2).copy(alpha = 0.3f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            (profile.discordUsername.orEmpty().take(1).ifBlank { "D" }).uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(profile.discordUsername.orEmpty(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("ID: ${profile.discordId.orEmpty()}", color = SoftGrayText, fontSize = 10.sp)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Since: ${profile.discordJoinDate.orEmpty()}", color = SoftGrayText, fontSize = 10.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (profile.discordServerMember) GreenVerify.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            if (profile.discordServerMember) "🟢 Server Member" else "🔴 Exited Server",
                                            color = if (profile.discordServerMember) GreenVerify else Color.Red,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = ElevatedSlate)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Perks section
                            Text("ACTIVE BENEFITS & AUTOMATIC CHECKS", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            val benefits = listOf(
                                "🚀 Faster Login Credentials" to "Instantly bypass manual inputs.",
                                "🔄 Discord Profile Sync" to "Syncs profile banner and nickname settings.",
                                "🛡️ Server Role privilege sync" to "Directly maps server roles into client badges.",
                                "📣 DM Notifications Link" to "Receive market bids, trade escrows, and disputes directly.",
                                "⭐ Reputation Score Sync" to "Carry server vouches directly into the application."
                            )

                            benefits.forEach { (title, desc) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("✅", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(desc, color = SoftGrayText, fontSize = 10.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = ElevatedSlate)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Action: Toggle DM Alerts
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Real-Time Direct Mail Alerts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Forward New Offers, Messages, Trade requests & approvals directly via bot", color = SoftGrayText, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = profile.discordNotificationsEnabled,
                                    onCheckedChange = { viewModel.updateDiscordNotifications(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF5865F2))
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = ElevatedSlate)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Action: Role sync
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text("Database Automatic Role Sync System", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Map your Discord Server ranks into KAT RP client badges dynamically.", color = SoftGrayText, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(ElevatedSlate, RoundedCornerShape(8.dp))
                                            .padding(8.dp)
                                    ) {
                                        Column {
                                            Text("Active Privilege Tier:", color = SoftGrayText, fontSize = 9.sp)
                                            Text(if (profile.discordRoleSynced) "👑 Synced Role: ${profile.role}" else "⚠️ Not Synchronized", color = GoldAccent, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { showRolesSyncDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                                    ) {
                                        Text("Sync Ranks", color = DeepSlateBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = ElevatedSlate)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Action Simulator leaves server
                            Column {
                                Text("Discord Membership simulator options", color = MutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.toggleDiscordServerMembership(!profile.discordServerMember) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (profile.discordServerMember) Color.DarkGray else Color(0xFF5865F2)
                                        )
                                    ) {
                                        Text(
                                            if (profile.discordServerMember) "Simulate Left Server" else "Simulate Joined Server",
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            // Connection Guide list when not connected
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Pairing benefits include:", color = SoftGrayText, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            listOf(
                                "🛡️ Verifications sync badge" to "Instantly maps Verified Trader, VIP, Moderator, Admin levels.",
                                "📢 Alerts system push" to "Send live notifications for: offers, escrows, and message threads.",
                                "💎 Faster logins check" to "Establish a single identity bypass credentials checking."
                            ).forEach { (perk, desc) ->
                                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text("🔥", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("$perk — $desc", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Alternatives authentication gateway (Google & Email)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("ALTERNATIVE IDENTITY GATEWAYS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Connect optional backup providers to facilitate credentials authentication.", color = SoftGrayText, fontSize = 11.sp)
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        // Google credential connection status card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ElevatedSlate, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🔵", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Google Authentication Sync", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(
                                        if (isGoogleConnected) "Adjoined: $googleConnectedEmail" else "Independent Profile",
                                        color = if (isGoogleConnected) GreenVerify else SoftGrayText,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    if (isGoogleConnected) {
                                        isGoogleConnected = false
                                    } else {
                                        showGoogleConnectDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isGoogleConnected) Color.DarkGray else Color(0xFF4285F4)
                                )
                            ) {
                                Text(if (isGoogleConnected) "Disconnect" else "Adjoin Gmail", color = Color.White, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Email credentials sign in
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ElevatedSlate, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📧", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Email/Password Credentials", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(
                                        if (isEmailConnected) "Attached: $emailInputText" else "Independent Profile",
                                        color = if (isEmailConnected) GreenVerify else SoftGrayText,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    if (isEmailConnected) {
                                        isEmailConnected = false
                                        emailInputText = ""
                                        passwordInputText = ""
                                    } else {
                                        showEmailConnectDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isEmailConnected) Color.DarkGray else GoldAccent
                                )
                            ) {
                                Text(
                                    if (isEmailConnected) "Disconnect" else "Bind Email",
                                    color = if (isEmailConnected) Color.White else DeepSlateBg,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                // Beautiful interactive community list: other players whose Discord is connected
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("⚫ SEEDED DISCORD SERVER COMMUNITY", color = Color(0xFF5865F2), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Displaying active community members who initialized Discord pairing:", color = SoftGrayText, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        val seededConnectedPlayers = listOf(
                            Triple("Aditya_Grand", "Aditya#1337", "Joined Jan 15, 2022"),
                            Triple("Roman_Vercetti", "Vercetti_Boss", "Joined Mar 10, 2023"),
                            Triple("Alisa_Petrova", "Alisa_Petrova_Nika", "Joined Jul 14, 2021")
                        )

                        seededConnectedPlayers.forEach { (appUser, discordUser, dateStr) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .background(ElevatedSlate, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(Color(0xFF5865F2).copy(alpha = 0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(discordUser.take(1).uppercase(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(appUser, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("(${discordUser})", color = SoftGrayText, fontSize = 10.sp)
                                        }
                                        Text(dateStr, color = MutedText, fontSize = 9.sp)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(GreenVerify.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .border(0.5.dp, GreenVerify.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text("🟢 Shared Server Status: Active", color = GreenVerify, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGS SECTION ---
    if (showDiscordOAuthDialog) {
        AlertDialog(
            onDismissRequest = { showDiscordOAuthDialog = false },
            containerColor = Color(0xFF2F3136), // Discord dark dialog colour
            confirmButton = {},
            dismissButton = {},
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚫ Discord OAuth Login Authorization", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "An external application KAT_MARKET_NIKA requests access to connect your Discord credentials identity.",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    var oauthUsernameInput by remember { mutableStateOf("Aditya") }
                    var oauthIdInput by remember { mutableStateOf("468930219602495499") }

                    OutlinedTextField(
                        value = oauthUsernameInput,
                        onValueChange = { oauthUsernameInput = it },
                        label = { Text("Discord Username", color = Color.Gray, fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF5865F2)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = oauthIdInput,
                        onValueChange = { oauthIdInput = it },
                        label = { Text("Discord ID Snowflake code", color = Color.Gray, fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF5865F2)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF36393F))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("OFFICIAL SCOPES TO DELEGATE:", color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("🛡️ Read server membership status.", color = Color.White, fontSize = 10.sp)
                            Text("🔄 Access Username and profile sync.", color = Color.White, fontSize = 10.sp)
                            Text("📣 Direct message notify alert forwarding.", color = Color.White, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showDiscordOAuthDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = Color.White, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.connectDiscordAccount(
                                    username = oauthUsernameInput,
                                    discordId = oauthIdInput,
                                    avatarUrl = null,
                                    serverMember = true
                                )
                                showDiscordOAuthDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5865F2)),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("Authorize & Connect", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        )
    }

    if (showRolesSyncDialog) {
        AlertDialog(
            onDismissRequest = { showRolesSyncDialog = false },
            containerColor = CardSlateBg,
            title = {
                Text("Simulate Guild Role Synchronizer", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Toggle roles you hold in our Discord server to map them instantly to app status privileges:",
                        color = SoftGrayText,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val rolesListStr = listOf("Verified Trader", "VIP", "Staff", "Administrator")
                    rolesListStr.forEach { role ->
                        val holding = selectedMockRoles.contains(role)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedMockRoles = if (holding) {
                                        selectedMockRoles - role
                                    } else {
                                        selectedMockRoles + role
                                    }
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = holding,
                                onCheckedChange = { checked ->
                                    selectedMockRoles = if (checked == true) {
                                        selectedMockRoles + role
                                    } else {
                                        selectedMockRoles - role
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = GoldAccent)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(role, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.syncDiscordRoles(selectedMockRoles.toList())
                        showRolesSyncDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("Trigger Sync Now", color = DeepSlateBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showRolesSyncDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate)
                ) {
                    Text("Cancel", color = Color.White, fontSize = 11.sp)
                }
            }
        )
    }

    if (showGoogleConnectDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleConnectDialog = false },
            containerColor = CardSlateBg,
            title = { Text("🔵 Google Authenticator Link", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Enter the Google Account Gmail to register authentication credentials linkage:", color = SoftGrayText, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = googleConnectedEmail,
                        onValueChange = { googleConnectedEmail = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isGoogleConnected = true
                        showGoogleConnectDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
                ) {
                    Text("Link Gmail", color = Color.White)
                }
            }
        )
    }

    if (showEmailConnectDialog) {
        AlertDialog(
            onDismissRequest = { showEmailConnectDialog = false },
            containerColor = CardSlateBg,
            title = { Text("📧 Bind Email/Password Profile", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Simulate attachment of standard email login credentials:", color = SoftGrayText, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = emailInputText,
                        onValueChange = { emailInputText = it },
                        placeholder = { Text("E-mail address...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = passwordInputText,
                        onValueChange = { passwordInputText = it },
                        placeholder = { Text("Enter secure password...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (emailInputText.isNotBlank()) {
                            isEmailConnected = true
                            showEmailConnectDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("Bind Credentials", color = DeepSlateBg)
                }
            }
        )
    }
}

@Composable
fun InfoLabelValue(label: String, value: String) {
    Column {
        Text(label, color = MutedText, fontSize = 10.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

// ----------------------------------------------------
// SECTION 6: REAL MONEY TRADE [RCD]
// ----------------------------------------------------
@Composable
fun RcdCenterView(viewModel: MarketViewModel, deals: List<RcdDeal>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("🛡️ KAT REAL CASH DEAL CENTER (RCD)", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Players can safely trade high-value properties and vehicle codes matching real-money transaction slips. Every ticket requires a staff visual appraisal check to approve release.",
                    color = SoftGrayText,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("RCD Audited Escrow Tickets Ledger", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))

        if (deals.isEmpty()) {
            Text("No ongoing payment trade tickets are active. Initiate from any listing card!", color = MutedText, fontSize = 11.sp)
        } else {
            deals.forEach { deal ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(deal.assetTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Buyer: ${deal.buyerName} • Seller: ${deal.sellerName}", color = SoftGrayText, fontSize = 11.sp)
                                Text("Payment: ${deal.paymentMethod} • Cost: $${deal.realCurrencyPrice}", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // Review status emblem
                            val statusBg = when (deal.staffReviewStatus) {
                                "PENDING" -> GoldAccent
                                "APPROVED" -> GreenVerify
                                "REJECTED" -> RedUrgent
                                else -> Color.Magenta
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(statusBg)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    deal.staffReviewStatus,
                                    fontSize = 10.sp,
                                    color = DeepSlateBg,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Upload proof simulations
                        if (deal.staffReviewStatus == "PENDING" && deal.buyerId == "me") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = ElevatedSlate)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Uploaded Slip: ${deal.proofImgName}", color = CyanInfo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row {
                                    TextButton(onClick = { viewModel.processStaffRcdDeal(deal, "APPROVED") }) {
                                        Text("Accept Confirm", color = GreenVerify, fontSize = 11.sp)
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

// ----------------------------------------------------
// SECTION 7: ADVERTISING Hub
// ----------------------------------------------------
@Composable
fun AdvertisingView(viewModel: MarketViewModel, ads: List<Advertisement>) {
    var adTitleInput by remember { mutableStateOf("") }
    var adDescInput by remember { mutableStateOf("") }
    var adCategoryInput by remember { mutableStateOf("Business") } // Business, Family, etc.
    var adPromoType by remember { mutableStateOf("STANDARD") } // STANDARD, FEATURED, PREMIUM

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("📢 PRE-MODERATED ADVERTISING HUB", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("Finance flyers and recruitments in public spaces to pull traffic.", color = SoftGrayText, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Create campaign flyer
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Launch Custom Ad Campaign", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = adTitleInput,
                    onValueChange = { adTitleInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Banner Headline Title", color = SoftGrayText) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = adDescInput,
                    onValueChange = { adDescInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Promotional Pitch Description Text", color = SoftGrayText) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                )

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val adPlans = listOf("STANDARD" to 20, "FEATURED" to 50, "PREMIUM" to 150)
                    adPlans.forEach { plan ->
                        val active = adPromoType == plan.first
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) GoldAccent else ElevatedSlate)
                                .clickable { adPromoType = plan.first }
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${plan.first}\n${plan.second} KC",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) DeepSlateBg else Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val cost = if (adPromoType == "STANDARD") 20 else if (adPromoType == "FEATURED") 50 else 150
                        viewModel.submitAd(adTitleInput, adDescInput, adCategoryInput, adPromoType, cost)
                        adTitleInput = ""
                        adDescInput = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("Fund Campaign Pitch Flyer", color = DeepSlateBg, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Active Promotions Roll", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))

        ads.forEach { ad ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlateBg)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(ElevatedSlate, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(ad.promotionType, fontSize = 9.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(ad.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Text("Views ${ad.views}", color = MutedText, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(ad.description, color = SoftGrayText, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Published by: ${ad.advertiserName}", color = MutedText, fontSize = 10.sp)
                }
            }
        }
    }
}

// ----------------------------------------------------
// SECTION 8: CRATES & LOOT INVENTORY
// ----------------------------------------------------
data class CoinPack(
    val id: String,
    val name: String,
    val icon: String,
    val coins: Int,
    val priceInr: Double,
    val description: String
)

val coinPacksList = listOf(
    CoinPack("starter", "Starter Pack", "🪙", 100, 10.0, "Great to test the waters"),
    CoinPack("basic", "Basic Pack", "✨", 500, 50.0, "Perfect for single listing boosts"),
    CoinPack("standard", "Standard Pack", "💎", 1000, 100.0, "Extended reach & priority boost"),
    CoinPack("premium", "Premium Pack", "⭐", 5000, 500.0, "Premium advertising campaigns"),
    CoinPack("ultimate", "Ultimate Pack", "👑", 10000, 1000.0, "VIP membership & elite priority")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpiCoinPurchaseDialog(
    packageId: String,
    packageName: String,
    coinAmount: Int,
    priceInr: Double,
    viewModel: MarketViewModel,
    onDismiss: () -> Unit
) {
    var upiTxId by remember { mutableStateOf("") }
    var mockScreenshotSelected by remember { mutableStateOf("Screenshot_UPI_${packageName.replace(" ", "_")}_verified.png") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .wrapContentHeight()
                .border(1.dp, ElevatedSlate, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DeepSlateBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🪙 Checkout Package", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Text("✕", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GoldAccent, RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(packageName.uppercase(), color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Credits: +$coinAmount KAT Coins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Amount Due: $${priceInr.toInt()} USD", color = GreenVerify, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ElevatedSlate)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("OFFICIAL UPI TARGET ID", color = SoftGrayText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("adityaghatule30@okaxis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("COPY", color = GoldAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable {
                                // simulated copy
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = upiTxId,
                    onValueChange = { upiTxId = it },
                    label = { Text("UPI Info/Transaction Ref ID", fontSize = 11.sp) },
                    placeholder = { Text("Enter 12-digit transaction ID", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("upi_purchase_txid_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = SoftGrayText,
                        focusedLabelColor = GoldAccent,
                        unfocusedLabelColor = SoftGrayText,
                        focusedContainerColor = CardSlateBg,
                        unfocusedContainerColor = CardSlateBg
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Attach Simulated Payment Screenshot (Auto)", color = SoftGrayText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardSlateBg, RoundedCornerShape(8.dp))
                            .border(1.dp, ElevatedSlate, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Text("🖼️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(mockScreenshotSelected, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("Generated payment receipt", color = SoftGrayText, fontSize = 8.sp)
                            }
                        }
                        
                        Button(
                            onClick = {
                                mockScreenshotSelected = "Screenshot_UPI_${packageName.replace(" ", "_")}_REF_${System.currentTimeMillis().toString().takeLast(6)}.png"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("RE-GEN", color = GoldAccent, fontSize = 8.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (upiTxId.isNotBlank()) {
                            viewModel.createCoinPurchaseRequest(
                                packageId = packageId,
                                packageName = packageName,
                                coins = coinAmount,
                                priceInr = priceInr,
                                upiTxId = upiTxId,
                                screenshotPath = mockScreenshotSelected
                            )
                            onDismiss()
                        } else {
                            viewModel.alertMessage.value = "UPI transaction reference ID cannot be blank!"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("upi_purchase_submit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("SUBMIT COIN DEPOSIT PROOF", color = DeepSlateBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("⚠️ Coins are manually verified and deposited only after support staff review. Typically instant.", color = SoftGrayText, fontSize = 8.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun InventoryView(viewModel: MarketViewModel, myInventory: List<InventoryItem>) {
    var selectedPackForDeposit by remember { mutableStateOf<CoinPack?>(null) }
    val purchases by viewModel.allCoinPurchases.collectAsState()
    val meProfile by viewModel.userProfile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // KAT COINS SHOP - UPI DEPOSITS
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🪙 KAT COINS PURCHASE TERMINAL", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("1 Coin = $0.10 USD", color = SoftGrayText, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
                Text("Select a coin bundle, pay the exact amount to the official UPI ID, then upload your transaction details.", color = SoftGrayText, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    coinPacksList.forEach { pack ->
                        Card(
                            modifier = Modifier
                                .width(135.dp)
                                .clickable { selectedPackForDeposit = pack }.testTag("coin_pack_card_${pack.id}"),
                            colors = CardDefaults.cardColors(containerColor = ElevatedSlate)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(pack.icon, fontSize = 26.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(pack.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${pack.coins} Coins", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(GreenVerify.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("$${pack.priceInr.toInt()}", color = GreenVerify, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Crate store
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("🎁 ROYAL CRATE TRADING HUB", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("Trade coin balances for random drop boxes containing massive Grand cash drops, VIP status extensions, or exclusive profile badges.", color = SoftGrayText, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CrateStoreCard("Silver Box", 150, "CRATE_SILVER", viewModel)
                    CrateStoreCard("Gold Box", 300, "CRATE_GOLD", viewModel)
                    CrateStoreCard("Diamond Box", 500, "CRATE_DIAMOND", viewModel)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Player Inventory Boxes Ledger", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))

        if (myInventory.isEmpty()) {
            Text("Owner inventory empty. Buy a drop container above to play!", color = MutedText, fontSize = 11.sp)
        } else {
            myInventory.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Category: ${item.type} • Stock: x${item.quantity}", color = SoftGrayText, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { viewModel.openCrate(item) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                        ) {
                            Text("Open Spin", color = DeepSlateBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // COIN PURCHASE LEDGER LIST
        Text("Your Coin Purchase Requests & Reviews Ledger", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))

        if (purchases.isEmpty()) {
            Text("No coins deposit requests recorded. Tap any package bundle above to buy via official UPI.", color = MutedText, fontSize = 11.sp)
        } else {
            purchases.forEach { req ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(req.packageName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Ref ID: ${req.transactionId} • Amount Paid: $${req.amountInr.toInt()}", color = SoftGrayText, fontSize = 11.sp)
                            }
                            // Render Status Badge
                            val badgeColor = when (req.status) {
                                "PENDING" -> Color(0xFFFBBF24) // Yellow
                                "APPROVED" -> Color(0xFF10B981) // Green
                                "REJECTED" -> Color(0xFFEF4444) // Red
                                "NEED_PROOF" -> Color(0xFFF97316) // Orange
                                else -> SoftGrayText
                            }
                            Box(
                                modifier = Modifier
                                    .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(req.status, color = badgeColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (req.reviewerFeedback.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Support Feedback: ${req.reviewerFeedback}", color = if (req.status == "NEED_PROOF") Color(0xFFF97316) else Color(0xFFE5E7EB), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }

                        if (req.status == "NEED_PROOF") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val matchPack = coinPacksList.find { it.id == req.packageId } ?: coinPacksList[0]
                                    selectedPackForDeposit = matchPack
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                modifier = Modifier.height(28.dp).testTag("resubmit_proof_button_${req.id}")
                            ) {
                                Text("RE-SUBMIT PROOF DATA", color = DeepSlateBg, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedPackForDeposit != null) {
        UpiCoinPurchaseDialog(
            packageId = selectedPackForDeposit!!.id,
            packageName = selectedPackForDeposit!!.name,
            coinAmount = selectedPackForDeposit!!.coins,
            priceInr = selectedPackForDeposit!!.priceInr,
            viewModel = viewModel,
            onDismiss = { selectedPackForDeposit = null }
        )
    }
}

@Composable
fun CrateStoreCard(name: String, price: Int, type: String, viewModel: MarketViewModel) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable { viewModel.buyCrate(name, type, price) },
        colors = CardDefaults.cardColors(containerColor = ElevatedSlate)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📦", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(name, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("$price KC", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ----------------------------------------------------
// SECTION 9: STAFF PANEL & ADMINISTRATION
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdministrationView(
    viewModel: MarketViewModel,
    auditLogs: List<AuditLog>,
    rcdDeals: List<RcdDeal>
) {
    var activeAdminTab by remember { mutableStateOf("ESCROWS") } // "ESCROWS", "COINS_QUEUE", "AUDITS"
    val coinPurchases by viewModel.allCoinPurchases.collectAsState()
    val netWorthRequests by viewModel.allNetWorthVerifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("🛠️ MASTER ADMINISTRATION OPERATING CONSOLE", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("Manage permissions, verify credentials, bypass database entities, and track audit ledgers.", color = SoftGrayText, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Direct bypass controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Instant System Override Actions", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.verifySelf(true) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenVerify)
                    ) {
                        Text("Grant ID Verified", color = DeepSlateBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.awardAdminCoins(5000) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text("Mint +5k Coins", color = DeepSlateBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.triggerFactoryCleanup() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = RedUrgent)
                ) {
                    Text("Owner Factory Purge Reset", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Three-tab Selector Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardSlateBg, RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                "ESCROWS" to "Escrows",
                "COINS_QUEUE" to "🪙 Coins Queue",
                "NET_WORTH" to "🛡 Wealth Queue",
                "AUDITS" to "Audits"
            )
            tabs.forEach { (tabId, label) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (activeAdminTab == tabId) ElevatedSlate else Color.Transparent)
                        .clickable { activeAdminTab = tabId }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        color = if (activeAdminTab == tabId) GoldAccent else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Active Tab Display
        when (activeAdminTab) {
            "AUDITS" -> {
                Text("System Security Audit Records", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                if (auditLogs.isEmpty()) {
                    Text("No administrative audit logs captured.", color = MutedText, fontSize = 11.sp)
                } else {
                    auditLogs.take(40).forEach { log ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(log.action, color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(log.actorName, color = SoftGrayText, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(log.details, color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
            "ESCROWS" -> {
                Text("Escrow Account Verification Slips", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                val pendingDeals = rcdDeals.filter { it.staffReviewStatus == "PENDING" }
                if (pendingDeals.isEmpty()) {
                    Text("Pristine! No escrows require staff clearance.", color = MutedText, fontSize = 11.sp)
                } else {
                    pendingDeals.forEach { deal ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(deal.assetTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Buyer: ${deal.buyerName} • Real Cost: $${deal.realCurrencyPrice}", color = SoftGrayText, fontSize = 11.sp)
                                Text("Uploaded Proof: ${deal.proofImgName}", color = CyanInfo, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.processStaffRcdDeal(deal, "APPROVED") },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = GreenVerify)
                                    ) {
                                        Text("Approve Deal", color = DeepSlateBg, fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { viewModel.processStaffRcdDeal(deal, "REJECTED") },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = RedUrgent)
                                    ) {
                                        Text("Reject Slip", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "COINS_QUEUE" -> {
                Text("Coin Purchases Moderation Queue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                val activeCoins = coinPurchases.filter { it.status == "PENDING" || it.status == "NEED_PROOF" }
                if (activeCoins.isEmpty()) {
                    Text("No pending coin purchases require approval.", color = MutedText, fontSize = 11.sp)
                } else {
                    activeCoins.forEach { req ->
                        var feedbackText by remember { mutableStateOf("") }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(req.packageName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("User: ${req.username} • Coins: +${req.coinAmount}", color = SoftGrayText, fontSize = 11.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (req.status == "PENDING") Color(0xFFFBBF24).copy(alpha = 0.15f)
                                                else Color(0xFFF97316).copy(alpha = 0.15f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            req.status,
                                            color = if (req.status == "PENDING") Color(0xFFFBBF24) else Color(0xFFF97316),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Transaction Ref: ${req.transactionId}", color = Color.White, fontSize = 11.sp)
                                Text("Screenshot proof file: ${req.proofImagePath}", color = CyanInfo, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                
                                if (req.reviewerFeedback.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Feedback logs: ${req.reviewerFeedback}", color = Color(0xFFFCA5A5), fontSize = 10.sp)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = feedbackText,
                                    onValueChange = { feedbackText = it },
                                    placeholder = { Text("Enter staff comment (e.g. rejection feedback or missing details description)...", fontSize = 10.sp) },
                                    modifier = Modifier.fillMaxWidth().testTag("admin_feedback_input_${req.id}"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = GoldAccent,
                                        unfocusedBorderColor = ElevatedSlate,
                                        focusedContainerColor = DeepSlateBg,
                                        unfocusedContainerColor = DeepSlateBg
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.processCoinPurchaseRequest(req, "APPROVED", feedbackText) },
                                        colors = ButtonDefaults.buttonColors(containerColor = GreenVerify),
                                        modifier = Modifier.weight(1f).height(32.dp).testTag("admin_approve_coins_button_${req.id}")
                                    ) {
                                        Text("✅ APPROVE", color = DeepSlateBg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { 
                                            if (feedbackText.isBlank()) {
                                                viewModel.alertMessage.value = "Specify rejection details in comments first!"
                                            } else {
                                                viewModel.processCoinPurchaseRequest(req, "REJECTED", feedbackText)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = RedUrgent),
                                        modifier = Modifier.weight(1.2f).height(32.dp).testTag("admin_reject_coins_button_${req.id}")
                                    ) {
                                        Text("❌ REJECT", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { 
                                            if (feedbackText.isBlank()) {
                                                viewModel.alertMessage.value = "Describe what proof is required in comments first!"
                                            } else {
                                                viewModel.processCoinPurchaseRequest(req, "NEED_PROOF", feedbackText)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                                        modifier = Modifier.weight(1.2f).height(32.dp).testTag("admin_request_coins_proof_button_${req.id}")
                                    ) {
                                        Text("📨 NEED PROOF", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "NET_WORTH" -> {
                Text("🛡 Wealth Proofs Verification Queue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                val pendingRequests = netWorthRequests.filter { it.status == "PENDING" }
                if (pendingRequests.isEmpty()) {
                    Text("Pristine! No net worth verifications pending review.", color = MutedText, fontSize = 11.sp)
                } else {
                    pendingRequests.forEach { req ->
                        var rejectReasonText by remember { mutableStateOf("") }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                            border = BorderStroke(1.dp, ElevatedSlate)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Username: ${req.username}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Status: ${req.status}", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                                
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("🏦 Submited Bank Balance: ${formatCurrency(req.bankBalance)}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Proof File details: ${req.bankScreenshotPath}", color = CyanInfo, fontSize = 10.sp)

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("🚗 Vehicles Submited Checklist:", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                if (req.vehiclesJson.isNotBlank() && req.vehiclesJson != "[]") {
                                    Text(req.vehiclesJson, color = SoftGrayText, fontSize = 10.sp)
                                } else {
                                    Text("No Vehicles listed inside proofs.", color = SoftGrayText, fontSize = 10.sp)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("🏠 Properties Submited Checklist:", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                if (req.propertiesJson.isNotBlank() && req.propertiesJson != "[]") {
                                    Text(req.propertiesJson, color = SoftGrayText, fontSize = 10.sp)
                                } else {
                                    Text("No Properties listed inside proofs.", color = SoftGrayText, fontSize = 10.sp)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("🏢 Businesses Submited Checklist:", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                if (req.businessesJson.isNotBlank() && req.businessesJson != "[]") {
                                    Text(req.businessesJson, color = SoftGrayText, fontSize = 10.sp)
                                } else {
                                    Text("No Businesses listed inside proofs.", color = SoftGrayText, fontSize = 10.sp)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = rejectReasonText,
                                    onValueChange = { rejectReasonText = it },
                                    placeholder = { Text("Reason for Rejection (Missing Evidence, Invalid Screenshot, Outdated Screenshot, Insufficient Proof, etc.)...", fontSize = 11.sp) },
                                    modifier = Modifier.fillMaxWidth().testTag("net_worth_reject_reason_input_${req.id}"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = GoldAccent,
                                        unfocusedBorderColor = ElevatedSlate,
                                        focusedContainerColor = DeepSlateBg,
                                        unfocusedContainerColor = DeepSlateBg
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.processNetWorthRequest(req, "APPROVED") },
                                        colors = ButtonDefaults.buttonColors(containerColor = GreenVerify),
                                        modifier = Modifier.weight(1f).testTag("net_worth_approve_button_${req.id}")
                                    ) {
                                        Text("APPROVE & SUM CERTIFY", color = DeepSlateBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { 
                                            if (rejectReasonText.isBlank()) {
                                                viewModel.alertMessage.value = "Specify rejection feedback comments first!"
                                            } else {
                                                viewModel.processNetWorthRequest(req, "REJECTED", rejectReasonText)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = RedUrgent),
                                        modifier = Modifier.weight(1.2f).testTag("net_worth_reject_button_${req.id}")
                                    ) {
                                        Text("REJECT WEALTH", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

// --------------------
@Composable
fun ListingDetailsDialog(
    listing: MarketListing,
    viewModel: MarketViewModel,
    onDismiss: () -> Unit
) {
    var showRcdBuyPanel by remember { mutableStateOf(false) }
    var rcdPaymentMethod by remember { mutableStateOf("UPI") }
    var rcdFiatAmount by remember { mutableStateOf("${listing.askingPrice / 1000000 * 5}") } // Simulated fiat conversion
    var rcdNotes by remember { mutableStateOf("") }

    // Swipe photo gallery + Video showcase
    var activeImageIndex by remember { mutableStateOf(0) }
    var showZoomedImage by remember { mutableStateOf<String?>(null) }
    var currentWatermarkState by remember { mutableStateOf(listing.watermarked) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Category header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(listing.category.uppercase(), color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Text("✕", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(listing.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text(listing.subType, color = SoftGrayText, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = ElevatedSlate)
                Spacer(modifier = Modifier.height(12.dp))

                // Swipe Media Gallery & Watermarking Section
                if (listing.images.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DeepSlateBg, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Text("📸 VERIFIED MEDIA MULTI-GALLERY", color = GoldAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        // Main image container
                        val currentImageName = listing.images[activeImageIndex]
                        SimulatedImageCanvas(
                            imageName = currentImageName,
                            category = listing.category,
                            watermarked = currentWatermarkState,
                            listingId = listing.id,
                            isZoomed = false
                        ) {
                            showZoomedImage = currentImageName
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Gallery navigation controls & indicators
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Image ${activeImageIndex + 1} of ${listing.images.size}",
                                color = SoftGrayText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        if (activeImageIndex > 0) activeImageIndex--
                                        else activeImageIndex = listing.images.size - 1
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("← Prev", color = Color.White, fontSize = 10.sp)
                                }
                                Button(
                                    onClick = {
                                        if (activeImageIndex < listing.images.size - 1) activeImageIndex++
                                        else activeImageIndex = 0
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Next →", color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        // Watermark configuration toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Secure Watermark Layer", color = SoftGrayText, fontSize = 10.sp)
                            Switch(
                                checked = currentWatermarkState,
                                onCheckedChange = {
                                    currentWatermarkState = it
                                    viewModel.updateListingWatermark(listing, it)
                                }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x33EA580C), RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, Color(0xFFF97316)), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text("⚠️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("LOW VISIBILITY HAZARD", color = Color(0xFFF97316), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "This listing doesn't feature verified proof screenshots. Buyers are advised extreme caution when trading unregistered units.",
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Optional video player mockups
                if (listing.videoUrl.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(1.dp, CyanInfo), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = DeepSlateBg)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📹", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("VIDEO PROOF SHOWCASE", color = CyanInfo, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color(0x3306B6D4), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("HD PLAYBACK", color = CyanInfo, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(listing.videoUrl, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Visual placeholder mock player HUD
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("▶️", fontSize = 24.sp)
                                    Text("Simulated player scrubbing pipeline...", color = Color.DarkGray, fontSize = 8.sp)
                                }
                                
                                // Progress bar slider HUD
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("0:24", color = Color.White, fontSize = 7.sp)
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(3.dp)
                                                .background(Color.DarkGray)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .fillMaxWidth(0.42f)
                                                    .background(CyanInfo)
                                            )
                                        }
                                        Text("0:59", color = Color.White, fontSize = 7.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Custom specs grid
                SpecRowLine("Asking Price", formatCurrency(listing.askingPrice))
                SpecRowLine("State Registry Cost", formatCurrency(listing.statePrice))
                SpecRowLine("Count Owners", "${listing.ownerCount} previous")
                if (listing.licensePlate.isNotBlank()) {
                    SpecRowLine("Plate Identifier", listing.licensePlate)
                }
                if (listing.location.isNotBlank()) {
                    SpecRowLine("Property Location", listing.location)
                }
                if (listing.profitDaily > 0) {
                    SpecRowLine("Assert Daily Profit", formatCurrency(listing.profitDaily))
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Notes & Details:", color = MutedText, fontSize = 11.sp)
                Text(listing.notes.ifBlank { "No additional specifications cataloged." }, color = Color.White, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = ElevatedSlate)
                Spacer(modifier = Modifier.height(12.dp))

                // Chat direct contact button
                if (listing.sellerId != "me") {
                    var showDirectChatPopup by remember { mutableStateOf(false) }
                    Button(
                        onClick = { showDirectChatPopup = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate)
                    ) {
                        Text("💬 Contact Seller / Open Live Negotiation Chat", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    if (showDirectChatPopup) {
                        val messagesList by viewModel.allChatMessages.collectAsState()
                        val activeChatHistory = messagesList.filter {
                            it.listingId == listing.id &&
                            ((it.senderId == "me" && it.receiverId == listing.sellerId) || (it.senderId == listing.sellerId && it.receiverId == "me"))
                        }
                        ChatDialog(
                            listing = listing,
                            viewModel = viewModel,
                            conversationMessages = activeChatHistory,
                            otherPartyName = listing.sellerName
                        ) {
                            showDirectChatPopup = false
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Staff media moderation hub
                val meProfile by viewModel.userProfile.collectAsState()
                if (meProfile?.role == "Owner" || meProfile?.role == "Administrator" || meProfile?.role == "Staff") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(1.dp, GoldAccent), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = ElevatedSlate)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("🛡️ STAFF MEDIA MODERATION HUB", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Current Media status: ${listing.mediaStatus}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val statuses = listOf("APPROVED", "REJECTED", "FLAGGED", "REQUEST_NEW")
                                statuses.forEach { stat ->
                                    Button(
                                        onClick = {
                                            viewModel.updateListingMediaStatus(listing, stat)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (listing.mediaStatus == stat) GoldAccent else DeepSlateBg
                                        ),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.weight(1f).height(24.dp)
                                    ) {
                                        Text(stat.replace("_", " "), color = if (listing.mediaStatus == stat) DeepSlateBg else Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (!showRcdBuyPanel) {
                    // Standard game-cash transactions
                    Button(
                        onClick = {
                            viewModel.buyActiveListing(listing)
                        },
                        modifier = Modifier.fillMaxWidth()
                            .testTag("buy_listing_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text("Buy with Bank Cash: ${formatCurrency(listing.askingPrice)}", color = DeepSlateBg, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showRcdBuyPanel = true },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, CyanInfo)
                    ) {
                        Text("Buy Securely via Real Cash Escrow (RCD)", color = CyanInfo)
                    }
                } else {
                    // Real money escrow panel details
                    Text("Create Escrow escrow Deal Ticket", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(" Select Payment Method Channel:", color = SoftGrayText, fontSize = 10.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val methods = listOf("UPI", "PayPal", "Bank Transfer", "Crypto")
                        methods.forEach { met ->
                            val isSelected = rcdPaymentMethod == met
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CyanInfo else ElevatedSlate)
                                    .clickable { rcdPaymentMethod = met }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(met, color = if (isSelected) DeepSlateBg else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = rcdFiatAmount,
                        onValueChange = { rcdFiatAmount = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Fiat Price ($ - Dollar equivalents)", color = SoftGrayText) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = CyanInfo)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = rcdNotes,
                        onValueChange = { rcdNotes = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Appraisal slip references / Notes", color = SoftGrayText) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = CyanInfo)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.submitRcdDeals(
                                listing = listing,
                                paymentMethod = rcdPaymentMethod,
                                realPrice = rcdFiatAmount.toDoubleOrNull() ?: 1.0,
                                notes = rcdNotes
                            )
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanInfo)
                    ) {
                        Text("Approve Escrow RCD Ticket", color = DeepSlateBg, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { showRcdBuyPanel = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Go back", color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }

    // Full screen zoom modal popup view
    if (showZoomedImage != null) {
        Dialog(onDismissRequest = { showZoomedImage = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DeepSlateBg),
                border = BorderStroke(1.dp, GoldAccent)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔍 Full Screen Media View", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showZoomedImage = null }, modifier = Modifier.size(24.dp)) {
                            Text("✕", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    SimulatedImageCanvas(
                        imageName = showZoomedImage!!,
                        category = listing.category,
                        watermarked = currentWatermarkState,
                        listingId = listing.id,
                        isZoomed = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Stamp status: Approved. Checks: pass. Format: RAW screencast.", color = GreenVerify, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
fun SpecRowLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = SoftGrayText, fontSize = 11.sp)
        Text(value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// ----------------------------------------------------
// SECURED KAT REGISTRY DATABASE LAYER
// ----------------------------------------------------
data class DBHouse(val number: Int, val city: String, val classType: String, val address: String)
data class DBApartment(val number: Int, val city: String, val classType: String, val address: String)
data class DBBusiness(val id: Int, val name: String, val location: String)

object KATDatabase {
    fun findHouse(num: Int): DBHouse? {
        if (num < 1 || num > 504) return null
        val (city, classType, address) = when {
            num in 1..21 -> Triple("Elite Village", "LUXURY", "ELITE VILLAGE")
            num in 22..39 -> Triple("GAREL", "STANDARD / ECONOMY", "SIDE OF THE ARZ YNZ BRIGDE")
            num in 40..61 -> Triple("KORYAKINO", "STANDARD / ECONOMY", "SIDE OF THE ARZ YNZ BRIGDE")
            num in 62..66 -> Triple("EDOVO", "STANDARD / ECONOMY", "NEAR THE PARKING OF EDOVO")
            num in 67..77 -> Triple("BATYREVO", "STANDARD / ECONOMY", "NEAR THE TWIX FACTORY OR MILITRY UNIT")
            num in 117..169 -> Triple("BUSAEVO", "STANDARD / LUXURY", "NEAR THE OCG WAR PLACE")
            num in 170..198 -> Triple("ARZAMAS", "STANDARD / ECONOMY", "NEAR CHURCH OR NEAR Kurgan OCG")
            num in 199..232 -> Triple("LYTKARINO", "STANDARD / LUXURY", "NEAR THE BRIDGE OF THE ARZ TO LYTKARINO")
            num in 233..287 -> Triple("BOGATYREVO", "LUXURY", "NEAR THE CASINO")
            num in 288..388 -> Triple("YUZHNY", "STANDARD / LUXURY", "NEAR THE CASH COLLECTOR JOB")
            num in 428..466 -> Triple("ARZAMAS", "STANDARD", "NEAR THE POST OFFICE")
            num in 467..504 -> Triple("SEA SIDE", "STANDARD / LUXURY", "NEAR THE PRISON")
            else -> Triple("KAT Sector", "STANDARD", "Registered Sector")
        }
        return DBHouse(num, city, classType, address)
    }

    fun findApartment(num: Int): DBApartment? {
        if (num < 1 || num > 760) return null
        val (city, classType, address) = when {
            num in 1..36 -> Triple("ARZAMAS", "LUXURY", "BACK SIDE ARZ PARKING OPPOSITE TO SEA TERMINAL")
            num in 37..354 -> Triple("YUZHNY", "STANDARD OR LUXURY", "BACK SIDE OF YNZ BANK")
            num in 355..441 -> Triple("LYTKARINO", "STANDARD", "NEAR THE BOXING CLUB OR BACK OF CAUCASIAN OCG")
            num in 442..558 -> Triple("EDOVO", "STANDARD", "BESIDE OF EDOVO BUS STATION OR BACK SIDE OF EDOVO PARK")
            num in 559..760 -> Triple("ARZAMAS", "STANDARD", "SIDE OF GOVERNMENT HOUSE")
            else -> Triple("KAT Zone", "STANDARD", "Registered Complex")
        }
        return DBApartment(num, city, classType, address)
    }

    fun findBusiness(query: String): DBBusiness? {
        val id = query.toIntOrNull()
        val list = listOf(
            DBBusiness(1, "Shop 24/7", "Arzamas Center – Next to Central State Bank"),
            DBBusiness(2, "Shop 24/7", "Arzamas North – Opposite Apartment Blocks"),
            DBBusiness(3, "Shop 24/7", "Arzamas West – Near Vehicle Dealership"),
            DBBusiness(4, "Shop 24/7", "Yuzhny Center – Main Spawn Square"),
            DBBusiness(5, "Shop 24/7", "Yuzhny Police Area – Opposite Police Department"),
            DBBusiness(6, "Shop 24/7", "Yuzhny East – Soviet Apartment Blocks"),
            DBBusiness(7, "Shop 24/7", "Lytkarino Center – Opposite City Hospital"),
            DBBusiness(8, "Shop 24/7", "Lytkarino West – Near Bank & Tuning Shop"),
            DBBusiness(9, "Shop 24/7", "Batyrevo Center – Housing Grid Center"),
            DBBusiness(10, "Clothing Store", "Arzamas Plaza – Central Shopping Square"),
            DBBusiness(11, "Clothing Store", "Arzamas North – Near Luxury Dealership"),
            DBBusiness(12, "Clothing Store", "Yuzhny Central – Walking Plaza"),
            DBBusiness(13, "Clothing Store", "Yuzhny License Center – Near Driving License Office"),
            DBBusiness(14, "Clothing Store", "Batyrevo Market – Public Square"),
            DBBusiness(15, "Restaurant", "Arzamas Center – Main Spawn Station"),
            DBBusiness(16, "Restaurant", "Arzamas North – High-Rise Residential Area"),
            DBBusiness(17, "Restaurant", "Arzamas South – Southern Apartments"),
            DBBusiness(18, "Restaurant", "Yuzhny Center – Next to Central Bank"),
            DBBusiness(19, "Restaurant", "Yuzhny East – Apartment Grid"),
            DBBusiness(20, "Restaurant", "Batyrevo West – Cottage Neighborhood"),
            DBBusiness(21, "Restaurant", "Batyrevo East – Eastern Housing District"),
            DBBusiness(22, "Restaurant", "Lytkarino Center – Opposite Hospital"),
            DBBusiness(23, "Restaurant", "Lytkarino East – Exit Highway"),
            DBBusiness(24, "Weapons Shop", "Arzamas South – Forest Border"),
            DBBusiness(25, "Petrol Station", "Yuzhny Bridge Checkpoint"),
            DBBusiness(26, "Gas Station", "Auchan Central Mall Parking Lot"),
            DBBusiness(27, "Parking Lot", "Arzamas South Apartments"),
            DBBusiness(28, "Parking Lot", "Batyrevo Central Spawn Area"),
            DBBusiness(29, "Parking Lot", "Yuzhny East High-Rises"),
            DBBusiness(30, "Gas Station", "Arzamas North Exit"),
            DBBusiness(31, "Gas Station", "Arzamas East Exit"),
            DBBusiness(32, "Gas Station", "Batyrevo-Arzamas Central Interchange"),
            DBBusiness(33, "Gas Station", "Batyrevo Center"),
            DBBusiness(34, "Gas Station", "Lytkarino Entry Highway"),
            DBBusiness(35, "Weapons Shop", "Batyrevo Outskirts"),
            DBBusiness(36, "Gas Station", "Busaevo Village Entrance"),
            DBBusiness(37, "Shop 24/7", "Edovo Industrial – Apartment Lobby"),
            DBBusiness(38, "Restaurant", "Edovo Depot – Trucking Depots"),
            DBBusiness(39, "CargoConnect", "Edovo North Logistics Hub"),
            DBBusiness(40, "VectorCargo", "Edovo East Industrial Zone"),
            DBBusiness(41, "TransCargoLiz", "Arzamas Outskirts Warehouse Ring"),
            DBBusiness(42, "Clothing Store", "Lytkarino Central"),
            DBBusiness(43, "Shop 24/7", "Batyrevo North – Military Base Highway"),
            DBBusiness(44, "Shop 24/7", "Arzamas–Yuzhny Highway Checkpoint"),
            DBBusiness(45, "Gas Station", "Batyrevo-Arzamas Secondary Highway Fork"),
            DBBusiness(46, "Gas Station", "Edovo North Exit"),
            DBBusiness(47, "Gas Station", "Edovo Freight Warehouses"),
            DBBusiness(48, "Parking Lot", "Edovo Apartment Entrances"),
            DBBusiness(49, "Shop 24/7", "Government District – Government House"),
            DBBusiness(50, "Gas Station", "Lytkarino-2 Coastal Road"),
            DBBusiness(51, "Parcel Terminal #1", "Arzamas Grid"),
            DBBusiness(52, "Parcel Terminal #2", "Arzamas Grid"),
            DBBusiness(53, "Parcel Terminal #3", "Arzamas Grid"),
            DBBusiness(54, "Parcel Terminal #4", "Arzamas Grid"),
            DBBusiness(55, "Parcel Terminal #5", "Arzamas Grid"),
            DBBusiness(56, "Parcel Terminal #6", "Yuzhny Grid"),
            DBBusiness(57, "Parcel Terminal #7", "Yuzhny Grid"),
            DBBusiness(58, "Parcel Terminal #8", "Yuzhny Grid"),
            DBBusiness(59, "Parcel Terminal #9", "Lytkarino Walkways"),
            DBBusiness(60, "Parcel Terminal #10", "Lytkarino Walkways"),
            DBBusiness(61, "Notary", "Arzamas Downtown – Opposite State Bank"),
            DBBusiness(62, "Notary", "Yuzhny Commercial Strip"),
            DBBusiness(63, "Parcel Terminal #11", "Edovo Square"),
            DBBusiness(64, "Pickaxe Shop", "Arzamas Exit – Mountain Road"),
            DBBusiness(65, "Pickaxe Shop", "Batyrevo West – Quarry Route"),
            DBBusiness(66, "Pickaxe Shop", "Quarry Road – Stone Pit Access"),
            DBBusiness(67, "Shop 24/7", "Egorovka Cottage Village Entrance"),
            DBBusiness(68, "Restaurant", "Fisherman's Pier"),
            DBBusiness(69, "24/7 Store", "Lytkarino Suburbs"),
            DBBusiness(70, "24/7 Store", "Elite Village Entrance"),
            DBBusiness(71, "Restaurant", "Central Crossroads"),
            DBBusiness(72, "Gas Station", "Rybatskoe Fishing Docks"),
            DBBusiness(73, "24/7 Store", "Tarely Coast"),
            DBBusiness(74, "Gas Station", "Damba-1 Dam Bridge"),
            DBBusiness(75, "24/7 Store", "Northern Dam Area"),
            DBBusiness(76, "Clothing Store", "Elite District")
        )
        if (id != null) {
            return list.find { it.id == id }
        }
        return list.find { it.name.contains(query, ignoreCase = true) }
    }

    fun getSimulatedStatePrice(category: String, subType: String, classType: String, city: String): Long {
        return when (category) {
            "Property" -> {
                if (subType == "House") {
                    when {
                        city.equals("Elite Village", true) -> 450000000L
                        city.equals("BOGATYREVO", true) -> 500000000L
                        classType.contains("LUXURY", true) -> 350000000L
                        else -> 45000000L
                    }
                } else { // Apartment
                    if (classType.contains("LUXURY", true)) 85000000L else 25000000L
                }
            }
            "Business" -> {
                when {
                    subType.contains("Weapons", true) || subType.contains("Gas", true) || subType.contains("Petrol", true) -> 400000000L
                    subType.contains("Cargo", true) || subType.contains("Connect", true) -> 250000000L
                    subType.contains("Notary", true) || subType.contains("Parking", true) -> 80000000L
                    else -> 120000000L
                }
            }
            else -> 10000000L
        }
    }
}

// ----------------------------------------------------
// DYNAMIC CREATE DIALOGUE SETUP FORM
// ----------------------------------------------------
@Composable
fun CreateListingDialog(viewModel: MarketViewModel, onDismiss: () -> Unit) {
    var stepCategory by remember { mutableStateOf("Vehicle") } // Vehicle, Property, Business, Skin, Item

    // Form inputs variables
    var inputTitle by remember { mutableStateOf("") }
    var inputSubType by remember { mutableStateOf("Car") }
    var inputStatePrice by remember { mutableStateOf("") }
    var inputAskingPrice by remember { mutableStateOf("") }
    var inputOwnersCount by remember { mutableStateOf("1") }
    var inputLicensePlate by remember { mutableStateOf("") }
    var inputLocation by remember { mutableStateOf("") }
    var inputDailyProfit by remember { mutableStateOf("") }
    var inputNotesText by remember { mutableStateOf("") }
    var checkedFeatured by remember { mutableStateOf(false) }
    var checkedUrgent by remember { mutableStateOf(false) }

    // Auto-fill Database States
    var searchRegistryInput by remember { mutableStateOf("") }
    var autoFillStatusMessage by remember { mutableStateOf("") }
    var isAutoFillSuccess by remember { mutableStateOf<Boolean?>(null) } // null, true=success, false=failed

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Publish Grand Board Listing", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(10.dp))

                // Select category chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val steps = listOf("Vehicle", "Property", "Business", "Skin", "Item")
                    steps.forEach { step ->
                        val active = stepCategory == step
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) GoldAccent else ElevatedSlate)
                                .clickable {
                                    stepCategory = step
                                    // Default subTypes automatically
                                    inputSubType = when (step) {
                                        "Vehicle" -> "Car"
                                        "Property" -> "House"
                                        "Business" -> "24/7 Store"
                                        "Skin" -> "Exclusive"
                                        else -> "Crate"
                                    }
                                    // Reset auto-fill states
                                    searchRegistryInput = ""
                                    autoFillStatusMessage = ""
                                    isAutoFillSuccess = null
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(step, color = if (active) DeepSlateBg else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SMART DATABASE AUTO-FILL SECTION
                if (stepCategory == "Property" || stepCategory == "Business") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
                        border = BorderStroke(1.dp, if (isAutoFillSuccess == true) GreenVerify else GoldAccent.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🤖 KAT Smart Auto-Fill Core", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(DeepSlateBg, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text("ACTIVE DATABASE", color = CoinGold, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            val promptLabel = when {
                                stepCategory == "Property" && inputSubType == "House" -> "Enter House Number (e.g., 20)"
                                stepCategory == "Property" && inputSubType == "Apartment" -> "Enter Apartment Number (e.g., 15)"
                                else -> "Enter Business Name or ID (e.g., 24)"
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = searchRegistryInput,
                                    onValueChange = { searchRegistryInput = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    placeholder = { Text(promptLabel, color = MutedText, fontSize = 11.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = GoldAccent,
                                        unfocusedBorderColor = DeepSlateBg,
                                        focusedContainerColor = CardSlateBg,
                                        unfocusedContainerColor = CardSlateBg
                                    )
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        if (searchRegistryInput.isBlank()) {
                                            isAutoFillSuccess = false
                                            autoFillStatusMessage = "Please enter an identifier to search registry."
                                            return@Button
                                        }

                                        when {
                                            stepCategory == "Property" && inputSubType == "House" -> {
                                                val hNum = searchRegistryInput.toIntOrNull()
                                                if (hNum != null) {
                                                    val record = KATDatabase.findHouse(hNum)
                                                    if (record != null) {
                                                        // Populate variables
                                                        inputTitle = "🏠 Luxury House #${record.number} in ${record.city}"
                                                        inputLocation = "${record.address}, ${record.city}"
                                                        val sp = KATDatabase.getSimulatedStatePrice("Property", "House", record.classType, record.city)
                                                        inputStatePrice = sp.toString()
                                                        inputNotesText = "Auto-filled from House database.\nLocation: ${record.city}\nClass: ${record.classType}\nGarage: Yes\nOwnership checked: Secure Transactable Asset."
                                                        isAutoFillSuccess = true
                                                        autoFillStatusMessage = "✅ Success! House #${record.number} sync'd correctly."
                                                    } else {
                                                        isAutoFillSuccess = false
                                                        autoFillStatusMessage = "❌ Registry ID #$searchRegistryInput not registered. Allowing manual input."
                                                    }
                                                } else {
                                                    isAutoFillSuccess = false
                                                    autoFillStatusMessage = "❌ Invalid Numeric House Identifier."
                                                }
                                            }
                                            stepCategory == "Property" && inputSubType == "Apartment" -> {
                                                val aNum = searchRegistryInput.toIntOrNull()
                                                if (aNum != null) {
                                                    val record = KATDatabase.findApartment(aNum)
                                                    if (record != null) {
                                                        inputTitle = "🏢 ${record.classType} Apartment #${record.number} in ${record.city}"
                                                        inputLocation = "${record.address}, ${record.city}"
                                                        val sp = KATDatabase.getSimulatedStatePrice("Property", "Apartment", record.classType, record.city)
                                                        inputStatePrice = sp.toString()
                                                        inputNotesText = "Auto-filled from Apartment database.\nLocation: ${record.city}\nClass: ${record.classType}\nRegistry status: CLEAR."
                                                        isAutoFillSuccess = true
                                                        autoFillStatusMessage = "✅ Success! Apartment #${record.number} sync'd correctly."
                                                    } else {
                                                        isAutoFillSuccess = false
                                                        autoFillStatusMessage = "❌ Registry ID #$searchRegistryInput not registered. Allowing manual input."
                                                    }
                                                } else {
                                                    isAutoFillSuccess = false
                                                    autoFillStatusMessage = "❌ Invalid Numeric Apartment Identifier."
                                                }
                                            }
                                            stepCategory == "Business" -> {
                                                val record = KATDatabase.findBusiness(searchRegistryInput)
                                                if (record != null) {
                                                    inputTitle = "💼 Corporate Asset: ${record.name} #${record.id}"
                                                    inputLocation = record.location
                                                    inputSubType = record.name
                                                    val sp = KATDatabase.getSimulatedStatePrice("Business", record.name, "", record.location)
                                                    inputStatePrice = sp.toString()
                                                    inputNotesText = "Auto-filled from Corporate database.\nBusiness Category: ${record.name}\nDesignated Location Address: ${record.location}\nSyndicate Audit: PASSED."
                                                    isAutoFillSuccess = true
                                                    autoFillStatusMessage = "✅ Success! Corporate ID #${record.id} (${record.name}) loaded."
                                                } else {
                                                    isAutoFillSuccess = false
                                                    autoFillStatusMessage = "❌ No registered corporate entity found matching: '$searchRegistryInput'."
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                    contentPadding = PaddingValues(horizontal = 12.dp),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Text("Pull Record", color = DeepSlateBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (autoFillStatusMessage.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    autoFillStatusMessage,
                                    color = if (isAutoFillSuccess == true) GreenVerify else Color.LightGray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Shared inputs
                OutlinedTextField(
                    value = inputTitle,
                    onValueChange = { inputTitle = it },
                    modifier = Modifier.fillMaxWidth()
                        .testTag("listing_form_title_input"),
                    label = { Text("Listing Public Title", color = SoftGrayText) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    OutlinedTextField(
                        value = inputStatePrice,
                        onValueChange = { inputStatePrice = it },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("State Price (R)", color = SoftGrayText) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = inputAskingPrice,
                        onValueChange = { inputAskingPrice = it },
                        modifier = Modifier.weight(1f)
                            .testTag("listing_form_asking_price_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Asking Price (R)", color = SoftGrayText) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category-specific configurations
                when (stepCategory) {
                    "Vehicle" -> {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val vTypes = listOf("Car", "Bike", "Boat", "Helicopter")
                            vTypes.forEach { vt ->
                                val active = inputSubType == vt
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) GoldAccent else ElevatedSlate)
                                        .clickable { inputSubType = vt }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(vt, color = if (active) DeepSlateBg else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            OutlinedTextField(
                                value = inputOwnersCount,
                                onValueChange = { inputOwnersCount = it },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                label = { Text("Owners Count", color = SoftGrayText) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = inputLicensePlate,
                                onValueChange = { inputLicensePlate = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("License Plate", color = SoftGrayText) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                            )
                        }
                    }

                    "Property" -> {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val pTypes = listOf("House", "Apartment")
                            pTypes.forEach { pt ->
                                val active = inputSubType == pt
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) GoldAccent else ElevatedSlate)
                                        .clickable {
                                            inputSubType = pt
                                            // Reset auto-fill registry identifier queries on subtype change
                                            searchRegistryInput = ""
                                            autoFillStatusMessage = ""
                                            isAutoFillSuccess = null
                                        }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(pt, color = if (active) DeepSlateBg else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = inputLocation,
                            onValueChange = { inputLocation = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Location Address (e.g. Arzamas Center)", color = SoftGrayText) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                        )
                    }

                    "Business" -> {
                        OutlinedTextField(
                            value = inputDailyProfit,
                            onValueChange = { inputDailyProfit = it },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("10 Day Avg Daily Profit (R)", color = SoftGrayText) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = inputLocation,
                            onValueChange = { inputLocation = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Location Address", color = SoftGrayText) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = inputNotesText,
                    onValueChange = { inputNotesText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Specs / Notes description", color = SoftGrayText) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Media proof files simulate upload checklist
                var attachMainPhoto by remember { mutableStateOf(true) }
                var attachLicensePhoto by remember { mutableStateOf(true) }
                var attachDetailPhoto by remember { mutableStateOf(false) }
                var inputVideoLink by remember { mutableStateOf("") }
                var checkedWatermark by remember { mutableStateOf(true) }

                Spacer(modifier = Modifier.height(10.dp))
                Text("📸 VERIFIED MEDIA ATTACHMENTS", color = GoldAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = attachMainPhoto, onCheckedChange = { attachMainPhoto = it })
                            Text("Main Photo", color = SoftGrayText, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = attachLicensePhoto, onCheckedChange = { attachLicensePhoto = it })
                            Text("Lic. Proof", color = SoftGrayText, fontSize = 10.sp)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = attachDetailPhoto, onCheckedChange = { attachDetailPhoto = it })
                            Text("Detail Spec", color = SoftGrayText, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = checkedWatermark, onCheckedChange = { checkedWatermark = it })
                            Text("Watermark", color = SoftGrayText, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = inputVideoLink,
                    onValueChange = { inputVideoLink = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Video Proof URL (e.g. proof_tour.mp4)", color = SoftGrayText, fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = GoldAccent)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = checkedFeatured, onCheckedChange = { checkedFeatured = it })
                        Text("Featured VIP (+50 KC)", color = SoftGrayText, fontSize = 11.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = checkedUrgent, onCheckedChange = { checkedUrgent = it })
                        Text("Urgent Fast", color = SoftGrayText, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val finalImages = mutableListOf<String>()
                        if (attachMainPhoto) finalImages.add("Main Screenshot Detail")
                        if (attachLicensePhoto) finalImages.add("License Registry ID Proof")
                        if (attachDetailPhoto) finalImages.add("Interior Engine Specifications Sheet")

                        viewModel.createMarketListing(
                            title = inputTitle,
                            category = stepCategory,
                            subType = inputSubType,
                            statePrice = inputStatePrice.toLongOrNull() ?: 0L,
                            askingPrice = inputAskingPrice.toLongOrNull() ?: 0L,
                            owners = inputOwnersCount.toIntOrNull() ?: 1,
                            location = inputLocation,
                            licensePlate = inputLicensePlate,
                            dailyProfit = inputDailyProfit.toLongOrNull() ?: 0L,
                            notes = inputNotesText,
                            isFeatured = checkedFeatured,
                            isUrgent = checkedUrgent,
                            images = finalImages,
                            videoUrl = inputVideoLink,
                            watermarked = checkedWatermark
                        )
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                        .testTag("submit_listing_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("Publish To Board", color = DeepSlateBg, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ----------------------------------------------------
// UTILITY STRING CONVERSION BUILDERS
// ----------------------------------------------------
fun formatCurrency(amount: Long): String {
    val resultStr = when {
        amount >= 1_000_000_000L -> "${String.format("%.1f", amount.toDouble() / 1_000_000_000)}B"
        amount >= 1_000_000L -> "${amount / 1_000_000}M"
        amount >= 1_000L -> "${amount / 1_000}K"
        else -> "$amount"
    }
    return "$$resultStr"
}

// ----------------------------------------------------
// IMAGE GRAPHICS, GALLERY, AND WATERMARK HOOKS
// ----------------------------------------------------

@Composable
fun SimulatedImageCanvas(
    imageName: String,
    category: String,
    watermarked: Boolean,
    listingId: Int,
    isZoomed: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val heightDp = if (isZoomed) 280.dp else 160.dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightDp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E293B),
                        Color(0xFF0F172A)
                    )
                )
            )
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            val emoji = when (category) {
                "Vehicle" -> "🚗"
                "Property" -> "🏠"
                "Business" -> "🏢"
                "Skin" -> "🎨"
                else -> "📦"
            }
            Text(emoji, fontSize = if (isZoomed) 48.sp else 32.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(imageName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("Click to Full Screen Zoom", color = MutedText, fontSize = 8.sp)
        }

        // Watermark HUD overlays if enabled
        if (watermarked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column(
                    modifier = Modifier
                        .background(Color(0xE60F1115), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("🛡️ KAT_MARKET_NIKA • REAL SECURE PROOF", color = GoldAccent, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                    Text("UID: #$listingId • DATE: 17.06.2026", color = Color.White, fontSize = 7.sp)
                }
            }
        }

        // Verification indicator
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF10B981), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text("APPROVED SCREENSHOT", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ----------------------------------------------------
// CHATS INBOX ENGINE & SCREEN VIEW
// ----------------------------------------------------

@Composable
fun ChatInboxView(viewModel: MarketViewModel) {
    val messages by viewModel.allChatMessages.collectAsState()
    val listings by viewModel.allListings.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val offers by viewModel.negotiationOffers.collectAsState()
    val tradeRooms by viewModel.tradeRooms.collectAsState()
    val richProfiles by viewModel.richList.collectAsState()
    
    var activeChatKey by remember { mutableStateOf<Pair<Int, String>?>(null) } // listingId to otherUserId
    var activeChatTitle by remember { mutableStateOf("") }
    var activeChatOtherName by remember { mutableStateOf("") }

    // Active Tab in Chat Workspace: "MESSAGES", "OFFERS", "INTERESTS", "TRADE"
    var chatSubTab by remember { mutableStateOf("MESSAGES") }

    var showCounterDialog by remember { mutableStateOf<NegotiationOffer?>(null) }
    var counterAmountInput by remember { mutableStateOf("") }

    var showTradeProofDialog by remember { mutableStateOf<TradeRoom?>(null) }
    var tradeProofPath by remember { mutableStateOf("") }

    // Grouping existing chat messages into conversations list
    val conversations = remember(messages) {
        val groups = messages.groupBy { msg ->
            val otherId = if (msg.senderId == "me") msg.receiverId else msg.senderId
            val otherName = if (msg.senderId == "me") msg.receiverName else msg.senderName
            Pair(msg.listingId, otherId) to otherName
        }
        groups.map { (keyAndName, msgList) ->
            val (pair, otherName) = keyAndName
            val (listingId, otherId) = pair
            val lastMsg = msgList.maxByOrNull { it.timestamp }!!
            object {
                val listingId = listingId
                val listingTitle = lastMsg.listingTitle
                val otherUserId = otherId
                val otherUserName = otherName
                val lastText = lastMsg.message
                val timestamp = lastMsg.timestamp
                val msgs = msgList
            }
        }.sortedByDescending { it.timestamp }
    }

    // Helper parser for Grand RP style numeric abbreviations (M, K, B)
    fun parseToAmount(input: String): Long {
        val clean = input.trim().uppercase()
        if (clean.endsWith("B")) {
            return ((clean.removeSuffix("B").toDoubleOrNull() ?: 0.0) * 1_000_000_000).toLong()
        }
        if (clean.endsWith("M")) {
            return ((clean.removeSuffix("M").toDoubleOrNull() ?: 0.0) * 1_000_000).toLong()
        }
        if (clean.endsWith("K")) {
            return ((clean.removeSuffix("K").toDoubleOrNull() ?: 0.0) * 1_000).toLong()
        }
        return clean.replace(",", "").replace("\$", "").toLongOrNull() ?: 0L
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("GRAND RP NEGOTIATION HUB", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(
                    when (chatSubTab) {
                        "MESSAGES" -> "💬 DIRECT INBOX CHAT"
                        "OFFERS" -> "💸 DIRECT SUM BIDS"
                        "INTERESTS" -> "👀 INTEREST HUB TRACKER"
                        else -> "🛡️ ACTIVE TRADE ROOMS"
                    },
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }
            if (chatSubTab == "MESSAGES") {
                Button(
                    onClick = {
                        val myActiveListing = listings.find { it.sellerId == "me" } ?: listings.firstOrNull()
                        if (myActiveListing != null) {
                            viewModel.simulateIncomingBuyerMessage(myActiveListing)
                        } else {
                            viewModel.alertMessage.value = "Create at least one listing first to receive bids!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("🤖 Simulate Bid", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- SUB-NAVIGATION BAR (Material Design 3 tab styling) ---
        ScrollableTabRow(
            selectedTabIndex = when (chatSubTab) {
                "MESSAGES" -> 0
                "OFFERS" -> 1
                "INTERESTS" -> 2
                else -> 3
            },
            containerColor = Color.Transparent,
            contentColor = GoldAccent,
            indicator = {},
            divider = {},
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth().height(36.dp)
        ) {
            val tabs = listOf(
                Pair("MESSAGES", "💬 Inbox"),
                Pair("OFFERS", "💸 Offers"),
                Pair("INTERESTS", "👀 Interests"),
                Pair("TRADE", "🛡️ Escrow")
            )
            tabs.forEachIndexed { index, tabInfo ->
                val (tabKey, tabLabel) = tabInfo
                val isSelected = chatSubTab == tabKey
                Box(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) GoldAccent else ElevatedSlate)
                        .clickable { chatSubTab = tabKey }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        tabLabel,
                        color = if (isSelected) DeepSlateBg else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- SUB-TABS CONTENT SWITCHER ---
        when (chatSubTab) {
            "MESSAGES" -> {
                // RENDER DIRECT CHATS LIST
                if (conversations.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(CardSlateBg, RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, ElevatedSlate), RoundedCornerShape(12.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.MailOutline, contentDescription = "None", tint = MutedText, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Direct inbox is pristine.", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Go to listings sheet, click 'Chat Seller' on any item, or submit direct offer terms to instantiate RP chat channels instantly.",
                                color = SoftGrayText,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(conversations) { conv ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        activeChatKey = Pair(conv.listingId, conv.otherUserId)
                                        activeChatTitle = conv.listingTitle
                                        activeChatOtherName = conv.otherUserName
                                    },
                                colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, ElevatedSlate)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(ElevatedSlate, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(conv.otherUserName.take(2).uppercase(), color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(conv.otherUserName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Listing #${conv.listingId}", color = MutedText, fontSize = 8.sp)
                                        }
                                        Text("RE: ${conv.listingTitle}", color = GoldAccent, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(conv.lastText, color = SoftGrayText, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "OFFERS" -> {
                // RENDER SUM NEGOTIATION OFFERS DIRECTLY
                if (offers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(CardSlateBg, RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, ElevatedSlate), RoundedCornerShape(12.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💸", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No pending negotiations.", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Submit a sum offer using 'Make Offer' under any listing to initiate a structured RP contract ledger.",
                                color = SoftGrayText,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(offers) { offer ->
                            val isMeBuyer = offer.buyerId == "me"
                            val isMeSeller = offer.sellerId == "me"
                            val statusColor = when (offer.status) {
                                "PENDING" -> GoldAccent
                                "ACCEPTED" -> GreenVerify
                                "DECLINED" -> RedUrgent
                                else -> CyanInfo
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(offer.listingTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                        Box(
                                            modifier = Modifier
                                                .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(offer.status, color = statusColor, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Proposed By: ${if (offer.isCreatedByBuyer) "Buyer (${offer.buyerName})" else "Seller (${offer.sellerName})"}", color = SoftGrayText, fontSize = 10.sp)
                                            Text("Terms Suggestion: ${formatCurrency(offer.amount)}", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Black)
                                            if (offer.counterAmount > 0) {
                                                Text("Counter Offer: ${formatCurrency(offer.counterAmount)}", color = CyanInfo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Party Link:", color = MutedText, fontSize = 8.sp)
                                            Text(if (isMeSeller) "You are Seller" else "You are Buyer", color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("💬 Message: \"${offer.message}\"", color = SoftGrayText, fontSize = 10.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)

                                    // Actions panel
                                    if (offer.status == "PENDING" || offer.status == "COUNTERED") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Divider(color = ElevatedSlate.copy(alpha = 0.5f))
                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            if (isMeSeller && offer.status == "PENDING") {
                                                // Seller actions on pending buyer proposal
                                                Button(
                                                    onClick = { viewModel.acceptOffer(offer) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x3310B981)),
                                                    border = BorderStroke(1.dp, GreenVerify),
                                                    modifier = Modifier.weight(1f).height(28.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Accept", color = GreenVerify, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }

                                                Button(
                                                    onClick = {
                                                        showCounterDialog = offer
                                                        counterAmountInput = ""
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                                                    modifier = Modifier.weight(1f).height(28.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Counter", color = CyanInfo, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }

                                                Button(
                                                    onClick = { viewModel.declineOffer(offer) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33EF4444)),
                                                    border = BorderStroke(1.dp, RedUrgent),
                                                    modifier = Modifier.weight(1f).height(28.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Decline", color = RedUrgent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            } else if (isMeBuyer && offer.status == "COUNTERED" && !offer.isCreatedByBuyer) {
                                                // Buyer actions on seller counter-proposal
                                                Button(
                                                    onClick = { viewModel.acceptOffer(offer.copy(amount = offer.counterAmount)) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x3310B981)),
                                                    border = BorderStroke(1.dp, GreenVerify),
                                                    modifier = Modifier.weight(1f).height(28.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Accept Counter", color = GreenVerify, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }

                                                Button(
                                                    onClick = { viewModel.declineOffer(offer) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33EF4444)),
                                                    border = BorderStroke(1.dp, RedUrgent),
                                                    modifier = Modifier.weight(1f).height(28.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Reject", color = RedUrgent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            } else {
                                                // Awaiting counterparty text
                                                Box(
                                                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("⌛ AWAITING COUNTERPARTY RESPONSE", color = MutedText, fontSize = 8.sp, fontWeight = FontWeight.Bold)
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

            "INTERESTS" -> {
                // RENDER INTEREST EVENTS LIST
                // Query system interest notices from ChatMessage list
                val interestNotices = remember(messages) {
                    messages.filter { it.message.contains("👀 [INTEREST EXPRESSED]") }
                }

                if (interestNotices.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(CardSlateBg, RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, ElevatedSlate), RoundedCornerShape(12.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👀", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No expressed interests yet.", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "When potential traders click 'Interested' in your listings, special alert receipts compile here.",
                                color = SoftGrayText,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(interestNotices) { notice ->
                            // Look up detailed details of this buyer from rich list
                            val profileInfo = richProfiles.find { it.username == notice.senderName } ?: UserProfile(
                                username = notice.senderName,
                                reputation = 90,
                                isVerified = true,
                                role = "Normal User"
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, ElevatedSlate)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("👀", fontSize = 12.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Interest Flagged!", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Text(
                                            java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault()).format(java.util.Date(notice.timestamp)),
                                            color = MutedText,
                                            fontSize = 8.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Listing: ${notice.listingTitle}", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Divider(color = ElevatedSlate.copy(alpha = 0.3f))
                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Display: Interested User, Reputation, Verified Status, Profile description
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier.size(30.dp).background(ElevatedSlate, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(profileInfo.username.take(2).uppercase(), color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(profileInfo.username, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                if (profileInfo.isVerified) {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Box(
                                                        modifier = Modifier.background(GreenVerify.copy(alpha = 0.2f), RoundedCornerShape(2.dp)).padding(horizontal = 3.dp, vertical = 1.dp)
                                                    ) {
                                                        Text("VERIFIED", color = GreenVerify, fontSize = 6.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                            Text("Reputation Balance: ⭐ ${profileInfo.reputation}% • Role: ${profileInfo.role}", color = SoftGrayText, fontSize = 9.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Actions: Message User / Invite To Deal Chat
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                activeChatKey = Pair(notice.listingId, notice.senderId)
                                                activeChatTitle = notice.listingTitle
                                                activeChatOtherName = notice.senderName
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                                            modifier = Modifier.weight(1f).height(26.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("💬 Open Chat Room", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                activeChatKey = Pair(notice.listingId, notice.senderId)
                                                activeChatTitle = notice.listingTitle
                                                activeChatOtherName = notice.senderName
                                                // Auto prefill offer dialog inside active conversation
                                                viewModel.alertMessage.value = "Chat loaded. Type direct bid offers inside the console!"
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                            modifier = Modifier.weight(1f).height(26.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Invite To Deal Chat", color = DeepSlateBg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                // RENDER SECURE TRADE ROOMS (ESCROW DEAL BOARD)
                if (tradeRooms.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(CardSlateBg, RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, ElevatedSlate), RoundedCornerShape(12.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🤝", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No secure escrow trade rooms open.", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Accept a sum negotiation offer in the Offers ledger to spawn a legal trading office session automatically.",
                                color = SoftGrayText,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tradeRooms) { room ->
                            val isMeBuyer = room.buyerId == "me"
                            val isMeSeller = room.sellerId == "me"
                            val tradeStatusColor = when (room.status) {
                                "PENDING_COMPLETION" -> GoldAccent
                                "COMPLETED" -> GreenVerify
                                else -> RedUrgent
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, tradeStatusColor.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    // Header: Listing name and Status Badge
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(room.listingTitle, color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                        Box(
                                            modifier = Modifier
                                                .background(tradeStatusColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(room.status.replace("_", " "), color = tradeStatusColor, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Agreed Settlement Sum: ${formatCurrency(room.agreedPrice)}", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Black)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Divider(color = ElevatedSlate.copy(alpha = 0.3f))
                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Display Details: Buyer Name, Seller Name, status notes
                                    Text("Buyer Roleplay: ${room.buyerName}", color = SoftGrayText, fontSize = 10.sp)
                                    Text("Seller Roleplay: ${room.sellerName}", color = SoftGrayText, fontSize = 10.sp)
                                    
                                    if (room.proofImagePath.isNotEmpty()) {
                                        Text("📸 Screenshot Uploaded: \"${room.proofImagePath}\"", color = GreenVerify, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    } else {
                                        Text("📸 Screenshot: No verification proof uploaded yet.", color = MutedText, fontSize = 10.sp)
                                    }

                                    if (room.status == "PENDING_COMPLETION") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            // Interactive Buttons: Chat, Upload Proof, Mark Completed, Open Dispute
                                            Button(
                                                onClick = {
                                                    activeChatKey = Pair(room.listingId, if (isMeBuyer) room.sellerId else room.buyerId)
                                                    activeChatTitle = room.listingTitle
                                                    activeChatOtherName = if (isMeBuyer) room.sellerName else room.buyerName
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                                                modifier = Modifier.weight(1f).height(26.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("💬 Chat", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = {
                                                    showTradeProofDialog = room
                                                    tradeProofPath = ""
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                                                modifier = Modifier.weight(1f).height(26.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("📸 Upload", color = CyanInfo, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = { viewModel.markTradeCompleted(room) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x3310B981)),
                                                border = BorderStroke(1.dp, GreenVerify),
                                                modifier = Modifier.weight(1f).height(26.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Complete", color = GreenVerify, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = { viewModel.openTradeDispute(room) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x33EF4444)),
                                                border = BorderStroke(1.dp, RedUrgent),
                                                modifier = Modifier.weight(1f).height(26.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Dispute", color = RedUrgent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
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
    }

    // --- Modal : Counter Offer Input ---
    if (showCounterDialog != null) {
        val targetOffer = showCounterDialog!!
        AlertDialog(
            onDismissRequest = { showCounterDialog = null },
            title = {
                Text("💸 PROPOSE COUNTER OFFER", color = CyanInfo, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Original user proposal: ${formatCurrency(targetOffer.amount)}", color = Color.White, fontSize = 11.sp)
                    Text("Suggest an updated pricing term to the buyer:", color = SoftGrayText, fontSize = 10.sp)
                    OutlinedTextField(
                        value = counterAmountInput,
                        onValueChange = { counterAmountInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("counter_amount_input"),
                        label = { Text("Counter Price (e.g. 480K, 41M)", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyanInfo,
                            unfocusedBorderColor = ElevatedSlate
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = parseToAmount(counterAmountInput)
                        if (parsed > 0) {
                            viewModel.submitCounterOffer(targetOffer, parsed, isByBuyer = false)
                            showCounterDialog = null
                        } else {
                            viewModel.alertMessage.value = "Enter valid pricing shorthand terms!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanInfo)
                ) {
                    Text("Propose Counter", color = DeepSlateBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCounterDialog = null }) {
                    Text("Nevermind", color = SoftGrayText, fontSize = 11.sp)
                }
            },
            containerColor = DeepSlateBg,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // --- Modal : Upload Trade Screenshot Proof ---
    if (showTradeProofDialog != null) {
        val targetRoom = showTradeProofDialog!!
        AlertDialog(
            onDismissRequest = { showTradeProofDialog = null },
            title = {
                Text("📸 UPLOAD TRADE SECURITY PROOF", color = CyanInfo, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Verify payments or asset transfers by inputting the in-character screenshot key path:", color = SoftGrayText, fontSize = 11.sp)
                    OutlinedTextField(
                        value = tradeProofPath,
                        onValueChange = { tradeProofPath = it },
                        modifier = Modifier.fillMaxWidth().testTag("trade_proof_input"),
                        label = { Text("Proof filepath or transaction key", fontSize = 11.sp) },
                        placeholder = { Text("e.g. file_root_grand_tx_screenshot_998.jpg", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyanInfo,
                            unfocusedBorderColor = ElevatedSlate
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val path = tradeProofPath.trim()
                        viewModel.uploadTradeProof(targetRoom, path)
                        showTradeProofDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanInfo)
                ) {
                    Text("Upload", color = DeepSlateBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTradeProofDialog = null }) {
                    Text("Cancel", color = SoftGrayText, fontSize = 11.sp)
                }
            },
            containerColor = DeepSlateBg,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Direct active chat dialog inside navigation workspace
    if (activeChatKey != null) {
        val (lstId, otherId) = activeChatKey!!
        val convMessages = messages.filter {
            it.listingId == lstId &&
            ((it.senderId == "me" && it.receiverId == otherId) || (it.senderId == otherId && it.receiverId == "me"))
        }
        val matchingListing = listings.find { it.id == lstId } ?: MarketListing(
            title = activeChatTitle,
            category = "Item",
            subType = "Negotiation",
            statePrice = 0L,
            askingPrice = 0L,
            sellerId = otherId,
            sellerName = activeChatOtherName
        )
        ChatDialog(
            listing = matchingListing,
            viewModel = viewModel,
            conversationMessages = convMessages,
            otherPartyName = activeChatOtherName
        ) {
            activeChatKey = null
        }
    }
}

// ----------------------------------------------------
// ANCHORED REAL-TIME LISTING CHAT COMPONENT
// ----------------------------------------------------

@Composable
fun ListingChat(
    listingId: Int,
    viewModel: MarketViewModel,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    val listings by viewModel.allListings.collectAsState()
    val messages by viewModel.allChatMessages.collectAsState()
    val scope = rememberCoroutineScope()

    val listing = remember(listings, listingId) {
        listings.find { it.id == listingId } ?: MarketListing(
            title = "Unknown Unit/Contract",
            category = "General",
            subType = "Nego-Deal",
            statePrice = 0L,
            askingPrice = 150_000L,
            sellerId = "simulated_user",
            sellerName = "Roman_Vercetti"
        )
    }

    // Is the current user the seller of this listing?
    val isMeSeller = listing.sellerId == "me"

    // Filter messages belonging to this listing ID
    val conversationsForThisListing = remember(messages, listingId) {
        messages.filter { it.listingId == listingId }
    }

    // Determine the peer we are talking to
    val otherPartyId = remember(conversationsForThisListing, listing) {
        if (isMeSeller) {
            // As seller, find guest messages
            conversationsForThisListing.find { it.senderId != "me" }?.senderId 
                ?: conversationsForThisListing.find { it.receiverId != "me" }?.receiverId 
                ?: "u3" // fallback mock buyer ID
        } else {
            listing.sellerId
        }
    }

    val otherPartyName = remember(conversationsForThisListing, listing, otherPartyId) {
        if (isMeSeller) {
            conversationsForThisListing.find { it.senderId == otherPartyId }?.senderName
                ?: conversationsForThisListing.find { it.receiverId == otherPartyId }?.receiverName
                ?: "Roman_Vercetti"
        } else {
            listing.sellerName
        }
    }

    // Final filtered conversation between "me" and this specific peer on this listing
    val conversationMessages = remember(conversationsForThisListing, otherPartyId) {
        conversationsForThisListing.filter {
            (it.senderId == "me" && it.receiverId == otherPartyId) ||
            (it.senderId == otherPartyId && it.receiverId == "me")
        }.sortedBy { it.timestamp }
    }

    var rawText by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    // Automatically scroll to the latest message when it is added
    LaunchedEffect(conversationMessages.size) {
        if (conversationMessages.isNotEmpty()) {
            lazyListState.animateScrollToItem(conversationMessages.size - 1)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, ElevatedSlate), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DeepSlateBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Channel Context Header Block
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSlateBg)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(ElevatedSlate, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            otherPartyName.take(2).uppercase(),
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            otherPartyName,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF10B981), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "NEGOTIATION CHANNEL • RE ID #${listingId}",
                                color = SoftGrayText,
                                fontSize = 8.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Manual trigger to force-simulate a message reply instantly in real-time
                    Button(
                        onClick = {
                            val msgTemplate = if (isMeSeller) {
                                listOf(
                                    "Is the vehicle in verified stock? Can I buy immediately?",
                                    "I am interested. Can on-site inspection is scheduled?",
                                    "Would you trade for business keys or cash deal?",
                                    "Let's wire RCD or in-game money inside the bank."
                                ).random()
                            } else {
                                listOf(
                                    "Hey, got your offer. My asking price is negotiable.",
                                    "Are we meeting in front of Grand Bank vault?",
                                    "No lowballing please, we have administrative validation logs.",
                                    "I can agree with that price. Let's create an escrow ticket."
                                ).random()
                            }
                            
                            // Insert a simulated real-time response directly into database
                            viewModel.simulateChatMessage(
                                listingId = listingId,
                                listingTitle = listing.title,
                                senderId = otherPartyId,
                                senderName = otherPartyName,
                                messageText = msgTemplate
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("listing_chat_simulate_reply_button")
                    ) {
                        Text(
                            "🤖 SIMULATE",
                            color = GoldAccent,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (onDismiss != null) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .minimumInteractiveComponentSize()
                        ) {
                            Text(
                                "✕",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Sub-context listing label
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepSlateBg)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    "Topic item: ${listing.title} • Asking: ${formatCurrency(listing.askingPrice)}",
                    color = GoldAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Divider(color = ElevatedSlate)

            // Scrollable Message History Area
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("listing_chat_messages_list")
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (conversationMessages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💬", fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No chat messages yet.",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Ask sellers directly or make in-character RP offers below!",
                                    color = SoftGrayText,
                                    fontSize = 9.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(conversationMessages) { msg ->
                        val isMe = msg.senderId == "me"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            if (!isMe) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(ElevatedSlate, CircleShape)
                                        .align(Alignment.Bottom),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        otherPartyName.take(1).uppercase(),
                                        color = GoldAccent,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 10.dp,
                                            topEnd = 10.dp,
                                            bottomStart = if (isMe) 10.dp else 2.dp,
                                            bottomEnd = if (isMe) 2.dp else 10.dp
                                        )
                                    )
                                    .background(if (isMe) GoldAccent else ElevatedSlate)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .widthIn(max = 220.dp)
                            ) {
                                Column {
                                    Text(
                                        msg.message,
                                        color = if (isMe) Color.Black else Color.White,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(msg.timestamp)),
                                        color = if (isMe) Color.DarkGray else MutedText,
                                        fontSize = 7.sp,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Divider(color = ElevatedSlate)

            // Text Input Field & Send Action Control
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSlateBg)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("listing_chat_input_text")
                        .minimumInteractiveComponentSize(),
                    placeholder = { Text("Ask seller / Negotiate price...", color = MutedText, fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = ElevatedSlate,
                        focusedContainerColor = DeepSlateBg,
                        unfocusedContainerColor = DeepSlateBg
                    ),
                    shape = RoundedCornerShape(24.dp)
                )

                IconButton(
                    onClick = {
                        if (rawText.isNotBlank()) {
                            // Anchor sender details to peer
                            val activeListing = listing.copy(sellerId = otherPartyId, sellerName = otherPartyName)
                            viewModel.sendChatMessage(activeListing, rawText)
                            rawText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(GoldAccent, CircleShape)
                        .testTag("listing_chat_send_button")
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send Message",
                        tint = DeepSlateBg,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// POPUP DIALOG CONVERSATION PANEL (BUYER - SELLER)
// ----------------------------------------------------

@Composable
fun ChatDialog(
    listing: MarketListing,
    viewModel: MarketViewModel,
    conversationMessages: List<ChatMessage>,
    otherPartyName: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        ListingChat(
            listingId = listing.id,
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp),
            onDismiss = onDismiss
        )
    }
}

// ----------------------------------------------------
// SCAMMER SHIELD CENTER & SYSTEM BADGE
// ----------------------------------------------------

@Composable
fun UserScamBadge(username: String, viewModel: MarketViewModel) {
    val reports by viewModel.scammerReports.collectAsState()
    val match = remember(reports, username) {
        reports.find { it.reportedUsername.equals(username, ignoreCase = true) }
    }

    if (match != null) {
        val (badgeBg, badgeText, badgeLabel) = when (match.status) {
            "CONFIRMED_SCAMMER" -> Triple(Color(0xFF2D1616), Color(0xFFEF4444), "🚨 CONFIRMED SCAMMER")
            "UNDER_INVESTIGATION" -> Triple(Color(0xFF2B2014), Color(0xFFF59E0B), "🛡️ UNDER AUDIT")
            else -> Triple(Color(0xFF141C2B), Color(0xFF3B82F6), "⚠ SCAM REPORTED")
        }

        Surface(
            color = badgeBg,
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, badgeText),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(badgeLabel, color = badgeText, fontSize = 7.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ScammerShieldView(viewModel: MarketViewModel) {
    val reports by viewModel.scammerReports.collectAsState()
    val myProfile by viewModel.userProfile.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Database, 1: Submit Form, 2: Control Hub
    var searchFilter by remember { mutableStateOf("") }

    // Form inputs
    var reportedNameInput by remember { mutableStateOf("") }
    var reasonSelection by remember { mutableStateOf("Fake Payments") }
    var descInput by remember { mutableStateOf("") }
    var txInput by remember { mutableStateOf("") }
    var proofInput by remember { mutableStateOf("") }

    // Mock category chips
    val reasons = listOf("Fake Payments", "Fake Listings", "Fake Properties", "Fake Screenshots", "Impersonation", "Suspicious Activity")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Mini Banner Info
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1510)),
            border = BorderStroke(1.dp, Color(0xFFE2B93B))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🛡️", fontSize = 24.sp)
                Column {
                    Text(
                        "GLOBAL RP SECURITY ESCROW & CODES",
                        color = Color(0xFFE2B93B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        "Protect yourself from screenshot forgeries, spoofed bank logs, and fake business deals.",
                        color = Color.White,
                        fontSize = 9.sp
                    )
                }
            }
        }

        // SubTabs Row
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = CardSlateBg,
            contentColor = GoldAccent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                Text("Database Logs", modifier = Modifier.padding(8.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                Text("File New Case", modifier = Modifier.padding(8.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                Text("Staff Guard Panel", modifier = Modifier.padding(8.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeTab == 0) {
            // DATABASE VIEW
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchFilter,
                    onValueChange = { searchFilter = it },
                    placeholder = { Text("Search reported username...", color = SoftGrayText, fontSize = 11.sp) },
                    modifier = Modifier.weight(1f).testTag("scammer_search_input"),
                    textStyle = TextStyle(color = Color.White, fontSize = 11.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ElevatedSlate,
                        unfocusedContainerColor = CardSlateBg,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }

            val filteredReports = remember(reports, searchFilter) {
                if (searchFilter.isBlank()) reports else {
                    reports.filter { it.reportedUsername.contains(searchFilter, ignoreCase = true) }
                }
            }

            if (filteredReports.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No reports matching query. Database is secure. 🏖️", color = SoftGrayText, fontSize = 11.sp)
                }
            } else {
                for (report in filteredReports) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                        border = BorderStroke(0.5.dp, ElevatedSlate)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(report.reportedUsername, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    UserScamBadge(report.reportedUsername, viewModel)
                                }
                                Text("ID: ${report.id}", color = SoftGrayText, fontSize = 9.sp)
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Category: ${report.reason}", color = Color(0xFFE2B93B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(report.description, color = Color.White, fontSize = 10.sp)

                            if (report.transactionId.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Attached Tx ID: ${report.transactionId}", color = Color.Gray, fontSize = 9.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = ElevatedSlate.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Reporter: ${report.reporterName}", color = SoftGrayText, fontSize = 9.sp)
                                if (report.staffNotes.isNotBlank()) {
                                    Text("Staff Audit Notes: ${report.staffNotes}", color = Color(0xFFF59E0B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else if (activeTab == 1) {
            // FILE NEW CASE FORM
            Text("REGISTER INVESTIGATION DESK CASE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = reportedNameInput,
                onValueChange = { reportedNameInput = it },
                label = { Text("Reported Username (Exact Roleplay Name)", fontSize = 10.sp) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).testTag("scam_form_user"),
                textStyle = TextStyle(color = Color.White, fontSize = 11.sp),
                singleLine = true
            )

            Text("Select Reason / Fraud Category:", color = SoftGrayText, fontSize = 10.sp, modifier = Modifier.padding(bottom = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (r in reasons) {
                    val selected = reasonSelection == r
                    Button(
                        onClick = { reasonSelection = r },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) Color(0xFFDC2626) else CardSlateBg,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(r, fontSize = 8.sp)
                    }
                }
            }

            OutlinedTextField(
                value = descInput,
                onValueChange = { descInput = it },
                label = { Text("What happened? Detail the fraud technique precisely.", fontSize = 10.sp) },
                modifier = Modifier.fillMaxWidth().height(100.dp).padding(bottom = 8.dp).testTag("scam_form_desc"),
                textStyle = TextStyle(color = Color.White, fontSize = 11.sp)
            )

            OutlinedTextField(
                value = txInput,
                onValueChange = { txInput = it },
                label = { Text("Transaction ID / Escrow Room ID (Optional)", fontSize = 10.sp) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textStyle = TextStyle(color = Color.White, fontSize = 11.sp),
                singleLine = true
            )

            OutlinedTextField(
                value = proofInput,
                onValueChange = { proofInput = it },
                label = { Text("Evidence Screenshots image paths (Optional)", fontSize = 10.sp) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                textStyle = TextStyle(color = Color.White, fontSize = 11.sp),
                singleLine = true
            )

            Button(
                onClick = {
                    if (reportedNameInput.isBlank() || descInput.isBlank()) {
                        viewModel.alertMessage.value = "Username and Description are mandatory!"
                    } else {
                        viewModel.submitScammerReport(
                            reportedNameInput,
                            reasonSelection,
                            descInput,
                            proofInput,
                            txInput
                        )
                        reportedNameInput = ""
                        descInput = ""
                        txInput = ""
                        proofInput = ""
                        viewModel.alertMessage.value = "DISPATCH SUCCESS: Case logged into global audit database!"
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("scam_form_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
                Text("SUBMIT COMPLAINT TO BLACKLIST AGENTS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            // STAFF SECURE MODERATION HUB
            Text("🔒 STAFF SECURE DECISION HUB", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Review reported cases. Confirm blacklists or adjust case statuses directly.", color = SoftGrayText, fontSize = 9.sp)
            Spacer(modifier = Modifier.height(10.dp))

            if (reports.isEmpty()) {
                Text("No cases found in DB.", color = Color.Gray, fontSize = 11.sp)
            } else {
                for (report in reports) {
                    var staffNoteInput by remember(report.id) { mutableStateOf(report.staffNotes) }

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                        border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("CASE ID #${report.id}: Against ${report.reportedUsername}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("Reason: ${report.reason} | Current: [${report.status}]", color = GoldAccent, fontSize = 9.sp)
                            Text(report.description, color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(vertical = 4.dp))

                            TextField(
                                value = staffNoteInput,
                                onValueChange = { staffNoteInput = it },
                                placeholder = { Text("Enter staff audit notes...", fontSize = 10.sp, color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                textStyle = TextStyle(color = Color.White, fontSize = 10.sp),
                                colors = TextFieldDefaults.colors(focusedContainerColor = ElevatedSlate, unfocusedContainerColor = ElevatedSlate)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.updateScammerReportStatus(report, "CONFIRMED_SCAMMER", staffNoteInput) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("LOCK RED FLAG", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.updateScammerReportStatus(report, "UNDER_INVESTIGATION", staffNoteInput) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("INVESTIGATING", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.updateScammerReportStatus(report, "DISMISSED", staffNoteInput) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("DISMISS CASE", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// BOUNTY BOARD & HUNTER CODES
// ----------------------------------------------------

@Composable
fun BountyBoardView(viewModel: MarketViewModel) {
    val bounties by viewModel.bounties.collectAsState()
    val claims by viewModel.bountyClaims.collectAsState()
    val meProfile by viewModel.userProfile.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Available Bounties, 1: Sponsor Form, 2: Claims Review
    var bountyCategoryFilter by remember { mutableStateOf("ALL") }

    val bountyTypes = listOf("ALL", "Vehicle Wanted", "Business Wanted", "Property Wanted", "Scam Investigation", "Missing Owner Search")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Stats Dashboard
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg)
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("ACTIVE BOUNTIES ON BOARD", color = SoftGrayText, fontSize = 8.sp)
                    Text("${bounties.count { it.status == "ACTIVE" }} Active Targets", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("YOUR KAT COINS", color = SoftGrayText, fontSize = 8.sp)
                    Text("🪙 ${meProfile?.coinBalance ?: 0}", color = GoldAccent, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            }
        }

        // Section Tabs
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = CardSlateBg,
            contentColor = GoldAccent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                Text("Board Targets", modifier = Modifier.padding(8.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                Text("Sponsor Hunt", modifier = Modifier.padding(8.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                Text("Inspect Claims", modifier = Modifier.padding(8.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeTab == 0) {
            // Target categories
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (cat in bountyTypes) {
                    val isSelected = bountyCategoryFilter == cat
                    Button(
                        onClick = { bountyCategoryFilter = cat },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) GoldAccent else ElevatedSlate,
                            contentColor = if (isSelected) DeepSlateBg else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(cat, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            val filteredBounties = remember(bounties, bountyCategoryFilter) {
                if (bountyCategoryFilter == "ALL") bounties else {
                    bounties.filter { it.bountyType.equals(bountyCategoryFilter, ignoreCase = true) }
                }
            }

            if (filteredBounties.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No bounties open. Sponsor a customized search target above!", color = SoftGrayText, fontSize = 11.sp)
                }
            } else {
                for (bounty in filteredBounties) {
                    var bountyFormOpen by remember { mutableStateOf(false) }
                    var claimDetails by remember { mutableStateOf("") }
                    var claimEvidence by remember { mutableStateOf("") }
                    var claimProofFile by remember { mutableStateOf("") }

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                        border = BorderStroke(1.dp, if (bounty.status == "ACTIVE") GoldAccent.copy(alpha = 0.3f) else ElevatedSlate)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎯 [${bounty.bountyType}]", color = GoldAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (bounty.status == "ACTIVE") {
                                        Box(modifier = Modifier.size(6.dp).background(Color.Green, CircleShape))
                                    } else {
                                        Text("[COMPLETED]", color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text("Limit: ${bounty.expirationDate}", color = SoftGrayText, fontSize = 8.sp)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(bounty.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(bounty.description, color = Color.White, fontSize = 10.sp)

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Sponsor: ${bounty.creatorName}", color = SoftGrayText, fontSize = 9.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("REWARD: ", color = SoftGrayText, fontSize = 9.sp)
                                    Text(
                                        when (bounty.rewardType) {
                                            "COINS" -> "🪙 ${bounty.rewardAmount} Coins"
                                            "CRATE" -> "📦 ${bounty.rewardAmount} Gold Crates"
                                            "VIP_DAYS" -> "👑 ${bounty.rewardAmount} Days premium"
                                            else -> "🏅 Elite Emblem"
                                        },
                                        color = GoldAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (bounty.status == "ACTIVE") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(color = ElevatedSlate.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(6.dp))

                                if (!bountyFormOpen) {
                                    Button(
                                        onClick = { bountyFormOpen = true },
                                        modifier = Modifier.fillMaxWidth().testTag("bounty_claim_trigger_" + bounty.id),
                                        colors = ButtonDefaults.buttonColors(containerColor = ElevatedSlate)
                                    ) {
                                        Text("SUBMIT INTEL & CLAIM REWARD", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Text("HUNTER EVIDENCE UPLOADER", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))

                                    OutlinedTextField(
                                        value = claimDetails,
                                        onValueChange = { claimDetails = it },
                                        placeholder = { Text("What information or findings did you discover?", fontSize = 9.sp) },
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp).testTag("bounty_claim_info"),
                                        textStyle = TextStyle(color = Color.White, fontSize = 10.sp)
                                    )

                                    OutlinedTextField(
                                        value = claimEvidence,
                                        onValueChange = { claimEvidence = it },
                                        placeholder = { Text("Imgur links, Discord chats, or details...", fontSize = 9.sp) },
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                        textStyle = TextStyle(color = Color.White, fontSize = 10.sp)
                                    )

                                    OutlinedTextField(
                                        value = claimProofFile,
                                        onValueChange = { claimProofFile = it },
                                        placeholder = { Text("Attached screenshot resource path...", fontSize = 9.sp) },
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                        textStyle = TextStyle(color = Color.White, fontSize = 10.sp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                if (claimDetails.isBlank()) {
                                                    viewModel.alertMessage.value = "Please detail your discovery!"
                                                } else {
                                                    viewModel.claimBounty(
                                                        bounty.id,
                                                        bounty.title,
                                                        claimEvidence,
                                                        claimDetails,
                                                        claimProofFile
                                                    )
                                                    bountyFormOpen = false
                                                    claimDetails = ""
                                                    claimEvidence = ""
                                                    claimProofFile = ""
                                                }
                                            },
                                            modifier = Modifier.weight(1f).testTag("bounty_claim_submit"),
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                                        ) {
                                            Text("COMMIT CLAIM", color = DeepSlateBg, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = { bountyFormOpen = false },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                                        ) {
                                            Text("CANCEL", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (activeTab == 1) {
            // SPONSOR HUNT FORM
            var bountyTitleInput by remember { mutableStateOf("") }
            var bountyDescInput by remember { mutableStateOf("") }
            var bountyRewardTypeChip by remember { mutableStateOf("COINS") }
            var bountyRewardVolume by remember { mutableStateOf("50") }
            var bountyTypeSelect by remember { mutableStateOf("Vehicle Wanted") }
            var bountyExpInput by remember { mutableStateOf("") }

            val rewardChips = listOf("COINS", "CRATE", "VIP_DAYS", "BADGE")
            val targetCategories = listOf("Vehicle Wanted", "Business Wanted", "Property Wanted", "Scam Investigation", "Missing Owner Search")

            Text("SPONSOR NEW BOUNTY TARGET ESCROW", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = bountyTitleInput,
                onValueChange = { bountyTitleInput = it },
                label = { Text("Bounty Title Target (Objective Summary)", fontSize = 10.sp) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).testTag("bounty_form_title"),
                textStyle = TextStyle(color = Color.White, fontSize = 11.sp),
                singleLine = true
            )

            OutlinedTextField(
                value = bountyDescInput,
                onValueChange = { bountyDescInput = it },
                label = { Text("Precise target description and instructions...", fontSize = 10.sp) },
                modifier = Modifier.fillMaxWidth().height(100.dp).padding(bottom = 8.dp).testTag("bounty_form_desc"),
                textStyle = TextStyle(color = Color.White, fontSize = 11.sp)
            )

            Text("Bounty Type Classification:", color = SoftGrayText, fontSize = 10.sp, modifier = Modifier.padding(bottom = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (cat in targetCategories) {
                    val isSelected = bountyTypeSelect == cat
                    Button(
                        onClick = { bountyTypeSelect = cat },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) GoldAccent else CardSlateBg,
                            contentColor = if (isSelected) DeepSlateBg else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(cat, fontSize = 8.sp)
                    }
                }
            }

            Text("Reward Type Backing Asset:", color = SoftGrayText, fontSize = 10.sp, modifier = Modifier.padding(bottom = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (rt in rewardChips) {
                    val isSelected = bountyRewardTypeChip == rt
                    Button(
                        onClick = { bountyRewardTypeChip = rt },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) GoldAccent else CardSlateBg,
                            contentColor = if (isSelected) DeepSlateBg else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(rt, fontSize = 8.sp)
                    }
                }
            }

            OutlinedTextField(
                value = bountyRewardVolume,
                onValueChange = { bountyRewardVolume = it },
                label = { Text("Reward Amount / Count (e.g. 50 COINS, 2 CRATES)", fontSize = 10.sp) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).testTag("bounty_form_reward"),
                textStyle = TextStyle(color = Color.White, fontSize = 11.sp),
                singleLine = true
            )

            OutlinedTextField(
                value = bountyExpInput,
                onValueChange = { bountyExpInput = it },
                label = { Text("Expiration Date (e.g. 29.06.2026)", fontSize = 10.sp) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                textStyle = TextStyle(color = Color.White, fontSize = 11.sp),
                singleLine = true
            )

            Button(
                onClick = {
                    val amount = bountyRewardVolume.toIntOrNull() ?: 0
                    if (bountyTitleInput.isBlank() || bountyDescInput.isBlank()) {
                        viewModel.alertMessage.value = "Title and Objective content cannot be blank!"
                    } else if (amount <= 0) {
                        viewModel.alertMessage.value = "Please enter a valid positive reward volume!"
                    } else {
                        viewModel.createBounty(
                            bountyTitleInput,
                            bountyDescInput,
                            bountyRewardTypeChip,
                            amount,
                            bountyTypeSelect,
                            bountyExpInput
                        )
                        bountyTitleInput = ""
                        bountyDescInput = ""
                        bountyExpInput = ""
                        viewModel.alertMessage.value = "BOUNTY FILED successfully on public boards!"
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("bounty_form_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("DEDUCT COLLATERAL & PUBLISH TARGET", color = DeepSlateBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            // CLAIMS MODERATOR INSPECT INTERFACE
            Text("🕵️ HUNTER CASE AUDIT BOARD", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Evaluate submitted visual proofs. Click approve to dispense backer escrow immediately.", color = SoftGrayText, fontSize = 9.sp)
            Spacer(modifier = Modifier.height(10.dp))

            if (claims.isEmpty()) {
                Text("No hunter submissions available for inspection yet.", color = Color.Gray, fontSize = 11.sp)
            } else {
                for (claim in claims) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                        border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("BOUNTY: ${claim.bountyTitle}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Surface(
                                    color = if (claim.status == "PENDING") Color(0xFF142B1A) else Color(0xFF2B1414),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                ) {
                                    Text(claim.status, color = if (claim.status == "PENDING") Color.Green else Color.Red, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 4.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Hunter Name: ${claim.claimantName}", color = GoldAccent, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Discovery Log Information:", color = SoftGrayText, fontSize = 9.sp)
                            Text(claim.information, color = Color.White, fontSize = 9.sp)

                            if (claim.evidence.isNotBlank()) {
                                Text("Discovery Link Proofs: ${claim.evidence}", color = Color.Cyan, fontSize = 9.sp)
                            }

                            if (claim.status == "PENDING") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(color = ElevatedSlate.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.moderateBountyClaim(claim, true) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.weight(1f).testTag("claim_moderate_approve_" + claim.id)
                                    ) {
                                        Text("APPROVE & DISBURSE", color = DeepSlateBg, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { viewModel.moderateBountyClaim(claim, false) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("REJECT PROOF", fontSize = 8.sp, fontWeight = FontWeight.Bold)
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
