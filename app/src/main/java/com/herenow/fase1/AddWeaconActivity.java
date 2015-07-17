package com.herenow.fase1;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import util.GPSCoordinates;
import util.TYPE;
import util.Weacon;

import static util.TYPE.OTHER;


public class AddWeaconActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private WifiManager wifi;
    private Spinner spinner;
    private ArrayList<String> lista;
    private String selectedSSID;
    private String selectedBSSID;
    private CropImageView mCropImageView;
    private Bitmap logo;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GPSCoordinates gps;
    private TextView tvMessage;
    private Bitmap croppedImage;
    private Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weacon);

        mCropImageView = (CropImageView) findViewById(R.id.iv_logo);
        tvMessage = (TextView) this.findViewById(R.id.tv_Message);
        buttonSend = (Button) this.findViewById(R.id.send_weacon_button);
        buttonSend.setFocusableInTouchMode(true);
        tvMessage.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) {
                    buttonSend.requestFocus();
                }
                return false;
            }
        });


        mCropImageView.setImageResource(R.mipmap.ic_launcher);

//        mCropImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        FillSpinner();
        GetLocation();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    private void GetLocation() {
        buildGoogleApiClient();
    }

    private void FillSpinner() {
        wifi = (WifiManager) this.getSystemService(this.getBaseContext().WIFI_SERVICE);
        wifi.startScan();
        final List<ScanResult> sr = wifi.getScanResults();
        lista = new ArrayList<String>();

        spinner = (Spinner) this.findViewById(R.id.spinner);
//        spinner = (Spinner) findViewById(R.id.spinner);
        for (ScanResult r : sr) {
            lista.add(r.SSID);
        }
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lista);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adaptador);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(parent.getContext(), "Seleccionado: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                selectedSSID = parent.getItemAtPosition(position).toString();
                selectedBSSID = sr.get(position).BSSID;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void OnClickAddLogo(View view) {

//        ImageView imageView = (ImageView) this.findViewById(R.id.iv_logo);
//        imageView.setImageBitmap(logo);
    }

    /**
     * Crop the image and set it back to the cropping view.
     */
//    public void onCropImageClick(View view) {
//        Bitmap cropped = mCropImageView.getCroppedImage(500, 500);
//        if (cropped != null)
//            mCropImageView.setImageBitmap(cropped);
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);
            mCropImageView.setImageUri(imageUri);
        }
    }

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    public void SendWeacon(View view) {
        int level = -76;
        boolean validated = false;
        TYPE type = OTHER;
        //TODO validation of fields
        TextView tvName = (TextView) this.findViewById(R.id.tv_name);
        TextView tvUrl = (TextView) this.findViewById(R.id.tv_url);


        String name = tvName.getText().toString();
        String url = tvUrl.getText().toString();
        String message = tvMessage.getText().toString();
        logo = Bitmap.createScaledBitmap(croppedImage, 120, 120, true);

        try {
            Weacon weacon = new Weacon(selectedSSID, selectedBSSID, name, url, message, gps, validated, type, level, logo);
            weacon.upload(this.getBaseContext());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_weacon, menu);
        return true;
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

    @Override
    public void onConnected(Bundle bundle) {
        //TODO replace by Position.GetlastPosition

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
//            cityText.setText(mLastLocation.toString());
        }
        gps = new GPSCoordinates(mLastLocation);
//        TextView textView = (TextView) this.findViewById(R.id.tv_Message);//testing
//        textView.setText("Recibida localización: " + gps);
//        textView.setText("Recibida localización: " + mLastLocation.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void OnClickCrop(View view) {
        croppedImage = mCropImageView.getCroppedImage();

//        ImageView croppedImageView = (ImageView) findViewById(R.id.croppedImageView);
        mCropImageView.setImageBitmap(croppedImage);

    }

    public void OnClickLoadImage(View view) {
        startActivityForResult(getPickImageChooserIntent(), 200);

        mCropImageView.setAspectRatio(200, 200);
        mCropImageView.setFixedAspectRatio(true);
        mCropImageView.setGuidelines(2);
    }
}
