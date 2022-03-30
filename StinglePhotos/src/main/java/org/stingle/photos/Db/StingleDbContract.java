package org.stingle.photos.Db;

import android.provider.BaseColumns;

public class StingleDbContract {

	private StingleDbContract() {}

	/* Inner class that defines the table contents */
	public static class Columns implements BaseColumns {
		public static final String TABLE_NAME_GALLERY = "files";
		public static final String TABLE_NAME_TRASH = "trash";
		public static final String TABLE_NAME_ALBUMS = "albums";
		public static final String TABLE_NAME_ALBUM_FILES = "album_files";
		public static final String TABLE_NAME_CONTACTS = "contacts";
		public static final String TABLE_NAME_IMPORTED_IDS = "imported_ids";
		public static final String TABLE_NAME_SEARCH_INDEX = "search_index";
		public static final String TABLE_NAME_FILE_INFO = "file_info";
		public static final String TABLE_NAME_FACES = "faces";
		public static final String TABLE_NAME_LOCATIONS = "locations";

		public static final String COLUMN_NAME_FILENAME = "filename";
		public static final String COLUMN_NAME_IS_LOCAL = "is_local";
		public static final String COLUMN_NAME_IS_REMOTE = "is_remote";
		public static final String COLUMN_NAME_VERSION = "version";
		public static final String COLUMN_NAME_REUPLOAD = "reupload";
		public static final String COLUMN_NAME_DATE_CREATED = "date_created";
		public static final String COLUMN_NAME_DATE_MODIFIED = "date_modified";
		public static final String COLUMN_NAME_DATE_USED = "date_used";
		public static final String COLUMN_NAME_HEADERS = "headers";

		public static final String COLUMN_NAME_ALBUM_ID = "album_id";
		public static final String COLUMN_NAME_ALBUM_SK = "album_sk";
		public static final String COLUMN_NAME_ALBUM_PK = "album_pk";
		public static final String COLUMN_NAME_METADATA = "metadata";
		public static final String COLUMN_NAME_IS_SHARED = "is_shared";
		public static final String COLUMN_NAME_IS_HIDDEN = "is_hidden";
		public static final String COLUMN_NAME_IS_OWNER = "is_owner";
		public static final String COLUMN_NAME_MEMBERS = "members";
		public static final String COLUMN_NAME_PERMISSIONS = "permissions";
		public static final String COLUMN_NAME_IS_LOCKED = "is_locked";
		public static final String COLUMN_NAME_COVER = "cover";
		public static final String COLUMN_NAME_SYNC_LOCAL = "sync_local";

		public static final String COLUMN_NAME_USER_ID = "user_id";
		public static final String COLUMN_NAME_EMAIL = "email";
		public static final String COLUMN_NAME_PUBLIC_KEY = "pk";

		public static final String COLUMN_NAME_MEDIA_ID = "media_id";
		public static final String COLUMN_NAME_IS_PROC = "is_proc";
		public static final String COLUMN_NAME_HASH = "hash";
		public static final String COLUMN_NAME_DATA = "data";
		public static final String COLUMN_NAME_FACE_ID = "face_id";
		public static final String COLUMN_NAME_LOC_ID = "loc_id";
	}

	public static final String SQL_CREATE_FILES =
			"CREATE TABLE " + Columns.TABLE_NAME_GALLERY + " (" +
					Columns._ID + " INTEGER PRIMARY KEY," +
					Columns.COLUMN_NAME_FILENAME + " TEXT NOT NULL UNIQUE," +
					Columns.COLUMN_NAME_IS_LOCAL + " INTEGER," +
					Columns.COLUMN_NAME_IS_REMOTE + " INTEGER," +
					Columns.COLUMN_NAME_VERSION + " INTEGER," +
					Columns.COLUMN_NAME_REUPLOAD + " INTEGER," +
					Columns.COLUMN_NAME_DATE_CREATED + " INTEGER," +
					Columns.COLUMN_NAME_DATE_MODIFIED + " INTEGER, " +
					Columns.COLUMN_NAME_HEADERS + " TEXT" +
					")";

	public static final String SQL_DELETE_FILES =
			"DROP TABLE IF EXISTS " + Columns.TABLE_NAME_GALLERY;

	public static final String SQL_FILES_FN_INDEX =
			"CREATE UNIQUE INDEX filename ON "+ Columns.TABLE_NAME_GALLERY +" ("+ Columns.COLUMN_NAME_FILENAME+")";
	public static final String SQL_FILES_LR_INDEX =
			"CREATE INDEX localremote ON "+ Columns.TABLE_NAME_GALLERY +" ("+ Columns.COLUMN_NAME_IS_LOCAL+", "+ Columns.COLUMN_NAME_IS_REMOTE+")";


