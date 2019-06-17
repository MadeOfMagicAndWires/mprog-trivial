package online.madeofmagicandwires.trivial;

import android.app.Instrumentation;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.internal.runner.listener.InstrumentationRunListener;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
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
    private List<TriviaQuestion> questionArr;
    private SparseArray<String> categoriesArr;


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
            /**
             * called when an error occurs during a request to the OpenTrivia API
             *
             * @param lastRequest the endpoint of the request;
             *                    note that this might not be entirely accurate due to async requests
             * @param errorMsg    the error message included.
             */
            @Override
            public void OnResponseError(String lastRequest, @Nullable String errorMsg) {
                Log.e("requestQuestions", lastRequest + ": " + errorMsg);
            }

            /**
             * Called if a TriviaDB question request successfully resolved
             *
             *
             * @param questions a {@link List} of {@link TriviaQuestion} objects
             *                  representing the trivia questions that were retrieved from the TriviaDB
             * @see TriviaRequestHelper#requestQuestions(int, TriviaRequestHelper.QuestionRequestListener)
             */
            @Override
            public void OnQuestionsRequestSuccess(List<TriviaQuestion> questions) {
                questionArr = questions;
                signal.countDown();

            }
        };

        helper.requestQuestions(10, testListener);
        boolean resolved = signal.await(30, TimeUnit.SECONDS);
        assertTrue("Request did not resolve within 30 seconds!", resolved);

        // test response list
        assertNotNull("Did not retrieve any questions array from questions request!", questionArr);
        assertEquals("Questions Array was empty!", 10, questionArr.size());

        // test question object from list

        questionArr.forEach((testQuestion) -> {
            assertNotNull("Retrieved question did not have a question!", testQuestion.getQuestion());
            assertFalse("Retrieved question's question was empty!", testQuestion.getQuestion().isEmpty());

            // test category
            assertNotNull("Retrieved question did not have a category!", testQuestion.getCategory());
            assertFalse("Retrieved question's answers were empty!", testQuestion.getAnswers().isEmpty());

            // test difficulty
            assertNotNull("Retrieved question did not have a difficulty!", testQuestion.getDifficulty());
            assertFalse("Retrieved question's difficulty was empty!", testQuestion.getDifficulty().isEmpty());
            @TriviaGame.Difficulty String testQuestionDifficulty = testQuestion.getDifficulty();
            assertNotNull("Difficulty was not set in TriviaGame.Difficulty!", testQuestionDifficulty);

            // test correct answer
            assertNotNull("Retrieved question did not have a correct answer!", testQuestion.getRightAnswer());
            assertFalse("Retrieved question's correct answer was empty!", testQuestion.getRightAnswer().isEmpty());
        });
    }

    /**
     * Tests {@link TriviaRequestHelper#requestCategories(TriviaRequestHelper.CategoriesRequestListener)}
     *       and {@link TriviaRequestHelper.CategoriesRequestListener}
     *
     * @throws InterruptedException
     */
    @Test
    public void requestCategories() throws InterruptedException {
        CountDownLatch signal = new CountDownLatch(1);
        TriviaRequestHelper.CategoriesRequestListener testListener = new TriviaRequestHelper.CategoriesRequestListener() {
            @Override
            public void OnCategoriesRequestSuccess(SparseArray<String> categories) {
                categoriesArr = categories;
                signal.countDown();
            }

            @Override
            public void OnResponseError(String lastRequest, @Nullable String errorMsg) {
                signal.countDown();
            }
        };
        helper.requestCategories(testListener);
        boolean resolved = signal.await(30, TimeUnit.SECONDS);
        assertTrue("Categories request took too long to resolve!", resolved);
        assertNotNull("Retrieved Category Array was not saved!", categoriesArr);
        assertNotEquals("Retrieved Category Array was empty!", 0, categoriesArr.size());

        // test individual entries
        for(int i=0;i<categoriesArr.size();i++) {
            Log.d("requestCategories", categoriesArr.keyAt(i) +  ": " + categoriesArr.valueAt(i));
            assertNotEquals("Category id was not saved correctly!", categoriesArr.keyAt(i), -1);
            assertNotEquals("Category name was not saved correctly!" , "Unknown");
        }
    }

}