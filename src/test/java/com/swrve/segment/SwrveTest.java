package com.swrve.segment;

import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.core.tests.BuildConfig;
import com.segment.analytics.integrations.Logger;
import com.segment.analytics.test.IdentifyPayloadBuilder;
import com.segment.analytics.test.TrackPayloadBuilder;
import com.swrve.sdk.SwrveSDK;
import com.swrve.sdk.SwrveSDKBase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static com.segment.analytics.Analytics.LogLevel.VERBOSE;
import static com.segment.analytics.Utils.createTraits;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 18, manifest = Config.NONE)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*", "androidx.*" })
@PrepareForTest({ SwrveSDK.class })

public class SwrveTest {

  @Rule public PowerMockRule rule = new PowerMockRule();
  @Mock Analytics analytics;

  private SwrveIntegration integration;

  @Before
  public void setUp() {
    initMocks(this);
    PowerMockito.mockStatic(SwrveSDKBase.class);

    integration = new SwrveIntegration(null, Logger.with(VERBOSE));
  }

  @Test
  public void identify() {
    integration.identify(new IdentifyPayloadBuilder().traits(createTraits("foo")).build());

    verifyStatic();
    Map<String, String> attributes = new HashMap<>();
    attributes.put("customer.id", "foo");
    SwrveSDK.userUpdate(attributes);
  }

  @Test
  public void track() {
    Properties properties = new Properties();
    integration.track(new TrackPayloadBuilder().event("foo").properties(properties).build());

    verifyStatic();
    SwrveSDK.event("foo", properties.toStringMap());
  }

  @Test
  public void payloads() {
    Properties properties = new Properties().putValue("null-value", null).putValue("included-value", "non-null");
    integration.track(new TrackPayloadBuilder().event("foo").properties(properties).build());

    verifyStatic();
    SwrveSDK.event("foo", new Properties().putValue("included-value", "non-null").toStringMap());
  }
}
