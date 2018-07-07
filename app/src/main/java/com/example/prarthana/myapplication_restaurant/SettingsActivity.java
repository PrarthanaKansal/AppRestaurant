package com.example.prarthana.myapplication_restaurant;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

 public class SettingsActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // <item name="preferenceTheme">@style/PreferenceThemeOverlay</item> add this in styles
            setContentView(R.layout.activity_settings);
            ActionBar actionBar= getSupportActionBar();
            if(actionBar!=null){
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id=item.getItemId();
            if(id==android.R.id.home){
                // onBackPressed();
//            Intent intent = new Intent(this,MainActivity.class);
//            startActivity(intent);
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }


    }


