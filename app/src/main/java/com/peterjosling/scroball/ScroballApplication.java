package com.peterjosling.scroball;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class ScroballApplication extends Application {

  private static EventBus eventBus = new EventBus();
  private static NowPlayingChangeEvent lastEvent = ImmutableNowPlayingChangeEvent.builder().source("").track(Track.empty()).build();

  private LastfmClient lastfmClient;
  private ScroballDB scroballDB;
  private SharedPreferences sharedPreferences;

  @Override
  public void onCreate() {
    super.onCreate();

    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    String userAgent = getString(R.string.user_agent);
    String sessionKeyKey = getString(R.string.saved_session_key);

    if (sharedPreferences.contains(sessionKeyKey)) {
      String sessionKey = sharedPreferences.getString(sessionKeyKey, null);
      lastfmClient = new LastfmClient(userAgent, sessionKey);
    } else {
      lastfmClient = new LastfmClient(userAgent);
    }

    scroballDB = new ScroballDB(new ScroballDBHelper(this));
    scroballDB.open();

    eventBus.register(this);
  }

  public LastfmClient getLastfmClient() {
    return lastfmClient;
  }

  public ScroballDB getScroballDB() {
    return scroballDB;
  }

  public SharedPreferences getSharedPreferences() {
    return sharedPreferences;
  }

  @Subscribe
  public void onNowPlayingChange(NowPlayingChangeEvent event) {
    lastEvent = event;
  }

  public static EventBus getEventBus() {
    return eventBus;
  }

  public static NowPlayingChangeEvent getLastNowPlayingChangeEvent() {
    return lastEvent;
  }
}
