package nl.fhict.delta.livesubs;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.content.Intent;
import android.util.Log;
import java.util.ArrayList;

/**
 * MainActivity class which handles basically everything
 */
public class MainActivity extends Activity
{
    /**
     * {@link CardScrollView} to use as the main content view.
     */
    private CardScrollView mCardScroller;
    CardBuilder card;
    CardScrollAdapter adapter;

    /**
     * The view
     */
    private View mView;

    /**
     * The listener instance
     */
    private Listener listener;

    /**
     * Boolean indicating whether we are currently listening or not
     */
    private boolean isListening = false;

    /**
     * String that holds the subtitle
     */
    private String sub = "";

    /**
     * Creates the view
     * @param bundle
     */
    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);

        mView = buildView();

        adapter = new CardScrollAdapter()
        {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return mView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mView;
            }

            @Override
            public int getPosition(Object item) {
                if (mView.equals(item)) {
                    return 0;
                }
                return AdapterView.INVALID_POSITION;
            }
        };

        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(adapter);
        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Check if we are already listening
                if (isListening)
                {
                    isListening = false;

                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.playSoundEffect(Sounds.DISMISSED);

                    card.setText("Tap to start listening");
                    card.setFootnote("");
                    setContentView(card.getView());
                }
                else
                {
                    isListening = true;

                    // Plays sound to indicate we start listening
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.playSoundEffect(Sounds.SUCCESS);

                    // Start listening
                    startListening();

                    // Update the subtitle text
                    updateSubtitle("Listening..");
                }
            }
        });
        setContentView(mCardScroller);
    }

    /**
     * OnResume
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        mCardScroller.activate();
    }

    /**
     * OnPause
     */
    @Override
    protected void onPause()
    {
        mCardScroller.deactivate();
        super.onPause();
    }

    /**
     * Builds the view
     */
    private View buildView()
    {
        // Check if card exists
        if (card == null)
            card = new CardBuilder(this, CardBuilder.Layout.TEXT);

        card.setText("Tap to start listening...");

        return card.getView();
    }

    /**
     * Creates the Listener and starts listening
     */
    private void startListening()
    {
        listener = new Listener(this);
        listener.startListening();
    }

    /**
     * Method that updates the subtitle
     * @param results string The result
     */
    public void updateSubtitle(String results)
    {
        // Check if there was a result
        if (results != null)
        {
            // Update card and refresh
            card.setFootnote(results);
            setContentView(card.getView());
            adapter.notifyDataSetChanged();
        }
    }


    /**
     * Method that updates the main text of the app
     * @param text string The new text
     */
    public void updateText(String text)
    {
        // Update text and refresh
        card.setText(text);
        setContentView(card.getView());
        adapter.notifyDataSetChanged();
    }
}
