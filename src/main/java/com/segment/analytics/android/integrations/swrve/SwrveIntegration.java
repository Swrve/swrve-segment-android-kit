package com.segment.analytics.android.integrations.swrve;

import android.app.Activity;
import android.app.Application;
import com.swrve.sdk.config.SwrveConfig;
import com.swrve.sdk.SwrveSDK;
import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.Logger;
import com.segment.analytics.integrations.ScreenPayload;
import com.segment.analytics.integrations.TrackPayload;
import java.util.HashMap;
import java.util.Map;

import static com.segment.analytics.internal.Utils.isNullOrEmpty;

public class SwrveIntegration extends Integration<Void> {
  private static final String SWRVE_KEY = "Swrve";

  // Swrve needs to be initialized early in order to work correctly.
  public static Factory createFactory(final Application application, int appId, String apiKey, SwrveConfig swrveConfig) {
    SwrveSDK.createInstance(application, appId, apiKey, swrveConfig);

    return new Factory() {
      @Override
      public Integration<?> create(ValueMap settings, Analytics analytics) {
        return new SwrveIntegration(analytics, settings);
      }

      @Override
      public String key() {
        return SWRVE_KEY;
      }
    };
  }

  final Logger logger;

  SwrveIntegration(Analytics analytics, ValueMap settings) {
    logger = analytics.logger(SWRVE_KEY);
  }

  @Override
  public void identify(IdentifyPayload identify) {
    super.identify(identify);

    String userId = identify.userId();
    if (!isNullOrEmpty(userId)) {
      Map<String, String> attributes = new HashMap<String, String>();
      attributes.put("customer.id", userId);
      SwrveSDK.userUpdate(attributes);
    }

    Traits traits = identify.traits();
    Map<String, String> properties = new HashMap<String, String>();
    for (Map.Entry<String, Object> entry : traits.entrySet()) {
      properties.put(entry.getKey(), String.valueOf(entry.getValue()));
    }
    SwrveSDK.userUpdate(properties);
  }

  @Override
  public void track(TrackPayload track) {
    super.track(track);
    SwrveSDK.event(track.event(), track.properties().toStringMap());
  }

  @Override
  public void screen(ScreenPayload screen) {
    String eventName = String.format("screen.%s", screen.event());
    SwrveSDK.event(eventName, screen.properties().toStringMap());
  }

  // alias, reset, group not implemented

  @Override
  public void onActivityResumed(Activity activity) {
    super.onActivityResumed(activity);
    SwrveSDK.onResume(activity);
  }

  @Override
  public void onActivityPaused(Activity activity) {
    super.onActivityPaused(activity);
    SwrveSDK.onPause();
  }

  @Override
  public void flush() {
    super.flush();
    SwrveSDK.sendQueuedEvents();
  }
}
