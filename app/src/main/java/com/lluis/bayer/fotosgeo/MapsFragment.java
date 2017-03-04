package com.lluis.bayer.fotosgeo;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.StorageReference;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment {

    Context mContext;
    MapView map;
    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getContext();
        View fragment = inflater.inflate(R.layout.fragment_maps, container, false);
        map = (MapView) fragment.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);


        IMapController mapController = map.getController();
        mapController.setZoom(18);

        GPSTracker tracker = new GPSTracker(getContext());
        if(tracker.canGetLocation()){
            GeoPoint startPoint = new GeoPoint(tracker.getLatitude(), tracker.getLongitude());
            mapController.setCenter(startPoint);
        }else{
            GeoPoint startPoint = new GeoPoint(41.390205, 2.154007);
            mapController.setCenter(startPoint);
        }

        final RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(mContext);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                // A new media has been added, add it to the displayed list
                try {
                    Media media = dataSnapshot.getValue(Media.class);
                    Marker startMarker = new Marker(map);
                    if(media.type.equals("photo")){
                        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_photo_camera_black_24px));
                    }else{
                        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_videocam_black_24px));
                    }
                    //startMarker.setInfoWindow(new InfoWindow(map));

                    File f = new File(media.absolutePath);
                    if (f.exists()) {
                        startMarker.setImage(getResources().getDrawable(R.drawable.com_facebook_auth_dialog_background));

                    } else {
                        startMarker.setImage(getResources().getDrawable(R.drawable.com_facebook_auth_dialog_background));
                    }

                    startMarker.setInfoWindow(new InfoWindow(map));
                    startMarker.setPosition(new GeoPoint(Double.parseDouble(media.lat), Double.parseDouble(media.lon)));
                    poiMarkers.add(startMarker);
                }catch(NullPointerException e){

                }catch(NumberFormatException e){

                }
                map.invalidate();
                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        ((MainActivity)getActivity()).getDB().addChildEventListener(childEventListener);

        map.getOverlays().add(poiMarkers);
        map.invalidate();

        return fragment;
    }
}
