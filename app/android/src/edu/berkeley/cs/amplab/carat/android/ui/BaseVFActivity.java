package edu.berkeley.cs.amplab.carat.android.ui;

import edu.berkeley.cs.amplab.carat.android.CaratMainActivity;
import edu.berkeley.cs.amplab.carat.android.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

public abstract class BaseVFActivity extends Fragment implements VFActivity {
    
    protected int viewIndex = 0;
    protected int baseViewIndex = 0;
    protected ViewFlipper vf = null;

    @Override
    public void setViewId(int id) {
        this.viewIndex = id;
    }
    
    public int getViewId() {
        return this.viewIndex;
    }
    
    /**
     * Switch to the given view by id and animate the change.
     * @param viewId The view to switch to, for findViewById().
     */
    public void switchView(int viewId){
        View target = this.getView().findViewById(viewId);
        switchView(target);
    }
    
    /**
     * Switch to the given view by object and animate the change.
     * @param v The view to switch to.
     */
    public void switchView(View v){
        vf.setOutAnimation(CaratMainActivity.outtoLeft);
        vf.setInAnimation(CaratMainActivity.inFromRight);
        vf.setDisplayedChild(vf.indexOfChild(v));
        viewIndex = vf.indexOfChild(v);
    }
    

    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null){
        int viewId = savedInstanceState.getInt("viewId");
        if (viewId != -1)
            viewIndex = viewId;
        }
        super.onCreate(savedInstanceState);
    }
    
    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        outState.putInt("viewId", getViewId());
        super.onSaveInstanceState(outState);
    }

    /**
     * 
     * @return true if the calling activity is finished.
     */
    public boolean onBackPressed() {
        if (vf.getDisplayedChild() != baseViewIndex) {
            vf.setOutAnimation(CaratMainActivity.outtoRight);
            vf.setInAnimation(CaratMainActivity.inFromLeft);
            vf.setDisplayedChild(baseViewIndex);
            viewIndex = baseViewIndex;
            return false;
        } else
            return true;
    }
}
