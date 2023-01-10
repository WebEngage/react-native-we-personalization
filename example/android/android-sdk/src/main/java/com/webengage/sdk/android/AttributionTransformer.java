package com.webengage.sdk.android;

import java.util.Map;

interface AttributionTransformer {
    Map<String, Object> transform(String referrer);
}
