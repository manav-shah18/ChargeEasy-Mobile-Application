package com.example.chargeeasy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "ChargeEasy.db";
    // INCREMENT VERSION TO FORCE onUpgrade! Or just uninstall the app.
    public static final int DATABASE_VERSION = 2;

    // --- User Table (Unchanged) ---
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_FULL_NAME = "full_name";
    public static final String COL_EMAIL = "email";
    public static final String COL_DOB = "dob";
    public static final String COL_PHONE_NUMBER = "phone_number";
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_FULL_NAME + " TEXT, " +
            COL_EMAIL + " TEXT, " +
            COL_DOB + " TEXT, " +
            COL_PHONE_NUMBER + " TEXT UNIQUE NOT NULL);";

    // --- Bookings Table (SCHEMA CHANGE) ---
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String COL_BOOKING_ID = "booking_id"; // Primary Key
    public static final String COL_STATION_ID_FK = "station_id_fk";
    public static final String COL_USER_ID_FK = "user_id_fk";
    public static final String COL_STATION_NAME = "station_name";
    public static final String COL_STATION_ADDRESS = "station_address";
    public static final String COL_BOOKING_START_TIME_MS = "booking_start_time_ms"; // <-- NEW
    public static final String COL_BOOKING_END_TIME_MS = "booking_end_time_ms";   // <-- NEW
    public static final String COL_DURATION_MINUTES = "duration_minutes";       // <-- NEW
    public static final String COL_COST = "cost";
    public static final String COL_IS_COMPLETED = "is_completed";

    // Old columns (booking_date, booking_time, duration) are REMOVED

    private static final String CREATE_TABLE_BOOKINGS = "CREATE TABLE " + TABLE_BOOKINGS + " (" +
            COL_BOOKING_ID + " TEXT PRIMARY KEY, " +
            COL_USER_ID_FK + " INTEGER NOT NULL, " +
            COL_STATION_ID_FK + " TEXT NOT NULL, " +
            COL_STATION_NAME + " TEXT, " +
            COL_STATION_ADDRESS + " TEXT, " +
            COL_BOOKING_START_TIME_MS + " INTEGER NOT NULL, " + // <-- NEW
            COL_BOOKING_END_TIME_MS + " INTEGER NOT NULL, " +   // <-- NEW
            COL_DURATION_MINUTES + " INTEGER, " +             // <-- NEW
            COL_COST + " REAL, " +
            COL_IS_COMPLETED + " INTEGER NOT NULL, " +
            "FOREIGN KEY(" + COL_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "));";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_BOOKINGS);
        Log.d(TAG, "Database tables (with time range logic) created successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database. Old data will be lost.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // --- USER METHODS (Unchanged) ---
    public long addUser(String f, String e, String d, String p) { /* ... same as before ... */
        SQLiteDatabase db = this.getWritableDatabase(); ContentValues cv = new ContentValues();
        cv.put(COL_FULL_NAME, f); cv.put(COL_EMAIL, e); cv.put(COL_DOB, d); cv.put(COL_PHONE_NUMBER, p);
        long id = db.insert(TABLE_USERS, null, cv); db.close(); return id;
    }
    public boolean checkUserExists() { /* ... same as before ... */
        SQLiteDatabase db = this.getReadableDatabase(); Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        boolean exists = c.getCount() > 0; c.close(); db.close(); return exists;
    }
    public Cursor getUserByPhone(String p) { /* ... same as before ... */
        SQLiteDatabase db = this.getReadableDatabase(); Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_PHONE_NUMBER + " = ?", new String[]{p});
        if (c != null) c.moveToFirst(); return c;
    }
    public Cursor getUserProfileById(long id) { /* ... same as before ... */
        SQLiteDatabase db = this.getReadableDatabase(); Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_ID + " = " + id, null);
        if (c != null) c.moveToFirst(); return c;
    }

    // --- BOOKING METHODS (Updated for Time Range) ---

    public boolean insertBooking(BookingItem booking, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_BOOKING_ID, booking.getBookingId());
        cv.put(COL_USER_ID_FK, userId);
        cv.put(COL_STATION_ID_FK, booking.getStationId());
        cv.put(COL_STATION_NAME, booking.getStationName());
        cv.put(COL_STATION_ADDRESS, booking.getStationAddress());
        cv.put(COL_BOOKING_START_TIME_MS, booking.getStartTimeMs()); // <-- NEW
        cv.put(COL_BOOKING_END_TIME_MS, booking.getEndTimeMs());   // <-- NEW
        cv.put(COL_DURATION_MINUTES, booking.getDurationMinutes()); // <-- NEW
        cv.put(COL_COST, booking.getCost());
        cv.put(COL_IS_COMPLETED, booking.isCompleted() ? 1 : 0);

        long result = db.insert(TABLE_BOOKINGS, null, cv);
        db.close();
        return result != -1;
    }

    public List<BookingItem> getBookings(long userId, boolean isCompleted) {
        List<BookingItem> bookingList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        int status = isCompleted ? 1 : 0;

        String query = "SELECT * FROM " + TABLE_BOOKINGS +
                " WHERE " + COL_IS_COMPLETED + " = " + status +
                " AND " + COL_USER_ID_FK + " = " + userId +
                " ORDER BY " + COL_BOOKING_START_TIME_MS + " DESC"; // Sort by newest first

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int idCol = cursor.getColumnIndexOrThrow(COL_BOOKING_ID);
                int stationIdCol = cursor.getColumnIndexOrThrow(COL_STATION_ID_FK);
                int nameCol = cursor.getColumnIndexOrThrow(COL_STATION_NAME);
                int addressCol = cursor.getColumnIndexOrThrow(COL_STATION_ADDRESS);
                int startCol = cursor.getColumnIndexOrThrow(COL_BOOKING_START_TIME_MS); // <-- NEW
                int endCol = cursor.getColumnIndexOrThrow(COL_BOOKING_END_TIME_MS);     // <-- NEW
                int durationCol = cursor.getColumnIndexOrThrow(COL_DURATION_MINUTES); // <-- NEW
                int costCol = cursor.getColumnIndexOrThrow(COL_COST);

                String bookingId = cursor.getString(idCol);
                String stationId = cursor.getString(stationIdCol);
                String stationName = cursor.getString(nameCol);
                String address = cursor.getString(addressCol);
                long startTime = cursor.getLong(startCol);
                long endTime = cursor.getLong(endCol);
                int duration = cursor.getInt(durationCol);
                double cost = cursor.getDouble(costCol);

                BookingItem booking = new BookingItem(bookingId, stationId, stationName, address, startTime, endTime, duration, cost, isCompleted);
                bookingList.add(booking);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bookingList;
    }

    // --- *** NEW OVERLAP CHECK METHOD *** ---
    /**
     * Counts active, overlapping bookings for a station at a specific time range.
     * This is the core clash avoidance logic.
     */
    public int getOverlappingBookingCount(String stationId, long newStartTimeMs, long newEndTimeMs) {
        SQLiteDatabase db = this.getReadableDatabase();

        // This query finds any booking that overlaps with the requested time range.
        // An overlap occurs if:
        // (Existing Start < New End) AND (Existing End > New Start)
        String query = "SELECT COUNT(*) FROM " + TABLE_BOOKINGS +
                " WHERE " + COL_STATION_ID_FK + " = ? " +
                " AND " + COL_IS_COMPLETED + " = 0 " + // Only check active bookings
                " AND (" + COL_BOOKING_START_TIME_MS + " < " + newEndTimeMs +
                " AND " + COL_BOOKING_END_TIME_MS + " > " + newStartTimeMs + ")";

        Cursor cursor = db.rawQuery(query, new String[]{stationId});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Found " + count + " overlapping bookings for " + stationId);
        return count;
    }

    // --- (updateBookingStatus and deleteBooking are unchanged) ---
    public void updateBookingStatus(String bookingId, boolean isCompleted) { /* ... same as before ... */
        SQLiteDatabase db = this.getWritableDatabase(); ContentValues cv = new ContentValues();
        cv.put(COL_IS_COMPLETED, isCompleted ? 1 : 0);
        db.update(TABLE_BOOKINGS, cv, COL_BOOKING_ID + " = ?", new String[]{bookingId}); db.close();
    }
    public void deleteBooking(String bookingId) { /* ... same as before ... */
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKINGS, COL_BOOKING_ID + " = ?", new String[]{bookingId}); db.close();
    }
}