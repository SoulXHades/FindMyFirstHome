package com.example.findmyfirsthome.Controller;

import android.app.Application;
import android.content.Context;

import com.example.findmyfirsthome.Entity.HDBDevelopment;

import java.util.ArrayList;
import java.util.HashMap;

public class DevelopmentDetailControl extends Application {
    private DatabaseController databaseController;
    private HDBDevelopment hdbd;

    public DevelopmentDetailControl(String estateName)
    {
        databaseController = new DatabaseController(this.getContext());

        //get data from Database Controller as entity object
        //hdb = databaseController.getHDBData(estateName);
    }

    public Context getContext() {
        return this.getBaseContext();
    }

    //get description from entity
    public String getDevelopmentDescription()
    {
        return this.hdbd.getDevelopmentDescription();
    }

    //get image from entity
    /*public String getDevelopmentImage()
    {
        return this.hdbd.getDevelopmentImage();
    }*/

    //return ArrayList of FlatType contents to the boundary
    public ArrayList<HashMap<String, Object>> getTableContent()
    {
        //get from development entity the ArrayList of FlatType details of that estate/development
        return hdbd.getHDBFlatTypeDetailsList();
    }
}
