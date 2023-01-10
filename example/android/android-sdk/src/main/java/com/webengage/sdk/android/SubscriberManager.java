package com.webengage.sdk.android;


import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

class SubscriberManager {
    private static volatile SubscriberManager instance = null;
    Map<Topic, LinkedHashSet<Subscriber>> subscriberMap = null;
    private Context applicationContext = null;
    private List<TopicInterceptor> topicInterceptorList = null;

    private SubscriberManager(Context context) {
        this.applicationContext = context.getApplicationContext();
        subscriberMap = new HashMap<Topic, LinkedHashSet<Subscriber>>();
        constructSubscriberMap();
    }

    static SubscriberManager get(Context context) {
        if (instance == null) {
            synchronized (SubscriberManager.class) {
                if (instance == null) {
                    instance = new SubscriberManager(context);
                }
            }
        }
        return instance;
    }

    private void constructSubscriberMap() {
        for (Topic topic : Topic.values()) {
            Subscriber.Factory[] factories = topic.getFactories();
            subscriberMap.put(topic, constructSubscriberList(factories));
        }
    }

    private LinkedHashSet<Subscriber> constructSubscriberList(Subscriber.Factory[] factories) {
        LinkedHashSet<Subscriber> subscribers = new LinkedHashSet<Subscriber>();
        if (factories != null) {
            for (Subscriber.Factory factory : factories) {
                if (factory != null) {
                    Subscriber subscriber = factory.initialize(this.applicationContext);
                    subscribers.add(subscriber);
                }
            }
        }
        return subscribers;
    }

    protected LinkedHashSet<Subscriber> getSubscribers(Topic topic) {
        return subscriberMap.get(topic);
    }


    protected void addTopicInterceptor(TopicInterceptor topicInterceptor) {
        if (topicInterceptorList == null) {
            topicInterceptorList = new ArrayList<TopicInterceptor>();
        }
        topicInterceptorList.add(topicInterceptor);
    }

    protected List<TopicInterceptor> getTopicInterceptorList() {
        return topicInterceptorList;
    }


    protected void callSubscribers(Topic topic, Object data) throws Exception {
        List<TopicInterceptor> topicInterceptorList = SubscriberManager.get(this.applicationContext).getTopicInterceptorList();
        if (preInterceptTopic(topic, data, topicInterceptorList)) {
            LinkedHashSet<Subscriber> subscribers = SubscriberManager.get(this.applicationContext).getSubscribers(topic);
            if (subscribers != null) {
                for (Subscriber subscriber : subscribers) {
                    subscriber.createAction(topic, data);
                }
            }
            postInterceptTopic(topic, data, topicInterceptorList);
        }
    }

    protected boolean preInterceptTopic(Topic topic, Object data, List<TopicInterceptor> topicInterceptorList) {
        boolean flag = true;
        if (topicInterceptorList != null) {
            for (TopicInterceptor topicInterceptor : topicInterceptorList) {
                flag = flag && topicInterceptor.preCall(topic, data);
            }
        }
        return flag;
    }

    protected void postInterceptTopic(Topic topic, Object data, List<TopicInterceptor> topicInterceptorList) {
        if (topicInterceptorList != null) {
            for (TopicInterceptor topicInterceptor : topicInterceptorList) {
                topicInterceptor.postCall(topic, data);
            }
        }
    }
}
