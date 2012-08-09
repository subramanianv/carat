package edu.berkeley.cs.amplab.carat.android;

import edu.berkeley.cs.amplab.carat.android.CaratApplication.Type;
import edu.berkeley.cs.amplab.carat.android.lists.HogsBugsAdapter;
import edu.berkeley.cs.amplab.carat.android.storage.SimpleHogBug;
import edu.berkeley.cs.amplab.carat.android.ui.BaseVFActivity;
import edu.berkeley.cs.amplab.carat.android.ui.DrawView;
import edu.berkeley.cs.amplab.carat.android.ui.FlipperBackListener;
import edu.berkeley.cs.amplab.carat.android.ui.LocalizedWebView;
import edu.berkeley.cs.amplab.carat.android.ui.SwipeListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

public class CaratBugsOrHogsActivity extends BaseVFActivity {

	protected boolean isBugsActivity = false;
	protected Type activityType = Type.HOG;
	private DrawView w = null;
	private View detailPage = null;
	private View tv = null;
	private int emptyIndex = -1;
	private int position = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	public CaratBugsOrHogsActivity(){
	    this(Type.BUG);
	}
	
	public CaratBugsOrHogsActivity(Type type){
		if (type != null) {
			if (type == Type.BUG) {
				activityType = Type.BUG;
				isBugsActivity = true;
			} else {
				activityType = Type.HOG;
				isBugsActivity = false;
			}
		}
	}
	
	public void setType(Type type){
	    this.activityType = type;
	    if (type == Type.BUG) {
            activityType = Type.BUG;
            isBugsActivity = true;
        } else {
            activityType = Type.HOG;
            isBugsActivity = false;
        }
	    refresh();
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.hogs, container, false);
        final Context c = this.getActivity().getApplicationContext();
		vf = (ViewFlipper) v.findViewById(R.id.flipper);
		View baseView = v.findViewById(android.R.id.list);
		baseView.setOnTouchListener(SwipeListener.instance);
		vf.setOnTouchListener(SwipeListener.instance);
		baseViewIndex = vf.indexOfChild(baseView);
		
		tv = inflater.inflate(R.layout.emptyactions, null);
		if (tv != null) {
			vf.addView(tv);
			emptyIndex = vf.indexOfChild(tv);
		}
		// initBugsView();
		// initGraphView();
		initGraphChart(v, inflater, c);
		initDetailView(v);

		if (savedInstanceState != null){
		    viewIndex = savedInstanceState.getInt("viewId");
		int pos = savedInstanceState.getInt("position");
		boolean bug = savedInstanceState.getBoolean("bug");
		if (bug)
		    this.setType(Type.BUG);
		else
		    this.setType(Type.HOG);
            if (pos != -1) {
                /*
                 * Fix up detail page
                 */
                final ListView lv = (ListView) v
                        .findViewById(android.R.id.list);
                Object o = lv.getItemAtPosition(pos);
                SimpleHogBug fullObject = (SimpleHogBug) o;
                if (fullObject != null) {
                    String label = CaratApplication.labelForApp(c,
                            fullObject.getAppName());
                    Drawable icon = CaratApplication.iconForApp(c,
                            fullObject.getAppName());
                    ((TextView) detailPage.findViewById(R.id.name))
                            .setText(label);
                    ((ImageView) detailPage.findViewById(R.id.appIcon))
                            .setImageDrawable(icon);
                    ((ProgressBar) detailPage.findViewById(R.id.confidenceBar))
                            .setProgress((int) (fullObject.getwDistance() * 100));
                    w.setHogsBugs(fullObject, label, isBugsActivity);
                }
            }
		}

		if (viewIndex == 0)
			vf.setDisplayedChild(baseViewIndex);
		else
			vf.setDisplayedChild(viewIndex);
		return v;
	}

	private void initGraphChart(View v, LayoutInflater inflater, final Context c) {
		detailPage = inflater.inflate(R.layout.graph, null);
		ViewGroup g = (ViewGroup) detailPage;
		w = new DrawView(c);
		g.addView(w);
		vf.addView(detailPage);

		OnClickListener detailViewer = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switchView(R.id.detailView);
			}
		};

		View moreinfo = detailPage.findViewById(R.id.moreinfo);
		moreinfo.setOnClickListener(detailViewer);

		View item = detailPage.findViewById(R.id.confidenceBar);
		item.setClickable(true);
		item.setOnClickListener(detailViewer);
		item = detailPage.findViewById(R.id.confidenceLegend);
		item.setClickable(true);
		item.setOnClickListener(detailViewer);
		item = detailPage.findViewById(R.id.name);
		item.setClickable(true);
		item.setOnClickListener(detailViewer);
		item = detailPage.findViewById(R.id.appIcon);
		item.setClickable(true);
		item.setOnClickListener(detailViewer);

		detailPage.setOnTouchListener(new FlipperBackListener(this, vf,
				baseViewIndex, true));

		final ListView lv = (ListView) v.findViewById(android.R.id.list);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
			    CaratBugsOrHogsActivity.this.position = position;
				Object o = lv.getItemAtPosition(position);
				SimpleHogBug fullObject = (SimpleHogBug) o;
				// View target = findViewById(R.id.hogsGraphView);
				View target = detailPage;
				String label = CaratApplication.labelForApp(
						c, fullObject.getAppName());
				Drawable icon = CaratApplication.iconForApp(
						c, fullObject.getAppName());
				((TextView) detailPage.findViewById(R.id.name)).setText(label);
				((ImageView) detailPage.findViewById(R.id.appIcon))
						.setImageDrawable(icon);
				((ProgressBar) detailPage.findViewById(R.id.confidenceBar))
						.setProgress((int) (fullObject.getwDistance() * 100));
				w.setHogsBugs(fullObject, label, isBugsActivity);
				w.postInvalidate();
				switchView(target);
			}
		});
	}

	private void initDetailView(View v) {
		LocalizedWebView webview = (LocalizedWebView) v.findViewById(R.id.detailView);

		webview.loadUrl("file:///android_asset/detailinfo.html");
		webview.setOnTouchListener(new FlipperBackListener(this, vf, vf
				.indexOfChild(detailPage), false));
	}

	public void refresh() {
	    Activity a = getActivity();
	    if (a == null)
	        return;
		CaratApplication app = (CaratApplication) a.getApplication();
		if (app == null)
		    return;
		final ListView lv = (ListView) getView().findViewById(android.R.id.list);
		if (isBugsActivity)
			lv.setAdapter(new HogsBugsAdapter(app, CaratApplication.s
					.getBugReport()));
		else
			lv.setAdapter(new HogsBugsAdapter(app, CaratApplication.s
					.getHogReport()));
		emptyCheck(lv);
	}

	private void emptyCheck(ListView lv) {
		if (lv.getAdapter().isEmpty()) {
			if (vf.getDisplayedChild() == baseViewIndex)
				vf.setDisplayedChild(emptyIndex);
		} else {
			if (vf.getDisplayedChild() == emptyIndex) {
				vf.setDisplayedChild(baseViewIndex);
			}
		}
	}

	
	
	/* (non-Javadoc)
     * @see edu.berkeley.cs.amplab.carat.android.ui.BaseVFActivity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("bug", isBugsActivity);
        outState.putInt("position", position);
        super.onSaveInstanceState(outState);
    }


    /**
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
    public void onResume() {
		if (isBugsActivity)
			CaratApplication.setBugs(this);
		else
			CaratApplication.setHogs(this);
		refresh();
		super.onResume();
	}
}
