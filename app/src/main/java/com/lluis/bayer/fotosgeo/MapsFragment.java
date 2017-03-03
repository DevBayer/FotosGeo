package com.lluis.bayer.fotosgeo;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;


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
