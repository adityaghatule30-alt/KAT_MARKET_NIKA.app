package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UserProfile::class,
        MarketListing::class,
        RcdDeal::class,
        FixedDeposit::class,
        BankTransaction::class,
        RegisteredBusiness::class,
        Family::class,
        Advertisement::class,
        AppNotification::class,
        UserVouch::class,
        InventoryItem::class,
        AuditLog::class,
        ChatMessage::class,
        CoinPurchaseRequest::class,
        NetWorthVerification::class,
        NegotiationOffer::class,
        TradeRoom::class,
        ScammerReport::class,
        Bounty::class,
        BountyClaim::class,
        MarketTransaction::class
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun marketDao(): MarketDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kat_market_nika_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
