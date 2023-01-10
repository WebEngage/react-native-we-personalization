package com.webengage.sdk.android;


import java.util.HashMap;
import java.util.Map;

class GAAttributionTransformer implements AttributionTransformer {
    Map<String, String> transformerMap = null;

    public GAAttributionTransformer() {
        this.transformerMap = new HashMap<String, String>();
        this.transformerMap.put("utm_campaign", UserSystemAttribute.CAMPAIGN_ID.toString());
        this.transformerMap.put("utm_source", UserSystemAttribute.CAMPAIGN_SOURCE.toString());
        this.transformerMap.put("utm_medium", UserSystemAttribute.CAMPAIGN_MEDIUM.toString());
        this.transformerMap.put("utm_term", UserSystemAttribute.CAMPAIGN_TERM.toString());
        this.transformerMap.put("utm_content", UserSystemAttribute.CAMPAIGN_CONTENT.toString());
        this.transformerMap.put("gclid", UserSystemAttribute.CAMPAIGN_GCLID.toString());
    }

    @Override
    public Map<String, Object> transform(String referrer) {
        Map<String, Object> map = new HashMap<String, Object>();

        String referrers[] = referrer.split("&");
        for (String referrerValue : referrers) {
            String keyValue[] = referrerValue.split("=");
            if (this.transformerMap.containsKey(keyValue[0]) && keyValue.length > 1) {
                map.put(this.transformerMap.get(keyValue[0]), keyValue[1]);
            }
        }

        return map;
    }
}