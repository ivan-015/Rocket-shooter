package edu.utep.cs.cs4381.rocketshooter;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import edu.utep.cs.cs4381.rocketshooter.view.RSView;

public class RSActivity extends AppCompatActivity {
    private RSView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get display size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        view = new RSView(this, size.x, size.y);
        setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.pause();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        view.pause();
    }
}