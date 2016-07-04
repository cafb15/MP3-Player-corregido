package com.example.android.mp3player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {
    static MediaPlayer mp;
    ArrayList<File> mySongs;
    int position;
    SeekBar sb;
    Thread updateSeekBar;
    Uri u;
    Button avanza_button, play_button, retrocede_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //Hay una declaraciión del tipo de variable en la clase PlayerActivity y acá se está convirtiendo los botones a Button.
        play_button = (Button) findViewById(R.id.play_button);
        avanza_button = (Button) findViewById(R.id.avanza_button);
        retrocede_button = (Button) findViewById(R.id.retrocede_button);

        play_button.setOnClickListener(this);
        avanza_button.setOnClickListener(this);
        retrocede_button.setOnClickListener(this);

        sb = (SeekBar) findViewById(R.id.seekBar);
        updateSeekBar = new Thread(){
            @Override
            public void run(){
                int totalDuration = mp.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration){
                    try {
                        sleep(500);
                        currentPosition = mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        if(mp != null){
            mp.stop();
            mp.release();
        }

        Intent i = getIntent();
        Bundle b = i.getExtras();
        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        position = b.getInt("pos",0);

        u = Uri.parse(mySongs.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(),u);
        mp.start();
        sb.setMax(mp.getDuration());
        updateSeekBar.start();

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){

            //Se le dá funcionalidad al botón play. Cuando la canción se escucha estará asi "||" y cuando no se escuche estará asi ">".
            case R.id.play_button:
                if(mp.isPlaying()){
                    play_button.setText(">");
                    mp.pause();
                }else {
                    play_button.setText("||");
                    mp.start();
                }
                break;

            //Se le dá funcionalidad al botón para que reproduzca la siguiente canción.
            case R.id.avanza_button:
                mp.stop();
                mp.release();
                position = (position + 1) % mySongs.size();
                u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(),u);
                mp.start();
                sb.setMax(mp.getDuration());
                break;

            //Se le dá funcionalidad al botón para que reproduzca la anterior canción.
            case R.id.retrocede_button:
                mp.stop();
                mp.release();
                position = (position - 1 < 0)? mySongs.size() - 1: position - 1;
                u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(),u);
                mp.start();
                sb.setMax(mp.getDuration());
                break;
        }
    }
}