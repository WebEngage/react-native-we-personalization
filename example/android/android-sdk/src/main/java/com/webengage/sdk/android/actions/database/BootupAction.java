package com.webengage.sdk.android.actions.database;

import android.content.Context;

import com.webengage.sdk.android.Action;

import java.util.Map;

class BootupAction extends Action {
    private Context applicationContext = null;

    protected BootupAction(Context context) {
        super(context);
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    protected Object preExecute(Map<String, Object> actionAttributes) {
        return null;
    }

    @Override
    protected Object execute(Object data) {
        String cuid = getCUID();
        Map<String, Object> allUserData = UserProfileDataManager.getInstance(this.applicationContext).getAllUserData(cuid.isEmpty() ? getLUID() : cuid);
        if (allUserData != null) {
            if (allUserData.size() > 0) {
                DataHolder.get().silentSetData(allUserData);
            }
        }
        return null;
    }

    @Override
    protected void postExecute(Object data) {

    }
}
