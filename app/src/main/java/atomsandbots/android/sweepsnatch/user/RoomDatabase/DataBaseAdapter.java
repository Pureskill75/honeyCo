package atomsandbots.android.sweepsnatch.user.RoomDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DataBaseAdapter {

    DataBaseHelper helper;

    public DataBaseAdapter(Context context) {
        helper = new DataBaseHelper(context);
    }

    public long insert(String name, String email, String phone, String postcode, String country, String address, String image) {
        if (phone.isEmpty()) {
            phone = "N/A";
        }
        if (postcode.isEmpty()) {
            phone = "N/A";
        }
        if (country.isEmpty()) {
            country = "N/A";
        }
        if (address.isEmpty()) {
            address = "N/A";
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.NAME, name);
        values.put(DataBaseHelper.EMAIL, email);
        values.put(DataBaseHelper.PHONE, phone);
        values.put(DataBaseHelper.Postcode, postcode);
        values.put(DataBaseHelper.COUNTRY, country);
        values.put(DataBaseHelper.ADDRESS, address);
        values.put(DataBaseHelper.IMAGE, image);
        return db.insert(DataBaseHelper.TABLE_NAME, null, values);
    }

    public int update(String email, String name, String phone, String postcode, String country, String address) {
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.NAME, name);
        contentValues.put(DataBaseHelper.PHONE, phone);
        contentValues.put(DataBaseHelper.Postcode, postcode);
        contentValues.put(DataBaseHelper.COUNTRY, country);
        contentValues.put(DataBaseHelper.ADDRESS, address);

        return database.update(DataBaseHelper.TABLE_NAME, contentValues, "_id=1", null);
    }

    public int updateImage(byte[] image) {
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.IMAGE, image);
        return database.update(DataBaseHelper.TABLE_NAME, contentValues, "_id=1", null);
    }

    public Cursor getData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] column = {DataBaseHelper.NAME, DataBaseHelper.EMAIL, DataBaseHelper.PHONE, DataBaseHelper.COUNTRY, DataBaseHelper.Postcode, DataBaseHelper.ADDRESS, DataBaseHelper.IMAGE,};
        return db.query(DataBaseHelper.TABLE_NAME, column, null, null, null, null, null);
    }

    static class DataBaseHelper extends SQLiteOpenHelper {
        static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        private static final String DATABASE_NAME;

        static {
            assert user != null;
            DATABASE_NAME = "ProfileData" + user.getUid();
        }

        private static final String TABLE_NAME = "Profile_Table";
        private static final int TABLE_VERSION = 3;
        private static final String UID = "_id";
        private static final String NAME = "Name";
        private static final String EMAIL = "Email";
        private static final String PHONE = "Phone";
        private static final String Postcode = "postcode";
        private static final String COUNTRY = "Country";
        private static final String ADDRESS = "Address";
        private static final String IMAGE = "Image";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + NAME + " VARCHAR (255) ," + EMAIL + " VARCHAR (255) ," +
                "" + PHONE + " VARCHAR (255)," + Postcode + " VARCHAR (255) , " + COUNTRY + " VARCHAR (255) ," + ADDRESS + " VARCHAR(255)," + IMAGE + " BLOB );";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        Context context;

        public DataBaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, TABLE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE);
            db.execSQL(CREATE_TABLE);
        }
    }
}
