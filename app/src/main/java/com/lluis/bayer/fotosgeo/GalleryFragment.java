package com.lluis.bayer.fotosgeo;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class GalleryFragment extends Fragment {

    private FirebaseListAdapter mAdapter;
    private AVLoadingIndicatorView avi;

    public GalleryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_main, container, false);

        avi= (AVLoadingIndicatorView) fragment.findViewById(R.id.avi);
        avi.setIndicator("BallPulseIndicator");
        avi.show();

        GridView gallery = (GridView) fragment.findViewById(R.id.gallery);

        mAdapter = new FirebaseListAdapter<Media>(getActivity(), Media.class, R.layout.gridview_media, ((MainActivity)getActivity()).getDB()) {
            @Override
            protected void populateView(View view, Media media, int position) {
                if(avi.isEnabled()){
                    avi.hide();
                }
                    ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                    File f = new File(media.absolutePath);
                    if (f.exists()) {
                        Glide.with(getContext())
                                .load(Uri.fromFile(f))
                                .centerCrop()
                                .into(imageView);
                    } else {
                        StorageReference storageReference = ((MainActivity) getActivity()).getStorage();
                        Glide.with(getContext())
                                .using(new FirebaseImageLoader())
                                .load(storageReference.child(media.name))
                                .centerCrop()
                                .into(imageView);
                    }
            }



        };

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Media media = (Media) adapterView.getItemAtPosition(i);
                if(media.type.equals("video")) {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    File file = new File(media.absolutePath);
                    intent.setDataAndType(Uri.fromFile(file), "video/*");
                    startActivity(intent);
                }else{
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    File file = new File(media.absolutePath);
                    intent.setDataAndType(Uri.fromFile(file), "image/*");
                    startActivity(intent);
                }
            }
        });

        gallery.setAdapter(mAdapter);

        return fragment;
    }
}
