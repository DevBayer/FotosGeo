package com.lluis.bayer.fotosgeo;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayWithIW;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by 23878410v on 03/03/17.
 */

public class InfoWindow extends BasicInfoWindow {
    private Context mContext;
    private String absolutePath;
    private String name;

    public InfoWindow(Context context, MapView mapView, String absolute, String name) {
        //super(R.layout.bonuspack_bubble, mapView);
        super(R.layout.bubble, mapView);
        mContext = context;
        absolutePath = absolute;
        this.name = name;
    }

    @Override
    public void onOpen(Object item) {
        //super.onOpen(item);
        ImageView imageView = (ImageView) mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_image);
        File f = new File(absolutePath);
        if (f.exists()) {
            Glide.with(mContext)
                    .load(Uri.fromFile(f))
                    .centerCrop()
                    .into(imageView);
        } else {
            StorageReference storageReference = ((MainActivity) mContext.getApplicationContext()).getStorage();
            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(storageReference.child(name))
                    .centerCrop()
                    .into(imageView);
        }
    }
}
