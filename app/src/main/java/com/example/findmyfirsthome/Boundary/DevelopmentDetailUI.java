package com.example.findmyfirsthome.Boundary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.findmyfirsthome.Controller.DevelopmentDetailControl;
import com.example.findmyfirsthome.Entity.AffordabilityReport;
import com.example.findmyfirsthome.Entity.MapData;
import com.example.findmyfirsthome.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class DevelopmentDetailUI extends FragmentActivity implements OnMapReadyCallback{

    private static Context context;
    private DevelopmentDetailControl ddc;
    private GoogleMap mMap;
    private ArrayList<HashMap<String, Object>> HDBFlatTypeDetailsList;
    private ArrayList<ArrayList> amenitiesDetailsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.development_detail_ui);
    }

    //runs after onCreate()
    @Override
    protected void onStart()
    {
        super.onStart();

        //to get data passed from previous intent/activity/boundary
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        //to be used when combine work with the rest
        final String estateName = extras.getString("HDBName");
        //String estateName = "Test @ Sembawang"; //temporary used for now before combining with others

        ////////////////////////////////////////////////////////////////////////////////////////////////////
        //initialize controller
        //get specific estate/development entity object
        ddc = new DevelopmentDetailControl(estateName, this);


        ////////////////////////////////////////////////////////////////////////////////////////////////////
        //set content to show in the UI
        //set what development/estate name to display in the UI
        final TextView estateNameView = findViewById(R.id.text_estateName);
        estateNameView.setText(estateName);

        //set image of development/estate
        final ImageView estateImage = findViewById(R.id.image_estatePic);
        Glide.with(this).load(ddc.getDevelopmentImage()).into(estateImage);

        //set description of development/estate
        //get from controller which get from database controller which get from database
        final TextView estateDescription = findViewById(R.id.text_estateDescription);
        estateDescription.setText(ddc.getDevelopmentDescription());


        //for Table of FlatType info
        final TableLayout tableLayOut = findViewById(R.id.table_developmentTable);

        HDBFlatTypeDetailsList = ddc.getTableContent();

        Object temp;

        for(HashMap<String, Object> HDBFlatTypeDetails : HDBFlatTypeDetailsList)
        {
            TextView tv = new TextView(this);
            tv.setGravity(Gravity.CENTER);
            //set all fields from HashMap
            temp = HDBFlatTypeDetails.get("flatType");
            if(temp == null)
                break;
            //setText must be in String
            tv.setText((String) temp);
            //set text color to black only if user can afford that flat room
            if((Boolean) HDBFlatTypeDetails.get("affordability"))
                tv.setTextColor(Color.parseColor("#000000"));

            TextView tv1 = new TextView(this);
            tv1.setGravity(Gravity.CENTER);
            //set all fields from HashMap
            temp = HDBFlatTypeDetails.get("price");

            if(temp == null)
                break;
            //setText cannot set Double
            tv1.setText("$" + String.format ("%,.2f", (Double) temp));
            //set text color to black only if user can afford that flat room
            if((Boolean) HDBFlatTypeDetails.get("affordability"))
                tv1.setTextColor(Color.parseColor("#000000"));


            //create generation button in table last column
            Button generateReportButton = new Button(this);
            generateReportButton.setGravity(Gravity.CENTER);
            generateReportButton.setId(((String) HDBFlatTypeDetails.get("flatType")).charAt(0));    //use room type number as id
            generateReportButton.setText("Generate");
            generateReportButton.setTextSize(5*(this.getResources().getDisplayMetrics().density));  //size of text
            generateReportButton.setLayoutParams(new FrameLayout.LayoutParams(200, 75));    //size for button
            generateReportButton.setPadding(0, 0, 0, 0);    //to not crop the words because default have some padding
            generateReportButton.setTextColor(Color.parseColor("#FFFFFF")); //set text to white color
            generateReportButton.setBackground(getDrawable(R.drawable.generate_report_btn));    //set round corner button
            generateReportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent generateAfReportIntent = new Intent(getApplicationContext(), AffordabilityReportUI.class);
                    generateAfReportIntent.putExtra("estateName", estateName);  //send the estate/development name
                    generateAfReportIntent.putExtra("FlatType", String.valueOf(v.getId())); //send the FlatType
                    startActivity(generateAfReportIntent);
                }
            });


            //set all fields from HashMap
            temp = HDBFlatTypeDetails.get("affordability");

            //set FlatType and price content of that row to dimgrey color
            if((Boolean)temp == false)
            {
                tv.setTextColor(Color.parseColor("#696969"));
                tv1.setTextColor(Color.parseColor("#696969"));
            }

            FrameLayout frameLayout_Buttons = new FrameLayout(this);
            frameLayout_Buttons.setPadding(100,20, 50,0);
            frameLayout_Buttons.setForegroundGravity(Gravity.CENTER);
            frameLayout_Buttons.addView(generateReportButton);

            //set each cell of the table into a row
            TableRow tr = new TableRow(this);
            tr.setGravity(Gravity.CENTER);
            tr.addView(tv);
            tr.addView(tv1);
            tr.addView(frameLayout_Buttons);

            //set row into table
            TableLayout tableLayout = findViewById(R.id.table_developmentTable);
            tableLayOut.addView(tr);
        }


        //for Map display
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_developmentDetails);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        amenitiesDetailsList = ddc.getAmenitiesDetailsList();

        //only plot if there is data received
        if(HDBFlatTypeDetailsList.size() != 0) {
            // get location of development location and add a marker to it
            LatLng developmentLoc = ddc.getDevelopmentLocation();
            mMap.addMarker(new MarkerOptions().position(developmentLoc).title(ddc.getDevelopmentName()));

            for(ArrayList amenity : amenitiesDetailsList) {
                LatLng nearBy = new LatLng(1.345734, 103.681283);
                mMap.addMarker(new MarkerOptions().position((LatLng) amenity.get(MapData.COORDINATES))
                        .title((String) amenity.get(MapData.AMENITIESNAME)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }

            //move map focus to the location of the main marker
            mMap.moveCamera(CameraUpdateFactory.newLatLng(developmentLoc));
        }
    }

}
