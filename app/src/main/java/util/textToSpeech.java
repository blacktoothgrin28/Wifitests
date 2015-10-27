package util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by halatm on 27/10/2015.
 */
public class textToSpeech extends Activity implements TextToSpeech.OnInitListener {
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    private Context context;
//http://code.tutsplus.com/tutorials/android-sdk-using-the-text-to-speech-engine--mobile-8540


    //speak the user text
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void speakWords(String speech) {

        //speak straight away
//        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null); //deprecated
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null, "1");
    }

    public void checkTTSdata() {
        //check for TTS data
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            myTTS.setLanguage(new Locale("es", "ES"));
        } else if (initStatus == TextToSpeech.ERROR) {
//            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
            myLog.add("Sorry! Text To Speech failed...");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(context, this);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }
}
