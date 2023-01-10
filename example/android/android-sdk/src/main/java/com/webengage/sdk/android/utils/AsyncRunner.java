package com.webengage.sdk.android.utils;


import com.webengage.sdk.android.utils.http.RequestObject;
import com.webengage.sdk.android.utils.http.Response;

public class AsyncRunner {

    public static void execute(Object object) {
        Runnable runnable = new WorkerThread(object);
        ThreadExecutor.get().execute(runnable);
    }

    private static class WorkerThread implements Runnable {
        private Object object;

        private WorkerThread(Object object) {
            this.object = object;
        }

        @Override
        public void run() {
            try {
                if (object instanceof Runnable) {
                    ((Runnable) object).run();
                } else if (object instanceof RequestObject) {
                    RequestObject requestObject = (RequestObject)object;
                    Response response = requestObject.execute();
                    if(response.isReadable()) {
                        response.closeInputStream();
                    } else {
                        response.closeErrorStream();
                    }
                }
            } catch (Exception e) {

            }
        }
    }
}
