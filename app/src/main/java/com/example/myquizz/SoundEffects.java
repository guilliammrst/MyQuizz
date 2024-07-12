package com.example.myquizz;

import android.content.Context;
import android.media.MediaPlayer;

// This class manages the sound effects for the application.
public class SoundEffects {
    private MediaPlayer clickSound;
    private MediaPlayer fakerSound;
    private MediaPlayer toastSound;
    private MediaPlayer errorSound;
    private MediaPlayer congratsSound;
    private MediaPlayer resultsSound;

    // Constructor initializes all the MediaPlayer instances with respective sound resources.
    public SoundEffects(Context context) {
        clickSound = MediaPlayer.create(context, R.raw.click_sound);
        fakerSound = MediaPlayer.create(context, R.raw.faker_sound);
        toastSound = MediaPlayer.create(context, R.raw.toast_sound);
        errorSound = MediaPlayer.create(context, R.raw.error_sound);
        congratsSound = MediaPlayer.create(context, R.raw.congrats_sound);
        resultsSound = MediaPlayer.create(context, R.raw.results_sound);
    }

    // Methods to play sounds effect.
    public void PlayClickSound()
    {
        clickSound.start();
    }
    public void PlayFakerSound()
    {
        fakerSound.start();
    }
    public void PlayToastSound()
    {
        toastSound.start();
    }
    public void PlayErrorSound()
    {
        errorSound.start();
    }
    public void PlayCongratsSound()
    {
        congratsSound.start();
    }
    public void PlayResultsSound()
    {
        resultsSound.start();
    }

    // Release method to free resources held by MediaPlayer instances.
    public void Release() {
        if (clickSound != null) {
            clickSound.release();
            clickSound = null;
        }
        if (fakerSound != null) {
            fakerSound.release();
            fakerSound = null;
        }
        if (toastSound != null) {
            toastSound.release();
            toastSound = null;
        }
        if (errorSound != null) {
            errorSound.release();
            errorSound = null;
        }
        if (congratsSound != null) {
            congratsSound.release();
            congratsSound = null;
        }
        if (resultsSound != null) {
            resultsSound.release();
            resultsSound = null;
        }
    }
}
