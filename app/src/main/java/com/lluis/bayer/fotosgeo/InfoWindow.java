package com.lluis.bayer.fotosgeo;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

/**
 * Created by 23878410v on 03/03/17.
 */

public class InfoWindow extends MarkerInfoWindow {
    POI mSelectedPoi;

    public InfoWindow(MapView mapView) {
        super(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mapView);
        Button btn = (Button) (mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_moreinfo));
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mSelectedPoi.mUrl != null) {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mSelectedPoi.mUrl));
                    view.getContext().startActivity(myIntent);
                } else {
                    Toast.makeText(view.getContext(), "Button clicked", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onOpen(Object item) {
        super.onOpen(item);
        mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_moreinfo).setVisibility(View.VISIBLE);
        Marker marker = (Marker) item;
        mSelectedPoi = (POI) marker.getRelatedObject();

        //8. put thumbnail image in bubble, fetching the thumbnail in background:
        if (mSelectedPoi.mThumbnailPath != null) {
            ImageView imageView = (ImageView) mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_image);
            mSelectedPoi.fetchThumbnailOnThread(imageView);
        }
    }
}
