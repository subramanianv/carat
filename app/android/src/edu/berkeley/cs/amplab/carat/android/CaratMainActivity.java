package edu.berkeley.cs.amplab.carat.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.flurry.android.FlurryAgent;
import com.zubhium.ZubhiumSDK;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;

import edu.berkeley.cs.amplab.carat.android.CaratApplication.Type;
import edu.berkeley.cs.amplab.carat.android.protocol.CommsThread;
import edu.berkeley.cs.amplab.carat.android.sampling.SamplingLibrary;
import edu.berkeley.cs.amplab.carat.android.ui.MultiTabListener;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Carat Android App Main Activity. Is loaded right after CaratApplication.
 * Holds the Tabs that comprise the UI. Place code related to tab handling and
 * global Activity code here.
 * 
 * @author Eemil Lagerspetz
 * 
 */
public class CaratMainActivity extends SherlockFragmentActivity {
    // Log tag
    private static final String TAG = "CaratMain";

    public static final String ACTION_BUGS = "bugs";
    public static final String ACTION_HOGS = "hogs";

    // 250 ms
    public static final long ANIMATION_DURATION = 250;

    // Thread that sends samples when phone is woken up, GUI is started, or at
    // 15 min intervals.
    private CommsThread sampleSender = null;
    // private distanceThread distanceInfo = null;
    //private UiRefreshThread uiRefreshThread = null;

    // Hold the tabs of the UI.
    public static ActionBar actionBar = null;
    private static int selectedTab = 0;

    // Zubhium SDK
    ZubhiumSDK sdk = null;

    // Key File
    private static final String ZUBHIUM_KEYFILE = "zubhium.properties";
    private static final String FLURRY_KEYFILE = "flurry.properties";

    private MenuItem feedbackItem = null;
    private MenuItem wifiOnly = null;

    private String fullVersion = null;
    
