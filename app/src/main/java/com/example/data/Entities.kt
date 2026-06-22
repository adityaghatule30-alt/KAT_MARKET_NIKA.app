package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@Entity(tableName = "user_profiles")
data class UserProfile(
    val username: String,
    @PrimaryKey val id: String = "me", // Current local user id "me", others for other players
    val role: String = "Normal User", // "Normal User", "Verified Trader", "VIP", "Staff", "Administrator", "Owner"
    val reputation: Int = 100, // 0 to 100
    val walletBalance: Long = 1000000L, // 1M start
    val bankBalance: Long = 5000000L, // 5M start
    val coinBalance: Int = 250, // Coins
    val isVerified: Boolean = false,
    val hasVip: Boolean = false,
    val vipDaysLeft: Int = 0,
    val familyName: String = "",
    val joinedDate: String = "17.06.2026",
    val title: String = "Novice Trader",
    val avatarIndex: Int = 0,
    val completedDeals: Int = 0,
    val vouchesCount: Int = 0,
    val partnerName: String = "", // Marriage
    
    // Discord integration fields
    val discordId: String? = null,
    val discordUsername: String? = null,
    val discordAvatarUrl: String? = null,
    val discordJoinDate: String? = null,
    val discordServerMember: Boolean = false,
    val discordRoleSynced: Boolean = false,
    val discordNotificationsEnabled: Boolean = false,
    
    // Net Worth Verification fields
    val isNetWorthVerified: Boolean = false,
    val verifiedNetWorth: Long = 0L,
    val verifiedBankCodeBalance: Long = 0L,
    val verifiedVehiclesWorth: Long = 0L,
    val verifiedPropertiesWorth: Long = 0L,
    val verifiedBusinessesWorth: Long = 0L,
    val netWorthVerifiedBy: String = "",
    val netWorthLastUpdated: String = "",
    val netWorthNeedsReverification: Boolean = false,
    val netWorthRejectionReason: String = "",
    val onlineStatus: String = "ONLINE" // "ONLINE", "AWAY", "OFFLINE"
)

