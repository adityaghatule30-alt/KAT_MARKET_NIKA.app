package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketDao {

    // --- User Profile ---
    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    fun getUserProfileFlow(id: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun getUserProfile(id: String): UserProfile?

    @Query("SELECT * FROM user_profiles ORDER BY (bankBalance + walletBalance) DESC")
    fun getRichListFlow(): Flow<List<UserProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    // --- Marketplace Listings ---
    @Query("SELECT * FROM listings ORDER BY isFeatured DESC, timestamp DESC")
    fun getAllListingsFlow(): Flow<List<MarketListing>>

    @Query("SELECT * FROM listings WHERE category = :category AND status = 'ACTIVE' ORDER BY isFeatured DESC, timestamp DESC")
    fun getActiveListingsByCategoryFlow(category: String): Flow<List<MarketListing>>

    @Query("SELECT * FROM listings WHERE id = :id LIMIT 1")
    fun getListingByIdFlow(id: Int): Flow<MarketListing?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: MarketListing)

    @Update
    suspend fun updateListing(listing: MarketListing)

    @Delete
    suspend fun deleteListing(listing: MarketListing)

    @Query("DELETE FROM listings")
    suspend fun clearAllListings()

    // --- Real Cash Deals (RCD) ---
    @Query("SELECT * FROM rcd_deals ORDER BY timestamp DESC")
    fun getAllRcdDealsFlow(): Flow<List<RcdDeal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRcdDeal(deal: RcdDeal)

    @Update
    suspend fun updateRcdDeal(deal: RcdDeal)

    @Query("DELETE FROM rcd_deals")
    suspend fun clearRcdDeals()

    // --- Wallet & Banking ---
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsFlow(userId: String): Flow<List<BankTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: BankTransaction)

    @Query("SELECT * FROM fixed_deposits WHERE ownerId = :userId ORDER BY isMatured ASC, timestampStart DESC")
    fun getFixedDepositsFlow(userId: String): Flow<List<FixedDeposit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFixedDeposit(fd: FixedDeposit)

    @Update
    suspend fun updateFixedDeposit(fd: FixedDeposit)

    // --- Business Network ---
    @Query("SELECT * FROM businesses ORDER BY estimatedValue DESC")
    fun getAllBusinessesFlow(): Flow<List<RegisteredBusiness>>

    @Query("SELECT * FROM businesses WHERE id = :id LIMIT 1")
    suspend fun getBusinessById(id: Int): RegisteredBusiness?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: RegisteredBusiness)

    @Update
    suspend fun updateBusiness(business: RegisteredBusiness)

    @Delete
    suspend fun deleteBusiness(business: RegisteredBusiness)

    @Query("DELETE FROM businesses")
    suspend fun clearBusinesses()

    // --- Families ---
    @Query("SELECT * FROM families ORDER BY vaultBalance DESC")
    fun getAllFamiliesFlow(): Flow<List<Family>>

    @Query("SELECT * FROM families WHERE id = :id LIMIT 1")
    suspend fun getFamilyById(id: Int): Family?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamily(family: Family)

    @Update
    suspend fun updateFamily(family: Family)

    @Delete
    suspend fun deleteFamily(family: Family)

    @Query("DELETE FROM families")
    suspend fun clearFamilies()

    // --- Advertisements ---
    @Query("SELECT * FROM advertisements ORDER BY promotionType = 'PREMIUM' DESC, promotionType = 'FEATURED' DESC, timestamp DESC")
    fun getAllAdsFlow(): Flow<List<Advertisement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAd(ad: Advertisement)

    @Update
    suspend fun updateAd(ad: Advertisement)

    // --- Notification Center ---
    @Query("SELECT * FROM app_notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsFlow(userId: String): Flow<List<AppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Query("UPDATE app_notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsRead(userId: String)

    // --- Vouch System ---
    @Query("SELECT * FROM marketplace_vouches WHERE userId = :userId ORDER BY timestamp DESC")
    fun getVouchesFlow(userId: String): Flow<List<UserVouch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVouch(vouch: UserVouch)

    // --- Inventory System ---
    @Query("SELECT * FROM inventory_items WHERE userId = :userId ORDER BY type DESC")
    fun getInventoryFlow(userId: String): Flow<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(item: InventoryItem)

    @Update
    suspend fun updateInventoryItem(item: InventoryItem)

    @Delete
    suspend fun deleteInventoryItem(item: InventoryItem)

    @Query("DELETE FROM inventory_items")
    suspend fun clearAllInventory()

    // --- Security / Audit Logs ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogsFlow(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog)

    @Query("DELETE FROM audit_logs")
    suspend fun clearAuditLogs()

    // --- Chat System ---
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessagesFlow(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Update
    suspend fun updateChatMessage(message: ChatMessage)

    @Query("UPDATE chat_messages SET isRead = 1 WHERE receiverId = :userId")
    suspend fun markAllMessagesRead(userId: String)

    // --- Coin Purchases ---
    @Query("SELECT * FROM coin_purchases ORDER BY timestamp DESC")
    fun getAllCoinPurchasesFlow(): Flow<List<CoinPurchaseRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinPurchase(purchase: CoinPurchaseRequest)

    @Update
    suspend fun updateCoinPurchase(purchase: CoinPurchaseRequest)

    @Query("DELETE FROM coin_purchases")
    suspend fun clearCoinPurchases()

    // --- Net Worth Verifications ---
    @Query("SELECT * FROM net_worth_verifications ORDER BY requestedTimestamp DESC")
    fun getAllNetWorthVerificationsFlow(): Flow<List<NetWorthVerification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNetWorthVerification(verification: NetWorthVerification)

    @Update
    suspend fun updateNetWorthVerification(verification: NetWorthVerification)

    @Query("DELETE FROM net_worth_verifications")
    suspend fun clearNetWorthVerifications()

    // --- Negotiation Offers ---
    @Query("SELECT * FROM negotiation_offers ORDER BY timestamp DESC")
    fun getAllNegotiationOffersFlow(): Flow<List<NegotiationOffer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNegotiationOffer(offer: NegotiationOffer)

    @Update
    suspend fun updateNegotiationOffer(offer: NegotiationOffer)

    // --- Trade Rooms ---
    @Query("SELECT * FROM trade_rooms ORDER BY timestamp DESC")
    fun getAllTradeRoomsFlow(): Flow<List<TradeRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTradeRoom(room: TradeRoom)

    @Update
    suspend fun updateTradeRoom(room: TradeRoom)

    // --- Scammer Reports ---
    @Query("SELECT * FROM scammer_reports ORDER BY timestamp DESC")
    fun getAllScammerReportsFlow(): Flow<List<ScammerReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScammerReport(report: ScammerReport)

    @Update
    suspend fun updateScammerReport(report: ScammerReport)

    @Query("DELETE FROM scammer_reports")
    suspend fun clearScammerReports()

    // --- Bounties ---
    @Query("SELECT * FROM bounties ORDER BY timestamp DESC")
    fun getAllBountiesFlow(): Flow<List<Bounty>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBounty(bounty: Bounty)

    @Update
    suspend fun updateBounty(bounty: Bounty)

    @Query("DELETE FROM bounties")
    suspend fun clearBounties()

    // --- Bounty Claims ---
    @Query("SELECT * FROM bounty_claims ORDER BY timestamp DESC")
    fun getAllBountyClaimsFlow(): Flow<List<BountyClaim>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBountyClaim(claim: BountyClaim)

    @Update
    suspend fun updateBountyClaim(claim: BountyClaim)

    @Query("DELETE FROM bounty_claims")
    suspend fun clearBountyClaims()
}