    /* Fields required for buttons: */
    private static CaratMyDeviceActivity myDevice = null;
    private static CaratBugsOrHogsActivity bugsActivity = null;
    private static CaratBugsOrHogsActivity hogsActivity = null;
    private static CaratSuggestionsActivity actionList = null;
    private static CaratAboutActivity aboutActivity = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar_ForceOverflow);
        super.onCreate(savedInstanceState);
        // If we want a progress bar for loading some screens at the top of the
        // title bar
        // This does not show if it is not updated
        getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.main);

        /*
         * Title stuff:
         */
        fullVersion = getString(R.string.app_name) + " "
                + getString(R.string.version_name);

        String secretKey = null;
        Properties properties = new Properties();
        try {
            InputStream raw = CaratMainActivity.this.getAssets().open(
                    ZUBHIUM_KEYFILE);
            if (raw != null) {
                properties.load(raw);
                if (properties.containsKey("secretkey"))
                    secretKey = properties
                            .getProperty("secretkey", "secretkey");
                Log.d(TAG, "Set secret key.");
            } else
                Log.e(TAG, "Could not open zubhium key file!");
        } catch (IOException e) {
            Log.e(TAG, "Could not open zubhium key file: " + e.toString());
        }
        if (secretKey != null) {
            sdk = ZubhiumSDK.getZubhiumSDKInstance(getApplicationContext(),
                    secretKey, fullVersion);
            sdk.registerUpdateReceiver(CaratMainActivity.this);
        }
        setTitleNormal();
        

        /*
         * Tab Stuff:
         */
        
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayShowTitleEnabled(false);

        String ac = getString(R.string.tab_actions);
        String md = getString(R.string.tab_my_device);
        String b = getString(R.string.tab_bugs);
        String h = getString(R.string.tab_hogs);
        String a = getString(R.string.tab_about);

        
        actionList = new CaratSuggestionsActivity();
        myDevice = new CaratMyDeviceActivity();
        bugsActivity = new CaratBugsOrHogsActivity(Type.BUG);
        hogsActivity = new CaratBugsOrHogsActivity(Type.HOG);
        aboutActivity = new CaratAboutActivity();
        
        MultiTabListener l = new MultiTabListener();
        l.addTab(ac, actionList);
        l.addTab(md, myDevice);
        l.addTab(b, bugsActivity);
        l.addTab(h, hogsActivity);
        l.addTab(a, aboutActivity);
        
        // Initialize a TabSpec for each tab and add it to the TabHost
        //CaratSuggestionsActivity.class);
        //CaratMyDeviceActivity.class);
        //CaratBugsOrHogsActivity.class);
        //CaratBugsOrHogsActivity.class);
        //CaratAboutActivity.class);
        

        Tab actions = actionBar.newTab().setText(ac).setTabListener(l)
                .setIcon(R.drawable.ic_tab_actions);
        Tab device = actionBar.newTab().setText(md).setTabListener(l)
                .setIcon(R.drawable.ic_tab_mydevice);
        Tab bugs = actionBar.newTab().setText(b).setTabListener(l)
                .setIcon(R.drawable.ic_tab_bugs);
        Tab hogs = actionBar.newTab().setText(h).setTabListener(l)
                .setIcon(R.drawable.ic_tab_hogs);
        Tab about = actionBar.newTab().setText(a).setTabListener(l)
                .setIcon(R.drawable.ic_tab_about);

        actionBar.addTab(actions);
        actionBar.addTab(device);
        actionBar.addTab(bugs);
        actionBar.addTab(hogs);
        actionBar.addTab(about);
        
        selectedTab = actionBar.getSelectedNavigationIndex();
    }
    
    public static void nextTab(){
        selectedTab = actionBar.getSelectedNavigationIndex();
        if (selectedTab < 4){
            //animateFirst(selectedTab, selectedTab+1);
            selectedTab+=1;
            actionBar.setSelectedNavigationItem(selectedTab);
            //animateSecond(selectedTab-1, selectedTab);
        }
    }
    
    public static void previousTab(){
        selectedTab = actionBar.getSelectedNavigationIndex();
        if (selectedTab > 0){
            //animateFirst(selectedTab, selectedTab-1);
            selectedTab-=1;
            actionBar.setSelectedNavigationItem(selectedTab);
            //animateSecond(selectedTab+1, selectedTab);
        }
    }
    
    private static void animateFirst(int tab1, int tab2) {
        Animation a1 = outtoLeft;
        if (tab1 > tab2) {
            a1 = outtoRight;
        }

        switch (tab1) {
        case 0:
            actionList.getView().setAnimation(a1);
            break;
        case 1:
            myDevice.getView().setAnimation(a1);
            break;
        case 2:
            bugsActivity.getView().setAnimation(a1);
            break;
        case 3:
            hogsActivity.getView().setAnimation(a1);
            break;
        case 4:
            aboutActivity.getView().setAnimation(a1);
            break;
        }
    }

    private static void animateSecond(int tab1, int tab2) {
        Animation a2 = inFromRight;
        if (tab1 > tab2) {
            a2 = inFromLeft;
        }

        switch (tab2) {
        case 0:
            actionList.getView().setAnimation(a2);
            break;
        case 1:
            myDevice.getView().setAnimation(a2);
            break;
        case 2:
            bugsActivity.getView().setAnimation(a2);
            break;
        case 3:
            hogsActivity.getView().setAnimation(a2);
            break;
        case 4:
            aboutActivity.getView().setAnimation(a2);
            break;
        }
    }

    public void setTitleNormal() {
        long s = CaratApplication.s.getSamplesReported();
        if (s > 0)
            this.setTitle(fullVersion + " - " + s + " "+getString(R.string.samplesreported));
        else
            this.setTitle(fullVersion);
    }

    public void setTitleUpdating(String what) {
        this.setTitle(/*fullVersion + " - " + */getString(R.string.updating)+" "+what);
    }

    public void setTitleUpdatingFailed(String what) {
        this.setTitle(/*fullVersion + " - " +*/getString(R.string.didntget)+" "+ what);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        String secretKey = null;
        Properties properties = new Properties();
        try {
            InputStream raw = CaratMainActivity.this.getAssets().open(
                    FLURRY_KEYFILE);
            if (raw != null) {
                properties.load(raw);
                if (properties.containsKey("secretkey"))
                    secretKey = properties
                            .getProperty("secretkey", "secretkey");
                Log.d(TAG, "Set Flurry secret key.");
            } else
                Log.e(TAG, "Could not open Flurry key file!");
        } catch (IOException e) {
            Log.e(TAG, "Could not open Flurry key file: " + e.toString());
        }
        if (secretKey != null) {
            FlurryAgent.onStartSession(getApplicationContext(), secretKey);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.ActivityGroup#onStop()
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        FlurryAgent.onEndSession(getApplicationContext());
    }

    /**
     * Animation for sliding a screen in from the right.
     * 
     * @return
     */
    public static Animation inFromRight = new TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT,
            0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f);
    {
        inFromRight.setDuration(ANIMATION_DURATION);
        inFromRight.setInterpolator(new AccelerateInterpolator());
    }

    /**
     * Animation for sliding a screen out to the left.
     * 
     * @return
     */
    public static Animation outtoLeft = new TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
            -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f);
    {
        outtoLeft.setDuration(ANIMATION_DURATION);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
    }

    /**
     * Animation for sliding a screen in from the left.
     * 
     * @return
     */
    public static Animation inFromLeft = new TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT,
            0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f);
    {
        inFromLeft.setDuration(ANIMATION_DURATION);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
    }

    /**
     * Animation for sliding a screen out to the right.
     * 
     * @return
     */

    public static Animation outtoRight = new TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
            +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f);
    {
        outtoRight.setDuration(ANIMATION_DURATION);
        outtoRight.setInterpolator(new AccelerateInterpolator());
    }

    /**
     * 
     * Starts a Thread that communicates with the server to send stored samples.
     * TODO: latest sample for GUI usage.
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        Log.i(TAG, "Resumed");
        CaratApplication.setMain(this);
        // Thread for sending samples every 15 mins

        if (sampleSender == null) {
            sampleSender = new CommsThread((CaratApplication) getApplication());
            sampleSender.start();
        } else {
            Log.d("CaratMainActivity", "Resuming SampleSender");
            new Thread() {
                public void run() {
                    sampleSender.appResumed();
                }
            }.start();
        }

        /*
         * if (distanceInfo == null) { distanceInfo= new
         * distanceThread((CaratApplication) getApplication());
         * distanceInfo.start(); } else { Log.d("CaratMainActivity",
         * "Resuming location distance calculation!"); new Thread() { public
         * void run() { distanceInfo.appResumed(); } }.start(); }
         */
        // Thread for refreshing the UI with new reports every 5 mins and on
        // resume
        
        Log.d(TAG, "Refreshing UI");
        new Thread() {
            public void run() {
                ((CaratApplication) getApplication()).refreshUi();
            }
        }.start();
        super.onResume();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.ActivityGroup#onPause()
     */
    @Override
    protected void onPause() {
        Log.i(TAG, "Paused");
        SamplingLibrary.resetRunningProcessInfo();
        sampleSender.paused();
        super.onPause();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#finish()
     */
    @Override
    public void finish() {
        sampleSender.stopRunning();
        sampleSender.appResumed();
        // distanceInfo.appResumed();
        // distanceInfo.stopRunning();
        //uiRefreshThread.stopRunning();
        //uiRefreshThread.appResumed();

        Log.d(TAG, "Finishing up");
        super.finish();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.ActivityGroup#onDestroy()
     */
    @Override
    protected void onDestroy() {
        if (sdk != null)
            sdk.unRegisterUpdateReceiver();
        super.onDestroy();
    }

    /**
     * Menus
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate your menu.
        getSupportMenuInflater().inflate(R.menu.share_action_provider, menu);

        // Set file with share history to the provider and set the share intent.
        MenuItem actionItem = menu
                .findItem(R.id.menu_item_share_action_provider_action_bar);
        ShareActionProvider actionProvider = (ShareActionProvider) actionItem
                .getActionProvider();
        actionProvider
                .setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        // Note that you can set/change the intent any time,
        // say when the user has selected an image.
        actionProvider.setShareIntent(createShareIntent());
        

        wifiOnly = menu.findItem(R.id.menu_wifionly);
        wifiOnly.setCheckable(true);
        final SharedPreferences p = PreferenceManager
                .getDefaultSharedPreferences(CaratMainActivity.this);
        boolean useWifiOnly = p.getBoolean(CaratApplication.PREFERENCE_WIFI_ONLY, false);
        if (useWifiOnly)
            wifiOnly.setTitle(R.string.wifionlyused);
        wifiOnly.setChecked(useWifiOnly);
        feedbackItem = menu.findItem(R.id.menu_feedback);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.actionbarsherlock.app.SherlockFragmentActivity#onOptionsItemSelected
     * (com.actionbarsherlock.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == wifiOnly) {
            final SharedPreferences p = PreferenceManager
                    .getDefaultSharedPreferences(CaratMainActivity.this);
            boolean useWifiOnly = p.getBoolean(
                    CaratApplication.PREFERENCE_WIFI_ONLY, false);
            if (useWifiOnly) {
                p.edit()
                        .putBoolean(CaratApplication.PREFERENCE_WIFI_ONLY,
                                false).commit();
                item.setChecked(false);
                item.setTitle(R.string.wifionly);
            } else {
                p.edit()
                        .putBoolean(CaratApplication.PREFERENCE_WIFI_ONLY, true)
                        .commit();
                item.setChecked(true);
                item.setTitle(R.string.wifionlyused);
            }
        } else if (item == feedbackItem) {
            sdk.openFeedbackDialog(CaratMainActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareIntent() {
        int jscore = CaratApplication.getJscore();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.myjscoreis) + " " + jscore);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharetext1)
                + " " + jscore + getString(R.string.sharetext2));
        return sendIntent;
    }

    public void viewJscoreInfo(View v){
        myDevice.viewJscoreInfo(v);
    }
    
    public void viewProcessList(View v){
        myDevice.viewProcessList(v, getApplicationContext());
    }

    public void showAppInfo(View v) {myDevice.showAppInfo(v, getApplicationContext());}
    public void showOsInfo(View v) {myDevice.showOsInfo(v, getApplicationContext());}
    public void showDeviceInfo(View v) {myDevice.showDeviceInfo(v, getApplicationContext());}
    public void showMemoryInfo(View v) { myDevice.showMemoryInfo(v);}

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        int idx = actionBar.getSelectedNavigationIndex();

        boolean shouldFinish = true;

        switch (idx) {
        case 0:
            shouldFinish = actionList.onBackPressed();
            break;
        case 1:
            shouldFinish = myDevice.onBackPressed();
            break;
        case 2:
            shouldFinish = bugsActivity.onBackPressed();
            break;
        case 3:
            shouldFinish = hogsActivity.onBackPressed();
            break;
        }
        if (shouldFinish)
            super.onBackPressed();
    }
}