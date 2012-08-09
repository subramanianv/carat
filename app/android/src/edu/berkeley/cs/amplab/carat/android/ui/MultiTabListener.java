package edu.berkeley.cs.amplab.carat.android.ui;

import java.util.HashMap;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;

import edu.berkeley.cs.amplab.carat.android.CaratMainActivity;
import edu.berkeley.cs.amplab.carat.android.R;

/**
 * Adds and removes content when tabs are selected
 * @author Eemil Lagerspetz
 */
public class MultiTabListener implements TabListener {
    private HashMap<String, Fragment> fragments = new HashMap<String, Fragment>();
    
    private int prev = 0;

    public MultiTabListener() {
    }

    public void addTab(String title, Fragment fragment) {
        if (title == null || fragment == null)
            return;
        fragments.put(title, fragment);
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    /**
     * Adds the content when the tab is selected. IMPORTANT: the first
     * parameter must be set and must be in the layout of the main app.
     */
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        if (tab == null || tab.getText() == null)
            return;
        int curr = tab.getPosition();
        String s = tab.getText().toString();
        if (curr < prev)
            ft.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        else
            ft.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_right,R.anim.slide_out_left);
        if (fragments.containsKey(s))
            ft.replace(R.id.content, fragments.get(s));
            //ft.add(R.id.content, fragments.get(s), null);
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        if (tab == null || tab.getText() == null)
            return;
        prev = tab.getPosition();
        //String s = tab.getText().toString();
        //ft.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        /*if (fragments.containsKey(s))
            ft.remove(fragments.get(s));*/
    }
}