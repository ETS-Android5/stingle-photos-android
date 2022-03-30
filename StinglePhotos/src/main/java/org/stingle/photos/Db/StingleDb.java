package org.stingle.photos.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StingleDb extends SQLiteOpenHelper {
	// If you change the database schema, you must increment the database version.
	public static final int DATABASE_VERSION = 4;
	public static final String DATABASE_NAME = "stingleFiles.db";

	public static final int SORT_ASC = 0;
	public static final int SORT_DESC = 1;

	protected SQLiteDatabase dbWrite;
	protected SQLiteDatabase dbRead;


	public StingleDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion == 1 && newVersion == 2){
			db.execSQL(StingleDbContract.SQL_CREATE_ALBUMS);
			db.execSQL(StingleDbContract.SQL_CREATE_ALBUM_FILES);

			db.execSQL(StingleDbContract.SQL_FILES_FN_INDEX);
			db.execSQL(StingleDbContract.SQL_FILES_LR_INDEX);

			db.execSQL(StingleDbContract.SQL_TRASH_FN_INDEX);
			db.execSQL(StingleDbContract.SQL_TRASH_LR_INDEX);

			db.execSQL(StingleDbContract.SQL_CREATE_ALBUMS_AID_INDEX);
			db.execSQL(StingleDbContract.SQL_CREATE_ALBUM_FILES_AID_INDEX);

			db.execSQL(StingleDbContract.SQL_CREATE_CONTACTS);
			db.execSQL(StingleDbContract.SQL_CREATE_CONTACTS_UID_INDEX);
		}
		if(oldVersion == 2 && newVersion == 3){
			db.execSQL(StingleDbContract.SQL_CREATE_IMPORTED_IDS);
			db.execSQL(StingleDbContract.SQL_CREATE_IMPORTED_IDS_MID_INDEX);
			//db.execSQL(StingleDbContract.SQL_CREATE_ALBUMS_SYNC_LOCAL_FIELD);
		} else if (oldVersion == 3 && newVersion == 4) {
			db.execSQL(StingleDbContract.SQL_CREATE_SEARCH_INDEX);
			db.execSQL(StingleDbContract.SQL_CREATE_FILE_INFO);
			db.execSQL(StingleDbContract.SQL_CREATE_FACES);
			db.execSQL(StingleDbContract.SQL_CREATE_LOCATIONS);
		}
	}
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	private void createTables(SQLiteDatabase db){
		db.execSQL(StingleDbContract.SQL_CREATE_FILES);
		db.execSQL(StingleDbContract.SQL_CREATE_TRASH);
		db.execSQL(StingleDbContract.SQL_CREATE_ALBUMS);
		db.execSQL(StingleDbContract.SQL_CREATE_ALBUM_FILES);

		db.execSQL(StingleDbContract.SQL_FILES_FN_INDEX);
		db.execSQL(StingleDbContract.SQL_FILES_LR_INDEX);

		db.execSQL(StingleDbContract.SQL_TRASH_FN_INDEX);
		db.execSQL(StingleDbContract.SQL_TRASH_LR_INDEX);

		db.execSQL(StingleDbContract.SQL_CREATE_ALBUMS_AID_INDEX);
		db.execSQL(StingleDbContract.SQL_CREATE_ALBUM_FILES_AID_INDEX);

		db.execSQL(StingleDbContract.SQL_CREATE_CONTACTS);
		db.execSQL(StingleDbContract.SQL_CREATE_CONTACTS_UID_INDEX);

		db.execSQL(StingleDbContract.SQL_CREATE_IMPORTED_IDS);
		db.execSQL(StingleDbContract.SQL_CREATE_IMPORTED_IDS_MID_INDEX);

		db.execSQL(StingleDbContract.SQL_CREATE_SEARCH_INDEX);
		db.execSQL(StingleDbContract.SQL_CREATE_FILE_INFO);
		db.execSQL(StingleDbContract.SQL_CREATE_FACES);
		db.execSQL(StingleDbContract.SQL_CREATE_LOCATIONS);
	}

	private void deleteTables(SQLiteDatabase db){
		db.execSQL(StingleDbContract.SQL_DELETE_FILES);
		db.execSQL(StingleDbContract.SQL_DELETE_TRASH);
		db.execSQL(StingleDbContract.SQL_DELETE_ALBUMS);
		db.execSQL(StingleDbContract.SQL_DELETE_ALBUM_FILES);
	}

	public void recreate(){
		deleteTables(getWritableDatabase());
		createTables(getWritableDatabase());
	}

	public SQLiteDatabase openWriteDb(){
		if(this.dbWrite == null || !this.dbWrite.isOpen()) {
			this.dbWrite = getWritableDatabase();
		}
		return this.dbWrite;
	}
	public SQLiteDatabase openReadDb(){
		if(this.dbRead == null || !this.dbRead.isOpen()) {
			this.dbRead = getReadableDatabase();
		}
		return this.dbRead;
	}

	public void close(){
		if(this.dbWrite != null) {
			this.dbWrite.close();
		}
		if(this.dbRead != null) {
			this.dbRead.close();
		}
	}
}

