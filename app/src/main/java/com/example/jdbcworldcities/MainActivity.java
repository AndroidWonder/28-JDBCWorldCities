package com.example.jdbcworldcities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Thread t = null;
    private ArrayList<String> list;
    private TextView texted;
    private String name, code, district;
    private int population;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<String>();
        t = new Thread(background);
        t.start();

        //wait ubtil thread is finished
        try {
            t.join();
        } catch (InterruptedException e) {
            Log.e("JDBC", "Interrupted Exception");
        }

        texted = (TextView) findViewById(R.id.text);
        texted.setText("");

        //write data to UI
        for (int i = 0; i < list.size(); i++) {
            texted.append(list.get(i) + "\n");
        }

    }

    Runnable background = new Runnable() {
        public void run() {
            String URL = "jdbc:mysql://frodo.bentley.edu:3306/world";
            String username = "Android";
            String password = "android";

            try { //load driver into VM memory
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                Log.e("JDBC", "Did not load driver");

            }

            Statement stmt = null;
            Connection con = null;
            try { //create connection to database
                con = DriverManager.getConnection(
                        URL,
                        username,
                        password);
                stmt = con.createStatement();

                ResultSet result = stmt.executeQuery(
                        "SELECT * FROM City ORDER BY Name LIMIT 30 OFFSET 10;");

                //for each record in City table add City to ArrayList and add city data to log
                while (result.next()) {
                    name = result.getString("Name");
                    code = result.getString("CountryCode");
                    district = result.getString("District");
                    population = result.getInt("Population");
                    City city = new City(name, code, district, population);
                    list.add(city.toString());
                    Log.e("JDBC", name + " " + code);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try { //close may throw checked exception
                    if (con != null)
                        con.close();
                } catch (SQLException e) {
                    Log.e("JDBC", "close connection failed");
                }
            }
            ;
        } //run
    }; //background


}
