package meucarro.com.meucarroapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import meucarro.com.meucarroapp.meucarro.com.meucarroapp.model.Carro;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "http://fredimartins.com:8080/carros";
    private JSONArray carros;
    private String selectedCar;
    private Integer selectedGroup;
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private HashMap<String, List<String>> listDataChild;
    private FloatingActionButton fab;
    private FloatingActionButton addFab;
    private FloatingActionButton updateFab;

    //Save the FAB's active status
    //false -> fab = close
    //true -> fab = open
    private boolean FAB_Status = false;

    //Animations
    private Animation show_fab_1;
    private Animation hide_fab_1;
    private Animation show_fab_2;
    private Animation hide_fab_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Floating Action Buttons
        fab = (FloatingActionButton) findViewById(R.id.fab);
        addFab = (FloatingActionButton) findViewById(R.id.fab_1);
        updateFab = (FloatingActionButton) findViewById(R.id.fab_2);

        //Animations
        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);

        try {

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (FAB_Status == false) {
                        //Display FAB menu
                        expandFAB();
                        FAB_Status = true;
                    } else {
                        //Close FAB menu
                        hideFAB();
                        FAB_Status = false;
                    }
                }
            });

            addFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openNewCar();
                }
            });

            updateFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCarsInWs();
                }
            });

            // get the listview
            expListView = (ExpandableListView) findViewById(R.id.lvExp);

            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    String item = (String) listAdapter.getChild(groupPosition, childPosition);
                    selectedCar = (String) listAdapter.getChild(groupPosition, 0); // get car id
                    selectedCar = selectedCar.replace("ID: ", "");
                    selectedGroup = groupPosition;

                    if (item.equals(getString(R.string.field_show_in_map))) {
                        openMap();
                    } else if (item.contains(getString(R.string.field_alarm))) {
                        ChangeAlarmStatus(groupPosition);
                    } else if (item.contains(getString(R.string.field_oil_change_in))) {
                        UpdateOilChange(groupPosition);
                    }
                    return true;
                }
            });

            getCarsInWs();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void changeLocale(String newLocale) {
        Locale locale = new Locale(newLocale);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.brLang) {
            changeLocale("pt");
        } else if (item.getItemId() == R.id.enLang) {
            changeLocale("en");
        }
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getCarsInWs();
    }

    private void expandFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) addFab.getLayoutParams();
        layoutParams.rightMargin += (int) (addFab.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (addFab.getHeight() * 0.25);
        addFab.setLayoutParams(layoutParams);
        addFab.startAnimation(show_fab_1);
        addFab.setClickable(true);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) updateFab.getLayoutParams();
        layoutParams2.rightMargin += (int) (updateFab.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (updateFab.getHeight() * 1.5);
        updateFab.setLayoutParams(layoutParams2);
        updateFab.startAnimation(show_fab_2);
        updateFab.setClickable(true);
    }

    private void hideFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) addFab.getLayoutParams();
        layoutParams.rightMargin -= (int) (addFab.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (addFab.getHeight() * 0.25);
        addFab.setLayoutParams(layoutParams);
        addFab.startAnimation(hide_fab_1);
        addFab.setClickable(false);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) updateFab.getLayoutParams();
        layoutParams2.rightMargin -= (int) (updateFab.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (updateFab.getHeight() * 1.5);
        updateFab.setLayoutParams(layoutParams2);
        updateFab.startAnimation(hide_fab_2);
        updateFab.setClickable(false);
    }

    protected void UpdateOilChange(final Integer group) {
        AlertDialog alerta;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_update_oil_change));
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton(getString(R.string.options_update_oil_change), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String OIL_URL = URL + "/" + selectedCar + "/set/trocaoleo/";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, OIL_URL, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_oil_change_success),
                                Toast.LENGTH_LONG).show();
                        carros.put(response);
                        updateCars(null, group);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_oil_change_fail),
                                Toast.LENGTH_LONG).show();
                        updateCars(error.toString(), null);
                    }
                });

                CustomVolleyController.getmInstance().addToRequestQueue(request);
            }
        });
        builder.setNegativeButton(getString(R.string.options_cancel_update_oil_change), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();

    }

    protected void ChangeAlarmStatus(final Integer group) {
        CharSequence options[] = new CharSequence[]{getString(R.string.options_deactivate), getString(R.string.options_activate)};
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.title_change_alarm))
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ALARM_URL = URL + "/" + selectedCar + "/set/alarme/" + String.valueOf(which);
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, ALARM_URL, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_alarm_change_success),
                                        Toast.LENGTH_LONG).show();
                                carros.put(response);
                                updateCars(null, group);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_alarm_change_fail),
                                        Toast.LENGTH_LONG).show();
                                updateCars(error.toString(), null);
                            }
                        });

                        CustomVolleyController.getmInstance().addToRequestQueue(request);
                    }
                }).show();
    }

    protected void openNewCar() {
        Intent myIntent = new Intent(MainActivity.this, NewCarAtivity.class);
        //myIntent.putExtra("CARID", selectedCar); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }

    protected void openMap() {
        Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
        myIntent.putExtra("CARID", selectedCar); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }

    protected void getCarsInWs() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_load_cars_success),
                        Toast.LENGTH_LONG).show();
                carros = response;
                updateCars(null, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_load_cars_fail),
                        Toast.LENGTH_LONG).show();
                updateCars(error.toString(), null);
            }
        });

        CustomVolleyController.getmInstance().addToRequestQueue(request);
    }

    protected void updateCars(String error, Integer opened_car) {
        listDataChild = new HashMap<String, List<String>>();

        if (error != null) {
            List<String> err = new ArrayList<String>();
            err.add(error.toString());
            listDataChild.put(getString(R.string.msg_error), err);
        } else if (carros != null) {
            try {

                for (int i = 0; i < carros.length(); i++) {
                    Carro ca = new Carro((JSONObject) carros.get(i));
                    listDataChild.put(ca.getHeaderString(), ca.getItensArray(this)); // Header, Child data
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        listAdapter = new ExpandableListAdapter(this, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        if (opened_car != null)
            expListView.expandGroup(opened_car);
    }
}
