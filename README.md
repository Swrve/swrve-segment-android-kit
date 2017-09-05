analytics-android-integration-swrve
======================================

Swrve integration for [analytics-android](https://github.com/segmentio/analytics-android).

## Installation

To install the Segment-Swrve integration, simply add this line to your gradle file:

```
compile 'com.segment.analytics.android.integrations:swrve:+'
```

## Usage

After adding the dependency, you must register the integration with our SDK.  To do this, import the Swrve integration:


```
import com.segment.analytics.android.integrations.swrve.SwrveIntegration;
```

And add the following line:

```
int appId = -1;
String apiKey = "api_key";

SwrveConfig swrveConfig = new SwrveConfig();
// To use the EU stack, include this in your config.
// swrveConfig.setSelectedStack(SwrveStack.EU);

analytics = new Analytics.Builder(this, "write_key")
                .use(SwrveIntegration.createFactory(application, appId, apiKey, swrveConfig)
                .build();
```


License
-------
© Copyright Swrve Mobile Inc or its licensors. Distributed under the [Apache 2.0 License](LICENSE).  
Google Play Services Library Copyright © 2012 The Android Open Source Project. Licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).  
Gradle Copyright © 2007-2011 the original author or authors. Licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).
