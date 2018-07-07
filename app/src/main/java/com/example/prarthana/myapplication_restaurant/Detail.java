package com.example.prarthana.myapplication_restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Detail extends AppCompatActivity {

    TextView name;
    TextView address;
    ImageView imageView;
    TextView rating;
    TextView link;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.share){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getIntent().getStringExtra("name"));
            sendIntent.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra("address"));
            sendIntent.setType("text/plain");
//            startActivity(sendIntent);
            startActivity(Intent.createChooser(sendIntent, "Share"));
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        name =  findViewById(R.id.name);
        address = findViewById(R.id.address);
        imageView = findViewById(R.id.imageView);
        rating = findViewById(R.id.rating);
       // if (getIntent().getSerializableExtra("Results")!=null) {

            Intent intent = getIntent();
           // Results result= (Results) getIntent().getSerializableExtra("Results");
            Picasso.with(this).load(intent.getStringExtra("image")).into(imageView);
            name.setText(intent.getStringExtra("name"));
            address.setText(intent.getStringExtra("address"));
            rating.setText(intent.getStringExtra("rating"));
//            Picasso.with(this).load(result.getIcon()).into(imageView);
//            name.setText(result.getName());
//            address.setText(result.getAddress());
//            rating.setText(result.getRating());
     //   }
    }
}
