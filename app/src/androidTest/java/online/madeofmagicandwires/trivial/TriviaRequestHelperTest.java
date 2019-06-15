package online.madeofmagicandwires.trivial;

import android.app.Instrumentation;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.internal.runner.listener.InstrumentationRunListener;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TriviaRequestHelperTest {

    private Context appContext;
    private TriviaRequestHelper helper;

    private String seshtoken;
    private String resetToken;
    private JSONArray questionArr;


    /**
     * Initialises the application context and TriviaRequestHelper instance
     */
    @Before
    public void setContext(){
        appContext = InstrumentationRegistry.getTargetContext();
        this.helper = TriviaRequestHelper.getInstance(appContext);
    }

    /**
     * Tests if {@link TriviaRequestHelper#getInstance(Context)} truly produces a singleton
     */
    @Test
    public void getInstance() {
        TriviaRequestHelper instance1 = TriviaRequestHelper.getInstance(appContext);
        TriviaRequestHelper instance2 = TriviaRequestHelper.getInstance(appContext);
        assertEquals(instance1, instance2);
    }


    /**
     * Tests {@link TriviaRequestHelper#requestSessionToken(TriviaRequestHelper.SessionTokenRequestListener)}
     * @throws InterruptedException when this method is interrupted while waiting for a network response
     */
    @Test
    public void requestSessionToken() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        final TriviaRequestHelper.SessionTokenRequestListener testListener =
                new TriviaRequestHelper.SessionTokenRequestListener() {
            @Override
            public void OnTokenRequestSuccess(String token) {
                seshtoken = token;
                signal.countDown();
            }

            @Override
            public void OnTokenResetSuccess(String token) {
                // not used here
            }

            @Override
            public void OnResponseError(String lastRequest, @Nullable String errorMsg) {
                Log.e("requestSessionToken", "request error: " +  errorMsg);
                signal.countDown();

            }
        };

        helper.requestSessionToken(testListener);
        // wait till the requestSessionToken request has resolved
        boolean resolved = signal.await(30, TimeUnit.SECONDS);
        assertFalse("Request was timed out!", resolved);

        Log.d("Session token: ", seshtoken);
        assertNotNull("Session token has not been filled in!", seshtoken);
        assertFalse("Session token " + seshtoken + " is empty!", seshtoken.isEmpty());
        assertEquals("Session token was not saved correctly", seshtoken, helper.getSessionToken());


    }

    /**
     * Tests {@link TriviaRequestHelper#resetSessionToken(TriviaRequestHelper.SessionTokenRequestListener)}
     * @throws InterruptedException when this method is interrupted while waiting for a network response
     */
    @Test
    public void resetSessionToken() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(2);
        final TriviaRequestHelper.SessionTokenRequestListener testListener =
                new TriviaRequestHelper.SessionTokenRequestListener() {
            @Override
            public void OnTokenRequestSuccess(String token) {
                Log.d("resetSessionToken", "received initial token: " + token);
                seshtoken = token;
                signal.countDown();
            }

            @Override
            public void OnTokenResetSuccess(String token) {
                Log.d("resetSessionToken", "reset token: " + token);
                resetToken = token;
                signal.countDown();
            }

            @Override
            public void OnResponseError(String lastRequest, @Nullable String errorMsg) {
                Log.e("resetSessionToken", "request error: " +  errorMsg);
                signal.countDown(); // release one thread whenever a request has failed
            }
        };

        helper.requestSessionToken(testListener);
        // wait till the requestSessionToken request has resolved
        boolean resolved = signal.await(30, TimeUnit.SECONDS);
        assertFalse("Request was timed out!", resolved);
        helper.resetSessionToken(testListener);
        // wait till resetSessionToken request has resolved
        resolved = signal.await(30, TimeUnit.SECONDS);
        assertTrue("Request was timed out!", resolved);

        // testing response
        assertNotNull("Reset token was not set!", resetToken);
        assertEquals("Reset token is not the same as the original!", seshtoken, resetToken);
    }

    /**
     * Tests {@link TriviaRequestHelper#requestQuestions(int, TriviaRequestHelper.QuestionRequestListener)}
     *
     */
    @Test
    public void requestQuestions() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        TriviaRequestHelper.QuestionRequestListener testListener = new TriviaRequestHelper.QuestionRequestListener() {
            @Override
            public void OnQuestionsRequestSuccess(JSONArray questions) {
                questionArr = questions;
                signal.countDown();
            }

            @Override
            public void OnResponseError(String lastRequest, @Nullable String errorMsg) {
                signal.countDown(); //release thread if error occurred
            }
        };

        helper.requestQuestions(10, testListener);
        boolean resolved = signal.await(30, TimeUnit.SECONDS);
        assertTrue("Request did not resolve within 30 seconds!", resolved);

        // test response
        assertNotNull("Did not retrieve any questions array from questions request!", questionArr);
        assertNotEquals("Questions Array was empty!", 0, questionArr.length());
    }

}