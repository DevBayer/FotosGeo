package app.adapters;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.lluis.bayer.fotosgeo.Activities.MainActivity;
import com.lluis.bayer.fotosgeo.R;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.io.File;

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
