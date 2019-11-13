package com.example.catalin.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import static com.example.catalin.tictactoe.Game.P1Turn;
import static com.example.catalin.tictactoe.Game.P2Turn;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Log.i("dada", "caca");
        Game game = new Game(MainActivity.this, P1Turn);


    }
}
