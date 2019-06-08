package online.madeofmagicandwires.trivial;

import android.app.Instrumentation;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.internal.runner.listener.InstrumentationRunListener;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TriviaRequestHelperTest implements TriviaRequestHelper.SessionTokenRequestListener {

    private Context appContext;
    private TriviaRequestHelper helper;

    private String seshtoken;


    @Before
    public void setContext(){
        appContext = InstrumentationRegistry.getTargetContext();
        this.helper = TriviaRequestHelper.getInstance(appContext);
    }

    @Test
    public void getInstance() {
        TriviaRequestHelper instance1 = TriviaRequestHelper.getInstance(appContext);
        TriviaRequestHelper instance2 = TriviaRequestHelper.getInstance(appContext);
        assertEquals(instance1, instance2);
    }



    public void makeRequest() {
    }

    public void makeRequest1() {
    }


    public void makeRequest2() {
    }

    @Test
    public void setLastRequest() {
    }

    @Test
    public void setListener() {
        helper.setListener(this);
        assertEquals(this, helper.getListener());

    }

    @Test
    public void requestSessionToken() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        Instrumentation instrument = InstrumentationRegistry.getInstrumentation();
        final TriviaRequestHelper.SessionTokenRequestListener these = this;
        instrument.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                Log.d("Runnable!", "Test!");
                System.out.println("println Test!");
                helper.requestSessionToken(new TriviaRequestHelper.SessionTokenRequestListener() {
                    @Override
                    public void OnTokenRequestSuccess(String token) {
                        seshtoken = token;
                    }

                    @Override
                    public void OnResponseError(String lastRequest, @Nullable String errorMsg) {
                        System.out.println(errorMsg);

                    }
                });
                signal.countDown();
            }
        });
        signal.await(30, TimeUnit.SECONDS);
        System.out.println("Session token: "  + seshtoken);
        assertNotNull("Session token has not been filled in!", seshtoken);
        assertFalse("Session token " + seshtoken + " is empty!", seshtoken.isEmpty());


    }

    public void resetSessionToken() {
    }

    /**
     * called when an error occurs during a request to the OpenTrivia API
     *
     * @param lastRequest the endpoint of the request;
     *                    note that this might not be entirely accurate due to async requests
     * @param errorMsg    the error message included.
     */
    @Override
    public void OnResponseError(String lastRequest, @Nullable String errorMsg) {
    }

    /**
     * called when a TriviaDB session request has been successfully requeste
     *
     * @param token the session token retrieved
     */
    @Override
    public void OnTokenRequestSuccess(String token) {
        System.out.println(token);
        seshtoken = token;
    }
}