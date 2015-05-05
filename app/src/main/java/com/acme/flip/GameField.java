package com.acme.flip;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

/** --------------------------------------------------
 *@author Josan Garrido
 *@version 1.0
 *Fecha 07/10/2014
 * 
 ---------------------------------------------------*/
public class GameField extends Activity {

    private  static final int []colors = new int[]{
            R.drawable.ic_1c,
            R.drawable.ic_2c,
            R.drawable.ic_3c,
            R.drawable.ic_4c,
            R.drawable.ic_5c,
            R.drawable.ic_6c
    };

    private static final int []numbers = new int[]{
            R.drawable.ic_1n,
            R.drawable.ic_2n,
            R.drawable.ic_3n,
            R.drawable.ic_4n,
            R.drawable.ic_5n,
            R.drawable.ic_6n

    };

    //mantener el array que el usuario haya decidido utilizar
    private int []pictures = null;
    //numero maximo de celdas horizontales y verticales
    private int topTileX = 3;
    private int topTileY = 3;
    //numero maximo de elementos a utilizar
    private int topElements = 2;
    //si se ha seleccionado o no usar sonido y vibracion
    private boolean hasSound = false;
    private boolean hasVibration = false;
    //Array con los identificadores de las celdas cuando se añadan al layout, para poder
    //recuperarlos durante la partida
    private int ids[][] = null;
    //Array para guardar los valores de los indeices de cada una de las celdas.
    //se utilizá para agilizar la comprobacion de si la partida ha acabado o no.
    private int values[][] = null;
    //Contador con el numero de pulsaciones que ha realizado el jugador
    private int numberOfClicks = 0;
    //Para reproducir un sonido cuando el usuario pulse una celda
    private MediaPlayer mp = null;
    //Para hacer vibrar el dispositivo cuando el usuario pulse una celda
    private Vibrator vibratorService = null;
    //Mostrara en pantalla las veces que el usuario ha pulsado una celda
    private TextView tvNumberOfClicks = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamefield);
        vibratorService = (Vibrator)(getSystemService(Service.VIBRATOR_SERVICE));
        mp = MediaPlayer.create(this, R.raw.touch);
        tvNumberOfClicks = (TextView) findViewById(R.id.clicksTxt);

        Bundle extras = getIntent().getExtras();
        topTileX = extras.getInt("xtiles") + 3;
        topTileY = extras.getInt("ytiles") + 3;

        topElements = extras.getInt("numcolors") + 2;
        //usar colores o numeros
        if("C".equals(extras.getString("tile"))){
            pictures = colors;
        }
        else {
            pictures = numbers;
        }
        hasSound = extras.getBoolean("hasSound");
        hasVibration = extras.getBoolean("hasVibration");

        //para evitar que entre partidas se mantengar las celdas anteriores, limpiamos el LinearLayout
        LinearLayout ll = (LinearLayout) findViewById(R.id.fieldLandscape);
        ll.removeAllViews();

        //Calculamos el tamaño de la pantalla con getMetrics()
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels / topTileX;
        int height = (dm.heightPixels - 180) / topTileY;

        //inicializamos los arrays para guardar los identificadores en pantalla de las distintas celdas
        //y los indices de trama de cada una de ellas

        ids = new int[topTileX][topTileY];
        values = new int[topTileX][topTileY];

        //Calcular las tramas aleatoriamente
        Random random = new Random(System.currentTimeMillis());
        int tilePictereToShow = random.nextInt(topElements);

        //añadir los restante linearlayout en el eje y y con tantas entradas como se hayar
        //definido en el eje x
        int ident = 0;
        for (int i = 0; i <topTileY; i++){
            LinearLayout l2 = new LinearLayout(this);
            l2.setOrientation(LinearLayout.HORIZONTAL);
            for (int j =0; j < topTileX ; j++){
                tilePictereToShow = random.nextInt(topElements);
                //guardamos la trama a mostrar
                values[j][i] = tilePictereToShow;
                TileView tv = new TileView(this, j, i, topElements, tilePictereToShow, pictures[tilePictereToShow]);
                ident++;
                //se asigna un identificador al objeto creado
                tv.setId(ident);
                //se guarda el identificador en una matrix
                ids[j][i] = ident;
                tv.setHeight(height);
                tv.setWidth(width);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hasClick(((TileView)view).x,((TileView)view).y);
                    }
                });
                l2.addView(tv);
            }
            ll.addView(l2);
        }
        Chronometer t =(Chronometer) findViewById(R.id.Chronometer);
        t.start();
    }

    public void hasClick(int x, int y){
        if(hasVibration) vibratorService.vibrate(100);
        if(hasSound) mp.start();
        changeView(x,y);
        //esquinas del tablero
        if(x==0 && y== 0){
            changeView(0,1);
            changeView(1,0);
            changeView(1,1);
        }
        else if(x== 0 && y ==topTileY - 1){
            changeView(0, topTileY-2);
            changeView(1, topTileY-2);
            changeView(1, topTileY-1);
        }
        else if (x == topTileX -1 && y == 0){
            changeView(topTileX-2, 0);
            changeView(topTileX-2, 1);
            changeView(topTileX-1, 1);
        }
        else if (x == topTileX - 1 && y == topTileY - 1){
            changeView(topTileX-2, topTileY-1);
            changeView(topTileX-2, topTileY-2);
            changeView(topTileX-1, topTileY-2);
        }
        //lados del tablero
        else if(x==0){
            changeView(x, y-1);
            changeView(x, y+1);
            changeView(x+1, y);
        }
        else if(y==0){
            changeView(x-1, y);
            changeView(x+1, y);
            changeView(x, y+1);
        }
        else if(x==topTileX-1){
            changeView(x, y-1);
            changeView(x, y+1);
            changeView(x-1, y);
        }
        else if(y==topTileY-1){
            changeView(x-1, y);
            changeView(x+1, y);
            changeView(x, y-1);
        }
        //resto
        else{
            changeView(x - 1, y);
            changeView(x+1,y);
            changeView(x,y-1);
            changeView(x,y+1);
        }
        numberOfClicks++;
        tvNumberOfClicks.setText(getString(R.string.score_clicks) + numberOfClicks);
        //se ha acabado la partida
        checkIfFinished();
    }

    /**
     * changeView() debe recuperar la celda da la interfaz grafica y modificar su contenido.
     * La recuperacion se hace mediante los identificadores que se han guardado en la matriz ids[][].
     * Una vez recuperado el objeto TileView, se obtiene su nuevo índice de trama y se guarda en la
     * matriz de indices values [][] que se utilizará para comprabar después si todas las celdas
     * tienen el mismo contenido.
     * @param x
     * @param y
     */

    private void changeView(int x, int y){
        TileView tt = (TileView) findViewById(ids[x][y]);
        int newIndex = tt.getNewIndex();
        values[x][y] = newIndex;
        tt.setBackgroundResource(pictures[newIndex]);
        tt.invalidate();
    }

    private void checkIfFinished(){
        int targetValue = values[0][0];
        for(int i=0; i<topTileY; i++){
            for (int j=0; i<topTileX; j++){
                if(values[j][i] != targetValue)return;
            }
        }
        Intent resultIntent = new Intent((String)null);
        resultIntent.putExtra("clicks",numberOfClicks);
        setResult(RESULT_OK,resultIntent);
        finish();
    }
}
