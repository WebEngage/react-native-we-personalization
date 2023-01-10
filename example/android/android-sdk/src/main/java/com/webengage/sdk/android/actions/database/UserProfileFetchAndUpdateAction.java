package com.webengage.sdk.android.actions.database;

import android.content.Context;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.BuildConfig;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.UserSystemAttribute;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.actions.rules.ConfigurationManager;
import com.webengage.sdk.android.utils.NetworkUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.http.CachePolicy;
import com.webengage.sdk.android.utils.http.RequestMethod;
import com.webengage.sdk.android.utils.http.RequestObject;
import com.webengage.sdk.android.utils.http.Response;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class UserProfileFetchAndUpdateAction extends Action {
    Context applicationContext = null;
    String userIdentifier = null;
    ConfigurationManager configurationManager = null;

    UserProfileFetchAndUpdateAction(Context context) {
        super(context);
        this.applicationContext = context;
    }

    @Override
    protected Object preExecute(Map<String, Object> actionAttributes) {
        try {
            configurationManager = new  ConfigurationManager(this.applicationContext);
            List<Object> eventCriteriaList = configurationManager.getEventCriteriaList();
            if (eventCriteriaList != null && eventCriteriaList.size() > 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return !getCUID().isEmpty();
    }

    @Override
    protected Object execute(Object data) {
        if ((Boolean) data) {
            String url = WebEngageConstant.Urls.getUserProfileEndPoint(getLUID(), getCUID(), WebEngage.get().getWebEngageConfig().getWebEngageKey());
            if (BuildConfig.DEBUG) {
                Logger.d(WebEngageConstant.TAG, "upf url: " + url);
            }
            RequestObject requestObject = new RequestObject.Builder(url, RequestMethod.GET, this.applicationContext)
                    .setCachePolicy(CachePolicy.GET_DATA_FROM_NETWORK_ONLY_NO_CACHING)
                    .build();
            Response response = requestObject.execute();

            if (response != null && response.isReadable()) {
                userIdentifier = getCUID().isEmpty() ? getLUID() : getCUID();
                Map<String, Object> userProfileData = null;
                try {
                    userProfileData = NetworkUtils.getAsMap(response.getInputStream(), true);
                } catch (Exception e) {
                    dispatchExceptionTopic(e);
                }
                if (userProfileData != null) {
                    userProfileData = (Map<String, Object>) userProfileData.get("upf");
                    if (userProfileData != null) {
                        if (getCUID().isEmpty()) {
                            for (DataContainer dataContainer : DataContainer.values()) {
                                if (dataContainer.isAnonymousUserContainer()) {
                                    readProfile(dataContainer, userProfileData);
                                }
                            }

                            loadUserDataFromProfile(UserSystemAttribute.CITY, userProfileData);
                            loadUserDataFromProfile(UserSystemAttribute.COUNTRY, userProfileData);
                            loadUserDataFromProfile(UserSystemAttribute.REGION, userProfileData);
                            loadUserDataFromProfile(UserSystemAttribute.LOCALITY, userProfileData);
                            loadUserDataFromProfile(UserSystemAttribute.POSTAL_CODE, userProfileData);

                        } else {
                            if (getCUID().equals(userProfileData.get("cuid"))) {
                                for (DataContainer dataContainer : DataContainer.values()) {
                                    if (dataContainer.isKnownUsersContainer()) {
                                        readProfile(dataContainer, userProfileData);
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                if (response != null) {
                    response.closeErrorStream();
                }
            }
        }
        return null;
    }

    @Override
    protected void postExecute(Object data) {

    }


    private void readProfile(DataContainer dataContainer, Map<String, Object> userProfile) {
        switch (dataContainer) {
            case USER:
                Set<String> keys = userProfile.keySet();
                Iterator<String> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    UserSystemAttribute userSystemAttribute = UserSystemAttribute.valueByString(key);
                    if (!"event_criterias".equals(key) && !"devices".equals(key) && !"user_attributes".equals(key) && !"journey".equals(key)) {
                        try {
                            DataHolder.get().setOrUpdateUserProfile(userIdentifier, key, userProfile.get(key), dataContainer, userSystemAttribute == null ? Operation.FORCE_UPDATE : userSystemAttribute.getOperation());
                        } catch (Exception e) {

                        }
                    }
                }
                break;
            case EVENT_CRITERIA:
                Set<String> criteriaIdsFromConfig = configurationManager.getEventCriteriaIds();
                List<Object> eventCriterias = (List<Object>) userProfile.get("event_criterias");
                if (eventCriterias != null) {
                    for (int i = 0; i < eventCriterias.size(); i++) {
                        Map<String, Object> criteria = (Map<String, Object>) eventCriterias.get(i);
                        if (criteria != null) {
                            try {
                                String criteriaId = (String) criteria.get("criteria_id");
                                if (criteriaIdsFromConfig != null && criteriaIdsFromConfig.contains(criteriaId)) {
                                    DataHolder.get().setOrUpdateEventCriteriaValue(userIdentifier, criteriaId, criteria);
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                }
                break;

            case ATTR:
                try {
                    DataHolder.get().setOrUpdateUsersCustomAttributes(userIdentifier, (Map<String, Object>) userProfile.get("user_attributes"));
                } catch (Exception e) {

                }
                break;

            case ANDROID:
                break;
            case IOS:
            case WEB:
                readDeviceData(userProfile, dataContainer);
                break;
        }
    }

    private void readDeviceData(Map<String, Object> userProfile, DataContainer dataContainer) {
        Map<String, Object> devices = (Map<String, Object>) userProfile.get("devices");
        if (devices != null) {
            List<Object> platformDataArray = (List<Object>) devices.get(dataContainer.getSDKID());
            if (platformDataArray != null && platformDataArray.size() > 0) {
                Map<String, Object> platformData = (Map<String, Object>) platformDataArray.get(0);
                if (platformData != null) {
                    Set<String> keys = platformData.keySet();
                    Iterator<String> iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        try {
                            DataHolder.get().setOrUpdateUserProfile(userIdentifier, key, platformData.get(key), dataContainer);
                        } catch (Exception e) {

                        }
                    }

                }
            }
        }
    }


    private void loadUserDataFromProfile(UserSystemAttribute userSystemAttribute, Map<String, Object> userProfile) {
        if (userProfile != null && userSystemAttribute != null) {
            String key = userSystemAttribute.toString();
            Object value = userProfile.get(key);
            if (value != null) {
                DataHolder.get().setOrUpdateUserProfile(userIdentifier, key, value, DataContainer.USER, Operation.FORCE_UPDATE);
            }
        }
    }


}
