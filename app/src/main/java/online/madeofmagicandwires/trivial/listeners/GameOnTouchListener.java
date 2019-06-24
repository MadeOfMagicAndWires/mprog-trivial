package online.madeofmagicandwires.trivial.listeners;

import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import online.madeofmagicandwires.trivial.fragments.GameFragment;

/**
 * Preforms various {@link online.madeofmagicandwires.trivial.fragments.GameFragment}-related
 * actions for various touch events.
 */
public class GameOnTouchListener implements View.OnTouchListener {

    /**
     * requests the previous/next Trivia Questions on horizontal swipes
     */
    private static class GameSwipeDetector extends SwipeGestureDetector {

        @Override
        public boolean OnSwipeLeftEvent() {
            Log.d(getClass().getSimpleName(), "Swipe left!");
            return true;
        }

        @Override
        public boolean OnSwipeRightEvent() {
            Log.d(getClass().getSimpleName(), "Swipe Right!");
            return true;
        }

        @Override
        public boolean OnSwipeUpEvent() {
            Log.d(getClass().getSimpleName(), "Swipe up!");
            return true;
        }

        @Override
        public boolean OnSwipeDownEvent() {
            Log.d(getClass().getSimpleName(), "Swipe down!");
            return true;
        }
    }

    private final GestureDetectorCompat detector;
    private final GameFragment fragment;

    /**
     * Standard constructor
     * @param fragment the gamefragment this event listener will be linked to
     */
    public GameOnTouchListener(GameFragment fragment) {
        this.detector = new GestureDetectorCompat(fragment.getContext(), new GameSwipeDetector());
        this.fragment = fragment;
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //TODO call v.preformclick if event is a click!
        Log.d(getClass().getSimpleName(), "OnTouch Event!");
        detector.onTouchEvent(event);
        return true;

    }
}
