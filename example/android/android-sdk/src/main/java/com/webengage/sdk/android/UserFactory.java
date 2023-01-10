package com.webengage.sdk.android;


import android.content.Context;

import java.util.Queue;

class UserFactory {
    static User user = null;
    static User noOp = null;
    static User queuedImpl = null;

    public static User getUser(Context context, Analytics analytics) {
        if (user == null) {
            user = new UserImpl(context.getApplicationContext(), analytics);
        }
        return user;
    }

    protected static User getNoOpUser() {
        if (noOp == null) {
            noOp = new UserNoOpImpl();
        }
        return noOp;
    }

    protected static User getQueuedImpl(Queue<Task> queue) {
        if(queuedImpl == null) {
            queuedImpl = new QueuedUserImpl(queue);
        }
        return queuedImpl;
    }
}
