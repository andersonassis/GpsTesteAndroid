package assis.anderson.gpsteste.ServicoGps;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by AndersonLuis on 27/12/2016.
 */

public class GpsService extends Service {
    //declaração das variaveis globais
    private LocationListener listener;
    private LocationManager locationManager;
    double latitude   = 0.0;
    double longitude  = 0.0;
    double latAnte    = 0.0;
    double LongAnte   = 0.0;
    Double distanciaFinal = 0.0;
    int velocidade = 0;
    int speed = 0;
    Double distancia = 0.0;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                latitude  = location.getLatitude();//pega a latitude
                longitude = location.getLongitude();//pega a longitude
                speed=(int) ((location.getSpeed()*3600)/1000);//pega a velocidade em movimento
                velocidade = speed;//passando o valor da velocidade para a variavel velocidade

                distance(latAnte,LongAnte, latitude,longitude,"K");    //chamando metodo do calcular distancia onde os parametros
                latAnte  = latitude;//guarda a ultima latitude        // latAnt,longAnte = ultimos valores e latitude,longitude = valores atuais
                LongAnte = longitude;//guarda a ultima longitude

                if (latitude != 0) {
                    Vibrar();//chama a vibração quando atualiza o gps
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
      // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 300, listener);//18000 = 3 minutos 300= 300mts
         locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,100,listener);// A CADA 100 METROS ATUALIZA

    }//fim do oncreate



    private void Vibrar() // metodo para vibrar
    {
        Vibrator rr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long milliseconds = 1000;//'1 seg' é o tempo em milissegundos, é basicamente o tempo de duração da vibração. portanto, quanto maior este numero, mais tempo de vibração você irá ter
        rr.vibrate(milliseconds);
    }

    public  double distance(double lat1, double lon1, double lat2, double lon2, String unit) {//metodo para calcular distancia

        if (lat1 !=0) {//if para verificar se o lat1 esta cheio
            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {// entra aqui se for em KM
                dist = dist * 1.609344;
                distancia = dist;
                distanciaFinal = distanciaFinal + distancia; // somando a distancia
                // formatar numero com duas casas decimais apos virgula
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
                DecimalFormat df = (DecimalFormat) nf;
                df.applyPattern("#0.###");
                String ss = df.format(distanciaFinal);
                distanciaFinal = Double.valueOf(ss);
                distancia = 0.0;

                //passando o valores para outra tela nos textViews
                Intent i = new Intent("location_update");
                i.putExtra("distancia", distanciaFinal); //valor km
                i.putExtra("velo",velocidade);//valor velocidade
                sendBroadcast(i);

            } else if (unit.equals("N")) {//so entra aqui se for em milhas
                dist = dist * 0.8684;
            }
            return (dist);
        }//fim do if para ver se existe lat1
        return 0;
    }

    //metodo usado para calcular em milhas
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    //metodo usado para calcular em KM
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    //  parar a atualização do gps
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(listener);
            Toast.makeText(GpsService.this, "GPS REMOVIDO :", Toast.LENGTH_SHORT).show();
        }
    }

}//fim da classe
