package ccoderad.bnds.shiyiquanevent.utils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

import ccoderad.bnds.shiyiquanevent.global.MultiThreadConstants;

/**
 * Created by CCoderAD on 2017/1/11.
 */

public class MultiThreadUtil {

    public static DefaultRetryPolicy createDefaultRetryPolicy() {
        return new DefaultRetryPolicy(MultiThreadConstants.REQUEST_TIME_OUT_LIMIT,
                MultiThreadConstants.REQUEST_FAILURE_RETRY_NUM,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    public static class RetryPolicyBuilder {
        private int mTimeLimit;
        private int mRetryNum;
        private float mBackOffMult;

        public RetryPolicyBuilder() {
            mTimeLimit = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;
            mRetryNum = DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
            mBackOffMult = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }

        public RetryPolicyBuilder setTimeLimit(int time) {
            mTimeLimit = time;
            return this;
        }

        public RetryPolicyBuilder setRetryTimes(int retryNum) {
            mRetryNum = retryNum;
            return this;
        }

        public RetryPolicyBuilder setBackOffMult(float mult) {
            mBackOffMult = mult;
            return this;
        }

        public RetryPolicy build() {
            return new DefaultRetryPolicy(mTimeLimit, mRetryNum, mBackOffMult);
        }
    }
}
