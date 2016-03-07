package nl.fhict.delta.livesubs;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class Listener implements RecognitionListener
{
    /**
     * Main activity object
     */
    private MainActivity mainActivity;

    /**
     * Speechrecognizer object
     */
    private SpeechRecognizer sr;

    /**
     * Speechrecognition settings
     */
    private Intent intent;

    /**
     * TAG for debugging
     */
    private final String TAG = "LiveSubs";

    /**
     * Constructor for the listener object
     * @param mainActivity
     */
    public Listener(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    /**
     * Method that constructs the SpeechRecognizer and
     * applies the settings
     */
    public void startListening()
    {
        // Create SpeechRecognizer
        sr = SpeechRecognizer.createSpeechRecognizer(mainActivity);
        sr.setRecognitionListener(this);

        // Set settings
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        // Start listening
        sr.startListening(intent);
    }

    /**
     * Callback that gets called when ready for speech recognition
     * @param params
     */
    public void onReadyForSpeech(Bundle params)
    {
        Log.d(TAG, "onReadyForSpeech");

        mainActivity.updateText("Listening");
    }

    /**
     * Callback that gets called when speech had been detected
     */
    public void onBeginningOfSpeech()
    {
        Log.d(TAG, "onBeginningOfSpeech");
    }

    /**
     * Callback that gets called when the dB level of the audio has changed
     * @param rmsdB
     */
    public void onRmsChanged(float rmsdB)
    {
    }

    /**
     * Callback that gets called when a buffer is received
     * @param buffer
     */
    public void onBufferReceived(byte[] buffer)
    {
    }

    /**
     * Callback that gets called when the end of the speech has been detected
     */
    public void onEndOfSpeech()
    {
    }

    /**
     * Callback that gets called when an error occures
     * @param error
     */
    public void onError(int error)
    {
        Log.d(TAG,  "error " +  error);

        // Check which error, so we can output error info
        switch (error)
        {
            case 3:
                mainActivity.updateText("Error: could not record audio"); break;
            case 2: case 1: case 4:
                mainActivity.updateText("Network error"); break;
        }

        // Start listening again
        sr.startListening(intent);
    }

    /**
     * Callback that gets called when results have been found
     * @param results
     */
    public void onResults(Bundle results)
    {
        String str = new String();
        Log.d(TAG, "onResults " + results);
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        // Start listening again
        sr.startListening(intent);
    }

    /**
     * Callback that gets called when a partial result has been found
     * @param partialResults
     */
    public void onPartialResults(Bundle partialResults)
    {
        ArrayList results = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String resultSet = "";

        // Check if there were results
        if (results != null) {
            for (int i = 0; i < results.size(); i++) {
                resultSet += results.get(i);
            }
        }

        Log.d("enne", "results gevonden" + results);

        // Update the view
        mainActivity.updateSubtitle(resultSet);
    }
    public void onEvent(int eventType, Bundle params)
    {
        Log.d(TAG, "onEvent " + eventType);
    }
}
