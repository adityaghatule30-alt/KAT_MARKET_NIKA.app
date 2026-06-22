package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class MarketRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val dao = db.marketDao()

    // --- Observable Flows ---
    val userProfileFlow: Flow<UserProfile?> = dao.getUserProfileFlow("me")
    val allListingsFlow: Flow<List<MarketListing>> = dao.getAllListingsFlow()
    val allBusinessesFlow: Flow<List<RegisteredBusiness>> = dao.getAllBusinessesFlow()
    val allFamiliesFlow: Flow<List<Family>> = dao.getAllFamiliesFlow()
    val allRcdDealsFlow: Flow<List<RcdDeal>> = dao.getAllRcdDealsFlow()
    val allAdsFlow: Flow<List<Advertisement>> = dao.getAllAdsFlow()
    val allCoinPurchasesFlow: Flow<List<CoinPurchaseRequest>> = dao.getAllCoinPurchasesFlow()
    val notificationsFlow: Flow<List<AppNotification>> = dao.getNotificationsFlow("me")
    val richListFlow: Flow<List<UserProfile>> = dao.getRichListFlow()
    val auditLogsFlow: Flow<List<AuditLog>> = dao.getAllAuditLogsFlow()
    val negotiationOffersFlow: Flow<List<NegotiationOffer>> = dao.getAllNegotiationOffersFlow()
    val tradeRoomsFlow: Flow<List<TradeRoom>> = dao.getAllTradeRoomsFlow()

    fun getFixedDepositsFlow(): Flow<List<FixedDeposit>> = dao.getFixedDepositsFlow("me")
    fun getVouchesFlow(userId: String): Flow<List<UserVouch>> = dao.getVouchesFlow(userId)
    fun getInventoryFlow(): Flow<List<InventoryItem>> = dao.getInventoryFlow("me")

    suspend fun insertInventoryItem(item: InventoryItem) {
        dao.insertInventoryItem(item)
    }

    // --- Initialize & Seed Core Data ---
    suspend fun initializeDatabaseIfNeeded() {
        val currentMe = dao.getUserProfile("me")
        if (currentMe == null) {
            Log.d("MarketRepository", "Seeding database with default sandbox assets...")

            // 1. Seed Current User
            val myProfile = UserProfile(
                id = "me",
                username = "NIKA_BOSS_RP",
                role = "Normal User",
                reputation = 94,
                walletBalance = 125000000L, // 125M Grand currency
                bankBalance = 450000000L,   // 450M Grand currency
                coinBalance = 2450,          // 2450 Kat Coins
                isVerified = false,
                hasVip = false,
                familyName = "Nika Syndicate",
                title = "Elite Trader",
                avatarIndex = 3,
                completedDeals = 45,
                vouchesCount = 18
            )
            dao.insertProfile(myProfile)

            // Seed other leader users for richlist scaling
            dao.insertProfile(
                UserProfile(
                    id = "u2",
                    username = "Aditya_Grand",
                    role = "Verified Trader",
                    reputation = 98,
                    walletBalance = 500000000L,
                    bankBalance = 2500000000L,
                    coinBalance = 8000,
                    isVerified = true,
                    hasVip = true,
                    vipDaysLeft = 30,
                    familyName = "Grand Elite Guild",
                    joinedDate = "12.04.2025",
                    title = "Marketplace Legend",
                    avatarIndex = 1,
                    completedDeals = 350,
                    vouchesCount = 89,
                    discordUsername = "Aditya#1337",
                    discordId = "468930219602495402",
                    discordJoinDate = "15.01.2022",
                    discordServerMember = true,
                    discordRoleSynced = true
                )
            )
            dao.insertProfile(
                UserProfile(
                    id = "u3",
                    username = "Roman_Vercetti",
                    role = "VIP",
                    reputation = 90,
                    walletBalance = 80000000L,
                    bankBalance = 1200000000L,
                    coinBalance = 500,
                    isVerified = false,
                    hasVip = true,
                    vipDaysLeft = 12,
                    familyName = "Vercetti Crime Family",
                    joinedDate = "01.01.2026",
                    title = "Business Tycoon",
                    avatarIndex = 2,
                    completedDeals = 110,
                    vouchesCount = 32,
                    discordUsername = "Vercetti_Boss",
                    discordId = "392019485721948572",
                    discordJoinDate = "10.03.2023",
                    discordServerMember = true,
                    discordRoleSynced = true
                )
            )
            dao.insertProfile(
                UserProfile(
                    id = "u4",
                    username = "Alisa_Petrova",
                    role = "Administrator",
                    reputation = 100,
                    walletBalance = 10000000L,
                    bankBalance = 9999000000L,
                    coinBalance = 50000,
                    isVerified = true,
                    hasVip = true,
                    vipDaysLeft = 365,
                    familyName = "Nika Syndicate",
                    joinedDate = "15.08.2024",
                    title = "Owner",
                    avatarIndex = 4,
                    completedDeals = 1200,
                    vouchesCount = 450,
                    discordUsername = "Alisa_Petrova_Nika",
                    discordId = "552019485721948576",
                    discordJoinDate = "14.07.2021",
                    discordServerMember = true,
                    discordRoleSynced = true
                )
            )

            // 2. Seed Initial General Market Listings
            val initialListings = listOf(
                MarketListing(
                    title = "BMW M5 F90 Custom Stage 3",
                    category = "Vehicle",
                    subType = "Car",
                    statePrice = 80000000L,
                    askingPrice = 115000000L,
                    ownerCount = 2,
                    licensePlate = "K777HA",
                    notes = "Fully tuned Stage 3 Nitro. Excellent handling, custom smoke purple paint.",
                    sellerId = "u2",
                    sellerName = "Aditya_Grand",
                    sellerReputation = 98,
                    isVerifiedSeller = true,
                    isFeatured = true,
                    imageUrl = "ic_launcher_foreground",
                    images = listOf("Main Vehicle Screenshot", "License Screenshot", "Front View", "Back View", "Side View", "Interior", "Engine"),
                    videoUrl = "bmw_m5_stage3_sound.mp4",
                    mediaStatus = "APPROVED",
                    watermarked = true
                ),
                MarketListing(
                    title = "Yamaha YZF-R1 Carbon",
                    category = "Vehicle",
                    subType = "Bike",
                    statePrice = 16000000L,
                    askingPrice = 24000000L,
                    ownerCount = 1,
                    licensePlate = "Y888RR",
                    notes = "Limited edition carbon build. Direct sale only, no trade ins.",
                    sellerId = "u3",
                    sellerName = "Roman_Vercetti",
                    sellerReputation = 90,
                    isVerifiedSeller = false,
                    isFeatured = false,
                    imageUrl = "ic_launcher_foreground",
                    images = listOf("Main Vehicle Screenshot", "License Screenshot", "Carbon Frame Details"),
                    mediaStatus = "APPROVED",
                    watermarked = false
                ),
                MarketListing(
                    title = "Arzamas Center Mansion #15",
                    category = "Property",
                    subType = "House",
                    statePrice = 150000000L,
                    askingPrice = 210000000L,
                    ownerCount = 3,
                    location = "Arzamas High Class District",
                    notes = "Complete premium high-tech restoration. Includes 5 garage spaces and swimming pool access.",
                    sellerId = "u2",
                    sellerName = "Aditya_Grand",
                    sellerReputation = 98,
                    isVerifiedSeller = true,
                    isFeatured = true,
                    imageUrl = "ic_launcher_foreground",
                    images = listOf("Property Screenshot", "Living Room", "Garage", "Kitchen", "Bedroom", "Exterior Pool view", "Map Location"),
                    videoUrl = "mansion_luxury_tour.mp4",
                    mediaStatus = "APPROVED",
                    watermarked = true
                ),
                MarketListing(
                    title = "Nikitino Cozy Apartment #58",
                    category = "Property",
                    subType = "Apartment",
                    statePrice = 30000000L,
                    askingPrice = 42000000L,
                    ownerCount = 1,
                    location = "Nikitino Suburbs",
                    notes = "Cheap, direct entry-level apartment. Includes basic furniture.",
                    sellerId = "me",
                    sellerName = "NIKA_BOSS_RP",
                    sellerReputation = 94,
                    isVerifiedSeller = false,
                    isFeatured = false,
                    imageUrl = "ic_launcher_foreground",
                    images = emptyList(), // Low Visibility!
                    mediaStatus = "APPROVED",
                    watermarked = false
                ),
                MarketListing(
                    title = "24/7 Store 'Nika Square'",
                    category = "Business",
                    subType = "24/7 Store",
                    statePrice = 450000000L,
                    askingPrice = 580000000L,
                    location = "Arzamas Central Square",
                    profitDaily = 8500000L,
                    notes = "Insane traffic. Sells over 500 first aid kits daily.",
                    sellerId = "u4",
                    sellerName = "Alisa_Petrova",
                    sellerReputation = 100,
                    isVerifiedSeller = true,
                    isFeatured = true,
                    imageUrl = "ic_launcher_foreground",
                    images = listOf("Business Screenshot", "Profit Screenshot", "Store Shelves Interior", "Customer Entrance", "Active Customers Log"),
                    videoUrl = "nika_247_activities.mp4",
                    mediaStatus = "APPROVED",
                    watermarked = true
                )
            )
            for (listing in initialListings) {
                dao.insertListing(listing)
            }

            // 3. Seed Business Network Directories
            val initialBusinesses = listOf(
                RegisteredBusiness(
                    name = "Arzamas Center 24/7",
                    type = "24/7 Store",
                    location = "Arzamas Square",
                    dailyProfit = 8500000L,
                    statePrice = 400000000L,
                    estimatedValue = 540000000L,
                    ownerId = "u4",
                    ownerName = "Alisa_Petrova",
                    rating = 4.9,
                    employeesCount = 8,
                    vacancyOpen = 2
                ),
                RegisteredBusiness(
                    name = "Arzamas North Gas Station",
                    type = "Gas Station",
                    location = "Main Bypass",
                    dailyProfit = 10200000L,
                    statePrice = 500000000L,
                    estimatedValue = 680000000L,
                    ownerId = "u2",
                    ownerName = "Aditya_Grand",
                    rating = 4.8,
                    employeesCount = 12,
                    vacancyOpen = 1
                ),
                RegisteredBusiness(
                    name = "Vercetti Notary Office",
                    type = "Notary Office",
                    location = "Yuzhny District",
                    dailyProfit = 4200000L,
                    statePrice = 200000000L,
                    estimatedValue = 260000000L,
                    ownerId = "u3",
                    ownerName = "Roman_Vercetti",
                    rating = 4.3,
                    employeesCount = 4,
                    vacancyOpen = 2
                )
            )
            for (biz in initialBusinesses) {
                dao.insertBusiness(biz)
            }

            // 4. Seed Initial Families
            dao.insertFamily(Family(name = "Nika Syndicate", leaderId = "me", leaderName = "NIKA_BOSS_RP", memberCount = 18, vaultBalance = 150000000L, description = "The ultimate alliance of expert traders. Custom family vehicles unlocked.", level = 3, logoIndex = 1))
            dao.insertFamily(Family(name = "Grand Elite Guild", leaderId = "u2", leaderName = "Aditya_Grand", memberCount = 42, vaultBalance = 1200000000L, description = "Oldest family on Grand RP, extreme billionaires.", level = 10, logoIndex = 2))

            // 5. Seed Initial General Advertisements
            dao.insertAd(Advertisement("Elite Tuning Service", "Best mechanics inside Arzamas. Low prices and fast restock!", "Business", "u3", "Roman_Vercetti", "STANDARD", 20, 240, 15, System.currentTimeMillis(), true))
            dao.insertAd(Advertisement("Nika Syndicate Recruitment", "Hiring mature, hyperactive traders! Full backing & free high-spec cars.", "Family", "me", "NIKA_BOSS_RP", "PREMIUM", 100, 1530, 110, System.currentTimeMillis() - 100000, true))

            // 6. Seed Initial Notifications
            dao.insertNotification(AppNotification(title = "Welcome Back!", message = "Welcome to KAT_MARKET_NIKA app, Grand Mobile's #1 custom RP trading client.", type = "INFO"))
            dao.insertNotification(AppNotification(title = "Special Offer Alert", message = "A new featured Stage 3 vehicle has been published by a verified seller!", type = "ALERT"))

            // 7. Seed Initial Inventory crates/rewards
            dao.insertInventoryItem(InventoryItem(name = "Gold Crate", type = "CRATE_GOLD", quantity = 3, valueCoins = 150))
            dao.insertInventoryItem(InventoryItem(name = "Diamond Crate", type = "CRATE_DIAMOND", quantity = 1, valueCoins = 500))
            dao.insertInventoryItem(InventoryItem(name = "VIP Supporter Title", type = "TITLE", quantity = 1, valueCoins = 100))

            // 8. Seed Default Vouches
            dao.insertVouch(UserVouch("me", "Roman_Vercetti", 5, "Amazing guy. Delivered the Rublevka house on time and handled notary fees!"))
            dao.insertVouch(UserVouch("me", "Aditya_Grand", 5, "Fast transaction, extremely trustworthy! 10/10 recommended!"))

            // 9. Initial Audit Log
            dao.insertAuditLog(AuditLog("Owner", "System Initialize", "Loaded full Grand Mobile RP database tables successfully under secure sandbox structures."))

            // 10. Seed Scammer Reports
            dao.insertScammerReport(
                ScammerReport(
                    reportedUsername = "scam_noob_123",
                    reporterName = "Roman_Vercetti",
                    reason = "Fake Screenshots",
                    description = "This user tried to buy my Yamaha bike using a photoshopped bank transfer screenshot. The receipt date and transaction status fonts are totally skewed.",
                    evidenceScreenshot = "screenshot_scam_fake_proof.png",
                    status = "UNDER_INVESTIGATION",
                    staffNotes = "Awaiting response from scam_noob_123 to verify original banking statement."
                )
            )
            dao.insertScammerReport(
                ScammerReport(
                    reportedUsername = "cheator_guy",
                    reporterName = "Aditya_Grand",
                    reason = "Fake Payments",
                    description = "Agreed to buy my business with real cash. Provided a fake UPI transaction ID that does not exist in the bank logs.",
                    evidenceScreenshot = "upi_forged_992.png",
                    status = "CONFIRMED_SCAMMER",
                    staffNotes = "Blacklisted. Transaction ID verified fake and UPI receipts proved to be duplicate design."
                )
            )

            // 11. Seed Bounties
            dao.insertBounty(
                Bounty(
                    title = "BMW M5 Search",
                    description = "Looking for a high performance custom Stage 3 BMW M5. Offer cap is 110M. Must have clean title.",
                    rewardType = "COINS",
                    rewardAmount = 50,
                    bountyType = "Vehicle Wanted",
                    expirationDate = "25.06.2026",
                    creatorName = "Roman_Vercetti"
                )
            )
            dao.insertBounty(
                Bounty(
                    title = "Investigate Scam Case",
                    description = "Investigate the UPI payment fraud committed by 'cheator_guy'. Provide supplementary log snapshots or payment status screenshots.",
                    rewardType = "COINS",
                    rewardAmount = 100,
                    bountyType = "Scam Investigation",
                    expirationDate = "30.06.2026",
                    creatorName = "Aditya_Grand"
                )
            )
            dao.insertBounty(
                Bounty(
                    title = "Arzamas Shop Search",
                    description = "Need to find an active owner of Shop 24/7 next to Arzamas bank. If you can establish a direct line to them, you get the crates.",
                    rewardType = "CRATE",
                    rewardAmount = 2,
                    bountyType = "Business Wanted",
                    expirationDate = "28.06.2026",
                    creatorName = "me_user"
                )
            )

            // 12. Seed Bounty Claims
            dao.insertBountyClaim(
                BountyClaim(
                    bountyId = 2,
                    claimantName = "NIKA_BOSS_RP",
                    evidence = "Imgur image links showing chat transcripts on Discord.",
                    information = "Here are the direct Discord chat transcripts of cheator_guy boasting about using fake payment apps to bypass escrow checks.",
                    proof = "discord_admission_logs.png",
                    status = "PENDING",
                    bountyTitle = "Investigate Scam Case"
                )
            )
        }
    }

    // --- Profile & Economy Services ---
    suspend fun getMeProfile(): UserProfile {
        return dao.getUserProfile("me") ?: UserProfile(username = "New User")
    }

    suspend fun saveProfile(profile: UserProfile) {
        dao.insertProfile(profile)
    }

    suspend fun depositMoney(amount: Long): Boolean {
        if (amount <= 0) return false
        val profile = getMeProfile()
        if (profile.walletBalance >= amount) {
            val updated = profile.copy(
                walletBalance = profile.walletBalance - amount,
                bankBalance = profile.bankBalance + amount
            )
            dao.insertProfile(updated)
            dao.insertTransaction(BankTransaction(type = "DEPOSIT", amount = amount, details = "Deposited to Bank Account"))
            dao.insertAuditLog(AuditLog(profile.username, "Bank Deposit", "Deposited ${amount} to bank account securely."))
            addNotification("Bank Updated", "You successfully deposited ${amount} grand currency into your bank account.", "INFO")
            return true
        }
        return false
    }

    suspend fun withdrawMoney(amount: Long): Boolean {
        if (amount <= 0) return false
        val profile = getMeProfile()
        if (profile.bankBalance >= amount) {
            val updated = profile.copy(
                walletBalance = profile.walletBalance + amount,
                bankBalance = profile.bankBalance - amount
            )
            dao.insertProfile(updated)
            dao.insertTransaction(BankTransaction(type = "WITHDRAW", amount = amount, details = "Withdrew from Bank Account"))
            dao.insertAuditLog(AuditLog(profile.username, "Bank Withdrawal", "Withdrew ${amount} from bank account."))
            addNotification("Bank Updated", "You successfully withdrew ${amount} grand currency from your bank account.", "INFO")
            return true
        }
        return false
    }

    suspend fun transferMoney(amount: Long, recipientName: String): Boolean {
        if (amount <= 0 || recipientName.isBlank()) return false
        val profile = getMeProfile()
        if (profile.bankBalance >= amount) {
            val updated = profile.copy(bankBalance = profile.bankBalance - amount)
            dao.insertProfile(updated)
            dao.insertTransaction(BankTransaction(type = "TRANSFER", amount = amount, details = "Transferred to $recipientName"))
            dao.insertAuditLog(AuditLog(profile.username, "Peer Transfer", "Transferred ${amount} to peer user: $recipientName"))

            // If recipient is Aditya or other seeded, simulate updating their balance (for sandbox depth!)
            val richList = dao.getRichListFlow().firstOrNull() ?: emptyList()
            val recipient = richList.find { it.username.equals(recipientName, ignoreCase = true) }
            if (recipient != null) {
                dao.insertProfile(recipient.copy(bankBalance = recipient.bankBalance + amount))
            }

            addNotification("Transfer Complete", "You securely transferred ${amount} grand currency to $recipientName.", "INFO")
            return true
        }
        return false
    }

    // --- Fixed Deposits (FD) ---
    suspend fun createFixedDeposit(amount: Long, durationDays: Int, interestPercent: Int): Boolean {
        val profile = getMeProfile()
        if (profile.bankBalance >= amount) {
            // Deduct
            dao.insertProfile(profile.copy(bankBalance = profile.bankBalance - amount))
            // Insert FD
            dao.insertFixedDeposit(FixedDeposit(amount = amount, durationDays = durationDays, interestPercent = interestPercent))
            dao.insertTransaction(BankTransaction(type = "FD_LOCKED", amount = amount, details = "Locked in FD for $durationDays Days (+${interestPercent}%)"))
            dao.insertAuditLog(AuditLog(profile.username, "Fixed Deposit Locked", "Locked ${amount} Grand Currency inside structured FD plan."))
            addNotification("Fixed Deposit Locked", "Locked ${amount} in bank vault. Maturity in $durationDays days for +$interestPercent% returns.", "INFO")
            return true
        }
        return false
    }

    suspend fun claimMaturedDeposit(fd: FixedDeposit) {
        val profile = getMeProfile()
        val yieldAmount = fd.amount + (fd.amount * fd.interestPercent / 100)
        dao.insertProfile(profile.copy(bankBalance = profile.bankBalance + yieldAmount))
        dao.insertFixedDeposit(fd.copy(isMatured = true))
        dao.insertTransaction(BankTransaction(type = "FD_MATURED", amount = yieldAmount, details = "Matured FD plan returned"))
        dao.insertAuditLog(AuditLog(profile.username, "Claimed FD Maturity", "Returned ${yieldAmount} Grand currency after mature savings completion."))
        addNotification("Savings Portfolio Matured", "Your vault deposit matured! Collected ${yieldAmount} back automatically.", "REWARD")
    }

    // --- Marketplace Transactions ---
    suspend fun purchaseListing(listing: MarketListing): Boolean {
        val profile = getMeProfile()
        if (profile.bankBalance >= listing.askingPrice) {
            // Update Buyer
            val updatedMe = profile.copy(
                bankBalance = profile.bankBalance - listing.askingPrice,
                completedDeals = profile.completedDeals + 1,
                netWorthNeedsReverification = if (profile.isNetWorthVerified) true else profile.netWorthNeedsReverification
            )
            dao.insertProfile(updatedMe)

            // Update Listing Status
            dao.updateListing(listing.copy(status = "SOLD"))

            // Record Bank Transaction
            dao.insertTransaction(BankTransaction(type = "MARKET_BUY", amount = listing.askingPrice, details = "Bought: ${listing.title}"))

            // Give seller their money (if in local simulation db)
            val seller = dao.getUserProfile(listing.sellerId)
            if (seller != null) {
                // Not me, other seed user
                val isSellerMe = (seller.id == "me")
                dao.insertProfile(seller.copy(
                    bankBalance = seller.bankBalance + listing.askingPrice, 
                    completedDeals = seller.completedDeals + 1,
                    netWorthNeedsReverification = if (seller.isNetWorthVerified) true else seller.netWorthNeedsReverification
                ))
            }

            // Insert into local notifications
            addNotification("Purchase Successful!", "You purchased '${listing.title}' for ${listing.askingPrice} Grand Currency.", "DEAL")
            dao.insertAuditLog(AuditLog(profile.username, "Item Purchase", "Purchased listing ID #${listing.id} (${listing.title}) for ${listing.askingPrice}."))
            return true
        }
        return false
    }

    suspend fun createNewListing(listing: MarketListing) {
        val profile = getMeProfile()
        dao.insertListing(listing.copy(sellerId = "me", sellerName = profile.username, sellerReputation = profile.reputation, isVerifiedSeller = profile.isVerified))
        // Flag needs reverification on asset added
        dao.insertProfile(profile.copy(
            netWorthNeedsReverification = if (profile.isNetWorthVerified) true else profile.netWorthNeedsReverification
        ))
        addNotification("Listing Published!", "Your listing '${listing.title}' is now live on the global GRAND RP board.", "INFO")
        dao.insertAuditLog(AuditLog(profile.username, "Created Listing", "Published listing '${listing.title}' with price ${listing.askingPrice}."))
    }

    suspend fun deleteOrArchiveListing(listing: MarketListing) {
        val profile = getMeProfile()
        dao.updateListing(listing.copy(status = "ARCHIVED"))
        // Flag needs reverification on asset sold/removed
        dao.insertProfile(profile.copy(
            netWorthNeedsReverification = if (profile.isNetWorthVerified) true else profile.netWorthNeedsReverification
        ))
        dao.insertAuditLog(AuditLog(profile.username, "Archived Listing", "Removed or archived listing ID #${listing.id} from market."))
    }

    // --- Real Cash Deals Center (RCD) ---
    suspend fun submitRcdDeal(listing: MarketListing, paymentMethod: String, fiatAmount: Double, notes: String) {
        val profile = getMeProfile()
        val deal = RcdDeal(
            listingId = listing.id,
            assetTitle = listing.title,
            category = listing.category,
            sellerId = listing.sellerId,
            sellerName = listing.sellerName,
            dealAmount = listing.askingPrice,
            realCurrencyPrice = fiatAmount,
            paymentMethod = paymentMethod,
            notes = notes
        )
        dao.insertRcdDeal(deal)
        addNotification("RCD Ticket Submitted", "Ticket for '${listing.title}' sent to review. Complete verification by uploading payment proof.", "ALERT")
        dao.insertAuditLog(AuditLog(profile.username, "Initiated RCD Deal", "Opened payment review deal tracker for '${listing.title}' matching fiat cost ${fiatAmount}."))
    }

    suspend fun updateRcdDealStatus(deal: RcdDeal, newStatus: String) {
        val profile = getMeProfile()
        dao.updateRcdDeal(deal.copy(staffReviewStatus = newStatus))
        dao.insertAuditLog(AuditLog("Staff Panel", "Moderated RCD Deal", "Dealt review ID #${deal.id} marked as $newStatus."))
        
        // If approved, complete the deal in-game automatically for the buyer!
        if (newStatus == "APPROVED" && deal.buyerId == "me") {
            // Find listing and mark sold
            val activeListings = dao.getAllListingsFlow().firstOrNull() ?: emptyList()
            val listing = activeListings.find { it.id == deal.listingId }
            if (listing != null) {
                dao.updateListing(listing.copy(status = "SOLD"))
            }
            addNotification("Deal Verified! ✅", "Staff Approved your payments for '${deal.assetTitle}'. Premium asset unlocked!", "REWARD")
        }
    }

    // --- Crates, Coins & Store Items ---
    suspend fun buyCoins(amount: Int, costFiat: Double): Boolean {
        val profile = getMeProfile()
        val updated = profile.copy(coinBalance = profile.coinBalance + amount)
        dao.insertProfile(updated)
        dao.insertTransaction(BankTransaction(type = "COIN_BUY", amount = amount.toLong(), details = "Purchased $amount Kat Coins"))
        dao.insertAuditLog(AuditLog(profile.username, "Coin Donation", "Purchased $amount Kat Coins dynamically via simulated secure checkout."))
        addNotification("Coins Purchased", "Successfully credited $amount Kat Coins. Thank you for supporting KAT_MARKET_NIKA!", "REWARD")
        return true
    }

    suspend fun buyVip(planDays: Int, costCoins: Int): Boolean {
        val profile = getMeProfile()
        if (profile.coinBalance >= costCoins) {
            val updated = profile.copy(
                coinBalance = profile.coinBalance - costCoins,
                hasVip = true,
                vipDaysLeft = profile.vipDaysLeft + planDays,
                role = if (profile.role == "Normal User") "VIP" else profile.role
            )
            dao.insertProfile(updated)
            dao.insertAuditLog(AuditLog(profile.username, "VIP Enrollment", "Enabled VIP status subscription plan ($planDays Days)."))
            addNotification("VIP Center Activated!", "$planDays Days of elite VIP benefits unlocked. Welcome, legend!", "REWARD")
            return true
        }
        return false
    }

    suspend fun buyCrateFromStore(crateName: String, crateType: String, costCoins: Int): Boolean {
        val profile = getMeProfile()
        if (profile.coinBalance >= costCoins) {
            // Deduct Coins
            dao.insertProfile(profile.copy(coinBalance = profile.coinBalance - costCoins))
            // Give Crate to Inventory
            val inventory = dao.getInventoryFlow("me").firstOrNull() ?: emptyList()
            val existing = inventory.find { it.name == crateName && it.type == crateType }
            if (existing != null) {
                dao.insertInventoryItem(existing.copy(quantity = existing.quantity + 1))
            } else {
                dao.insertInventoryItem(InventoryItem(name = crateName, type = crateType, quantity = 1, valueCoins = costCoins))
            }
            dao.insertAuditLog(AuditLog(profile.username, "Purchased Store Box", "Bought crate ($crateName) worth $costCoins coins."))
            addNotification("Crate Deposited", "$crateName successfully placed inside your offline inventory hub.", "INFO")
            return true
        }
        return false
    }

    suspend fun openCrateFromInventory(item: InventoryItem): String {
        if (item.quantity <= 0) return "Crate is empty!"
        val profile = getMeProfile()

        // Roll logic:
        val rand = Random.nextInt(100)
        val rewardMessage = when {
            rand < 40 -> { // Cash prize
                val rewardCash = (10000000L..50000000L).random() // 10M to 50M
                dao.insertProfile(profile.copy(walletBalance = profile.walletBalance + rewardCash))
                "💰 Cash Prize! You rolled and won ${rewardCash} Grand Currency!"
            }
            rand < 70 -> { // Coin prize
                val rewardCoins = (100..400).random()
                dao.insertProfile(profile.copy(coinBalance = profile.coinBalance + rewardCoins))
                "🪙 Gold Jackpot! Won $rewardCoins Kat Coins!"
            }
            rand < 85 -> { // VIP Days
                dao.insertProfile(profile.copy(hasVip = true, vipDaysLeft = profile.vipDaysLeft + 7, role = if (profile.role == "Normal User") "VIP" else profile.role))
                "💎 VIP PASS! Successfully rolled 7 Days of VIP Access perks."
            }
            else -> { // Exclusive Custom Title
                val titles = listOf("Grand Admiral", "Arzamas Kingpin", "VIP Elite", "Nika Mogul", "Wheelman")
                val selectedTitle = titles.random()
                // Check if title already in inventory, if not add
                dao.insertInventoryItem(InventoryItem(name = "Title: $selectedTitle", type = "TITLE", quantity = 1, valueCoins = 100))
                db.marketDao().insertProfile(profile.copy(title = selectedTitle))
                "🎖 EXCLUSIVE TITLE! Unlocked the epic player title banner: '$selectedTitle'!"
            }
        }

        if (item.quantity == 1) {
            dao.deleteInventoryItem(item)
        } else {
            dao.updateInventoryItem(item.copy(quantity = item.quantity - 1))
        }

        dao.insertAuditLog(AuditLog(profile.username, "Opened Box Loot", "Opened ${item.name} from inventory and rolled rewards."))
        addNotification("Crate Opened!", rewardMessage, "REWARD")
        return rewardMessage
    }

    // --- Families Center ---
    suspend fun createFamily(familyName: String, costCoins: Int): Boolean {
        val profile = getMeProfile()
        if (profile.coinBalance >= costCoins && profile.familyName.isBlank()) {
            dao.insertProfile(profile.copy(coinBalance = profile.coinBalance - costCoins, familyName = familyName))
            dao.insertFamily(Family(name = familyName, leaderId = "me", leaderName = profile.username, memberCount = 1, vaultBalance = 0L, description = "New alliance on Grand RP.", level = 1, logoIndex = Random.nextInt(5)))
            addNotification("Family Registered", "Successfully founded family '$familyName'! Recruits can join now.", "INFO")
            dao.insertAuditLog(AuditLog(profile.username, "Formed Family Alliance", "Created family alliance matching brand: '$familyName'"))
            return true
        }
        return false
    }

    suspend fun depositToFamilyBank(family: Family, amount: Long): Boolean {
        val profile = getMeProfile()
        if (profile.bankBalance >= amount) {
            dao.insertProfile(profile.copy(bankBalance = profile.bankBalance - amount))
            dao.updateFamily(family.copy(vaultBalance = family.vaultBalance + amount))
            dao.insertTransaction(BankTransaction(type = "FAMILY_VAULT", amount = amount, details = "Deposited to ${family.name} vault"))
            dao.insertAuditLog(AuditLog(profile.username, "Family Bank Donation", "Sent ${amount} bank currency to ${family.name} bank vault."))
            addNotification("Family Bank Updated", "Contributed ${amount} directly to the alliance vault.", "INFO")
            return true
        }
        return false
    }

    // --- Marriage ---
    suspend fun proposeMarriage(partnerName: String): Boolean {
        val profile = getMeProfile()
        if (profile.partnerName.isBlank()) {
            dao.insertProfile(profile.copy(partnerName = partnerName))
            dao.insertAuditLog(AuditLog(profile.username, "Engagement Marriage", "Officially registered couple tie with: $partnerName."))
            addNotification("Marriage Registered!", "Congratulations! You are now locked in a couple profile with $partnerName.", "REWARD")
            return true
        }
        return false
    }

    // --- Advertisements ---
    suspend fun purchaseAd(title: String, description: String, category: String, promoType: String, costCoins: Int): Boolean {
        val profile = getMeProfile()
        if (profile.coinBalance >= costCoins) {
            dao.insertProfile(profile.copy(coinBalance = profile.coinBalance - costCoins))
            dao.insertAd(Advertisement(title = title, description = description, category = category, advertiserId = "me", advertiserName = profile.username, promotionType = promoType, budgetCoins = costCoins, isApproved = false))
            dao.insertAuditLog(AuditLog(profile.username, "Paid Ad Campaign", "Submitted advertisement '$title' with $promoType priority."))
            addNotification("Ad Submitted for Approval", "Your promotional campaign is pending staff approval.", "INFO")
            return true
        }
        return false
    }

    suspend fun approveAdCampaign(ad: Advertisement) {
        dao.insertAd(ad.copy(isApproved = true))
        dao.insertAuditLog(AuditLog("Admin Panel", "Approved Ad Block", "Allowed ad campaign ID #${ad.id} to render publicly."))
    }

    // --- Review and moderation panel ---
    suspend fun addVouch(targetUserId: String, text: String, rating: Int) {
        val profile = getMeProfile()
        dao.insertVouch(UserVouch(userId = targetUserId, authorName = profile.username, rating = rating, comment = text))
        
        // Update local reputation simulation if it's "me"
        if (targetUserId == "me") {
            val me = getMeProfile()
            val newRep = if (rating >= 4) (me.reputation + 2).coerceAtMost(100) else (me.reputation - 5).coerceAtLeast(0)
            dao.insertProfile(me.copy(reputation = newRep, vouchesCount = me.vouchesCount + 1))
        }
        dao.insertAuditLog(AuditLog(profile.username, "Left Vouch", "Wrote feedback or rating for ID: $targetUserId."))
    }

    // --- App Notifications Helper ---
    suspend fun addNotification(title: String, message: String, type: String = "INFO") {
        dao.insertNotification(AppNotification(title = title, message = message, type = type))
        
        // Forward to Discord if enabled
        val profile = dao.getUserProfile("me")
        if (profile != null && profile.discordUsername != null && profile.discordNotificationsEnabled) {
            dao.insertNotification(
                AppNotification(
                    title = "⚫ [DISCORD BOT] Direct Forwarded",
                    message = "Trigger Event \"$title\" received! Sent to Discord @${profile.discordUsername} direct message link.",
                    type = "INFO"
                )
            )
        }
    }

    suspend fun dismissAllNotifications() {
        dao.markAllNotificationsRead("me")
    }

    // --- Admin Dashboard moderations ---
    suspend fun updateMeVerificationStatus(verified: Boolean) {
        val profile = getMeProfile()
        dao.insertProfile(profile.copy(isVerified = verified, role = if (verified) "Verified Trader" else "Normal User"))
        dao.insertAuditLog(AuditLog("Admin Panel", "Verification Moderation", "Configured verify credentials badge for player me: $verified."))
    }

    suspend fun rewardMeWithCoins(coins: Int) {
        val profile = getMeProfile()
        dao.insertProfile(profile.copy(coinBalance = profile.coinBalance + coins))
        dao.insertAuditLog(AuditLog("Owner", "Economy Grant", "Awarded bonus ${coins} coins directly to account."))
    }

    // --- Complete owner cleanup ---
    suspend fun fullOwnerDatabaseCleanup() {
        dao.clearAllListings()
        dao.clearBusinesses()
        dao.clearFamilies()
        dao.clearRcdDeals()
        dao.clearAllInventory()
        dao.clearAuditLogs()
        // Reset local me profile
        val profile = UserProfile(
            id = "me",
            username = "NIKA_BOSS_RP",
            role = "Owner",
            reputation = 100,
            walletBalance = 1000000000L, // 1B for owner sandbox
            bankBalance = 5000000000L,   // 5B
            coinBalance = 10000,
            isVerified = true,
            hasVip = true,
            familyName = "Nika Syndicate",
            title = "App Founder"
        )
        dao.insertProfile(profile)
        // Add audit log
        dao.insertAuditLog(AuditLog("Owner Portal", "Factory Database Reset", "Wiped all data and re-initialized secure master developer environment values."))
        addNotification("Factory Reset Complete", "All sandbox databases have been scrubbed and owner parameters loaded.", "ALERT")
    }

    // --- Chat System Operations ---
    val allChatMessagesFlow: Flow<List<ChatMessage>> = dao.getAllChatMessagesFlow()

    suspend fun sendChatMessage(listingId: Int, listingTitle: String, receiverId: String, receiverName: String, text: String): ChatMessage {
        val me = getMeProfile()
        val msg = ChatMessage(
            listingId = listingId,
            listingTitle = listingTitle,
            senderId = "me",
            senderName = me.username,
            receiverId = receiverId,
            receiverName = receiverName,
            message = text,
            timestamp = System.currentTimeMillis()
        )
        dao.insertChatMessage(msg)
        return msg
    }

    suspend fun sendChatMessageWithDetailedFields(
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
    ): ChatMessage {
        val me = getMeProfile()
        val msg = ChatMessage(
            listingId = listingId,
            listingTitle = listingTitle,
            senderId = "me",
            senderName = me.username,
            receiverId = receiverId,
            receiverName = receiverName,
            message = text,
            timestamp = System.currentTimeMillis(),
            attachmentType = attachmentType,
            attachmentPath = attachmentPath,
            attachmentId = attachmentId,
            attachmentTitle = attachmentTitle,
            replyToId = replyToId,
            replyToText = replyToText
        )
        dao.insertChatMessage(msg)
        return msg
    }

    suspend fun insertReceivedChatMessage(msg: ChatMessage) {
        dao.insertChatMessage(msg)
        addNotification("New Text Message", "${msg.senderName}: \"${msg.message}\"", "INFO")
    }

    suspend fun updateChatMessage(msg: ChatMessage) {
        dao.updateChatMessage(msg)
    }

    suspend fun updateListingMediaStatus(listing: MarketListing, newStatus: String) {
        dao.updateListing(listing.copy(mediaStatus = newStatus))
        dao.insertAuditLog(AuditLog("Staff Moderation", "Media Status", "Listing ID #${listing.id} set to $newStatus."))
    }

    suspend fun updateListingWatermark(listing: MarketListing, enabled: Boolean) {
        dao.updateListing(listing.copy(watermarked = enabled))
    }

    suspend fun updateListing(listing: MarketListing) {
        dao.updateListing(listing)
    }

    suspend fun createCoinPurchaseRequest(packageId: String, packageName: String, coins: Int, priceInr: Double, upiTxId: String, screenshotPath: String) {
        val user = getMeProfile()
        val request = CoinPurchaseRequest(
            packageId = packageId,
            packageName = packageName,
            coinAmount = coins,
            amountInr = priceInr,
            transactionId = upiTxId,
            proofImagePath = screenshotPath,
            status = "PENDING",
            userId = user.id,
            username = user.username
        )
        dao.insertCoinPurchase(request)
        addNotification("🪙 Coin Purchase Submitted", "Your order for $packageName ($coins Coins) is in reviews. Support staff will verify soon.", "INFO")
        dao.insertAuditLog(AuditLog(user.username, "Submit Coin Purchase", "Request for $packageName ($coins Coins, $$priceInr USD, TxID: $upiTxId) submitted."))
    }

    suspend fun processCoinPurchaseRequest(request: CoinPurchaseRequest, newStatus: String, feedback: String = "") {
        val updated = request.copy(status = newStatus, notes = feedback, reviewerFeedback = feedback, timestamp = System.currentTimeMillis())
        dao.updateCoinPurchase(updated)

        if (newStatus == "APPROVED") {
            val targetUserId = request.userId
            val user = dao.getUserProfile(targetUserId)
            if (user != null) {
                dao.insertProfile(user.copy(coinBalance = user.coinBalance + request.coinAmount))
                dao.insertTransaction(
                    BankTransaction(
                        type = "COIN_PURCHASE",
                        amount = request.coinAmount.toLong(),
                        details = "Approved buy: ${request.packageName} (+${request.coinAmount} Coins, $${request.amountInr} USD)",
                        userId = targetUserId
                    )
                )
            }
            addNotification("🪙 Coins Credited", "Approved! +${request.coinAmount} Coins added to your account for transaction: ${request.transactionId}.", "REWARD")
            dao.insertAuditLog(AuditLog("Staff Panel", "Coin Purchase Approval", "Approved purchase #${request.id}: +${request.coinAmount} coins to ${request.username}. Received: $${request.amountInr} USD."))
        } else if (newStatus == "REJECTED") {
            addNotification("❌ Coin Purchase Rejected", "Your request for ${request.packageName} was rejected. Feedback: ${feedback.ifBlank { "Invalid / missing details" }}.", "ALERT")
            dao.insertAuditLog(AuditLog("Staff Panel", "Coin Purchase Rejection", "Rejected purchase #${request.id} for ${request.username}. Reason: $feedback."))
        } else if (newStatus == "NEED_PROOF") {
            addNotification("📨 More Proof Requested", "Staff requested additional proof or details for your coin purchase: $feedback.", "ALERT")
            dao.insertAuditLog(AuditLog("Staff Panel", "Coin Purchase Proof Requested", "Requested more proof on purchase #${request.id} for ${request.username}."))
        }
    }

    // --- Net Worth Verifications Hub ---
    val allNetWorthVerificationsFlow: Flow<List<NetWorthVerification>> = dao.getAllNetWorthVerificationsFlow()

    fun calculateJsonSum(json: String, key: String): Long {
        if (json.isBlank()) return 0L
        var sum = 0L
        val regex = "\"$key\"\\s*:\\s*(\\d+)".toRegex()
        val matches = regex.findAll(json)
        for (match in matches) {
            val valueStr = match.groupValues[1]
            sum += valueStr.toLongOrNull() ?: 0L
        }
        return sum
    }

    suspend fun submitNetWorthVerification(
        bankBalance: Long,
        bankScreenshot: String,
        vehiclesJson: String,
        propertiesJson: String,
        businessesJson: String
    ) {
        val user = getMeProfile()
        val request = NetWorthVerification(
            status = "PENDING",
            bankBalance = bankBalance,
            bankScreenshotPath = bankScreenshot,
            vehiclesJson = vehiclesJson,
            propertiesJson = propertiesJson,
            businessesJson = businessesJson,
            userId = user.id,
            username = user.username,
            requestedTimestamp = System.currentTimeMillis()
        )
        dao.insertNetWorthVerification(request)

        // Reset needs-reverification warning on submission of a new active review
        dao.insertProfile(user.copy(
            netWorthNeedsReverification = false
        ))

        addNotification("💰 Verification Submitted", "Net worth verification request sent to reviews queue.", "INFO")
        dao.insertAuditLog(AuditLog(user.username, "Submit Wealth Proofs", "Bank Proof balance is $${bankBalance} along with structured asset lines."))
    }

    suspend fun processNetWorthVerificationRequest(
        request: NetWorthVerification,
        newStatus: String,
        feedback: String = ""
    ) {
        val reviewer = "Staff_Alisa"
        val approvedTime = System.currentTimeMillis()
        val updatedRequest = request.copy(
            status = newStatus,
            rejectionReason = feedback,
            reviewedBy = reviewer,
            reviewedTimestamp = approvedTime
        )
        dao.updateNetWorthVerification(updatedRequest)

        val targetUser = dao.getUserProfile(request.userId) ?: return
        if (newStatus == "APPROVED") {
            val vehiclesTotal = calculateJsonSum(request.vehiclesJson, "value")
            val propertiesTotal = calculateJsonSum(request.propertiesJson, "value")
            val businessesTotal = calculateJsonSum(request.businessesJson, "value")
            val totalVerifiedNetWorth = request.bankBalance + vehiclesTotal + propertiesTotal + businessesTotal

            val updatedUser = targetUser.copy(
                isNetWorthVerified = true,
                verifiedNetWorth = totalVerifiedNetWorth,
                verifiedBankCodeBalance = request.bankBalance,
                verifiedVehiclesWorth = vehiclesTotal,
                verifiedPropertiesWorth = propertiesTotal,
                verifiedBusinessesWorth = businessesTotal,
                netWorthVerifiedBy = reviewer,
                netWorthLastUpdated = "17 June 2026",
                netWorthNeedsReverification = false,
                netWorthRejectionReason = ""
            )
            dao.insertProfile(updatedUser)
            addNotification("🛡 Net Worth Certified! ✅", "StaffName approved your asset proofs. Net Worth updated to $${totalVerifiedNetWorth}.", "REWARD")
            dao.insertAuditLog(AuditLog("Staff Panel", "Approved Net Wealth", "Approved net worth checklist and set verified value to: ${totalVerifiedNetWorth} for user ${request.username}."))
        } else if (newStatus == "REJECTED") {
            val updatedUser = targetUser.copy(
                isNetWorthVerified = false,
                netWorthRejectionReason = feedback
            )
            dao.insertProfile(updatedUser)
            addNotification("❌ Verification Disapproved", "Your wealth proof review was rejected: $feedback", "ALERT")
            dao.insertAuditLog(AuditLog("Staff Panel", "Rejected Wealth Checklist", "Closed checklist ID #${request.id} for ${request.username} as REJECTED. Reason: $feedback"))
        }
    }

    suspend fun insertNegotiationOffer(offer: NegotiationOffer) {
        dao.insertNegotiationOffer(offer)
    }

    suspend fun updateNegotiationOffer(offer: NegotiationOffer) {
        dao.updateNegotiationOffer(offer)
    }

    suspend fun insertTradeRoom(room: TradeRoom) {
        dao.insertTradeRoom(room)
    }

    suspend fun updateTradeRoom(room: TradeRoom) {
        dao.updateTradeRoom(room)
    }

    suspend fun insertAuditLog(log: AuditLog) {
        dao.insertAuditLog(log)
    }

    suspend fun insertNotification(notification: AppNotification) {
        dao.insertNotification(notification)
    }

    // --- Scammer Reports Flow & Operations ---
    val allScammerReportsFlow: Flow<List<ScammerReport>> = dao.getAllScammerReportsFlow()

    suspend fun insertScammerReport(report: ScammerReport) {
        dao.insertScammerReport(report)
    }

    suspend fun updateScammerReport(report: ScammerReport) {
        dao.updateScammerReport(report)
    }

    suspend fun clearScammerReports() {
        dao.clearScammerReports()
    }

    // --- Bounties Flow & Operations ---
    val allBountiesFlow: Flow<List<Bounty>> = dao.getAllBountiesFlow()

    suspend fun insertBounty(bounty: Bounty) {
        dao.insertBounty(bounty)
    }

    suspend fun updateBounty(bounty: Bounty) {
        dao.updateBounty(bounty)
    }

    suspend fun clearBounties() {
        dao.clearBounties()
    }

    // --- Bounty Claims Flow & Operations ---
    val allBountyClaimsFlow: Flow<List<BountyClaim>> = dao.getAllBountyClaimsFlow()

    suspend fun insertBountyClaim(claim: BountyClaim) {
        dao.insertBountyClaim(claim)
    }

    suspend fun updateBountyClaim(claim: BountyClaim) {
        dao.updateBountyClaim(claim)
    }

    suspend fun clearBountyClaims() {
        dao.clearBountyClaims()
    }

    // --- Discord Integration Service ---
    suspend fun connectDiscord(username: String, discordId: String, avatarUrl: String?, serverMember: Boolean) {
        val profile = getMeProfile()
        dao.insertProfile(
            profile.copy(
                discordUsername = username,
                discordId = discordId,
                discordAvatarUrl = avatarUrl,
                discordJoinDate = "18.06.2024",
                discordServerMember = serverMember,
                discordRoleSynced = true
            )
        )
        insertAuditLog(AuditLog("Discord Core", "Account Link", "Connected Discord identity $username ($discordId)."))
        addNotification("⚫ Discord Account Connected!", "Your identity is now synchronized with $username on KAT_MARKET_NIKA.", "REWARD")
    }

    suspend fun disconnectDiscord() {
        val profile = getMeProfile()
        dao.insertProfile(
            profile.copy(
                discordUsername = null,
                discordId = null,
                discordAvatarUrl = null,
                discordJoinDate = null,
                discordServerMember = false,
                discordRoleSynced = false,
                discordNotificationsEnabled = false
            )
        )
        insertAuditLog(AuditLog("Discord Core", "Account Unlink", "Disconnected Discord identity from user me."))
        addNotification("⚫ Discord Account Unlinked", "Discord identity and role sync dismantled.", "ALERT")
    }

    suspend fun syncRolesFromDiscord(roles: List<String>): String {
        val profile = getMeProfile()
        var newRole = profile.role
        var isVerified = profile.isVerified
        var hasVip = profile.hasVip
        var vipDays = profile.vipDaysLeft
        
        var message = "Roles checked. No higher app role privileges found on Discord."
        
        if (roles.contains("Admin") || roles.contains("Administrator")) {
            newRole = "Administrator"
            isVerified = true
            message = "Role sync completed: App permissions upgraded to Administrator!"
        } else if (roles.contains("Staff")) {
            newRole = "Staff"
            isVerified = true
            message = "Role sync completed: App permissions upgraded to Staff!"
        } else if (roles.contains("VIP")) {
            newRole = "VIP"
            hasVip = true
            if (vipDays < 30) vipDays = 30
            message = "Role sync completed: Synced VIP status (+30 Active Days)!"
        } else if (roles.contains("Verified Trader")) {
            newRole = "Verified Trader"
            isVerified = true
            message = "Role sync completed: Verified Trader badge unlocked!"
        }
        
        dao.insertProfile(
            profile.copy(
                role = newRole,
                isVerified = isVerified,
                hasVip = hasVip,
                vipDaysLeft = vipDays,
                discordRoleSynced = true
            )
        )
        insertAuditLog(AuditLog("Discord Roles", "Role Sync", "Synced roles: ${roles.joinToString()}. New role: $newRole."))
        addNotification("🛡️ Discord Sync Triggered", message, "REWARD")
        return message
    }

    suspend fun toggleDiscordNotifications(enabled: Boolean) {
        val profile = getMeProfile()
        dao.insertProfile(profile.copy(discordNotificationsEnabled = enabled))
        insertAuditLog(AuditLog("Discord Settings", "Notification Toggle", "Set Discord direct messages alerts: $enabled."))
        addNotification(
            if (enabled) "⚫ Discord DMs Activated" else "❌ Discord DMs Deactivated",
            if (enabled) "New Offers, trade alerts, and messages will be forwarded to your Discord channel." else "Notifications muted.",
            "INFO"
        )
    }

    suspend fun updateDiscordMembershipStatus(inServer: Boolean) {
        val profile = getMeProfile()
        if (profile.discordUsername == null) return
        
        var newRole = profile.role
        var isVerified = profile.isVerified
        var hasVip = profile.hasVip
        var vipDays = profile.vipDaysLeft
        
        if (!inServer) {
            // Drop roles optionally if they left server
            if (profile.discordRoleSynced) {
                newRole = "Normal User"
                isVerified = false
                hasVip = false
                vipDays = 0
            }
        }
        
        dao.insertProfile(
            profile.copy(
                discordServerMember = inServer,
                role = newRole,
                isVerified = isVerified,
                hasVip = hasVip,
                vipDaysLeft = vipDays,
                discordRoleSynced = if (inServer) profile.discordRoleSynced else false
            )
        )
        insertAuditLog(
            AuditLog(
                "Discord Core",
                "Server Status",
                "Membership status changed. Joined Server: $inServer."
            )
        )
    }
}
