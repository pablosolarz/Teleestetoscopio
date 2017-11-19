package com.example.pablo.clientrqtelestetoscopio;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    TextView txNroTel,txNroReceptores;
    Switch swConectar;
    private String salida,retorno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txNroTel =   (TextView) findViewById(R.id.txNroTel);
        txNroReceptores =   (TextView) findViewById(R.id.textNroReceptores);
        swConectar = (Switch) findViewById(R.id.switchConectar);
        final ConectaTCP conectaTCP = new ConectaTCP();

/*
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
// Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
// Show an expanation to the user *asynchronously* -- don't block
// this thread waiting for the user's response! After the user
// sees the explanation, try again to request the permission.
            } else {
// No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
// app-defined int constant. The callback method gets the
// result of the request.
            }
        }else{

            TelephonyManager tMgr;
            tMgr= (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String idDispositivo = tMgr.getDeviceId();

      //      if (!idDispositivo.isEmpty())
      //         txNroTel.setText(idDispositivo);
      //      else
      //          txNroTel.setText("ID Dispoitivo NO Encontrado");
            //      TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            //   IMEI = mngr.getDeviceId();
            //device_unique_id = Settings.Secure.getString(this.getContentResolver(),
            //         Settings.Secure.ANDROID_ID);
            //textView.setText(device_unique_id+"----"+mngr.getDeviceId());
            //         //  READ_PHONE_STATE permission is already been granted.
            //Toast.makeText(this,"Alredy granted",Toast.LENGTH_SHORT).show();
        }
  */
        idTelefono();
        iniNroReceptores();
        iniEstadoConexion();
        swConectar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", "" + isChecked);
//Modificaciones según estado de conexión
                conectaTCP.execute(txNroTel.getText().toString(),retorno,salida);
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
// If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
// permission was granted, yay! Do the
// contacts-related task you need to do.
                } else {
// permission denied, boo! Disable the
// functionality that depends on this permission.
                }
                return;
            }
// other 'case' lines to check for other
// permissions this app might request
        }
    }
    private void idTelefono() {
        TelephonyManager tMgr;
        tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String idDispositivo = tMgr.getDeviceId();
       // if (idDispositivo != null) {
            if (!idDispositivo.isEmpty())
                txNroTel.setText(idDispositivo);
            else
                txNroTel.setText("ID Dispoitivo NO Encontrado");
       // } else
        //    idDispositivo = "Nulo";
        //    txNroTel.setText("Nulo");
    }
    private void iniNroReceptores(){
        txNroReceptores.setText("0");
    }
    private void iniEstadoConexion(){
        if(swConectar.isChecked())
            swConectar.toggle();
    }
    public class ConectaTCP extends AsyncTask<String, String, String> {
        private StringReader strr;
        private String frase, fraseModificada;
        private static final int SERVERPORT = 6789;
        private static final String SERVER_IP = "10.0.0.11";
        // private String str;
        public Socket conexion() {
            Socket socketCliente = null;
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socketCliente = new Socket(SERVER_IP, SERVERPORT);  //Aqui es detectada la conexión
            }catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }catch (Exception e){
                System.err.println("Error:  " + e);
            }
            return socketCliente;
        }
        @Override
        protected String doInBackground(String... str) {
            strr = new StringReader(str[0]);
//String salida = new String();
            StringBuilder builder = new StringBuilder();
            Socket socketCliente;
//DataOutputStream salidaAServidor;
            PrintStream salidaAServidor;
            String salida="";
            String inputLine, responseLine, respuestaServidor, ultimaRespuesta="";
            try {
                BufferedReader entradaDesdeUsuario = new BufferedReader(strr);
//InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
//socketCliente = new Socket(SERVER_IP,SERVERPORT);  //Aqui es detectada la conexión
                socketCliente = this.conexion();
//txtView.setText(entradaDesdeUsuario.readLine());
//salidaAServidor = new DataOutputStream(socketCliente.getOutputStream());
                salidaAServidor = new PrintStream(socketCliente.getOutputStream());
                BufferedReader entradaDesdeServidor = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
                if (socketCliente != null && salidaAServidor != null && entradaDesdeServidor != null) {
                    try {
/*
* Keep on reading from/to the socket till we receive the "Ok" from the
* server, once we received that then we break.
*/
//System.out.println("The client started. Type any text. To quit it type 'Ok'.");
                        inputLine = entradaDesdeUsuario.readLine();
                        responseLine = inputLine;
                        salidaAServidor.println(responseLine); //Aqui envía el texto de entrada al servidor
//responseLine = entradaDesdeServidor.readLine();
///////stream audio + publishProgress(.....)
                        respuestaServidor = entradaDesdeServidor.readLine();
                        if(!respuestaServidor.isEmpty() && !respuestaServidor.equals(ultimaRespuesta))  //Verificar si es el id de otro telefono
                            publishProgress(respuestaServidor);  //Incremento de usuarios activos
/*
* Close the output stream, close the input stream, close the socket.
*/
                        salidaAServidor.close();
                        entradaDesdeServidor.close();
                        entradaDesdeUsuario.close();
                        socketCliente.close();
                    } catch (UnknownHostException e) {
                        System.err.println("Trying to connect to unknown host: " + e);
                    } catch (IOException e) {
                        System.err.println("IOException:  " + e);
                    }catch (Exception e){
                        System.err.println("Error:  " + e);
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.out.println("Unknown host...");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to connect...");
            }catch (Exception e){
                e.printStackTrace();
                e.getMessage();
                System.out.println("Error...");
            }
            salida = "Chau";
            return salida;
        }
        @Override
        protected void onProgressUpdate(String... s){
//     for (long i = 0; i < 1000000; i++) {
            txNroReceptores.setText(String.valueOf(Integer.valueOf(txNroReceptores.getText().toString())+1));
        }
        protected void onPostExecute(String s){
           Toast t =  Toast.makeText(MainActivity.this, "Tarea finalizada!",Toast.LENGTH_LONG);
                   t.show();
 //           iniEstadoConexion();
 //           iniNroReceptores();
        }
    }
}
