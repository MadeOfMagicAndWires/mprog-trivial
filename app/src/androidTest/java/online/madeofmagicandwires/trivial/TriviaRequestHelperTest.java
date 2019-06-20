package online.madeofmagicandwires.trivial;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.util.SparseArray;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import online.madeofmagicandwires.trivial.helpers.TriviaRequestHelper;
import online.madeofmagicandwires.trivial.models.TriviaGame;
import online.madeofmagicandwires.trivial.models.TriviaQuestion;

import java.lang.InterruptedException;
import java.lang.Integer;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TriviaRequestHelperTest {

    private Context appContext;
    private TriviaRequestHelper helper;

    // needed by requestSessionToken
    private String seshtoken;
    private String resetToken;

    // needed by requestQuestions
    private List<TriviaQuestion> questionArr;

    // needed by requestCategories
    private SparseArray<String> categoriesArr;


    // needed by requestQuestionCount
    private int globalTotal;
    private int globalCategoryTotal;
    private int categoryTotal;
    private int categoryByDifficulty;


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
     * Tests {@link TriviaRequestHelper#requestSessionToken(TriviaRequestHelper.SessionTokenResponseListener)}
     * @throws InterruptedException when this method is interrupted while waiting for a network response
     */
    @Test
    public void requestSessionToken() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        final TriviaRequestHelper.SessionTokenResponseListener testListener =
                new TriviaRequestHelper.SessionTokenResponseListener() {
            @Override
            public void OnRequestTokenResponse(String token) {
                seshtoken = token;
                signal.countDown();
            }

            @Override
            public void OnResetTokenResponse(String token) {
                // not used here
            }

            @Override
            public void OnErrorResponse(String lastRequest, @Nullable String errorMsg) {
                Log.e("requestSessionToken", "request error: " +  errorMsg);
                signal.countDown();

            }
        };

        helper.requestSessionToken(testListener);
        // wait till the requestSessionToken request has resolved
        boolean resolved = signal.await(30, TimeUnit.SECONDS);
        assertTrue("Request was timed out!", resolved);

        Log.d("Session token: ", seshtoken);
        assertNotNull("Session token has not been filled in!", seshtoken);
        assertFalse("Session token " + seshtoken + " is empty!", seshtoken.isEmpty());
        assertEquals("Session token was not saved correctly", seshtoken, helper.getSessionToken());


    }

    /**
     * Tests {@link TriviaRequestHelper#resetSessionToken(TriviaRequestHelper.SessionTokenResponseListener)}
     * @throws InterruptedException when this method is interrupted while waiting for a network response
     */
    @Test
    public void resetSessionToken() throws InterruptedException {
        final CountDownLatch initialSignal = new CountDownLatch(1);
        final CountDownLatch resetSignal =  new CountDownLatch(1);
        final TriviaRequestHelper.SessionTokenResponseListener testListener =
                new TriviaRequestHelper.SessionTokenResponseListener() {
            @Override
            public void OnRequestTokenResponse(String token) {
                Log.d("resetSessionToken", "received initial token: " + token);
                seshtoken = token;
                initialSignal.countDown();
            }

            @Override
            public void OnResetTokenResponse(String token) {
                Log.d("resetSessionToken", "reset token: " + token);
                resetToken = token;
                resetSignal.countDown();
            }

            @Override
            public void OnErrorResponse(String lastRequest, @Nullable String errorMsg) {
                Log.e("resetSessionToken", "request error: " +  errorMsg);
                // release one thread whenever a request has failed
                initialSignal.countDown();
                resetSignal.countDown();
            }
        };

        helper.requestSessionToken(testListener);
        // wait till the requestSessionToken request has resolved
        boolean resolved = initialSignal.await(30, TimeUnit.SECONDS);
        assertTrue("Request was timed out!", resolved);
        helper.resetSessionToken(testListener);
        // wait till resetSessionToken request has resolved
        resolved = resetSignal.await(30, TimeUnit.SECONDS);
        assertTrue("Request was timed out!", resolved);

        // testing response
        assertNotNull("Reset token was not set!", resetToken);
        assertEquals("Reset token is not the same as the original!", seshtoken, resetToken);
    }

    /**
     * Tests {@link TriviaRequestHelper#requestQuestions(int, TriviaRequestHelper.QuestionResponseListener)}
     *
     */
    @Test
    public void requestQuestions() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        TriviaRequestHelper.QuestionResponseListener testListener = new TriviaRequestHelper.QuestionResponseListener() {
            /**
             * called when an error occurs during a request to the OpenTrivia API
             *
             * @param lastRequest the endpoint of the request;
             *                    note that this might not be entirely accurate due to async requests
             * @param errorMsg    the error message included.
             */
            @Override
            public void OnErrorResponse(String lastRequest, @Nullable String errorMsg) {
                Log.e("requestQuestions", lastRequest + ": " + errorMsg);
            }

            /**
             * Called if a TriviaDB question request successfully resolved
             *
             *
             * @param questions a {@link List} of {@link TriviaQuestion} objects
             *                  representing the trivia questions that were retrieved from the TriviaDB
             * @see TriviaRequestHelper#requestQuestions(int, TriviaRequestHelper.QuestionResponseListener)
             */
            @Override
            public void OnQuestionsResponse(List<TriviaQuestion> questions) {
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
     * Tests {@link TriviaRequestHelper#requestCategories(TriviaRequestHelper.CategoriesResponseListener)}
     *       and {@link TriviaRequestHelper.CategoriesResponseListener}
     *
     * @throws InterruptedException when thread is interrupted
     */
    @Test
    public void requestCategories() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        TriviaRequestHelper.CategoriesResponseListener testListener = new TriviaRequestHelper.CategoriesResponseListener() {
            @Override
            public void OnCategoriesResponse(SparseArray<String> categories) {
                categoriesArr = categories;
                signal.countDown();
            }

            @Override
            public void OnErrorResponse(String lastRequest, @Nullable String errorMsg) {
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

    /**
     * Tests {@link TriviaRequestHelper#requestQuestionCount(TriviaRequestHelper.QuestionCountResponseListener, Integer)}
     * with neither a category parameter or a session category set
     *
     * @see TriviaRequestHelper.QuestionCountResponseListener#OnQuestionCountResponse(int, int)
     * @throws InterruptedException when thread is interrupted
     */
    @Test
    public void requestQuestionCountGlobalNoCategory() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        TriviaRequestHelper.QuestionCountResponseListener testListener =
                new TriviaRequestHelper.QuestionCountResponseListener() {
            @Override
            public void OnQuestionCountResponse(int total, int categoryCount) {
                globalTotal = total;
                globalCategoryTotal = categoryCount;
                signal.countDown();
            }

            @Override
            public void OnCategoryQuestionCountResponse(int total, int difficultyCount) {
                categoryTotal = total;
                categoryByDifficulty = difficultyCount;
                signal.countDown();

            }

            @Override
            public void OnErrorResponse(String lastRequest, @Nullable String errorMsg) {
                Log.e("requestQuestionCount", errorMsg);
                signal.countDown();
            }
        };

        // global test, no session category
        helper.setCategoryId(null);
        helper.requestQuestionCount(testListener);
        boolean resolved = signal.await(30, TimeUnit.SECONDS);

        Log.d("requestQuestionCount", "Testing 'Global count, no session category'");
        assertTrue("Request took too long to resolve!", resolved);
        assertNotEquals("Global count was not set!", -1, globalTotal);
        assertNotEquals("Category  total was not set!", -1, globalCategoryTotal);
        assertEquals("Category total differed from global total without session category set!", globalTotal, globalCategoryTotal);
    }

    /**
     * Tests {@link TriviaRequestHelper#requestQuestionCount(TriviaRequestHelper.QuestionCountResponseListener, Integer)}
     * without category parameter but with a session category set
     *
     * @see TriviaRequestHelper.QuestionCountResponseListener#OnQuestionCountResponse(int, int)
     * @throws InterruptedException when a thread is interrupted
     */
    @Test
    public void requestQuestionCountGlobalWithCategory() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        TriviaRequestHelper.QuestionCountResponseListener testListener =
                new TriviaRequestHelper.QuestionCountResponseListener() {
                    @Override
                    public void OnQuestionCountResponse(int total, int categoryCount) {
                        globalTotal = total;
                        globalCategoryTotal = categoryCount;
                        signal.countDown();
                    }

                    @Override
                    public void OnCategoryQuestionCountResponse(int total, int difficultyCount) {
                        categoryTotal = total;
                        categoryByDifficulty = difficultyCount;
                        signal.countDown();

                    }

                    @Override
                    public void OnErrorResponse(String lastRequest, @Nullable String errorMsg) {
                        Log.e("requestQuestionCount", errorMsg);
                        signal.countDown();
                    }
                };

        // global test, with session category
        helper.setCategoryId(22);
        helper.requestQuestionCount(testListener);
        boolean resolved = signal.await(30, TimeUnit.SECONDS);

        Log.d("requestQuestionCount", "Testing 'Global count, with session category'");
        assertTrue("Request took too long to resolve!", resolved);
        assertNotEquals("Global count was not set!", -1, globalTotal);
        assertNotEquals("Category  total was not set!", -1, globalCategoryTotal);
        assertNotEquals("Category total equalled global total with session category set!", globalTotal, globalCategoryTotal);
    }

    /**
     * Tests {@link TriviaRequestHelper#requestQuestionCount(TriviaRequestHelper.QuestionCountResponseListener, Integer)}
     * with category parameter but no session difficulty set
     *
     * @see TriviaRequestHelper.QuestionCountResponseListener#OnCategoryQuestionCountResponse(int, int)
     * @throws InterruptedException when a thread is interrupted
     */
    @Test
    public void requestQuestionCountCategoryNoDifficulty() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        TriviaRequestHelper.QuestionCountResponseListener testListener =
                new TriviaRequestHelper.QuestionCountResponseListener() {
                    @Override
                    public void OnQuestionCountResponse(int total, int categoryCount) {
                        globalTotal = total;
                        globalCategoryTotal = categoryCount;
                        signal.countDown();
                    }

                    @Override
                    public void OnCategoryQuestionCountResponse(int total, int difficultyCount) {
                        categoryTotal = total;
                        categoryByDifficulty = difficultyCount;
                        signal.countDown();

                    }

                    @Override
                    public void OnErrorResponse(String lastRequest, @Nullable String errorMsg) {
                        Log.e("requestQuestionCount", errorMsg);
                        signal.countDown();
                    }
                };

        // category test, no session difficulty
        helper.setCategoryId(22);
        helper.setDifficulty(null);
        helper.requestQuestionCount(testListener, 22);
        boolean resolved = signal.await(30, TimeUnit.SECONDS);

        Log.d("requestQuestionCount", "Testing 'Category count, no session difficulty'");
        assertTrue("Request took too long to resolve!", resolved);
        assertNotEquals("Global count was not set!", -1, categoryTotal);
        assertNotEquals("Category  total was not set!", -1, categoryByDifficulty);
        assertEquals("Category total differed from category-by-difficulty without session difficulty set!", categoryTotal, categoryByDifficulty);
    }

    /**
     * Tests {@link TriviaRequestHelper#requestQuestionCount(TriviaRequestHelper.QuestionCountResponseListener, Integer)}
     * with category parameter and a session difficulty set
     *
     * @see TriviaRequestHelper.QuestionCountResponseListener#OnCategoryQuestionCountResponse(int, int)
     * @throws InterruptedException when a thread is interrupted
     */
    @Test
    public void requestQuestionCountCategoryWithDifficulty() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        TriviaRequestHelper.QuestionCountResponseListener testListener =
                new TriviaRequestHelper.QuestionCountResponseListener() {
                    @Override
                    public void OnQuestionCountResponse(int total, int categoryCount) {
                        globalTotal = total;
                        globalCategoryTotal = categoryCount;
                        signal.countDown();
                    }

                    @Override
                    public void OnCategoryQuestionCountResponse(int total, int difficultyCount) {
                        categoryTotal = total;
                        categoryByDifficulty = difficultyCount;
                        signal.countDown();

                    }

                    @Override
                    public void OnErrorResponse(String lastRequest, @Nullable String errorMsg) {
                        Log.e("requestQuestionCount", errorMsg);
                        signal.countDown();
                    }
                };

        // category test, with session difficulty
        helper.setCategoryId(22);
        helper.setDifficulty(TriviaGame.Difficulty.EASY);
        helper.requestQuestionCount(testListener, 22);
        boolean resolved = signal.await(30, TimeUnit.SECONDS);

        Log.d("requestQuestionCount", "Testing 'Category count, with session difficulty'");
        assertTrue("Request took too long to resolve!", resolved);
        assertNotEquals("Global count was not set!", -1, categoryTotal);
        assertNotEquals("Category  total was not set!", -1, categoryByDifficulty);
        assertNotEquals("Category total equalled from category-by-difficulty with session category set!", categoryTotal, categoryByDifficulty);
    }

}