	public static final String SQL_CREATE_TRASH =
			"CREATE TABLE " + Columns.TABLE_NAME_TRASH + " (" +
					Columns._ID + " INTEGER PRIMARY KEY," +
					Columns.COLUMN_NAME_FILENAME + " TEXT NOT NULL UNIQUE," +
					Columns.COLUMN_NAME_IS_LOCAL + " INTEGER," +
					Columns.COLUMN_NAME_IS_REMOTE + " INTEGER," +
					Columns.COLUMN_NAME_VERSION + " INTEGER," +
					Columns.COLUMN_NAME_REUPLOAD + " INTEGER," +
					Columns.COLUMN_NAME_DATE_CREATED + " INTEGER," +
					Columns.COLUMN_NAME_DATE_MODIFIED + " INTEGER, " +
					Columns.COLUMN_NAME_HEADERS + " TEXT" +
					")";

	public static final String SQL_TRASH_FN_INDEX =
			"CREATE UNIQUE INDEX trash_filename ON "+ Columns.TABLE_NAME_TRASH+" ("+ Columns.COLUMN_NAME_FILENAME+")";
	public static final String SQL_TRASH_LR_INDEX =
			"CREATE INDEX trash_localremote ON "+ Columns.TABLE_NAME_TRASH+" ("+ Columns.COLUMN_NAME_IS_LOCAL+", "+ Columns.COLUMN_NAME_IS_REMOTE+")";

	public static final String SQL_DELETE_TRASH =
			"DROP TABLE IF EXISTS " + Columns.TABLE_NAME_TRASH;


	public static final String SQL_CREATE_ALBUMS =
			"CREATE TABLE " + Columns.TABLE_NAME_ALBUMS + " (" +
					Columns._ID + " INTEGER PRIMARY KEY," +
					Columns.COLUMN_NAME_ALBUM_ID + " TEXT NOT NULL UNIQUE," +
					Columns.COLUMN_NAME_ALBUM_SK + " TEXT NOT NULL," +
					Columns.COLUMN_NAME_ALBUM_PK + " TEXT NOT NULL, " +
					Columns.COLUMN_NAME_METADATA + " TEXT, " +
					Columns.COLUMN_NAME_IS_SHARED + " INTEGER, " +
					Columns.COLUMN_NAME_IS_HIDDEN + " INTEGER, " +
					Columns.COLUMN_NAME_IS_OWNER + " INTEGER, " +
					Columns.COLUMN_NAME_MEMBERS + " TEXT, " +
					Columns.COLUMN_NAME_PERMISSIONS + " TEXT, " +
					Columns.COLUMN_NAME_SYNC_LOCAL + " INTEGER, " +
					Columns.COLUMN_NAME_IS_LOCKED + " INTEGER, " +
					Columns.COLUMN_NAME_COVER + " TEXT, " +
					Columns.COLUMN_NAME_DATE_CREATED + " INTEGER," +
					Columns.COLUMN_NAME_DATE_MODIFIED + " INTEGER" +
					")";
	public static final String SQL_CREATE_ALBUMS_AID_INDEX =
					"CREATE UNIQUE INDEX a_album_id ON "+ Columns.TABLE_NAME_ALBUMS +" ("+ Columns.COLUMN_NAME_ALBUM_ID +")";

	public static final String SQL_DELETE_ALBUMS =
			"DROP TABLE IF EXISTS " + Columns.TABLE_NAME_ALBUMS;

	public static final String SQL_CREATE_ALBUM_FILES =
			"CREATE TABLE " + Columns.TABLE_NAME_ALBUM_FILES + " (" +
					Columns._ID + " INTEGER PRIMARY KEY," +
					Columns.COLUMN_NAME_ALBUM_ID + " TEXT NOT NULL," +
					Columns.COLUMN_NAME_FILENAME + " TEXT NOT NULL," +
					Columns.COLUMN_NAME_IS_LOCAL + " INTEGER," +
					Columns.COLUMN_NAME_IS_REMOTE + " INTEGER," +
					Columns.COLUMN_NAME_VERSION + " INTEGER," +
					Columns.COLUMN_NAME_REUPLOAD + " INTEGER," +
					Columns.COLUMN_NAME_HEADERS + " TEXT, " +
					Columns.COLUMN_NAME_DATE_CREATED + " INTEGER, " +
					Columns.COLUMN_NAME_DATE_MODIFIED + " INTEGER" +
					")";
	public static final String SQL_CREATE_ALBUM_FILES_AID_INDEX =
			"CREATE UNIQUE INDEX af_album_id ON "+ Columns.TABLE_NAME_ALBUM_FILES +" ("+ Columns.COLUMN_NAME_ALBUM_ID +", "+ Columns.COLUMN_NAME_FILENAME+")";

