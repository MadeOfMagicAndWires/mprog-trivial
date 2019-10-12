package online.madeofmagicandwires.trivial.helpers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import online.madeofmagicandwires.trivial.models.HighscoreList;

import static org.junit.Assert.*;

public class HighscoreRequestHelperTest {


    private Context appContext;
    private HighscoreRequestHelper helper;

    @Before
    public void setUp() {
        if(helper == null)
            appContext = InstrumentationRegistry.getTargetContext();
            helper = HighscoreRequestHelper.getInstance(appContext);
    }

    @Test
    public void getInstance() {
        HighscoreRequestHelper helper1 = HighscoreRequestHelper.getInstance(appContext);
        assertNotNull("getinstance returned null!", helper1);
        assertEquals("Helper instance was not the same", helper, helper1);
        assertTrue("Helper instance was not a singleton", (helper == helper1));

    }

    // TODO: Actually run this with server on.
    @Test
    public void requestHighscores() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<HighscoreList.Score> scoreResponse = new ArrayList<>();
        HighscoreRequestHelper.OnHighscoreListResponseListener listener =
                new HighscoreRequestHelper.OnHighscoreListResponseListener() {

            @Override
            public void OnHighscoreListResponse(List<HighscoreList.Score> scores) {
                scores.forEach( score -> Log.d(getClass().getSimpleName(), score.toString()));
                scoreResponse.addAll(scores);
                latch.countDown();
            }

            @Override
            public void OnErrorResponse(@Nullable String errorMsg) {
                Log.d(getClass().getSimpleName(), errorMsg);
                latch.countDown();
            }
        };

        helper.requestHighscores(listener);
        boolean resolved = latch.await(60, TimeUnit.SECONDS);
        assertTrue("request timed out!", resolved);
        assertNotEquals("could not retrieve score list!", 0, scoreResponse.size());
        scoreResponse.forEach(score -> Log.d(getClass().getSimpleName(), score.toString()));

    }
}