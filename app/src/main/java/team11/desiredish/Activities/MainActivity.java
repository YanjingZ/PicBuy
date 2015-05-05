package team11.desiredish.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import java.util.HashMap;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabWidget;
import android.widget.TextView;

import team11.desiredish.Fragment.FriendsFoodFragment;
import team11.desiredish.Fragment.MyFoodFragment;
import team11.desiredish.R;
import team11.desiredish.Fragment.TakePictureFragment;


public class MainActivity extends FragmentActivity implements TabHost.OnTabChangeListener{
    public final static String EXTRA_MESSAGE = "team11.desiredish.MESSAGE";
    private static final String DEBUG_TAG = "yanjing-debug";
    private TabHost mTabHost;
    private HashMap mapTabInfo = new HashMap();
    private TabInfo mLastTab = null;
    private String UserID;
    private class TabInfo {
        private String tag;
        private Class clss;
        private Bundle args;
        private Fragment fragment;
        TabInfo(String tag, Class clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }

    }
    class TabFactory implements TabContentFactory {

        private final Context mContext;

        /**
         * @param context
         */
        public TabFactory(Context context) {
            mContext = context;
        }

        /** (non-Javadoc)
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Step 1: Inflate layout
        setContentView(R.layout.activity_main);



        Intent intent = getIntent();
        UserID = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        if(savedInstanceState==null){
            savedInstanceState=new Bundle();
            Log.i(DEBUG_TAG,"MainAct: UserID="+UserID);
        }
        savedInstanceState.putString("UserID",UserID);

        // Step 2: Setup TabHost
        initialiseTabHost(savedInstanceState);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_fllow:
                followFriend();
                return true;
            case R.id.action_logout:
                Logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void followFriend(){
        Intent intent = new Intent(this, FollowFriendActivity.class);
        intent.putExtra(EXTRA_MESSAGE, UserID);
        startActivity(intent);
    }
    public void Logout() {
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }
    /**
     * Step 2: Setup TabHost
     */
    private void initialiseTabHost(Bundle args) {
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;

        MainActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("MyFood").setIndicator("My Dish"), ( tabInfo = new TabInfo("MyFood", MyFoodFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        MainActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("TakePicture").setIndicator("",getResources().getDrawable(R.drawable.ic_action_camera)), ( tabInfo = new TabInfo("TakePicture", TakePictureFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        MainActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("FriendsFood").setIndicator("Friends' Dish"), ( tabInfo = new TabInfo("FriendsFood", FriendsFoodFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);


        mTabHost.setOnTabChangedListener(this);
        mTabHost.setCurrentTab(1);
        TabWidget tabWidget = mTabHost.getTabWidget();
        for(int i = 0; i < tabWidget.getChildCount(); i++) {
            View v = tabWidget.getChildAt(i);

            // Look for the title view to ensure this is an indicator and not a divider.
            TextView tv = (TextView)v.findViewById(android.R.id.title);
            if(tv == null) {
                continue;
            }
            v.setBackgroundResource(R.drawable.tabhost);
            tabWidget.setStripEnabled(true);
            tabWidget.setRightStripDrawable(R.drawable.tab_unselected_example);
            tabWidget.setLeftStripDrawable(R.drawable.tab_unselected_example);
        }

    }


    private static void addTab(MainActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(activity.new TabFactory(activity));
        String tag = tabSpec.getTag();

        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.detach(tabInfo.fragment);
            ft.commit();
            activity.getSupportFragmentManager().executePendingTransactions();
        }

        tabHost.addTab(tabSpec);
    }
    public void onTabChanged(String tag) {
        TabInfo newTab = (TabInfo) this.mapTabInfo.get(tag);
        Log.i(DEBUG_TAG, "Tag is " + tag);
        if (mLastTab != newTab) {
            FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    ft.detach(mLastTab.fragment);
                }
            }
            if (newTab != null) {
                if(tag.equals("TakePicture")&&newTab.fragment!=null){
                    ft.attach(newTab.fragment);
                }
                else{
                    newTab.fragment = Fragment.instantiate(this,
                            newTab.clss.getName(), newTab.args);
                    ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
                }
            }

            mLastTab = newTab;
            ft.commit();
            this.getSupportFragmentManager().executePendingTransactions();
        }
    }

}