@Entity(tableName = "net_worth_verifications")
data class NetWorthVerification(
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val bankBalance: Long = 0L,
    val bankScreenshotPath: String = "", // e.g. "Screenshots_Bank.png"
    val vehiclesJson: String = "", // JSON list of vehicles: [{"name":"BMW M3","value":15000000,"owners":1}]
    val propertiesJson: String = "", // JSON list of properties: [{"num":15,"location":"Arzamas","value":85000000}]
    val businessesJson: String = "", // JSON list of businesses: [{"name":"Shop 24/7","value":100000000,"profit":8000000}]
    val requestedTimestamp: Long = System.currentTimeMillis(),
    val reviewedTimestamp: Long = 0L,
    val reviewedBy: String = "",
    val rejectionReason: String = "",
    val userId: String = "me",
    val username: String = "me_user",
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "listings")
data class MarketListing(
    val title: String,
    val category: String, // "Vehicle", "Property", "Business", "Skin", "Item"
    val subType: String, // "Car", "Bike", "House", "Apartment", "24/7 Store", "Gas Station", "Collectibles" etc.
    val statePrice: Long,
    val askingPrice: Long,
    val sellerId: String,
    val sellerName: String,
    val ownerCount: Int = 1,
    val licensePlate: String = "",
    val location: String = "",
    val profitDaily: Long = 0L,
    val notes: String = "",
    val sellerReputation: Int = 100,
    val isVerifiedSeller: Boolean = false,
    val isFeatured: Boolean = false,
    val isUrgent: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE", // "ACTIVE", "SOLD", "ARCHIVED"
    val views: Int = 12,
    val favoritesCount: Int = 0,
    val imageUrl: String = "", // Generated asset Or symbol
    val images: List<String> = emptyList(), // Swipe gallery
    val videoUrl: String = "", // Optional video URL
    val mediaStatus: String = "APPROVED", // "APPROVED", "PENDING", "REJECTED", "FLAGGED", "REQUEST_NEW"
    val watermarked: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    val listingId: Int,
    val listingTitle: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val receiverName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val attachmentType: String = "TEXT", // "TEXT", "IMAGE", "LISTING", "BUSINESS", "PROPERTY", "VEHICLE"
    val attachmentPath: String = "",
    val attachmentId: Int = 0,
    val attachmentTitle: String = "",
    val replyToId: Int = 0,
    val replyToText: String = "",
    val reactions: String = "", // comma-separated emojis, e.g. "👍,❤️" or react-user formatted
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "rcd_deals")
data class RcdDeal(
    val listingId: Int,
    val assetTitle: String,
    val category: String,
    val sellerId: String,
    val sellerName: String,
    val dealAmount: Long, // e.g. RCD real money price (e.g. 500 Rubles)
    val paymentMethod: String, // "UPI", "Bank Transfer", "PayPal", "Crypto"
    val buyerId: String = "me",
    val buyerName: String = "NIKA_BOSS_RP",
    val realCurrencyPrice: Double = 0.0, // Money trade
    val proofImgName: String = "Screenshot_Proof.png",
    val buyerConfirmed: Boolean = true,
    val sellerConfirmed: Boolean = false,
    val staffReviewStatus: String = "PENDING", // "PENDING", "APPROVED", "REJECTED", "SUSPICIOUS"
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "fixed_deposits")
data class FixedDeposit(
    val amount: Long,
    val durationDays: Int, // 7, 14, 30, 60, 90
    val interestPercent: Int, // +10%, +15% etc
    val timestampStart: Long = System.currentTimeMillis(),
    val isMatured: Boolean = false,
    val ownerId: String = "me",
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "transactions")
data class BankTransaction(
    val type: String, // "DEPOSIT", "WITHDRAW", "TRANSFER", "COIN_PURCHASE", "MARKETPLAY"
    val amount: Long,
    val details: String,
    val userId: String = "me",
    val timestamp: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "businesses")
data class RegisteredBusiness(
    val name: String,
    val type: String, // "24/7 Store", "Gas Station", "Restaurant", etc.
    val location: String,
    val dailyProfit: Long,
    val statePrice: Long,
    val estimatedValue: Long,
    val ownerId: String,
    val ownerName: String,
    val rating: Double = 5.0,
    val employeesCount: Int = 2,
    val vacancyOpen: Int = 0,
    val isForSale: Boolean = false,
    val askingPrice: Long = 0L,
    val isPromoted: Boolean = false,
    val promoType: String = "", // "STANDARD", "FEATURED", "URGENT", "PREMIUM"
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "families")
data class Family(
    val name: String,
    val leaderId: String,
    val leaderName: String,
    val memberCount: Int = 1,
    val vaultBalance: Long = 0L,
    val description: String = "",
    val level: Int = 1,
    val logoIndex: Int = 0,
    val bannerText: String = "",
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "advertisements")
data class Advertisement(
    val title: String,
    val description: String,
    val category: String, // "Business", "Family", "Marketplace", "Recruitment"
    val advertiserId: String,
    val advertiserName: String,
    val promotionType: String, // "STANDARD", "FEATURED", "URGENT", "PREMIUM"
    val budgetCoins: Int,
    val views: Int = 0,
    val clicks: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isApproved: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "app_notifications")
data class AppNotification(
    val title: String,
    val message: String,
    val type: String, // "INFO", "ALERT", "BID", "REWARD", "DEAL"
    val isRead: Boolean = false,
    val userId: String = "me",
    val timestamp: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "marketplace_vouches")
data class UserVouch(
    val userId: String, // Target user profile id
    val authorName: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val type: String = "Positive", // "Positive", "Neutral", "Negative"
    val timestamp: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "inventory_items")
