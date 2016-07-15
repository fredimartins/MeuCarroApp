package meucarro.com.meucarroapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewCarAtivity extends Activity {

    Button btnSend;
    TextView placa;
    TextView fabricante;
    TextView modelo;
    ProgressDialog progress;
    Double lat;
    Double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_car);

        btnSend = (Button) findViewById(R.id.btnEnviar);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    sendNewCar();
                }
            }
        });

        placa = (TextView) findViewById(R.id.placa);
        modelo = (TextView) findViewById(R.id.modelo);
        fabricante = (TextView) findViewById(R.id.fabricante);

        double[] latLong = getGPS();
        lat = latLong[0];
        lng = latLong[1];
    }

    private double[] getGPS() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        Location l = null;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

        for (int i = providers.size() - 1; i >= 0; i--) {

            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        double[] gps = new double[2];
        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }else
        {
            gps[0] = -19.931866;
            gps[1] = -44.142197;
        }
        return gps;
    }


    protected void sendNewCar() {
        progress = ProgressDialog.show(this, getString(R.string.msg_calling_server),
                getString(R.string.msg_wait_new_car), true);

        JSONObject jsonBody = new JSONObject();
        try {
            //nao internacionalizar, o ws é em pt-br
            jsonBody.put("placa", placa.getText().toString());
            jsonBody.put("desc", fabricante.getText().toString() + " " + modelo.getText().toString());
            jsonBody.put("lat", lat);
            jsonBody.put("lng", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = "http://fredimartins.com:8080/carros";
        JsonObjectRequest req = new JsonObjectRequest(URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(getApplicationContext(), getString(R.string.msg_new_car_ok),
                                    Toast.LENGTH_SHORT).show();
                            NewCarAtivity.this.finish();
                        } catch (Exception e) {
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


        // add the request object to the queue to be executed
        CustomVolleyController.getmInstance().addToRequestQueue(req);

        progress.dismiss();

    }

    protected boolean validate() {
        boolean result = true;

        Pattern placa_pattern = Pattern.compile("[a-zA-Z]{3,3}-\\d{4,4}");
        Matcher matcher = placa_pattern.matcher(placa.getText().toString());
        if (!matcher.find()) {
            result = false;
            placa.setError(getString(R.string.warn_license_plate));
            placa.requestFocus();
            placa.setSelected(true);
        }

        Pattern modelo_pattern = Pattern.compile("[a-zA-Z] \\d{1,1}.\\d{1,1}");
        matcher = modelo_pattern.matcher(modelo.getText().toString());
        if (!matcher.find()) {
            result = false;
            modelo.setError(getString(R.string.warn_model));
            modelo.requestFocus();
            modelo.setSelected(true);
        }

        if(fabricante.getText().toString().equals(""))
        {
            result = false;
            fabricante.setError(getString(R.string.warn_manufacturer));
            fabricante.requestFocus();
            fabricante.setSelected(true);
        }

        return result;
    }

}

