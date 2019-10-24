package com.logika.bookingvoiceapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    /**
     * se inicia decalrando los elementos con los que vamos a trabajar.
     * En lo personal me confunde hacerlo al vuelo como se van requiriendo las cosas
     * me suena mas ir con el plan.... en ese orden de ideas..
     * <p>
     * Lo que se pretende es construir una funcionalidad que permita la interaccion del dispositivo
     * utilizando la voz.. de entrada 2 objetos: SpeechRecognizer y TextToSpeech (para las respuestas
     * aunque eso no es que sea obligado conque interactue es suficiente) ,,, la primera linea es
     * la solicitud de permisos
     */
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private TextToSpeech objTTS;
    private SpeechRecognizer objSR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/**
 * La funcion de voz se activa con el boton flotante
 */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * primero se chequea el permiso del microfono
                 */
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    /**
                     * si el permiso aun no esta otorgado, se indica en un toast porque se requiere
                     * esta parte es opcional
                     */

                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.RECORD_AUDIO)) {
                        /**
                         * Mensaje
                         */
                    } else {
                        /**
                         * si el permiso aun no esta otorgado se emite una alerta que lo solicita
                         */
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                    }
                } else {
                    /**
                     * Con el permiso otorgado, se crea un un intent que inicie el reconocimiento
                     */
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    /**
                     * mediante estos extras el modelo de lenguaje a reconocer y la cantidad de resultados que se tomaran
                     * en cuenta al momento de analizar palabras en una solicitud por voz
                     */
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    /**
                     * se inicia la funcion en el objeto SpeechRecognition y se le pasa el intent con la info
                     */
                    objSR.startListening(intent);
                }
            }
        });
        /**
         * se inicializan los metodos de escucha y sintesis de voz
         */
        iniciarTTS();
        iniciarSR();
    }

    /**
     * se inicia la sintesis de voz o texto a voz, se crea un listener para la funcion,
     */
    private void iniciarTTS() {

        objTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                /**
                 * se chequea si el dispositivo tiene la capacidad de sintesis
                 */
                if (objTTS.getEngines().size() == 0) {
                    Toast.makeText(MainActivity.this, getString(R.string.objTTS_sin_motor), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    /**
                     * si se posee la capacidad, se establece el modelo de lenguaje,
                     */
                        Locale locale = new Locale("es", "MX");
                        objTTS.setLanguage(locale);
                        speak("Hola bienvenido al servicio de comandos por voz");

                    /**
                     * el metodo speak (no es buena idea cambiar ese nombre) ejecuta la sintesis del texto
                     */

                }
            }
        });
    }

    /**
     * Este metodo ejecuta la sintesis de un mensaje tipo string, los parametros
     * a recibir dependen de la version del sdk
     */
    private void speak(String message) {
        if (Build.VERSION.SDK_INT >= 21) {
            objTTS.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            objTTS.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void iniciarSR() {
        /**
         * Se checkea que el dispositivo cuente con la capacidad de usar
         * la funcionalidad si es asi se inicia el objSR y se crea un listener
         * para ese SR
         */
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            objSR = SpeechRecognizer.createSpeechRecognizer(this);
            objSR.setRecognitionListener(new RecognitionListener() {
                /**
                 * estos metodos otorgan mas caracteristicas y complejidad al SR
                 * para este ejemplo se utiliza el onResults ya que es el caso
                 * donde ya se tiene una info reconocida por el SR
                 */
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                /**
                 * con el results como parametro, se crea una lista y se guardan esos resultados
                 * que colocaran 1 palabra o esultado en cada posicion
                 */
                @Override
                public void onResults(Bundle results) {
                    List<String> result_arr = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    /**
                     * se crea un metodo que analice los resultados al cotejarlos
                     * con parametros predeterminados
                     */
                    processResult(result_arr.get(0));
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }

    private void processResult(String result_message) {
        result_message = result_message.toLowerCase();
/**
 * se busca coincidencias con palabras predeterminadas separadas para
 * generar resultados predeterminados
 */
        if (result_message.indexOf("nombre") != -1) {
            speak("Yo soy booking voice app version 0.9 beta");
        } else if (result_message.indexOf("hora") != -1) {
            String time_now = DateUtils.formatDateTime(this, new Date().getTime(), DateUtils.FORMAT_SHOW_TIME);
            speak("La hora actual es: " + time_now);
        } else if (result_message.indexOf("hotel") != -1) {
            speak("Actualmente se encuentra en uno de nuestros fantásticos hoteles");
        } else if ((result_message.indexOf("hotel") != -1) && (result_message.indexOf("cerca") != -1) ) {
            speak("Accediendo a la red mundial de computadoras.");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=hoteles+cerca&rlz=1C1CHBF_esCO866CO866&oq=hoteles+cerca&aqs=chrome.0.0l6.2415j0j7&sourceid=chrome&ie=UTF-8"));
            startActivity(intent);
        } else if ((result_message.indexOf("restaurante") != -1) && (result_message.indexOf("cerca") != -1) ) {
            speak("Accediendo a la red mundial de computadoras.");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?rlz=1C1CHBF_esCO866CO866&sxsrf=ACYBGNRAs7OLfPHEjKhsFKYvZxiwMHUiMg%3A1571892801549&ei=QS6xXYWeIezU5gKR5J-YBg&q=restaurantes+cerca&oq=restacerca&gs_l=psy-ab.3.0.0i7i30l10.87919.89552..90776...0.1..0.157.719.0j5......0....1..gws-wiz.......0i71.8MkJtGSml7w"));
            startActivity(intent);
        } else if (result_message.indexOf("internet") != -1) {
            speak("Accediendo a la red mundial de computadoras.");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"));
            startActivity(intent);
        } else if ((result_message.indexOf("tipo") != -1) && (result_message.indexOf("lugar") != -1)){
            speak("Este es el listado de tipos de lugares en nuestra base de datos.");
            OpenTipoLugar();
        }
    }

    private void OpenTipoLugar() {
     Intent intent = new Intent(this, TipoLugarActivity.class);

     startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * este desbordamiento termina la actividad al ejecutar el onpause
     */
    @Override
    protected void onPause() {
        super.onPause();
        objTTS.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * se reinicia los servicios sr y tts en eventos como cuando se abre un navegador
         *
         */
        iniciarSR();
        iniciarTTS();
    }
}
