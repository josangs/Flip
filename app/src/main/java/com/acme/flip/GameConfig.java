package com.acme.flip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;


/** --------------------------------------------------
 *@author Josan Garrido
 *@version 1.0
 *Fecha 07/10/2014
 * 
 ---------------------------------------------------*/


public class GameConfig extends Activity {

    private static final int ACTION_PLAY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_config);

        //boton de inicio de partida
        Button btn = (Button) findViewById(R.id.startBtn);

        //control numero de celdas horizontales
        SeekBar xTiles = (SeekBar) findViewById(R.id.seekBarX);
        xTiles.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateXTiles(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //actualiza la etiqueta no solo cuando varia el valor de la barra sino tambien
        //cuando se crea. Y con getProgress obtenemos el valor actual de la barra.
        updateXTiles(xTiles.getProgress());

        //barra para las celdas verticales
        SeekBar yTiles = (SeekBar) findViewById(R.id.seekBarY);
        yTiles.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateYTiles(seekBar.getProgress());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateYTiles(yTiles.getProgress());

        //barra para la trama
        SeekBar colors= (SeekBar) findViewById(R.id.seekBarColors);
        colors.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateColors(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateColors(colors.getProgress());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlay();
            }
        });
    }

    private void updateXTiles(int i){
        TextView tv = (TextView) findViewById(R.id.seekBarXtxt);
        tv.setText(getString(R.string.num_elem_x) + (i + 3));
    }

    private void updateYTiles(int i){
        TextView tv = (TextView) findViewById(R.id.seekBarYtxt);
        tv.setText(getString(R.string.num_elem_y) + (i + 3));
    }

    private void updateColors(int i){
        TextView tv = (TextView) findViewById(R.id.seekBarColorstxt);
        tv.setText(getString(R.string.num_colors) + (i + 2));
    }

    private void startPlay(){
        Intent in = new Intent(this,GameField.class);
        //Configurar la partida
        SeekBar sb = (SeekBar) findViewById(R.id.seekBarX);
        in.putExtra("xtiles", sb.getProgress());
        sb = (SeekBar) findViewById(R.id.seekBarY);
        in.putExtra("ytiles",sb.getProgress());

        sb = (SeekBar) findViewById(R.id.seekBarColors);
        in.putExtra("numcolors", sb.getProgress());

        RadioButton r = (RadioButton) findViewById(R.id.radioButtonC);
        in.putExtra("tile", r.isChecked() ? "C" : "N");
        //control de sonido
        CheckBox chSound = (CheckBox) findViewById(R.id.checkBoxSound);
        in.putExtra("hasSound",chSound.isChecked());
        //control de vibracion
        CheckBox chVib = (CheckBox)findViewById(R.id.checkBoxVibrate);
        in.putExtra("hasVibration", chVib.isChecked());
        //comenzar activity
        //Para lanzar una actividad de la cual intereza su resultado, el comando lo definimos
        //nosotros con un entero, el comando el ACTION_PLAY.
        startActivityForResult(in, ACTION_PLAY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case ACTION_PLAY:
                    new AlertDialog.Builder(this).setMessage(getResources().getString(R.string.game_end_1)
                    + data.getIntExtra("clicks",0) + getResources().getString(R.string.game_end_2)).setPositiveButton(android.R.string.ok,null).show();
                    break;
            }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void showAbout(){
        Intent j = new Intent(this,About.class);
        startActivity(j);
    }

    private void showHowTo(){
        Intent z = new Intent(this,HowTo.class);
        startActivity(z);
    }

    private void showPlayer(){

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.m_player: showPlayer();
                return true;
            case R.id.m_howto: showHowTo();
                return true;
            case R.id.m_about: showAbout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
