package com.swrve.segment;

import android.app.Application;

import com.swrve.sdk.SwrveLogger;
import com.swrve.sdk.config.SwrveConfig;
import com.swrve.sdk.SwrveHelper;
import com.swrve.sdk.SwrveSDK;
import com.swrve.sdk.SwrveIdentityResponse;
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

public class SwrveIntegration extends Integration<Void> {
  private static final String SWRVE_KEY = "Swrve";

  // Swrve needs to be initialized early in order to work correctly.
  public static Factory createFactory(
      final Application application, int appId, String apiKey, SwrveConfig swrveConfig) {
    SwrveSDK.createInstance(application, appId, apiKey, swrveConfig);

    return new Factory() {
      @Override
      public Integration<?> create(ValueMap settings, Analytics analytics) {
        Logger logger = analytics.logger(SWRVE_KEY);
        return new SwrveIntegration(settings, logger);
      }

      @Override
      public String key() {
        return SWRVE_KEY;
      }
    };
  }

  private final Logger logger;

  SwrveIntegration(ValueMap settings, Logger logger) {
    this.logger = logger;
  }

  @Override
  public void identify(IdentifyPayload identify) {
    try {
      super.identify(identify);
      Map<String, String> payload = new HashMap<>();
      if (identify.traits().containsKey("swrve_external_id")) {
        final String external_id = identify.traits().get("swrve_external_id").toString();
        SwrveSDK.identify(external_id, new SwrveIdentityResponse() {
          @Override
          public void onSuccess(String status, String swrveId) {
            // Success, continue with your logic
            logger.verbose("Successfully identified swrve_user_id %s with external_id %s", swrveId, external_id);
          }

          @Override
          public void onError(int responseCode, String errorMessage) {
            // Error should be handled.
            logger.verbose("Swrve identification failed with error code %d: %s", responseCode, errorMessage);
          }
        });
      }

      for (String key : identify.traits().keySet() ) {
        if (!key.equals("swrve_external_id")) {
          payload.put(key, identify.traits().get(key).toString());
        }
      }

      String userId = identify.userId();
      if (SwrveHelper.isNotNullOrEmpty(userId)) {
        Map<String, String> attributes = new HashMap<>();

        attributes.put("customer.id", userId);
        SwrveSDK.userUpdate(attributes);
        logger.verbose("SwrveSDK.userUpdate(%s)", attributes);
      }

      SwrveSDK.userUpdate(payload);
      logger.verbose("SwrveSDK.userUpdate(%s);", payload.toString());
    } catch (Exception e) {
      SwrveLogger.e("Exception in identify api.", e);
    }
  }

  private Map<String,String> flatten(Map<String,Object> properties) {
    Map<String, String> payload = new HashMap<>();
    for (String key: properties.keySet()) {
      Object value = properties.get(key);
      if(value != null){ // drop null-valued properties
        Map<String, Object> valueMap = (value instanceof Map) ? (Map) value : null;
        if (valueMap != null) {
          Map<String, String> flat_map = flatten(valueMap);
          for (String newKey: flat_map.keySet()) {
            if (flat_map.get(newKey) != null) {
              payload.put(newKey, flat_map.get(newKey).toString());
            }
          }
        } else {
          payload.put(key, value.toString());
        }
      }
    }
    return payload;
  }

  @Override
  public void track(TrackPayload track) {
    try {
      super.track(track);
      Map<String, String> payload = flatten(track.properties());
      SwrveSDK.event(track.event(), payload);
      logger.verbose("SwrveSDK.event(%s, %s)", track.event(), payload);
    } catch (Exception e) {
      SwrveLogger.e("Exception in track api.", e);
    }
  }

  @Override
  public void screen(ScreenPayload screen) {
    try {
      String eventName = String.format("screen.%s", screen.event());
      SwrveSDK.event(eventName, screen.properties().toStringMap());
      logger.verbose("SwrveSDK.event(%s, %s)", eventName, screen.properties().toStringMap());
    } catch (Exception e) {
      SwrveLogger.e("Exception in screen api.", e);
    }
  }

  @Override
  public void flush() {
    try {
      super.flush();
      SwrveSDK.sendQueuedEvents();
      logger.verbose("SwrveSDK.sendQueuedEvents();");
    } catch (Exception e) {
      SwrveLogger.e("Exception in flush api.", e);
    }
  }
}
