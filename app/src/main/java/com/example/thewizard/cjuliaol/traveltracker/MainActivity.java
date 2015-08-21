package com.example.thewizard.cjuliaol.traveltracker;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener,
        MemoryDialogFragment.Listener,GoogleMap.OnMarkerDragListener,GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivityLog";
    private static final String MEMORY_DIALOG_TAG = "MemoryDialogTag";
    private  GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private HashMap<String,Memory> mMemories = new HashMap<>();
    private MemoriesDataSource mDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        mDataSource = new MemoriesDataSource(this);

        // CJL: This works with the loader classes: DbCursorLoader, MemoriesLoader and interface LoaderCallbacks<>
        // This Loader will manage our Cursor over the lifecycle of an Activity. Avoiding refetch when recreate activity
        // This mecanism persists beyond activities
        getLoaderManager().initLoader(0, null, this);

        // CJL: Having both the Loader and the map be asynchronous caused some problems which we fixed by waiting for the map to load
        //mapFragment.getMapAsync(this);

        mGoogleMap = mapFragment.getMap();
        onMapReady(mGoogleMap);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMapClickListener(this);

        mGoogleMap.setInfoWindowAdapter(new MarkerAdapter(getLayoutInflater(), mMemories));
        mGoogleMap.setOnMarkerDragListener(this);
        mGoogleMap.setOnInfoWindowClickListener(this);



        // Comment it to use DbCursorLoader and MemoriesLoader instead. This is for better when rotate, recreate, etc.
        /*new AsyncTask<Void,Void,List<Memory>>() {
            @Override
            protected List<Memory> doInBackground(Void... params) {
                return mDataSource.getAllMemories();
            }

            @Override
            protected void onPostExecute(List<Memory> memories) {
               Log.d(TAG,"Got results");
                onFetchedMemories(memories);
            }
        }.execute();

        //List<Memory> memories =
        Log.d(TAG, "End of MapReady");*/



    }

    private void onFetchedMemories(List<Memory> memories) {
        // CJL: Add all markers saved in database
        for (Memory memory: memories) {
            addMarker(memory);
        }
    }


    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "Latitude and Longitude: " + latLng);

        Memory memory =new Memory();

        updateMemoryPosition(memory, latLng);



        MemoryDialogFragment.newInstance (memory).show(getFragmentManager(), MEMORY_DIALOG_TAG);


    }



    @Override
    public void OnSaveClicked(Memory memory) {
        addMarker(memory);
        mDataSource.createMemory(memory);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG,"onCreateLoader");

        return new MemoriesLoader(this, mDataSource);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
       Log.d(TAG,"onLoadFinished");
        onFetchedMemories(mDataSource.cursorToMemories(data));
    }

    private void addMarker(Memory memory) {
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .draggable(true)
                .position(new LatLng(memory.latitude,memory.longitude))   );

        mMemories.put(marker.getId(), memory);
    }

    @Override
    public void OnCancelClicked(Memory memory) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
      Memory memory = mMemories.get(marker.getId());
        updateMemoryPosition(memory,marker.getPosition());
        mDataSource.updateMemory(memory);

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        Log.d(TAG,"Clicked on InfoWindows");
        final Memory memory = mMemories.get(marker.getId());


        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
       String [] actions = {"Edit","Delete"};
        builder
                .setTitle(memory.city + ", " + memory.country)
                .setItems(actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 1) {  // CJL: index in actions Array
                            marker.remove();
                            mDataSource.deleteMemory(memory);
                        }
                    }
                });

        //CJL: Another way to do it
        /*builder .setTitle("Do you want delete memory?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               Log.d(TAG,"Clicked delete button");
                marker.remove();
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG,"Clicked No button");
                    }
                });*/

        builder.create().show();

    }

    private void updateMemoryPosition(Memory memory, LatLng latLng) {
        Geocoder geocoder= new Geocoder(this);
        List<Address> matches = null;

        try {
            matches = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address bestMatch = (matches.isEmpty())? null: matches.get(0);

        int maxLine = bestMatch.getMaxAddressLineIndex();
        String city = bestMatch.getAddressLine(maxLine - 1);
        String country = bestMatch.getAddressLine(maxLine);


        memory.city = city;
        memory.country = country;
        memory.latitude = latLng.latitude;
        memory.longitude = latLng.longitude;
    }

    //Not use yet
    private void addGoogleAPIClient(){

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    //Not use yet
    @Override
    protected void onStart() {
        super.onStart();
        //mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


}
