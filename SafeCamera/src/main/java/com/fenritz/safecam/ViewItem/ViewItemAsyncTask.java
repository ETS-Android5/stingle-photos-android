package com.fenritz.safecam.ViewItem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.widget.ContentLoadingProgressBar;

import com.fenritz.safecam.Auth.KeyManagement;
import com.fenritz.safecam.Crypto.Crypto;
import com.fenritz.safecam.Crypto.CryptoException;
import com.fenritz.safecam.Db.StingleDbFile;
import com.fenritz.safecam.Db.StingleDbHelper;
import com.fenritz.safecam.Net.HttpsClient;
import com.fenritz.safecam.Net.StingleResponse;
import com.fenritz.safecam.R;
import com.fenritz.safecam.SafeCameraApplication;
import com.fenritz.safecam.Sync.FileManager;
import com.fenritz.safecam.Sync.SyncManager;
import com.fenritz.safecam.Util.Helpers;
import com.fenritz.safecam.Util.MemoryCache;
import com.fenritz.safecam.Video.StingleDataSourceFactory;
import com.fenritz.safecam.Video.StingleHttpDataSource;
import com.fenritz.safecam.Widget.AnimatedGifImageView;
import com.fenritz.safecam.Widget.ImageHolderLayout;
import com.fenritz.safecam.Widget.photoview.PhotoViewAttacher;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.FileDataSource;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class ViewItemAsyncTask extends AsyncTask<Void, Integer, ViewItemAsyncTask.ViewItemTaskResult> {

	private Context context;

	private int position = 0;
	private final ImageHolderLayout parent;
	private final ViewPagerAdapter adapter;
	private final StingleDbHelper db;
	private int folder = SyncManager.FOLDER_MAIN;

	private final OnClickListener onClickListener;
	private final MemoryCache memCache;
	private boolean zoomable = false;
	private boolean isGif = false;
	private final View.OnTouchListener touchListener;

	private int fileType;


	public ViewItemAsyncTask(Context context, ViewPagerAdapter adapter, int position, ImageHolderLayout parent, StingleDbHelper db, int folder, OnClickListener onClickListener, View.OnTouchListener touchListener, MemoryCache memCache) {
		super();
		this.context = context;
		this.adapter = adapter;
		this.position = position;
		this.parent = parent;
		this.db = db;
		this.folder = folder;
		this.onClickListener = onClickListener;
		this.memCache = memCache;
		this.touchListener = touchListener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected ViewItemAsyncTask.ViewItemTaskResult doInBackground(Void... params) {
		ViewItemTaskResult result = new ViewItemTaskResult();

		result.folder = folder;

		StingleDbFile dbFile = db.getFileAtPosition(position);
		result.filename = dbFile.filename;
		if(dbFile.isLocal){
			File file = new File(Helpers.getHomeDir(context) + "/" + dbFile.filename);
			try {
				Crypto.Header fileHeader = SafeCameraApplication.getCrypto().getFileHeader(new FileInputStream(file));
				fileType = fileHeader.fileType;
				result.fileType = Crypto.FILE_TYPE_VIDEO;

				if(fileType == Crypto.FILE_TYPE_PHOTO) {
					result.fileType = Crypto.FILE_TYPE_PHOTO;
					FileInputStream input = new FileInputStream(file);

					byte[] decryptedData = SafeCameraApplication.getCrypto().decryptFile(input, null, this);

					if (decryptedData != null) {
						result.bitmap = Helpers.decodeBitmap(decryptedData, getSize(context));
					}
				}
			}
			catch (IOException | CryptoException e) {
				e.printStackTrace();
			}
		}
		else if(dbFile.isRemote){
			try{
				byte[] encThumb = FileManager.getAndCacheThumb(context, dbFile.filename, folder);
				Crypto.Header fileHeader = SafeCameraApplication.getCrypto().getFileHeader(new ByteArrayInputStream(encThumb));
				fileType = fileHeader.fileType;
				result.fileType = fileType;
				result.isRemote = true;

				HashMap<String, String> postParams = new HashMap<String, String>();

				postParams.put("token", KeyManagement.getApiToken(context));
				postParams.put("file", dbFile.filename);
				postParams.put("folder", String.valueOf(folder));

				JSONObject json = HttpsClient.postFunc(context.getString(R.string.api_server_url) + context.getString(R.string.get_url_path), postParams);
				StingleResponse response = new StingleResponse(this.context, json, false);

				if (response.isStatusOk()) {
					String url = response.get("url");

					if(url != null){
						result.url = url;
						if(fileType == Crypto.FILE_TYPE_PHOTO) {
							byte[] decryptedData = SafeCameraApplication.getCrypto().decryptFile(encThumb, this);

							if (decryptedData != null) {
								result.bitmap = Helpers.decodeBitmap(decryptedData, getSize(context));
							}
						}
					}
				}

			} catch (IOException | CryptoException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static int getSize(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);

		int size;
		if(metrics.widthPixels <= metrics.heightPixels){
			size = (int) Math.floor(metrics.widthPixels * 2);
		}
		else{
			size = (int) Math.floor(metrics.heightPixels * 2);
		}

		return size;
	}

	@Override
	protected void onPostExecute(ViewItemAsyncTask.ViewItemTaskResult result) {
		super.onPostExecute(result);

		parent.removeAllViews();
		parent.setFileType(result.fileType);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		if(result.fileType == Crypto.FILE_TYPE_PHOTO) {
			ImageView image = new ImageView(context);
			PhotoViewAttacher attacher = new PhotoViewAttacher(image);
			if (touchListener != null) {
				parent.setOnTouchListener(touchListener);
			}
			if (onClickListener != null) {
				attacher.setOnClickListener(onClickListener);
			}

			image.setLayoutParams(params);

			if (result.bitmap != null) {
				image.setImageBitmap(result.bitmap);
			} else {
				image.setImageResource(R.drawable.no);
			}

			image.setPadding(3, 3, 3, 3);

			attacher.update();

			if(result.isRemote){
				(new GetOriginalRemotePhotoTask(context, result, image, attacher)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}

			parent.addView(image);
		}
		else if (result.fileType == Crypto.FILE_TYPE_VIDEO){
			PlayerView playerView = new PlayerView(context);
			playerView.setLayoutParams(params);

			LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(Helpers.convertDpToPixels(context,100), Helpers.convertDpToPixels(context,100));
			final ContentLoadingProgressBar loading = new ContentLoadingProgressBar(context);

			loading.setLayoutParams(params);

			parent.removeAllViews();
			parent.addView(playerView);
			parent.addView(loading);

			if(touchListener != null){
				parent.setOnTouchListener(touchListener);
			}
			if (onClickListener != null) {
				parent.setOnClickListener(onClickListener);
			}

			SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(
					new DefaultRenderersFactory(context),
					new DefaultTrackSelector(), new DefaultLoadControl());
			adapter.addPlayer(position, player);


			playerView.setPlayer(player);
			player.addListener(new Player.EventListener() {
				@Override
				public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

				}

				@Override
				public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

				}

				@Override
				public void onLoadingChanged(boolean isLoading) {

				}

				@Override
				public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
					if (playbackState == ExoPlayer.STATE_BUFFERING){
						loading.setVisibility(View.VISIBLE);
						Log.d("buffering", "yes");
					} else {
						loading.setVisibility(View.INVISIBLE);
						Log.d("buffering", "no");
					}
				}

				@Override
				public void onRepeatModeChanged(int repeatMode) {

				}

				@Override
				public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

				}

				@Override
				public void onPlayerError(ExoPlaybackException error) {

				}

				@Override
				public void onPositionDiscontinuity(int reason) {

				}

				@Override
				public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

				}

				@Override
				public void onSeekProcessed() {

				}
			});

			MediaSource mediaSource;
			if(result.isRemote){
				Uri uri = Uri.parse(result.url);
				Log.d("url", result.url);

				StingleHttpDataSource http = new StingleHttpDataSource("stingle", null);
				StingleDataSourceFactory stingle = new StingleDataSourceFactory(context, http);

				mediaSource = new ExtractorMediaSource.Factory(stingle).createMediaSource(uri);
			}
			else{
				Uri uri = Uri.fromFile(new File(Helpers.getHomeDir(context) + "/" + result.filename));
				FileDataSource file = new FileDataSource();
				StingleDataSourceFactory stingle = new StingleDataSourceFactory(context, file);
				mediaSource = new ExtractorMediaSource.Factory(stingle).createMediaSource(uri);
			}

			player.setPlayWhenReady(false);
			/*if(adapter.getCurrentPosition() == position) {
				player.setPlayWhenReady(true);
			}
			else{
				player.setPlayWhenReady(false);
			}*/
			playerView.setShowShuffleButton(false);
			player.prepare(mediaSource, true, false);

		}
	}

	public class ViewItemTaskResult{
		public int fileType = Crypto.FILE_TYPE_PHOTO;
		public String filename = null;
		public Bitmap bitmap = null;
		public boolean isRemote = false;
		public String url = null;
		public int folder = SyncManager.FOLDER_MAIN;
	}
}