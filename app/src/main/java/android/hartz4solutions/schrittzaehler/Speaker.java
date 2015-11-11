package android.hartz4solutions.schrittzaehler;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by christian on 11.11.15.
 */
public class Speaker implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;

    public Speaker(Context context) {
        tts = new TextToSpeech(context,this);
    }
    public void speak(String text){
        tts.speak(text,TextToSpeech.QUEUE_FLUSH, Bundle.EMPTY,null);
    }

    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.GERMAN);
    }
}
