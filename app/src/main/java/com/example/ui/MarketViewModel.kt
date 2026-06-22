package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.GeminiManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive

enum class MainSection {
    DASHBOARD,
    MARKETPLACE,
    AI_ADVISOR,
    PROFILE_FAMILY,
    BUSINESS_NETWORK,
    RCD_CENTER,
    ADVERTISING,
    INVENTORY,
    ADMINISTRATION,
    CHATS,
    SCAMMER_SHIELD,
    BOUNTY_SYSTEM
}

data class AiMessage(
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class MarketViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MarketRepository(application)

    // --- Core State Observables ---
    val userProfile: StateFlow<UserProfile?> = repository.userProfileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allListings: StateFlow<List<MarketListing>> = repository.allListingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBusinesses: StateFlow<List<RegisteredBusiness>> = repository.allBusinessesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFamilies: StateFlow<List<Family>> = repository.allFamiliesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRcdDeals: StateFlow<List<RcdDeal>> = repository.allRcdDealsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAds: StateFlow<List<Advertisement>> = repository.allAdsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<AppNotification>> = repository.notificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val richList: StateFlow<List<UserProfile>> = repository.richListFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLog>> = repository.auditLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCoinPurchases: StateFlow<List<CoinPurchaseRequest>> = repository.allCoinPurchasesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNetWorthVerifications: StateFlow<List<NetWorthVerification>> = repository.allNetWorthVerificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fixedDeposits: StateFlow<List<FixedDeposit>> = repository.getFixedDepositsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myInventory: StateFlow<List<InventoryItem>> = repository.getInventoryFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myVouches: StateFlow<List<UserVouch>> = repository.getVouchesFlow("me")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val negotiationOffers: StateFlow<List<NegotiationOffer>> = repository.negotiationOffersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tradeRooms: StateFlow<List<TradeRoom>> = repository.tradeRoomsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scammerReports: StateFlow<List<ScammerReport>> = repository.allScammerReportsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bounties: StateFlow<List<Bounty>> = repository.allBountiesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bountyClaims: StateFlow<List<BountyClaim>> = repository.allBountyClaimsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- UI Navigation and Filtering State ---
    var currentSection = MutableStateFlow(MainSection.DASHBOARD)
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow("ALL") // "ALL", "Vehicle", "Property", "Business", "Skin", "Item"
    val showOnlyVerifiedSellers = MutableStateFlow(false)
    val showOnlyFeatured = MutableStateFlow(false)

    // Detailed Item Selection
    val selectedListing = MutableStateFlow<MarketListing?>(null)

    // --- AI Chat History State ---
    val aiChatHistory = MutableStateFlow<List<AiMessage>>(listOf(
        AiMessage("ai", "Hello Grand Mobile RP investor! I am your KAT_MARKET_NIKA AI Advisor. Ask me to:\n• 'Check price for BMW M5'\n• 'Should I buy a Gas Station?'\n• 'Generate a description for my high-end house!'")
    ))
    val isAiLoading = MutableStateFlow(false)

    // --- UI Info/Error Alerts ---
    val alertMessage = MutableStateFlow<String?>(null)

    // --- Google Login Session States ---
    private val appPrefs = getApplication<Application>().getSharedPreferences("kat_market_google_auth", android.content.Context.MODE_PRIVATE)
    val isLoggedIn = MutableStateFlow(appPrefs.getBoolean("is_logged_in", false))
    val loggedInEmail = MutableStateFlow(appPrefs.getString("logged_in_email", ""))

    fun loginWithGoogle(email: String, username: String, templateId: String? = null) {
        viewModelScope.launch {
            appPrefs.edit()
                .putBoolean("is_logged_in", true)
                .putString("logged_in_email", email)
                .putString("logged_in_username", username)
                .apply()

            // Synchronize the DB "me" profile based on the selected Google Sign-In identity
            val defaultMe = repository.getMeProfile()

            if (templateId != null) {
                val dbTemplate = repository.getProfileById(templateId)
                if (dbTemplate != null) {
                    repository.saveProfile(dbTemplate.copy(id = "me", username = username))
                }
            } else {
                // Custom user login
                repository.saveProfile(
                    UserProfile(
                        id = "me",
                        username = username,
                        role = "Normal User",
                        reputation = 100,
                        walletBalance = 5000000L,
                        bankBalance = 15000000L,
                        coinBalance = 500,
                        title = "Custom Google Trader"
                    )
                )
            }

            isLoggedIn.value = true
            loggedInEmail.value = email
            
            repository.addNotification("Google Sign-In Connected", "Logged in securely via $email.", "REWARD")
            repository.insertAuditLog(AuditLog(username, "Google Sign-In", "Successfully authenticated via $email account."))
            alertMessage.value = "Welcome back, $username! Connected via Google."
        }
    }

    fun logoutGoogle() {
        viewModelScope.launch {
            val username = appPrefs.getString("logged_in_username", "User")
            appPrefs.edit().clear().apply()
            
            isLoggedIn.value = false
            loggedInEmail.value = ""
            
            // Reset "me" profile back to seeded default on next login or default NIKA_BOSS_RP
            repository.saveProfile(
                UserProfile(
                    id = "me",
                    username = "NIKA_BOSS_RP",
                    role = "Normal User",
                    reputation = 94,
                    walletBalance = 125000000L,
                    bankBalance = 450000000L,
                    coinBalance = 2450,
                    familyName = "Nika Syndicate",
                    title = "Elite Trader",
                    avatarIndex = 3,
                    completedDeals = 45,
                    vouchesCount = 18
                )
            )
            
            selectedListing.value = null
            currentSection.value = MainSection.DASHBOARD
            alertMessage.value = "Successfully logged out from $username Google Session."
        }
    }

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfNeeded()
            startRealTimeFeedSimulator()
        }
    }

    // --- General Filtered List ---
    val filteredListings: Flow<List<MarketListing>> = combine(
        allListings,
        searchQuery,
        selectedCategoryFilter,
        showOnlyVerifiedSellers,
        showOnlyFeatured
    ) { listings, query, cat, verifiedOnly, featuredOnly ->
        listings.filter { item ->
            // Filter by Category
            val matchesCat = if (cat == "ALL") true else item.category.equals(cat, ignoreCase = true)
            // Filter by query (Search Everywhere)
            val matchesQuery = if (query.isBlank()) true else {
                item.title.contains(query, ignoreCase = true) ||
                item.subType.contains(query, ignoreCase = true) ||
                item.location.contains(query, ignoreCase = true) ||
                item.licensePlate.contains(query, ignoreCase = true) ||
                item.notes.contains(query, ignoreCase = true)
            }
            // Filter by verification status
            val matchesVer = if (verifiedOnly) item.isVerifiedSeller else true
            // Filter by Featured status
            val matchesFeat = if (featuredOnly) item.isFeatured else true
            
            matchesCat && matchesQuery && matchesVer && matchesFeat && item.status == "ACTIVE"
        }.sortedByDescending { it.images.isNotEmpty() }
    }

    // --- Form action implementations ---
    fun selectSection(section: MainSection) {
        currentSection.value = section
    }

    // Wallet & Banking transactions
    fun depositAmount(amount: Long) {
        viewModelScope.launch {
            val ok = repository.depositMoney(amount)
            if (!ok) {
                alertMessage.value = "Insufficient pocket wallet money!"
            }
        }
    }

    fun withdrawAmount(amount: Long) {
        viewModelScope.launch {
            val ok = repository.withdrawMoney(amount)
            if (!ok) {
                alertMessage.value = "Insufficient bank account deposit reserves!"
            }
        }
    }

    fun transferToPeer(amount: Long, recipientName: String) {
        viewModelScope.launch {
            val ok = repository.transferMoney(amount, recipientName)
            if (!ok) {
                alertMessage.value = "Transfer failed. Check bank reserves or secure names."
            } else {
                alertMessage.value = "Successfully securely transferred ${amount} to $recipientName."
            }
        }
    }

    fun createSavingsFD(amount: Long, duration: Int, interest: Int) {
        viewModelScope.launch {
            val ok = repository.createFixedDeposit(amount, duration, interest)
            if (!ok) {
                alertMessage.value = "Insufficient bank reserves!"
            }
        }
    }

    fun redeemMaturedFD(fd: FixedDeposit) {
        viewModelScope.launch {
            repository.claimMaturedDeposit(fd)
        }
    }

    // Buy / Sell Listings
    fun buyActiveListing(listing: MarketListing) {
        viewModelScope.launch {
            val ok = repository.purchaseListing(listing)
            if (ok) {
                selectedListing.value = null
                alertMessage.value = "Successfully purchased '${listing.title}'! Clean license transferred."
            } else {
                alertMessage.value = "Insufficient funds in bank account!"
            }
        }
    }

    val allChatMessages: StateFlow<List<ChatMessage>> = repository.allChatMessagesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createMarketListing(
        title: String,
        category: String,
        subType: String,
        statePrice: Long,
        askingPrice: Long,
        owners: Int,
        location: String,
        licensePlate: String,
        dailyProfit: Long,
        notes: String,
        isFeatured: Boolean,
        isUrgent: Boolean,
        images: List<String> = emptyList(),
        videoUrl: String = "",
        watermarked: Boolean = false
    ) {
        viewModelScope.launch {
            val listing = MarketListing(
                title = title,
                category = category,
                subType = subType,
                statePrice = statePrice,
                askingPrice = askingPrice,
                ownerCount = owners,
                location = location,
                licensePlate = licensePlate,
                profitDaily = dailyProfit,
                notes = notes,
                isFeatured = isFeatured,
                isUrgent = isUrgent,
                sellerId = "me",
                sellerName = "",
                images = images,
                videoUrl = videoUrl,
                watermarked = watermarked,
                mediaStatus = "APPROVED"
            )
            repository.createNewListing(listing)
            alertMessage.value = "Successfully published list item ID #${kotlin.random.Random.nextInt(89999) + 10000} with gallery media assets."
        }
    }

    fun sendChatMessage(listing: MarketListing, messageText: String) {
        if (messageText.isBlank()) return
        viewModelScope.launch {
            repository.sendChatMessage(
                listingId = listing.id,
                listingTitle = listing.title,
                receiverId = listing.sellerId,
                receiverName = listing.sellerName,
                text = messageText
            )
            // Simulates automated real-time RP negotiation replies from mock Sellers
            if (listing.sellerId != "me") {
                kotlinx.coroutines.delay(1800)
                val responses = listOf(
                    "Hey, received your offer for '${listing.title}'. The price is mostly firm, but I can do a small discount.",
                    "Is that deal instant cash? Let's meet at the Grand center bank vault to complete it.",
                    "I can do ${formatCurrencySimulated(listing.askingPrice - (listing.askingPrice/15))} minimum limit, anything lower is lowballing.",
                    "Sounds fair enough, let's close this via a secure escrow RCD deal. Are you online?",
                    "Hello! Yes, the vehicle license details are verified as shown on screenshot. Let's trade.",
                    "Can we hold on for 15 mins? I am in a family clan event right now."
                )
                val reply = responses.random()
                repository.insertReceivedChatMessage(
                     ChatMessage(
                         listingId = listing.id,
                         listingTitle = listing.title,
                         senderId = listing.sellerId,
                         senderName = listing.sellerName,
                         receiverId = "me",
                         receiverName = "NIKA_BOSS_RP",
                         message = reply,
                         timestamp = System.currentTimeMillis()
                     )
                )
            }
        }
    }

    private fun formatCurrencySimulated(amount: Long): String {
        return if (amount >= 1_000_000_000L) {
            String.format("%.1fB", amount.toDouble() / 1_000_000_000L)
        } else if (amount >= 1_000_000L) {
            String.format("%.1fM", amount.toDouble() / 1_000_000L)
        } else {
            amount.toString()
        }
    }

    fun simulateIncomingBuyerMessage(listing: MarketListing) {
        viewModelScope.launch {
            val buyerUsernames = listOf("Roman_Vercetti", "Aditya_Grand", "Alisa_Petrova")
            val selectedBuyer = buyerUsernames.filter { it != listing.sellerName }.randomOrNull() ?: "Roman_Vercetti"
            val textResponses = listOf(
                "Hey! Is your '${listing.title}' still available for sale?",
                "Saw your board listing. What is the lowest price you'd take for a quick deal?",
                "Let's trade! I have the cash ready to wire from my family bank right now.",
                "Can you show me the license sheet? Let's talk business."
            )
            repository.insertReceivedChatMessage(
                 ChatMessage(
                     listingId = listing.id,
                     listingTitle = listing.title,
                     senderId = "u3", // Mock sender ID
                     senderName = selectedBuyer,
                     receiverId = "me",
                     receiverName = "NIKA_BOSS_RP",
                     message = textResponses.random(),
                     timestamp = System.currentTimeMillis()
                 )
            )
        }
    }

    fun simulateChatMessage(listingId: Int, listingTitle: String, senderId: String, senderName: String, messageText: String) {
        viewModelScope.launch {
            repository.insertReceivedChatMessage(
                ChatMessage(
                    listingId = listingId,
                    listingTitle = listingTitle,
                    senderId = senderId,
                    senderName = senderName,
                    receiverId = "me",
                    receiverName = "NIKA_BOSS_RP",
                    message = messageText,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateListingMediaStatus(listing: MarketListing, newStatus: String) {
        viewModelScope.launch {
            repository.updateListingMediaStatus(listing, newStatus)
        }
    }

    fun updateListingWatermark(listing: MarketListing, watermark: Boolean) {
        viewModelScope.launch {
            repository.updateListingWatermark(listing, watermark)
        }
    }

    fun deleteList(listing: MarketListing) {
        viewModelScope.launch {
            repository.deleteOrArchiveListing(listing)
        }
    }

    // AI advisor chatbot flow
    fun sendAiChat(message: String) {
        if (message.isBlank()) return
        val userMsg = AiMessage("user", message)
        aiChatHistory.value = aiChatHistory.value + userMsg
        isAiLoading.value = true

        viewModelScope.launch {
            val instruction = "You are the premium KAT_MARKET_NIKA assistant, an expert marketplace adviser for Grand Mobile RP players. Keep answers very direct, helpful, styled beautifully with structure, and centered on asset valuation, daily profit, payout calculations, listing pitches, or general economy queries."
            val response = GeminiManager.generateResponse(message, instruction)
            aiChatHistory.value = aiChatHistory.value + AiMessage("ai", response)
            isAiLoading.value = false
        }
    }

    // Crate openings
    fun buyCrate(name: String, keyType: String, priceCoins: Int) {
        viewModelScope.launch {
            val ok = repository.buyCrateFromStore(name, keyType, priceCoins)
            if (!ok) {
                alertMessage.value = "Insufficient Kat coins! Refill in bottom Supporter terminal."
            }
        }
    }

    fun openCrate(item: InventoryItem) {
        viewModelScope.launch {
            val rewardMsg = repository.openCrateFromInventory(item)
            alertMessage.value = rewardMsg
        }
    }

    // RCD Real Money Trade flow
    fun submitRcdDeals(listing: MarketListing, paymentMethod: String, realPrice: Double, notes: String) {
        viewModelScope.launch {
            repository.submitRcdDeal(listing, paymentMethod, realPrice, notes)
            alertMessage.value = "RCD deal request created! Pending staff audits. Upload confirmation screenshots."
        }
    }

    fun submitAd(title: String, description: String, category: String, promoType: String, costCoins: Int) {
        viewModelScope.launch {
            val ok = repository.purchaseAd(title, description, category, promoType, costCoins)
            if (!ok) {
                alertMessage.value = "Insufficient Kat Coins to finance this promotional ad!"
            } else {
                alertMessage.value = "Ad campaign successfully queued for moderation audits."
            }
        }
    }

    fun createMarriage(partner: String) {
        viewModelScope.launch {
            val ok = repository.proposeMarriage(partner)
            if (ok) {
                alertMessage.value = "Marriage certified! Partner name displayed on couple card."
            }
        }
    }

    fun createFamily(familyName: String, costCoins: Int) {
        viewModelScope.launch {
            val ok = repository.createFamily(familyName, costCoins)
            if (!ok) {
                alertMessage.value = "Insufficient coins to establish a new family!"
            }
        }
    }

    fun addVouch(targetUserId: String, text: String, rating: Int) {
        viewModelScope.launch {
            repository.addVouch(targetUserId, text, rating)
        }
    }

    fun fundFamilyBank(family: Family, amount: Long) {
        viewModelScope.launch {
            val ok = repository.depositToFamilyBank(family, amount)
            if (!ok) {
                alertMessage.value = "Insufficient bank reserves!"
            }
        }
    }

    fun buyVipPlan(days: Int, priceCoins: Int) {
        viewModelScope.launch {
            val ok = repository.buyVip(days, priceCoins)
            if (!ok) {
                alertMessage.value = "Insufficient Kat coins for VIP activation!"
            }
        }
    }

    fun buyCoinsViaSupporter(amount: Int, costFiat: Double) {
        viewModelScope.launch {
            repository.buyCoins(amount, costFiat)
        }
    }

    fun createCoinPurchaseRequest(packageId: String, packageName: String, coins: Int, priceInr: Double, upiTxId: String, screenshotPath: String) {
        viewModelScope.launch {
            repository.createCoinPurchaseRequest(packageId, packageName, coins, priceInr, upiTxId, screenshotPath)
        }
    }

    fun processCoinPurchaseRequest(request: CoinPurchaseRequest, newStatus: String, feedback: String = "") {
        viewModelScope.launch {
            repository.processCoinPurchaseRequest(request, newStatus, feedback)
        }
    }

    // --- Net Worth Verifications ---
    fun submitNetWorthRequest(
        bankBalance: Long,
        bankScreenshot: String,
        vehiclesJson: String,
        propertiesJson: String,
        businessesJson: String
    ) {
        viewModelScope.launch {
            repository.submitNetWorthVerification(
                bankBalance,
                bankScreenshot,
                vehiclesJson,
                propertiesJson,
                businessesJson
            )
        }
    }

    fun processNetWorthRequest(request: NetWorthVerification, status: String, feedback: String = "") {
        viewModelScope.launch {
            repository.processNetWorthVerificationRequest(request, status, feedback)
        }
    }

    // Staff actions & Moderation panel
    fun processStaffRcdDeal(deal: RcdDeal, newStatus: String) {
        viewModelScope.launch {
            repository.updateRcdDealStatus(deal, newStatus)
        }
    }

    fun verifySelf(verified: Boolean) {
        viewModelScope.launch {
            repository.updateMeVerificationStatus(verified)
        }
    }

    fun awardAdminCoins(coins: Int) {
        viewModelScope.launch {
            repository.rewardMeWithCoins(coins)
        }
    }

    // Owner administrative operations
    fun triggerFactoryCleanup() {
        viewModelScope.launch {
            repository.fullOwnerDatabaseCleanup()
            alertMessage.value = "Owner Panel executed full system wipe. All logs seeded cleanly."
        }
    }

    fun clearAlert() {
        alertMessage.value = null
    }

    // --- Negotiation & Make Offer Actions ---
    fun submitOffer(listing: MarketListing, amount: Long, message: String) {
        viewModelScope.launch {
            val me = repository.getMeProfile()
            val offer = NegotiationOffer(
                listingId = listing.id,
                listingTitle = listing.title,
                buyerId = "me",
                buyerName = me.username,
                sellerId = listing.sellerId,
                sellerName = listing.sellerName,
                amount = amount,
                message = message,
                status = "PENDING",
                isCreatedByBuyer = true
            )
            repository.insertNegotiationOffer(offer)
            
            repository.insertAuditLog(AuditLog(me.username, "Submit Negotiation Offer", "Offered \$${formatCurrencySimulated(amount)} for ${listing.title}."))

            // Create system message to initiate chat automatically
            repository.sendChatMessage(
                listingId = listing.id,
                listingTitle = listing.title,
                receiverId = listing.sellerId,
                receiverName = listing.sellerName,
                text = "💸 [OFFER SUBMITTED] User ${me.username} submitted an offer of \$${formatCurrencySimulated(amount)}. Message: \"$message\""
            )
            
            repository.addNotification("💸 Offer Submitted", "Your negotiation offer for ${listing.title} has been recorded.", "INFO")
        }
    }

    fun submitCounterOffer(offer: NegotiationOffer, counterAmount: Long, isByBuyer: Boolean) {
        viewModelScope.launch {
            val updated = offer.copy(
                status = "COUNTERED",
                counterAmount = counterAmount,
                isCreatedByBuyer = isByBuyer,
                timestamp = System.currentTimeMillis()
            )
            repository.updateNegotiationOffer(updated)

            val senderName = if (isByBuyer) offer.buyerName else offer.sellerName
            val receiverId = if (isByBuyer) offer.sellerId else offer.buyerId
            val receiverName = if (isByBuyer) offer.sellerName else offer.buyerName

            repository.insertAuditLog(AuditLog(senderName, "Counter Offer", "Proposed Counter-Offer: \$${formatCurrencySimulated(counterAmount)} for ${offer.listingTitle}."))

            repository.insertReceivedChatMessage(
                ChatMessage(
                    listingId = offer.listingId,
                    listingTitle = offer.listingTitle,
                    senderId = if (isByBuyer) "me" else receiverId,
                    senderName = senderName,
                    receiverId = if (isByBuyer) receiverId else "me",
                    receiverName = receiverName,
                    message = "💸 [COUNTER OFFER] Countered with an offer of \$${formatCurrencySimulated(counterAmount)}.",
                    timestamp = System.currentTimeMillis()
                )
            )
            
            repository.addNotification("💸 Counter Offer Sent", "Successfully proposed counter offer of \$${formatCurrencySimulated(counterAmount)}.", "INFO")
        }
    }

    fun acceptOffer(offer: NegotiationOffer) {
        viewModelScope.launch {
            val updated = offer.copy(status = "ACCEPTED")
            repository.updateNegotiationOffer(updated)

            repository.insertAuditLog(AuditLog("me", "Accept Negotiation Offer", "Accepted deal of \$${formatCurrencySimulated(offer.amount)} for ${offer.listingTitle}."))

            repository.insertReceivedChatMessage(
                ChatMessage(
                    listingId = offer.listingId,
                    listingTitle = offer.listingTitle,
                    senderId = offer.sellerId,
                    senderName = offer.sellerName,
                    receiverId = offer.buyerId,
                    receiverName = offer.buyerName,
                    message = "✅ [OFFER ACCEPTED] Offer of \$${formatCurrencySimulated(offer.amount)} was approved! Establishing secure Trade Room.",
                    timestamp = System.currentTimeMillis()
                )
            )

            // Create Trade Room in pending completion state
            val trade = TradeRoom(
                listingId = offer.listingId,
                listingTitle = offer.listingTitle,
                agreedPrice = offer.amount,
                buyerId = offer.buyerId,
                buyerName = offer.buyerName,
                sellerId = offer.sellerId,
                sellerName = offer.sellerName,
                status = "PENDING_COMPLETION"
            )
            repository.insertTradeRoom(trade)

            repository.addNotification("✅ Offer Approved!", "You accepted the offer for ${offer.listingTitle}. Trade Room opened.", "REWARD")
        }
    }

    fun declineOffer(offer: NegotiationOffer) {
        viewModelScope.launch {
            val updated = offer.copy(status = "DECLINED")
            repository.updateNegotiationOffer(updated)

            repository.insertAuditLog(AuditLog("me", "Decline Negotiation Offer", "Declined deal of \$${formatCurrencySimulated(offer.amount)} for ${offer.listingTitle}."))

            repository.insertReceivedChatMessage(
                ChatMessage(
                    listingId = offer.listingId,
                    listingTitle = offer.listingTitle,
                    senderId = offer.sellerId,
                    senderName = offer.sellerName,
                    receiverId = offer.buyerId,
                    receiverName = offer.buyerName,
                    message = "❌ [OFFER DECLINED] Offer of \$${formatCurrencySimulated(offer.amount)} was declined.",
                    timestamp = System.currentTimeMillis()
                )
            )

            repository.addNotification("❌ Offer Declined", "Declined offer of \$${formatCurrencySimulated(offer.amount)}.", "ALERT")
        }
    }

    fun markTradeCompleted(tradeRoom: TradeRoom) {
        viewModelScope.launch {
            val updated = tradeRoom.copy(status = "COMPLETED")
            repository.updateTradeRoom(updated)

            val meProfile = repository.getMeProfile()
            if (tradeRoom.buyerId == "me") {
                val currentWallet = meProfile.walletBalance
                val newWallet = (currentWallet - tradeRoom.agreedPrice).coerceAtLeast(0)
                repository.saveProfile(meProfile.copy(walletBalance = newWallet, completedDeals = meProfile.completedDeals + 1))
            } else if (tradeRoom.sellerId == "me") {
                val currentBank = meProfile.bankBalance
                val newBank = currentBank + tradeRoom.agreedPrice
                repository.saveProfile(meProfile.copy(bankBalance = newBank, completedDeals = meProfile.completedDeals + 1))
            }

            repository.insertAuditLog(AuditLog("me", "Complete Trade", "Trade Room finalized. Price: \$${formatCurrencySimulated(tradeRoom.agreedPrice)} for ${tradeRoom.listingTitle}."))

            repository.insertReceivedChatMessage(
                ChatMessage(
                    listingId = tradeRoom.listingId,
                    listingTitle = tradeRoom.listingTitle,
                    senderId = "me",
                    senderName = meProfile.username,
                    receiverId = if (tradeRoom.buyerId == "me") tradeRoom.sellerId else tradeRoom.buyerId,
                    receiverName = if (tradeRoom.buyerId == "me") tradeRoom.sellerName else tradeRoom.buyerName,
                    message = "🎉 [TRADE COMPLETED] Deal finalized successfully by both parties! Status updated to COMPLETED.",
                    timestamp = System.currentTimeMillis()
                )
            )

            repository.addNotification("🎉 Trade Finished!", "Deal for ${tradeRoom.listingTitle} completed at \$${formatCurrencySimulated(tradeRoom.agreedPrice)}!", "REWARD")
        }
    }

    fun openTradeDispute(tradeRoom: TradeRoom) {
        viewModelScope.launch {
            val updated = tradeRoom.copy(status = "DISPUTED")
            repository.updateTradeRoom(updated)

            repository.insertAuditLog(AuditLog("me", "Open Dispute", "Opened trade dispute for ${tradeRoom.listingTitle}."))

            val meProfile = repository.getMeProfile()
            repository.insertReceivedChatMessage(
                ChatMessage(
                    listingId = tradeRoom.listingId,
                    listingTitle = tradeRoom.listingTitle,
                    senderId = "me",
                    senderName = meProfile.username,
                    receiverId = if (tradeRoom.buyerId == "me") tradeRoom.sellerId else tradeRoom.buyerId,
                    receiverName = if (tradeRoom.buyerId == "me") tradeRoom.sellerName else tradeRoom.buyerName,
                    message = "🚨 [TRADE DISPUTED] Owner / Admin attention requested! Scam investigation has been queued.",
                    timestamp = System.currentTimeMillis()
                )
            )

            repository.addNotification("🚨 Dispute Opened", "Trade room flagged for arbitration. Admins are reviewing.", "ALERT")
        }
    }

    fun notifyInterest(listing: MarketListing) {
        viewModelScope.launch {
            val me = repository.getMeProfile()
            
            repository.insertNotification(
                AppNotification(
                    title = "👀 Player is Interested",
                    message = "User ${me.username} is interested in your '${listing.title}'. Reputation: ${me.reputation}%, Verified: ${me.isVerified}.",
                    type = "INFO",
                    userId = listing.sellerId
                )
            )

            repository.sendChatMessage(
                listingId = listing.id,
                listingTitle = listing.title,
                receiverId = listing.sellerId,
                receiverName = listing.sellerName,
                text = "👀 [INTEREST EXPRESSED] User ${me.username} is highly interested in your '${listing.title}'. Let's talk or trade!"
            )

            repository.insertAuditLog(
                AuditLog(
                    actorName = me.username,
                    action = "Express Interest",
                    details = "Expressed interest in '${listing.title}' (ID #${listing.id}). Profile details sent to seller."
                )
            )

            repository.addNotification("👀 Interest Sent", "You successfully declared interest to ${listing.sellerName}!", "INFO")
        }
    }

    fun toggleFavorite(listing: MarketListing, isFav: Boolean) {
        viewModelScope.launch {
            val newFavCount = if (isFav) listing.favoritesCount - 1 else listing.favoritesCount + 1
            repository.updateListing(listing.copy(favoritesCount = newFavCount.coerceAtLeast(0)))
            repository.addNotification(
                if (isFav) "💔 Favorite Removed" else "💖 Added to Favorites",
                "You adjusted interest on '${listing.title}'.",
                "INFO"
            )
        }
    }

    fun submitReportListing(listing: MarketListing, reason: String) {
        viewModelScope.launch {
            val me = repository.getMeProfile()
            repository.insertReceivedChatMessage(
                ChatMessage(
                    listingId = listing.id,
                    listingTitle = listing.title,
                    senderId = "me",
                    senderName = me.username,
                    receiverId = listing.sellerId,
                    receiverName = listing.sellerName,
                    message = "🚨 [FLAGGED / REPORTED] Listing reported to admin desk. Reason: \"$reason\"",
                    timestamp = System.currentTimeMillis()
                )
            )
            repository.insertAuditLog(
                AuditLog(
                    actorName = me.username,
                    action = "Report Listing",
                    details = "Reported listing #${listing.id} ('${listing.title}'): $reason"
                )
            )
            repository.addNotification("🚨 Listing Reported", "Your report for '${listing.title}' has been dispatched to administrators.", "ALERT")
        }
    }

    fun uploadTradeProof(room: TradeRoom, proofPath: String) {
        viewModelScope.launch {
            repository.updateTradeRoom(
                room.copy(
                    proofImagePath = proofPath.ifBlank { "grand_rp_escrow_transfer_99214.png" }
                )
            )
            repository.addNotification("📸 Screenshot Secured", "Transacted security proof successfully locked into Escrow room.", "REWARD")
        }
    }

    // --- Scammer Shield Center ---
    fun submitScammerReport(
        reportedUsername: String,
        reason: String,
        description: String,
        evidenceScreenshot: String,
        transactionId: String
    ) {
        viewModelScope.launch {
            val me = repository.getMeProfile()
            val report = ScammerReport(
                reportedUsername = reportedUsername,
                reason = reason,
                description = description,
                evidenceScreenshot = evidenceScreenshot.ifBlank { "evidence_scam_preview.png" },
                transactionId = transactionId,
                reporterId = "me",
                reporterName = me.username
            )
            repository.insertScammerReport(report)
            repository.insertAuditLog(
                AuditLog(
                    actorName = me.username,
                    action = "Submit Scammer Report",
                    details = "Reported player '$reportedUsername' for reasoning: $reason"
                )
            )
            repository.addNotification(
                "🚨 Alert Registered",
                "Your scam report against $reportedUsername is under audit by admins.",
                "ALERT"
            )
        }
    }

    fun updateScammerReportStatus(report: ScammerReport, newStatus: String, notes: String) {
        viewModelScope.launch {
            val updated = report.copy(status = newStatus, staffNotes = notes)
            repository.updateScammerReport(updated)

            // Log details and alert
            repository.insertAuditLog(
                AuditLog(
                    actorName = "Global Guard",
                    action = "Verdict on Case",
                    details = "Case ID #${report.id} on player '${report.reportedUsername}' resolved to: $newStatus"
                )
            )
            repository.addNotification(
                "🛡️ Guard Duty Update",
                "Case #${report.id} on '${report.reportedUsername}' has been processed.",
                "ALERT"
            )
        }
    }

    // --- Bounty Hunter System ---
    fun createBounty(
        title: String,
        description: String,
        rewardType: String,
        rewardAmount: Int,
        bountyType: String,
        expirationDate: String
    ) {
        viewModelScope.launch {
            val me = repository.getMeProfile()
            if (rewardType == "COINS" && me.coinBalance < rewardAmount) {
                alertMessage.value = "Insufficient Kat Coins in wallet to sponsor this bounty!"
                return@launch
            }

            // Deduct coins if sponsoring with Kat coins
            if (rewardType == "COINS") {
                repository.saveProfile(me.copy(coinBalance = me.coinBalance - rewardAmount))
                repository.insertAuditLog(
                    AuditLog(
                        actorName = me.username,
                        action = "Sponsor Bounty",
                        details = "Deducted $rewardAmount Coins as reward backing for bounty: $title"
                    )
                )
            }

            val bounty = Bounty(
                title = title,
                description = description,
                rewardType = rewardType,
                rewardAmount = rewardAmount,
                bountyType = bountyType,
                expirationDate = expirationDate.ifBlank { "25".plus(".06.2026") },
                creatorId = "me",
                creatorName = me.username
            )
            repository.insertBounty(bounty)
            repository.addNotification(
                "🎯 Bounty Sponsered",
                "Bounty for '$title' is published on the public board.",
                "REWARD"
            )
        }
    }

    fun claimBounty(
        bountyId: Int,
        bountyTitle: String,
        evidence: String,
        information: String,
        proof: String
    ) {
        viewModelScope.launch {
            val me = repository.getMeProfile()
            val claim = BountyClaim(
                bountyId = bountyId,
                bountyTitle = bountyTitle,
                claimantId = "me",
                claimantName = me.username,
                evidence = evidence,
                information = information,
                proof = proof.ifBlank { "claim_evidence_visual.png" }
            )
            repository.insertBountyClaim(claim)
            repository.insertAuditLog(
                AuditLog(
                    actorName = me.username,
                    action = "Claim Submission",
                    details = "Claimed rewards on bounty #$bountyId with details: $information"
                )
            )
            repository.addNotification(
                "📨 Info Dispatched",
                "Your claim evidence on bounty was received for inspection.",
                "INFO"
            )
        }
    }

    fun moderateBountyClaim(claim: BountyClaim, isApproved: Boolean) {
        viewModelScope.launch {
            val newStatus = if (isApproved) "APPROVED" else "REJECTED"
            repository.updateBountyClaim(claim.copy(status = newStatus))

            // Load bounties lists
            val allB = repository.allBountiesFlow.firstOrNull() ?: emptyList()
            val targetB = allB.find { it.id == claim.bountyId }

            if (isApproved && targetB != null && targetB.status == "ACTIVE") {
                // Set bounty as closed
                repository.updateBounty(targetB.copy(status = "CLAIMED"))

                // Deliver target rewards to the claimant profile
                val profiles = repository.richListFlow.firstOrNull() ?: emptyList()
                val claimantProfile = profiles.find { it.id == claim.claimantId } ?: repository.getMeProfile()

                when (targetB.rewardType) {
                    "COINS" -> {
                        if (claimantProfile.id == "me") {
                            val me = repository.getMeProfile()
                            repository.saveProfile(me.copy(coinBalance = me.coinBalance + targetB.rewardAmount))
                        } else {
                            repository.saveProfile(claimantProfile.copy(coinBalance = claimantProfile.coinBalance + targetB.rewardAmount))
                        }
                    }
                    "VIP_DAYS" -> {
                        if (claimantProfile.id == "me") {
                            val me = repository.getMeProfile()
                            repository.saveProfile(me.copy(hasVip = true, vipDaysLeft = me.vipDaysLeft + targetB.rewardAmount))
                        } else {
                            repository.saveProfile(claimantProfile.copy(hasVip = true, vipDaysLeft = claimantProfile.vipDaysLeft + targetB.rewardAmount))
                        }
                    }
                    "CRATE" -> {
                        repository.insertInventoryItem(
                            InventoryItem(
                                name = "Locked Scavenger Cache",
                                type = "CRATE_GOLD",
                                quantity = targetB.rewardAmount,
                                valueCoins = 50 * targetB.rewardAmount,
                                userId = claimantProfile.id
                            )
                        )
                    }
                    "BADGE" -> {
                        repository.insertInventoryItem(
                            InventoryItem(
                                name = "Elite Bounty Hunter Emblem",
                                type = "BADGE",
                                quantity = 1,
                                valueCoins = 100,
                                userId = claimantProfile.id
                            )
                        )
                    }
                }

                repository.insertAuditLog(
                    AuditLog(
                        actorName = "Staff Board",
                        action = "Claim Disbursal",
                        details = "Approved bounty #$${targetB.id} reward disbursal: ${targetB.rewardAmount} ${targetB.rewardType} to brand user ${claim.claimantName}."
                    )
                )
                repository.addNotification(
                    "🏆 Bounty Reward Disbursed",
                    "Success! ${targetB.rewardAmount} ${targetB.rewardType} credited to claimant ${claim.claimantName}!",
                    "REWARD"
                )
            } else {
                repository.insertAuditLog(
                    AuditLog(
                        actorName = "Staff Board",
                        action = "Claim Rejection",
                        details = "Bounty claim review #${claim.id} ended as REJECTED."
                    )
                )
                repository.addNotification(
                    "❌ Claim Disapproved",
                    "Claim on bounty #$${claim.bountyId} was rejected as insufficient proof.",
                    "ALERT"
                )
            }
        }
    }

    // ----------------------------------------------------
    // ADVANCED CHAT SYSTEM CAPABILITIES (CHAT SKILL REINFORCEMENTS)
    // ----------------------------------------------------

    val blockedUsers = kotlinx.coroutines.flow.MutableStateFlow<Set<String>>(emptySet())
    val mutedUsers = kotlinx.coroutines.flow.MutableStateFlow<Set<String>>(emptySet())

    fun toggleBlockUser(username: String) {
        val current = blockedUsers.value
        val updated = if (current.contains(username)) {
            current - username
        } else {
            current + username
        }
        blockedUsers.value = updated
        val action = if (updated.contains(username)) "Blocked" else "Unblocked"
        alertMessage.value = "User $username has been $action."
        viewModelScope.launch {
            repository.insertAuditLog(AuditLog("Security System", "Toggle Block", "User $username has been $action."))
        }
    }

    fun toggleMuteUser(username: String) {
        val current = mutedUsers.value
        val updated = if (current.contains(username)) {
            current - username
        } else {
            current + username
        }
        mutedUsers.value = updated
        val action = if (updated.contains(username)) "Muted" else "Unmuted"
        alertMessage.value = "User $username has been $action."
        viewModelScope.launch {
            repository.insertAuditLog(AuditLog("Security System", "Toggle Mute", "User $username has been $action."))
        }
    }

    fun reportUserFromChat(username: String, reason: String) {
        if (reason.isBlank()) return
        viewModelScope.launch {
            repository.insertAuditLog(AuditLog("Chat Shield", "Report User", "Reported $username inside conversation. Reason: $reason"))
            repository.addNotification("🚨 Scam Report Filed", "Staff has been alerted regarding user $username.", "ALERT")
            alertMessage.value = "Scam report for $username filed. Safeguard measures activated."
        }
    }

    fun toggleMessageReaction(message: ChatMessage, emoji: String) {
        viewModelScope.launch {
            val key = "$emoji:me"
            val existingList = message.reactions.split(",").filter { it.isNotBlank() }.toMutableList()
            if (existingList.contains(key)) {
                existingList.remove(key)
            } else {
                existingList.add(key)
            }
            val updatedReactions = existingList.joinToString(",")
            val updatedMsg = message.copy(reactions = updatedReactions)
            repository.updateChatMessage(updatedMsg)
        }
    }

    fun sendChatMessageWithAttachment(
        listingId: Int,
        listingTitle: String,
        receiverId: String,
        receiverName: String,
        text: String,
        attachmentType: String = "TEXT",
        attachmentPath: String = "",
        attachmentId: Int = 0,
        attachmentTitle: String = "",
        replyToId: Int = 0,
        replyToText: String = ""
    ) {
        if (text.isBlank() && attachmentPath.isBlank() && attachmentId == 0) return
        
        // Block executable and dangerous files from attachment paths
        if (attachmentType == "IMAGE" || attachmentType == "PROOF") {
            val lower = attachmentPath.lowercase().trim()
            if (lower.endsWith(".exe") || lower.endsWith(".apk") || lower.endsWith(".bat") || lower.endsWith(".msi") || lower.endsWith(".sh")) {
                alertMessage.value = "🛡️ FILE COMPROMISED: Executable and scripting paths are forbidden for security."
                return
            }
        }

        viewModelScope.launch {
            repository.sendChatMessageWithDetailedFields(
                listingId = listingId,
                listingTitle = listingTitle,
                receiverId = receiverId,
                receiverName = receiverName,
                text = text,
                attachmentType = attachmentType,
                attachmentPath = attachmentPath,
                attachmentId = attachmentId,
                attachmentTitle = attachmentTitle,
                replyToId = replyToId,
                replyToText = replyToText
            )
            
            // Notification triggers for chat events
            repository.addNotification("Message Sent", "Message successfully transfered with $attachmentType metadata.", "INFO")

            // Simulate response
            if (receiverId != "me" && listingId >= 0) {
                kotlinx.coroutines.delay(1800)
                if (blockedUsers.value.contains(receiverName)) return@launch

                val reply = when (attachmentType) {
                    "IMAGE" -> "Wow, that screenshot proof looks extremely clean. Genuine trader indeed."
                    "LISTING" -> "Thanks for sharing listing #${attachmentId} details, ready to negotiate terms?"
                    "BUSINESS" -> "Awesome business key details! Can you schedule on-site delivery?"
                    "VEHICLE" -> "Beautiful vehicle specs! Is that the exact model license plate?"
                    "PROPERTY" -> "That real estate property option checks out. Let's start contracts."
                    else -> listOf(
                        "Received message! Price suggestions are negotiable under standard administrative terms.",
                        "Are you active near the Grand center bank vault?",
                        "Let's wire in-game money or create escrow trade ledger.",
                        "I can agree with that. Complete the trade status when online!",
                        "Sounds solid, let's file ownership screenshot proof."
                    ).random()
                }

                repository.insertReceivedChatMessage(
                    ChatMessage(
                        listingId = listingId,
                        listingTitle = listingTitle,
                        senderId = receiverId,
                        senderName = receiverName,
                        receiverId = "me",
                        receiverName = "NIKA_BOSS_RP",
                        message = reply,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun simulateGroupChatMessage(groupChatId: Int, groupChatName: String) {
        viewModelScope.launch {
            val mockSenders = listOf("Roman_Vercetti", "Aditya_Grand", "Alisa_Petrova", "VIP_Nika_Trader", "Marcus_M5", "Officer_Ivan", "Nika_Loyal")
            val sender = mockSenders.random()
            val text = when (groupChatId) {
                -1 -> listOf("Anyone selling some pristine gold bars cheap?", "Is the market active today?", "Watch out for scams, trade in verified escrow only!").random()
                -2 -> listOf("My BMW M5 F90 is absolute fire. Sells for clean price.", "Who is looking for a rare customized license plate?", "Selling custom drift specs right now!").random()
                -3 -> listOf("Selling apartment near Grand Bureau with luxury space.", "Looking for family mansion space.", "Real estate properties prices are skyrocketing!").random()
                -4 -> listOf("My business generated 1.2M in-game cash today! Sells key soon.", "Buying fuel stations or oil rigs.", "Always verify businesses licenses before wire.").random()
                -5 -> listOf("Nika Syndicate faction patrol starting in 15 mins! Join up.", "Family bank deposits have open high-yield spaces.", "Hanging out near family garage. Spawn on me!").random()
                -6 -> listOf("VIP Lounge rewards are online! Claim now.", "Best high wealth server hub. Post proof of net worth!", "50 contract milestone unlocked!").random()
                else -> listOf("All verified traders present, escrow check verified.", "Safeguard protocol guarantees 100% scam shield verification.", "Never share executables in chats! Staff is watching.").random()
            }
            repository.insertReceivedChatMessage(
                ChatMessage(
                    listingId = groupChatId,
                    listingTitle = groupChatName,
                    senderId = "mock_group_sender_${kotlin.random.Random.nextInt(100)}",
                    senderName = sender,
                    receiverId = "communal",
                    receiverName = "Community Chat",
                    message = text,
                    timestamp = System.currentTimeMillis()
                )
            )
            repository.addNotification("Channel Mention", "$sender posted inside $groupChatName channel.", "INFO")
        }
    }

    fun connectDiscordAccount(username: String, discordId: String, avatarUrl: String?, serverMember: Boolean) {
        viewModelScope.launch {
            repository.connectDiscord(username, discordId, avatarUrl, serverMember)
            alertMessage.value = "Discord account linking completed successfully!"
        }
    }

    fun disconnectDiscordAccount() {
        viewModelScope.launch {
            repository.disconnectDiscord()
            alertMessage.value = "Discord account unlinked successfully."
        }
    }

    fun syncDiscordRoles(roles: List<String>) {
        viewModelScope.launch {
            val result = repository.syncRolesFromDiscord(roles)
            alertMessage.value = result
        }
    }

    fun updateDiscordNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleDiscordNotifications(enabled)
            alertMessage.value = if (enabled) "Discord DM Notifications Enabled!" else "Discord DM Notifications Disabled."
        }
    }

    fun toggleDiscordServerMembership(inServer: Boolean) {
        viewModelScope.launch {
            repository.updateDiscordMembershipStatus(inServer)
            if (!inServer) {
                alertMessage.value = "⚠️ WARN: You left the Discord server. Synced privileges dropped."
                repository.addNotification("Discord Server Alert", "Detect status change: You left the official Discord server. Roles unsynced.", "ALERT")
            } else {
                alertMessage.value = "Welcome back to the Discord community server! Re-synchronizing roles..."
                repository.addNotification("Discord Server Joined", "Connected back to Discord server. Sync roles requested.", "INFO")
            }
        }
    }

    private fun startRealTimeFeedSimulator() {
        viewModelScope.launch {
            // Continually simulated background real-time board updates (Listings, Chats, Live offers)
            while (isActive) {
                // Delay randomly between 12 to 18 seconds
                kotlinx.coroutines.delay(kotlin.random.Random.nextLong(12000, 18000))

                // Select a simulated task
                val task = kotlin.random.Random.nextInt(5)
                val mockPlayers = listOf("Aditya_Grand", "Roman_Vercetti", "Alisa_Petrova", "Marcus_M5", "Officer_Ivan", "Nika_Loyal", "Boss_RP", "Dmitry_Sokolov", "Elena_GTR")
                val sender = mockPlayers.random()

                when (task) {
                    0 -> {
                        // Action 1: Add a new simulated Market Listing
                        val categories = listOf("Vehicle", "Property", "Business", "Skin", "Item")
                        val category = categories.random()
                        
                        val title = when (category) {
                            "Vehicle" -> listOf(
                                "Mercedes-Benz G63 AMG Brabus",
                                "Lamborghini Huracán Evo Spyder",
                                "Porsche 911 Turbo S",
                                "Nissan GT-R Stage 3",
                                "BMW M5 F90 Stage 4"
                            ).random()
                            "Property" -> listOf(
                                "Vinewood Hills Villa #108",
                                "Paleto Bay Beachfront House",
                                "Downtown Penthouse Suite",
                                "Mirror Park Suburban Condo"
                            ).random()
                            "Business" -> listOf(
                                "Gas Station #12 Premium",
                                "24/7 Store Rockford Hills",
                                "Ammunation Gun Shop Vinewood",
                                "Custom Tuning Workshop Central"
                            ).random()
                            "Skin" -> listOf(
                                "Gold Syndicate Kevlar Suit",
                                "Desert Camo Tactical Armor",
                                "White Tuxedo Vercetti Spec"
                            ).random()
                            else -> listOf(
                                "Sovereign Gold Watch",
                                "Level 3 Tactical Military Backpack",
                                "VIP Double Coin Booster",
                                "Rare Licence Plate: B0SS_RP"
                            ).random()
                        }

                        val subType = when (category) {
                            "Vehicle" -> "Car"
                            "Property" -> "House"
                            "Business" -> "Store"
                            "Skin" -> "Apparel"
                            else -> "Hardware"
                        }

                        val baseVal = when (category) {
                            "Vehicle" -> kotlin.random.Random.nextLong(20000000, 150000000)
                            "Property" -> kotlin.random.Random.nextLong(50000000, 400000000)
                            "Business" -> kotlin.random.Random.nextLong(80000000, 600000000)
                            else -> kotlin.random.Random.nextLong(500000, 15000000)
                        }

                        val askVal = (baseVal * (1.0 + kotlin.random.Random.nextDouble(-0.1, 0.25))).toLong()

                        val newListing = MarketListing(
                            title = title,
                            category = category,
                            subType = subType,
                            statePrice = baseVal,
                            askingPrice = askVal,
                            ownerCount = kotlin.random.Random.nextInt(1, 4),
                            location = listOf("Chamberlain Hills", "Vespucci", "Vinewood", "Mirror Park", "Downtown").random(),
                            licensePlate = "RP" + kotlin.random.Random.nextInt(100, 999) + listOf("HA", "XY", "AM", "KK").random(),
                            profitDaily = if (category == "Business") kotlin.random.Random.nextLong(150000, 2500000) else 0L,
                            notes = listOf("Selling fast for quick cash transfer. Non-negotiable.", "Stage 3 tuned with custom engine smoke.", "Clean license registry passport included.", "Urgent trade! Going to family clan raid.").random(),
                            sellerId = sender.lowercase(),
                            sellerName = sender,
                            sellerReputation = kotlin.random.Random.nextInt(85, 100),
                            isVerifiedSeller = kotlin.random.Random.nextBoolean(),
                            isFeatured = kotlin.random.Random.nextDouble() < 0.25,
                            isUrgent = kotlin.random.Random.nextDouble() < 0.35,
                            imageUrl = "ic_launcher_foreground",
                            images = if (category == "Vehicle") listOf("bulletproof_m5_neon.png") else if (category == "Property") listOf("modern_villa_hillside.png") else emptyList(),
                            mediaStatus = "APPROVED",
                            watermarked = false
                        )

                        repository.createNewListing(newListing)
                        repository.addNotification(
                            "⚡ Live Listing Alert",
                            "$sender posted a new $category listing: \"$title\" for \$${formatCurrencySimulated(askVal)}!",
                            "INFO"
                        )
                        repository.insertAuditLog(AuditLog(sender, "Simulated Publish", "Listed $title for \$${formatCurrencySimulated(askVal)} on the board."))
                    }
                    1 -> {
                        // Action 2: Simulate Chat message in community channels
                        val activeChannels = listOf(
                            -1 to "🎯 general-marketplace",
                            -2 to "🏎️ vehicle-exchange",
                            -3 to "🏠 property-trading",
                            -4 to "💼 business-deals",
                            -5 to "👥 syndicate-alliance",
                            -6 to "👑 high-net-worth-lounge"
                        )
                        val (chId, chName) = activeChannels.random()
                        simulateGroupChatMessage(chId, chName)
                    }
                    2 -> {
                        // Action 3: Simulate an offer made on the user's active listings (if any)
                        allListings.value.filter { it.sellerId == "me" && it.status == "ACTIVE" }.let { myListings ->
                            if (myListings.isNotEmpty()) {
                                val targetListing = myListings.random()
                                val bidMultiplier = listOf(0.85, 0.90, 0.95, 1.0, 1.05).random()
                                val bidAmt = (targetListing.askingPrice * bidMultiplier).toLong()

                                val offer = NegotiationOffer(
                                    listingId = targetListing.id,
                                    listingTitle = targetListing.title,
                                    buyerId = sender.lowercase(),
                                    buyerName = sender,
                                    sellerId = "me",
                                    sellerName = "me_user",
                                    amount = bidAmt,
                                    message = listOf(
                                        "Hey boss, direct cash in hand deal! Ready to buy right now.",
                                        "Is this price fully negotiable? I can offer you cash.",
                                        "Sent a counter offer, deal instant wire at bank vault?",
                                        "Would love to inspect document license pass first."
                                    ).random()
                                )

                                repository.insertNegotiationOffer(offer)
                                repository.addNotification(
                                    "🤝 New Offer Received",
                                    "$sender submitted an offer of \$${formatCurrencySimulated(bidAmt)} for your '${targetListing.title}'!",
                                    "REWARD"
                                )
                                repository.insertAuditLog(AuditLog(sender, "Negotiation Offer Received", "Made live offer for user's ${targetListing.title}."))
                            }
                        }
                    }
                    3 -> {
                        // Action 4: Simulate a chat message inside an open user trade-room/chat if we have one
                        val meRooms = tradeRooms.value.filter { it.status == "PENDING_COMPLETION" }
                        if (meRooms.isNotEmpty()) {
                            val chosenRoom = meRooms.random()
                            val otherParty = if (chosenRoom.buyerId == "me") chosenRoom.sellerName else chosenRoom.buyerName
                            val replyText = listOf(
                                "I have uploaded the legal passport. Check it.",
                                "Escrow balance secured! Please publish your transfer.",
                                "Is support staff online in case we need escrow oversight?",
                                "Excellent trading with you! Best quality of deal."
                            ).random()

                            repository.insertReceivedChatMessage(
                                ChatMessage(
                                    listingId = chosenRoom.listingId,
                                    listingTitle = chosenRoom.listingTitle,
                                    senderId = otherParty.lowercase(),
                                    senderName = otherParty,
                                    receiverId = "me",
                                    receiverName = "me_user",
                                    message = replyText,
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                            repository.addNotification("New Trade Chat", "$otherParty sent: \"$replyText\" inside escrow channel.", "INFO")
                        }
                    }
                    4 -> {
                        // Action 5: Simulate a background RCD transaction completion notice
                        val otherParty = sender
                        val assetName = listOf("Bugatti Chiron Blue Custom", "Vespucci Villa Penthouse", "Rockford Hills 24/7 Store", "Custom VIP Faction Pass").random()
                        val value = kotlin.random.Random.nextLong(10000000, 200000000)

                        repository.addNotification(
                            "🎉 Deal Completed",
                            "Vetted trade completed: $otherParty successfully obtained $assetName via verified Escrow agent!",
                            "REWARD"
                        )
                        repository.insertAuditLog(AuditLog("Escrow Bot", "Vetted Transaction Completed", "Secured Escrow trade of $assetName valued at \$${formatCurrencySimulated(value)}."))
                    }
                }
            }
        }
    }
}
