package com.webengage.sdk.android.actions.rules;

import android.content.Context;
import android.content.Intent;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.BuildConfig;
import com.webengage.sdk.android.IntentFactory;
import com.webengage.sdk.android.LocationManagerFactory;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.actions.database.DataContainer;
import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.actions.database.UserProfileDataManager;
import com.webengage.sdk.android.utils.NetworkUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.http.CachePolicy;
import com.webengage.sdk.android.utils.http.HttpDataManager;
import com.webengage.sdk.android.utils.http.RequestMethod;
import com.webengage.sdk.android.utils.http.RequestObject;
import com.webengage.sdk.android.utils.http.Response;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ConfigurationAction extends Action {
    private Context applicationContext = null;
    private Topic topic = null;

    ConfigurationAction(Context context) {
        super(context);
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    protected Object preExecute(Map<String, Object> actionAttributes) {
        String configUrl = (String) actionAttributes.get(ConfigurationController.CONGIF_URL);
        topic = (Topic) actionAttributes.get(ConfigurationController.TOPIC);
        int cachePolicy = CachePolicy.GET_VALIDATED_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE;
        switch (topic) {
            case BOOT_UP:
                cachePolicy = CachePolicy.GET_DATA_FROM_CACHE_ONLY;
                break;
            case CONFIG_REFRESH:
                cachePolicy = CachePolicy.GET_VALIDATED_DATA_FROM_CACHE_FIRST_ELSE_DOWNLOAD_AND_CACHE;
                break;
        }
        if (BuildConfig.DEBUG) {
            Logger.d(WebEngageConstant.TAG, "Config url: " + configUrl);
        }
        RequestObject requestObject = new RequestObject.Builder(configUrl, RequestMethod.GET, this.applicationContext)
                .setCachePolicy(cachePolicy)
                .build();
        Response response = requestObject.execute();
        return response;
    }

    @Override
    protected Object execute(Object data) {
        Response response = (Response) data;
        if (response.isReadable()) {
            switch (topic) {
                case BOOT_UP:
                    try {
//                        InputStream stream = new ByteArrayInputStream(WebEngageConstant.configString.getBytes());
//                        ConfigurationManager configurationManager = new ConfigurationManager(NetworkUtils.getAsMap(stream, false));
                        ConfigurationManager configurationManager = new ConfigurationManager(NetworkUtils.getAsMap(response.getInputStream(), false));
                        configurationManager.initRuntime(RuleExecutorFactory.getRuleExecutor(), DataHolder.get());
                        Map<String, Object> geofences = DataHolder.get().getGeoFences();
                        addGeofences(geofences);
                       // Logger.d(WebEngageConstant.TAG, " saving session destroy time at BOOT up: " + configurationManager.getSessionDestroyTime());
                        saveSessionDestroyTime(configurationManager.getSessionDestroyTime());
                    } catch (Exception e) {
                        dispatchExceptionTopic(e);
                    }
                    break;
                case CONFIG_REFRESH:
                    try {
                        if (response.getResponseCode() == 200) {
                            DataHolder.get().silentSetData(WebEngageConstant.REFRESH_CONFIG_RULE,true);
                            Map<String, Object> oldGeoFences = DataHolder.get().getGeoFences();
//                            InputStream stream = new ByteArrayInputStream(WebEngageConstant.configString.getBytes());
//                            ConfigurationManager configurationManager = new ConfigurationManager(NetworkUtils.getAsMap(stream, false));
                            ConfigurationManager configurationManager = new ConfigurationManager(NetworkUtils.getAsMap(response.getInputStream(), false));
                            Set<String> experimentIds = configurationManager.initRuntime(RuleExecutorFactory.getRuleExecutor(), DataHolder.get());
                            performCleanUp(configurationManager, experimentIds);
                            NetworkUtils.preFetchResourcesAsync(configurationManager.getGlobalResources(), this.applicationContext);
                            Map<String, Object> newGeoFences = DataHolder.get().getGeoFences();
                            manageGeoFences(oldGeoFences, newGeoFences);
                        //    Logger.d(WebEngageConstant.TAG, " saving session destroy time at config refresh: " + configurationManager.getSessionDestroyTime());
                            saveSessionDestroyTime(configurationManager.getSessionDestroyTime());
                        }
                    } catch (Exception e) {
                        dispatchExceptionTopic(e);
                    } finally {
                        response.closeInputStream();
                    }
                    break;
            }
            return response;
        } else {
            response.closeErrorStream();
        }
        return null;
    }

    @Override
    protected void postExecute(Object data) {
        //commented to avoid duplicate upf and jcx calls on app launch and config changes
//        if (data != null) {
//            if (Topic.CONFIG_REFRESH.equals(topic) && ((Response) data).getResponseCode() == 200) {
//                // fetch user profile as config has changed
//                Intent intent = IntentFactory.newIntent(Topic.FETCH_PROFILE, null, this.applicationContext);
//                WebEngage.startService(intent, applicationContext);
//
//                // fetch journey context
//                Intent jcxIntent = IntentFactory.newIntent(Topic.JOURNEY_CONTEXT, null, this.applicationContext);
//                WebEngage.startService(jcxIntent, applicationContext);
//            }
//        }
    }

    private void performCleanUp(ConfigurationManager configurationManager, Set<String> experimentIds) {
        Set<String> totalResources = configurationManager.getAllResources();
        Set<String> cachedResources = HttpDataManager.getInstance(this.applicationContext).getAllCachedResources();
        cachedResources.removeAll(totalResources);
        HttpDataManager.getInstance(this.applicationContext).removeResourcesByURL(cachedResources);


        Set<String> fetchedIds = configurationManager.getEventCriteriaIds();
        Map<String, Set<String>> presentEventCriteria = UserProfileDataManager.getInstance(this.applicationContext).getAllEventCriteriaIdsAcrossUsers();
        if (presentEventCriteria != null) {
            for (Map.Entry<String, Set<String>> entry : presentEventCriteria.entrySet()) {
                String userId = entry.getKey();
                Set<String> presentIds = entry.getValue();
                presentIds.removeAll(fetchedIds);
                if (presentIds.size() > 0) {
                    Iterator<String> idsToRemove = presentIds.iterator();
                    while (idsToRemove.hasNext()) {
                        DataHolder.get().setOrUpdateEventCriteriaValue(userId, idsToRemove.next(), null);
                    }
                }
            }
        }

        Map<String, Set<String>> scopesAcrossUsers = UserProfileDataManager.getInstance(this.applicationContext).getAllScopesAcrossUser();
        if (scopesAcrossUsers != null) {
            for (Map.Entry<String, Set<String>> entry : scopesAcrossUsers.entrySet()) {
                String userId = entry.getKey();
                Set<String> scopes = entry.getValue();
                Set<String> presentIds = new HashSet<String>();
                Iterator<String> iterator = scopes.iterator();
                while (iterator.hasNext()) {
                    String scopeKey = iterator.next();
                    String expId = "";
                    int index = scopeKey.indexOf('[');
                    if (index == -1) {
                        index = scopeKey.indexOf('_');
                    }
                    if (index != -1) {
                        expId = scopeKey.substring(0, index);
                        presentIds.add(expId);
                    }
                }
                presentIds.removeAll(experimentIds);
                if (presentIds.size() > 0) {
                    Iterator<String> itr = presentIds.iterator();
                    while (itr.hasNext()) {
                        String idToRemove = itr.next();
                        Iterator<String> scopesIterator = scopes.iterator();
                        while (scopesIterator.hasNext()) {
                            String scope = scopesIterator.next();
                            if (scope.startsWith(idToRemove)) {
                                DataHolder.get().setOrUpdateUserProfile(userId, scope, null, DataContainer.SCOPES);
                            }
                        }
                    }
                }
            }
        }
    }


    private void manageGeoFences(Map<String, Object> oldGeoFences, Map<String, Object> newGeoFences) {
        addGeofences(newGeoFences);

        if (oldGeoFences != null) {
            List<String> ids = new ArrayList<>(oldGeoFences.keySet());
            if (newGeoFences != null) {
                ids.removeAll(newGeoFences.keySet());
            }
            if (!ids.isEmpty()) {
                LocationManagerFactory.getLocationManager(this.applicationContext).unregisterGeoFence(ids);
            }
        }
    }

    private void addGeofences(Map<String, Object> geoFences) {
        if (geoFences != null) {
            for (Map.Entry<String, Object> entry : geoFences.entrySet()) {
                Map<String, Object> value = (Map<String, Object>) entry.getValue();
                if (value != null) {
                    double latitude = (double) value.get("lat");
                    double longitude = (double) value.get("long");
                    float radius = Float.parseFloat(value.get("radius").toString());
                    LocationManagerFactory.getLocationManager(this.applicationContext).registerGeoFence(latitude, longitude, radius, entry.getKey(), WebEngage.get().getWebEngageConfig());
                }
            }
        }
    }

}
