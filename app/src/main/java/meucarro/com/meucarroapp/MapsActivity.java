package meucarro.com.meucarroapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import meucarro.com.meucarroapp.meucarro.com.meucarroapp.model.Carro;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String CARID;
    private String URL = "http://fredimartins.com:8080/carros/";
    private Carro carro;
    Handler mainHandler;
    MyTimerTask yourTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        CARID = intent.getStringExtra("CARID");
        URL = URL + CARID;
    }

    protected void stopAll(){
        if(yourTask != null){
            yourTask.cancel();
            yourTask = null;
        }
        finish();
    }

    @Override
    public void onBackPressed()
    {
        stopAll();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAll();
    }

    public void getCarFields() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    carro = new Carro(response);
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_car_updated),
                            Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(),
                        Toast.LENGTH_LONG).show();
            }
        });

        CustomVolleyController.getmInstance().addToRequestQueue(request);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        yourTask = new MyTimerTask();
        Timer t = new Timer();
        t.scheduleAtFixedRate(yourTask, 0, 5000);  // 5 sec interval

        mainHandler = new Handler(this.getMainLooper());
    }

    class MyTimerTask extends TimerTask {
        public void run() {
            getCarFields();
            if (carro != null) {
                try {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LatLng carLocation = new LatLng(carro.getLat(), carro.getLng());
                            MarkerOptions options = new MarkerOptions();
                            options.position(carLocation).title(carro.getHeaderString());
                            options.position(carLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.meucarro_icon));
                            mMap.addMarker(options);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(carLocation));
                            mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
