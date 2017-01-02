package assis.anderson.gpsteste;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import assis.anderson.gpsteste.ServicoGps.GpsService;
import assis.anderson.gpsteste.Toast.ToastManager;

public class MainActivity extends AppCompatActivity {
    private Button btnInicioGps;
    private Button btnFimGps;
    private TextView texto,txtVelocidade;
    private BroadcastReceiver broadcastReceiver;
    String id; //variavel para receber o valor da outra activity
    String vel; //variavel para receber o valor da outra activity


    @Override
    protected void onResume() {//serve para mostrar o texto da outra classe
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    texto         = (TextView)findViewById(R.id.txtDistancia);
                    txtVelocidade = (TextView)findViewById(R.id.txtVelocidade);
                    ToastManager.show(getApplicationContext(), "Calculando metros: "  +intent.getExtras().get("distancia"), ToastManager.INFORMATION);
                    id = String.valueOf(intent.getExtras().get("distancia"));
                    vel = String.valueOf(intent.getExtras().get("velo"));

                    texto.setText("Distancia em KM:" + id);
                    txtVelocidade.setText("Velocidade: " + vel);
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnInicioGps  = (Button)findViewById(R.id.btnInicio);
        btnFimGps     = (Button)findViewById(R.id.btnFim);


        //botão iniciar GPS
        btnInicioGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),GpsService.class);
                startService(i);
            }
        });


        //botão parar GPS
        btnFimGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),GpsService.class);
                stopService(i);
            }
        });

    }//fim Oncreate




}//fim da classe MainActivity
