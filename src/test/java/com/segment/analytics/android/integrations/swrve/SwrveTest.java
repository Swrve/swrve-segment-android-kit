package com.segment.analytics.android.integrations.swrve;

import com.swrve.sdk.SwrveSDKBase;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.core.tests.BuildConfig;
import com.segment.analytics.test.IdentifyPayloadBuilder;
import com.segment.analytics.test.TrackPayloadBuilder;
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

import static com.segment.analytics.Utils.createTraits;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
@PrepareForTest({ SwrveSDKBase.class })

public class SwrveTest {

  @Rule public PowerMockRule rule = new PowerMockRule();
  @Mock Analytics analytics;

  SwrveIntegration integration;

  @Before public void setUp() {
    initMocks(this);
    PowerMockito.mockStatic(SwrveSDKBase.class);

    integration = new SwrveIntegration(analytics, null);
  }

  @Test public void identify() {
    integration.identify(new IdentifyPayloadBuilder().traits(createTraits("foo")).build());

    verifyStatic();
  }

  @Test public void track() {
    integration.track(new TrackPayloadBuilder().event("foo").build());

    verifyStatic();
  }

  @Test public void trackWithRevenue() {
    TrackPayloadBuilder builder = new TrackPayloadBuilder()
        .event("qaz") //
        .properties(new Properties().putRevenue(10));
    integration.track(builder.build());

    verifyStatic();
  }
}
