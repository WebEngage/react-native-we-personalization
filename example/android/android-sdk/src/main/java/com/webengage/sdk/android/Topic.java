package com.webengage.sdk.android;

import com.webengage.sdk.android.actions.database.BootupActionController;
import com.webengage.sdk.android.actions.database.DataController;
import com.webengage.sdk.android.actions.database.JourneyContextController;
import com.webengage.sdk.android.actions.database.ReportingController;
import com.webengage.sdk.android.actions.database.SyncActionController;
import com.webengage.sdk.android.actions.database.UserProfileFetchAndUpdateController;
import com.webengage.sdk.android.actions.deeplink.DeepLinkActionController;
import com.webengage.sdk.android.actions.exception.ExceptionController;
import com.webengage.sdk.android.actions.gcm.GCMRegistrationActionController;
import com.webengage.sdk.android.actions.render.RenderingController;
import com.webengage.sdk.android.actions.rules.ConfigurationController;
import com.webengage.sdk.android.actions.rules.RuleExecutionController;

public enum Topic {
    BOOT_UP(new Subscriber.Factory[]{BootupActionController.FACTORY, ConfigurationController.FACTORY, GCMRegistrationActionController.FACTORY}),
    EVENT(new Subscriber.Factory[]{RenderingController.FACTORY, DataController.FACTORY, RuleExecutionController.FACTORY, GCMRegistrationActionController.FACTORY, ReportingController.FACTORY}),
    GCM_MESSAGE(new Subscriber.Factory[]{DataController.FACTORY, RenderingController.FACTORY}),
    CONFIG_REFRESH(new Subscriber.Factory[]{ConfigurationController.FACTORY}),
    SYNC_TO_SERVER(new Subscriber.Factory[]{SyncActionController.FACTORY}),
    DEEPLINK(new Subscriber.Factory[]{DeepLinkActionController.FACTORY}),
    EXCEPTION(new Subscriber.Factory[]{ExceptionController.FACTORY}),
    INTERNAL_EVENT(new Subscriber.Factory[]{RenderingController.FACTORY, DataController.FACTORY, RuleExecutionController.FACTORY}),
    DATA(new Subscriber.Factory[]{DataController.FACTORY}),
    RENDER(new Subscriber.Factory[]{RenderingController.FACTORY}),
    RULE_EXECUTION(new Subscriber.Factory[]{RuleExecutionController.FACTORY}),
    FETCH_PROFILE(new Subscriber.Factory[]{UserProfileFetchAndUpdateController.FACTORY}),
    JOURNEY_CONTEXT(new Subscriber.Factory[]{JourneyContextController.FACTORY}),
    REPORT(new Subscriber.Factory[]{ReportingController.FACTORY}),
    AMPLIFY(new Subscriber.Factory[]{AmplifyController.FACTORY});

    Subscriber.Factory[] factories;

    Topic(Subscriber.Factory[] factories) {
        this.factories = factories;
    }

    protected Subscriber.Factory[] getFactories() {
        return this.factories;
    }
}
