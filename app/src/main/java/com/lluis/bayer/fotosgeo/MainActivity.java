package com.lluis.bayer.fotosgeo;

import android.content.Intent;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_TAKE_VIDEO = 2;

    private FloatingActionButton fab_photo;
    private FloatingActionButton fab_video;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private StorageReference userStorage;
    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference userDatabase;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String uuid = intent.getStringExtra("uuid");

        userStorage = mStorageRef.child(uuid);
        userDatabase = mDatabaseRef.child(uuid);


        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        fab_photo = (FloatingActionButton) findViewById(R.id.fab_photo);
        fab_video = (FloatingActionButton) findViewById(R.id.fab_video);

        fab_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        fab_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakeVideoIntent();
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public DatabaseReference getDB(){
        return userDatabase;
    }

    public StorageReference getStorage(){
        return userStorage;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment(), "Gallery");
        adapter.addFragment(new MapsFragment(), "Maps");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = Utils.createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                file = photoFile;
            }
        }
    }

    public void dispatchTakeVideoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = Utils.createVideoFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_VIDEO);
                file = photoFile;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_TAKE_VIDEO) {
            if (resultCode == RESULT_OK) {
                StorageReference uploadRef = userStorage.child(file.getName());
                uploadRef.putFile(Uri.fromFile(file));
                String lat = null;
                String lon = null;
                try {
                    ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                    String LATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                    String LATITUDE_REF = exif
                            .getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                    String LONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                    String LONGITUDE_REF = exif
                            .getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

                    Double Latitude = null, Longitude = null;

                    if ((LATITUDE != null) && (LATITUDE_REF != null) && (LONGITUDE != null)
                            && (LONGITUDE_REF != null)) {

                        if (LATITUDE_REF.equals("N")) {
                            Latitude = convertToDegree(LATITUDE);
                            lat = ""+Latitude;
                        } else {
                            Latitude = 0 - convertToDegree(LATITUDE);
                            lat = ""+Latitude;
                        }

                        if (LONGITUDE_REF.equals("E")) {
                            Longitude = convertToDegree(LONGITUDE);
                            lon = ""+Longitude;
                        } else {
                            Longitude = 0 - convertToDegree(LONGITUDE);
                            lon = ""+Longitude;
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                GPSTracker gps = new GPSTracker(this);
                if (lat == null && gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    lat = ""+latitude;
                }
                if (lon == null && gps.canGetLocation()) {
                    double longitude = gps.getLongitude();
                    lon = ""+longitude;
                }



                Media media = new Media(uploadRef.getName(), (requestCode == REQUEST_TAKE_PHOTO ? "photo" : "video"), file.getAbsolutePath(), lat, lon);
                userDatabase.push().setValue(media);

            }
        }
    }


    private Double convertToDegree(String location) {
        Double result = null;
        String[] DMS = location.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Double(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;

    };
}