	public static final String SQL_DELETE_ALBUM_FILES =
			"DROP TABLE IF EXISTS " + Columns.TABLE_NAME_ALBUM_FILES;


	public static final String SQL_CREATE_CONTACTS =
			"CREATE TABLE " + Columns.TABLE_NAME_CONTACTS + " (" +
					Columns._ID + " INTEGER PRIMARY KEY," +
					Columns.COLUMN_NAME_USER_ID + " INTEGER NOT NULL UNIQUE," +
					Columns.COLUMN_NAME_EMAIL + " TEXT NOT NULL," +
					Columns.COLUMN_NAME_PUBLIC_KEY + " TEXT NOT NULL, " +
					Columns.COLUMN_NAME_DATE_USED + " INTEGER, " +
					Columns.COLUMN_NAME_DATE_MODIFIED + " INTEGER" +
					")";
	public static final String SQL_CREATE_CONTACTS_UID_INDEX =
			"CREATE UNIQUE INDEX c_user_id ON "+ Columns.TABLE_NAME_CONTACTS +" ("+ Columns.COLUMN_NAME_USER_ID +")";

	public static final String SQL_CREATE_IMPORTED_IDS =
			"CREATE TABLE " + Columns.TABLE_NAME_IMPORTED_IDS + " (" +
					Columns._ID + " INTEGER PRIMARY KEY," +
					Columns.COLUMN_NAME_MEDIA_ID + " INTEGER NOT NULL UNIQUE" +
					")";
	public static final String SQL_CREATE_IMPORTED_IDS_MID_INDEX =
			"CREATE UNIQUE INDEX c_m_id ON "+ Columns.TABLE_NAME_IMPORTED_IDS +" ("+ Columns.COLUMN_NAME_MEDIA_ID +")";

	public static final String SQL_CREATE_ALBUMS_SYNC_LOCAL_FIELD =
			"ALTER TABLE "+Columns.TABLE_NAME_ALBUMS+" ADD "+Columns.COLUMN_NAME_SYNC_LOCAL+" INTEGER;";

	public static final String SQL_CREATE_SEARCH_INDEX =
			"CREATE TABLE " + Columns.TABLE_NAME_SEARCH_INDEX + " (" +
					Columns._ID + " INTEGER PRIMARY KEY," +
					Columns.COLUMN_NAME_FILENAME + " TEXT NOT NULL," +
					Columns.COLUMN_NAME_HASH + " TEXT NOT NULL," +
					Columns.COLUMN_NAME_IS_PROC + " TEXT NOT NULL," +
					Columns.COLUMN_NAME_DATE_MODIFIED + " INTEGER" +
					")";

	public static final String SQL_CREATE_FILE_INFO =
			"CREATE TABLE " + Columns.TABLE_NAME_FILE_INFO + " (" +
					Columns.COLUMN_NAME_FILENAME + " TEXT NOT NULL PRIMARY KEY," +
					Columns.COLUMN_NAME_DATA + " TEXT NOT NULL," +
					Columns.COLUMN_NAME_DATE_MODIFIED + " INTEGER" +
					")";

	public static final String SQL_CREATE_FACES =
			"CREATE TABLE " + Columns.TABLE_NAME_FACES + " (" +
					Columns.COLUMN_NAME_FACE_ID + " TEXT NOT NULL PRIMARY KEY," +
					Columns.COLUMN_NAME_DATA + " TEXT NOT NULL," +
					Columns.COLUMN_NAME_DATE_MODIFIED + " INTEGER" +
					")";

	public static final String SQL_CREATE_LOCATIONS =
			"CREATE TABLE " + Columns.TABLE_NAME_LOCATIONS + " (" +
					Columns.COLUMN_NAME_LOC_ID + " TEXT NOT NULL PRIMARY KEY," +
					Columns.COLUMN_NAME_DATA + " TEXT NOT NULL," +
					Columns.COLUMN_NAME_DATE_MODIFIED + " INTEGER" +
					")";
}
