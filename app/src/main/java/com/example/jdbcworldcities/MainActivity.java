/*
Background thread does SELECT, each record is put into a City object,
the object is placed in an ArrayList<City>, bachground thread finishes and
main thread writes data to UI.

Note the use of a try with resources block and a thread join.
 */

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

        //wait until thread is finished
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
            String username = "harry";
            String password = "harry";

            Statement stmt = null;

            //Note try with resources block
            try  //create connection to database
                    (Connection con = DriverManager.getConnection(
                        URL,
                        username,
                        password)) {
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
            }
        } //run
    }; //background


}
