package com.example.redes.leccion_jarvis;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    Button btn_play, btn_guardar;
    EditText texto_nombre;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_guardar = (Button)findViewById(R.id.btn_guardar);
        btn_play = (Button)findViewById(R.id.btn_play);
        texto_nombre = (EditText)findViewById(R.id.nombre);

        btn_guardar.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();


    }
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id) {
            case R.id.btn_guardar:



                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {

                    CargaImagenes nuevaTarea = new CargaImagenes();
                    nuevaTarea.execute();

                } else {
                    Toast.makeText(getApplicationContext(), "Lo siento no tiene Internet", Toast.LENGTH_LONG).show();
                    btn_guardar.setEnabled(false);
                }


                break;
            case R.id.btn_play:
                MediaPlayer mp = MediaPlayer.create(this, R.raw.gato);
                mp.start();
                break;

        }
    }



    private class CargaImagenes extends AsyncTask<String, Void, Bitmap> { //Clase asytask
        //PARAMETROS                    1. TIPOS DE DATOS DE ENTRADA (DO IN BACKGROUND), ENTRADA
        //                              2. TIPO DE DATOS CON EL QUE SE ACTUALIZA LA TAREA (PUBLISH PROGRESS)
        //                              3. RESULTADO QUE DEVUELVE AL FINAL (DESPUES DE DO IN BACKGROUND, LUEGO AL POST EXECUTE), SALIDA

        ProgressDialog pDialog;

        //Ejecutaremos en el hilo principal cosas que queremos poner ANTES del segundo plano, ej iniciar variables, objectos, preparar componenes
        //antes
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Subiendo al servidor");
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();



        }

        //LO QUE HAY QUE HACER EN SEGUNDO PLANO, TAREA LARGA, PUBLISH PROGRESS SE EJECUTA CON EL HILO PRINCIPAL
        //DURANTE
        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub

            Persona user = new Persona(texto_nombre.getText().toString());
            mDatabase.child("persona").child("nombre").setValue(user);

            pDialog.dismiss();
            return null;

            //publishProgress(i*10);
        }

        //Se ejecuta en el hilo de la interfaz de usuario, hilo principal, se ejecuta cuando se hace una llamada, se prolonga todo el tiempo que
        //sea necesario hasta que la tarea en segundo plano acabr
        //SE COMUNICA CON EL HILO PRINCIPAL
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        //YA ACABA TODO, TIPO MENSAJE AL PARECER
        //DESPUES DEL SEGUNDO HILO
        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(),"Cargado en la bd", Toast.LENGTH_LONG).show();


        }

        //SI CORTO LA EJECUCIÒN DE ESE SEGUNDO HILO SE LLAMA A ESTA FUNCION Y EJECUTA LA FUNCION EN CASO DE QUE SE CANCELE EL HILO
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