data class InventoryItem(
    val name: String,
    val type: String, // "CRATE_GOLD", "CRATE_SILVER", "BADGE", "TITLE", "VIP_COSMETIC"
    val quantity: Int = 1,
    val valueCoins: Int = 0,
    val userId: String = "me",
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    val actorName: String,
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "coin_purchases")
data class CoinPurchaseRequest(
    val packageId: String,          // "starter", "basic", "standard", "premium", "ultimate"
    val packageName: String,        // e.g. "Starter Pack"
    val coinAmount: Int,            // 100, 500 etc.
    val amountInr: Double,          // ₹10, ₹50 etc.
    val upiId: String = "adityaghatule30@okaxis",
    val transactionId: String,      // User entered tx id
    val proofImagePath: String,     // Screenshot fake path
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED", "NEED_PROOF"
    val userId: String = "me",
    val username: String = "me_user",
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val rejectionReason: String = "",
    val reviewerFeedback: String = "",
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "negotiation_offers")
data class NegotiationOffer(
    val listingId: Int,
    val listingTitle: String,
    val buyerId: String,
    val buyerName: String,
    val sellerId: String,
    val sellerName: String,
    val amount: Long,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // "PENDING", "ACCEPTED", "DECLINED", "COUNTERED"
    val counterAmount: Long = 0L,
    val isCreatedByBuyer: Boolean = true,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "trade_rooms")
data class TradeRoom(
    val listingId: Int,
    val listingTitle: String,
    val agreedPrice: Long,
    val buyerId: String,
    val buyerName: String,
    val sellerId: String,
    val sellerName: String,
    val status: String = "PENDING_COMPLETION", // "PENDING_COMPLETION", "COMPLETED", "DISPUTED"
    val proofImagePath: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

class DatabaseConverters {
    private val moshi = Moshi.Builder().build()
    private val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(stringListType)

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { adapter.toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { adapter.fromJson(it) }
    }
}

@Entity(tableName = "scammer_reports")
data class ScammerReport(
    val reportedUsername: String,
    val reporterId: String = "me",
    val reporterName: String = "me_user",
    val reason: String, // "Fake Payments", "Fake Listings", "Fake Properties", "Fake Businesses", "Fake Screenshots", "Impersonation", "Suspicious Activity"
    val description: String,
    val evidenceScreenshot: String = "evidence_scam_preview.png",
    val transactionId: String = "",
    val status: String = "PENDING", // "PENDING", "UNDER_INVESTIGATION", "CONFIRMED_SCAMMER", "DISMISSED"
    val staffNotes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "bounties")
data class Bounty(
    val title: String,
    val description: String,
    val rewardType: String = "COINS", // "COINS", "VIP_DAYS", "CRATE", "BADGE"
    val rewardAmount: Int,
    val bountyType: String, // "Vehicle Wanted", "Property Wanted", "Business Wanted", "Scam Investigation", "Missing Owner Search", "Item Search"
    val expirationDate: String, // e.g. "25.06.2026"
    val creatorId: String = "me",
    val creatorName: String = "NIKA_BOSS_RP",
    val status: String = "ACTIVE", // "ACTIVE", "CLAIMED", "EXPIRED"
    val timestamp: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "bounty_claims")
data class BountyClaim(
    val bountyId: Int,
    val claimantId: String = "me",
    val claimantName: String = "me_user",
    val evidence: String,
    val information: String,
    val proof: String = "",
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val timestamp: Long = System.currentTimeMillis(),
    val bountyTitle: String = "",
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Entity(tableName = "market_transactions")
data class MarketTransaction(
    val buyerName: String,
    val sellerName: String,
    val assetTitle: String,
    val category: String, // "Vehicle", "Property", "Business", "Skin", "Item"
    val price: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val transactionType: String = "ESCROW", // "ESCROW", "RCD", "COMMUNITY"
    val isUserInvolved: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)


