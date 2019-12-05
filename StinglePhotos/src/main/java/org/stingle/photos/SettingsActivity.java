package org.stingle.photos;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.MenuItem;

import org.stingle.photos.Files.FileManager;
import org.stingle.photos.Sync.SyncManager;
import org.stingle.photos.Util.AsyncTasks;
import org.stingle.photos.Auth.FingerprintManagerWrapper;
import org.stingle.photos.Util.Helpers;
import org.stingle.photos.Auth.LoginManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {


    public final static String CUSTOM_HOME_VALUE = "-custom-";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        Helpers.blockScreenshotsIfEnabled(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LoginManager.disableLockTimer(this);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            }
            else if (preference.getKey().equals("upload_battery_level")) {
                preference.setSummary(stringValue + "%");
            }
            else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static void bindPreferenceSummaryToValueInt(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getInt(preference.getKey(), 0));
    }



    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || SecurityPreferenceFragment.class.getName().equals(fragmentName)
                || SyncPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            //initClearCacheButton();
            initResyncDBButton();

            bindPreferenceSummaryToValue(findPreference("lock_time"));

			SwitchPreference blockScreenshotsSetting = (SwitchPreference)findPreference("block_screenshots");

            blockScreenshotsSetting.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
                    final Context context = GeneralPreferenceFragment.this.getContext();
                    Helpers.showConfirmDialog(context, getString(R.string.need_restart), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new Intent(context, GalleryActivity.class);
                            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                            //intent.putExtra(KEY_RESTART_INTENT, nextIntent);
                            context.startActivity(intent);

                            Runtime.getRuntime().exit(0);
                        }
                    }, null);
					return true;
				}
			});
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        protected void initClearCacheButton() {
            Preference clearCachePref = findPreference("clear_cache");
            clearCachePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GeneralPreferenceFragment.this.getContext());
                    builder.setMessage(getString(R.string.confirm_delete_cache));
                    builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @SuppressWarnings("unchecked")
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ArrayList<File> selectedFiles = new ArrayList<File>();
                            File thumbsDir = new File(FileManager.getThumbsDir(GeneralPreferenceFragment.this.getContext()));
                            File[] folderFiles = thumbsDir.listFiles();
                            for (File file : folderFiles) {
                                selectedFiles.add(file);
                            }
                            new AsyncTasks.DeleteFiles(GeneralPreferenceFragment.this.getContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, selectedFiles);
                        }
                    });
                    builder.setNegativeButton(getString(R.string.no), null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });
        }
        protected void initResyncDBButton() {
            Preference resyncDBPref = findPreference("resync_db");
            resyncDBPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference preference) {
                    final ProgressDialog spinner = Helpers.showProgressDialog(GeneralPreferenceFragment.this.getContext(), getString(R.string.syncing_db), null);
                    SyncManager.syncFSToDB(GeneralPreferenceFragment.this.getContext(), new SyncManager.OnFinish() {
                        @Override
                        public void onFinish(Boolean needToUpdateUI) {
                            spinner.dismiss();
                        }
                    }, AsyncTask.THREAD_POOL_EXECUTOR);
                    return true;
                }
            });
        }
    }


    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class SecurityPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_security);
            setHasOptionsMenu(true);

            SwitchPreference fingerprintSetting = (SwitchPreference)findPreference("fingerprint");
            final FingerprintManagerWrapper fingerprintManager = new FingerprintManagerWrapper(getActivity());

            if(!fingerprintManager.isFingerprintAvailable()){
                fingerprintSetting.setEnabled(false);
            }
            fingerprintSetting.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isEnabled = (boolean)newValue;
                    if (isEnabled) {
                        fingerprintManager.setupFingerprint(preference);

                        return false;
                    }
                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    public static class SyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_sync);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValueInt(findPreference("upload_battery_level"));

            /*Preference uploadBatteryLevel = findPreference("upload_battery_level");

            uploadBatteryLevel.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
            sBindPreferenceSummaryToValueListener.onPreferenceChange(uploadBatteryLevel,
                    String.valueOf(PreferenceManager
                            .getDefaultSharedPreferences(uploadBatteryLevel.getContext())
                            .getInt(uploadBatteryLevel.getKey(), 0)) + "%");*/
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

}
