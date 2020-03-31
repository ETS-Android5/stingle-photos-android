package org.stingle.photos.Db.Objects;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;
import org.stingle.photos.Db.StingleDbContract;

public class StingleDbAlbum {
	public Integer id;
	public String albumId;
	public String data;
	public String albumPK;
	public Long dateCreated;
	public Long dateModified;

	public StingleDbAlbum(Cursor cursor){
		this.id = cursor.getInt(cursor.getColumnIndexOrThrow(StingleDbContract.Columns._ID));
		this.albumId = cursor.getString(cursor.getColumnIndexOrThrow(StingleDbContract.Columns.COLUMN_NAME_ALBUM_ID));
		this.data = cursor.getString(cursor.getColumnIndexOrThrow(StingleDbContract.Columns.COLUMN_NAME_DATA));
		this.albumPK = cursor.getString(cursor.getColumnIndexOrThrow(StingleDbContract.Columns.COLUMN_NAME_ALBUM_PK));

		int index;

		if((index = cursor.getColumnIndex(StingleDbContract.Columns.COLUMN_NAME_DATE_CREATED)) != -1) {
			this.dateCreated = cursor.getLong(index);
		}
		if((index = cursor.getColumnIndex(StingleDbContract.Columns.COLUMN_NAME_DATE_MODIFIED)) != -1) {
			this.dateModified = cursor.getLong(index);
		}
	}

	public StingleDbAlbum(JSONObject json) throws JSONException {
		this.id = json.getInt("id");
		this.albumId = json.getString("albumId");
		this.data = json.getString("data");
		this.albumPK = json.getString("albumPK");
		this.dateCreated = json.getLong("dateCreated");
		this.dateModified = json.getLong("dateModified");
	}
}
