package atomsandbots.android.sweepsnatch.user.RoomDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class DrawDatabase {

    DrawDatabase.DataBaseHelper helper;
    public DrawDatabase(Context context){
        helper= new DataBaseHelper(context);
    }

    public long insert(String name,String description,String price,String category,String pid,byte[] image){
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(DrawDatabase.DataBaseHelper.NAME,name);
        values.put(DataBaseHelper.DESCRIPTION,description);
        values.put(DataBaseHelper.PRICE,price);
        values.put(DataBaseHelper.CATEGORY,category);
        values.put(DataBaseHelper.PRODUCTID,pid);
        values.put(DataBaseHelper.IMAGE, image);
        long id = db.insert(DrawDatabase.DataBaseHelper.TABLE_NAME,null,values);
        return id;
    }

    public Cursor getdata() {
        SQLiteDatabase db=helper.getWritableDatabase();
        String[] colmuns = {DrawDatabase.DataBaseHelper.NAME, DataBaseHelper.DESCRIPTION, DataBaseHelper.PRICE, DataBaseHelper.CATEGORY, DataBaseHelper.PRODUCTID, DataBaseHelper.IMAGE,};
        Cursor cursor = db.query(DrawDatabase.DataBaseHelper.TABLE_NAME,colmuns,null,null,null,null,null);
        return cursor;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static class DataBaseHelper extends SQLiteOpenHelper {
        static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        private static final String DATABASE_NAME="DrawsData"+ Objects.requireNonNull(user).getUid();
        private static final String TABLE_NAME = "Draws_Table";
        private static final int TABLE_VERSION = 2;
        private static final String UID = "_id";
        private static final String NAME = "Name";
        private static final String DESCRIPTION = "Description";
        private static final String PRICE = "Price";
        private static final String CATEGORY = "Category";
        private static final String PRODUCTID = "ProductID";
        private static final String IMAGE = "Image";
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"+ NAME +" VARCHAR (255) ,"+ DESCRIPTION +" VARCHAR (255) ," +
                "" + PRICE + " VARCHAR (255),"+ CATEGORY +" VARCHAR (255) , "+ PRODUCTID +" VARCHAR (255) ,"+ IMAGE +" BLOB );";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

        Context context;

        public DataBaseHelper(@Nullable Context context) {
            super(context,DATABASE_NAME, null,TABLE_VERSION);
            this.context=context;
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
