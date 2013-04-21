package hu.sze.zoltan.futarflotta;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
    {
        if(!shadow)
        {
            super.draw(canvas, mapView, false);
        }
    }

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	public MyItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		Log.d("Tapped", mOverlays.get(index).getSnippet());
		return true;
	}

}
