package online.madeofmagicandwires.trivial.listeners;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;


/**
 * A simple gesture detector that recognizes swipe and click gestures
 *
 * @see "https://stackoverflow.com/a/12938787/8571352"
 */
abstract public class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

    public static final int SWIPE_DISTANCE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;

    /**
     * Called when a "fling" gesture has been registered by the detector.
     * Checks whether said fling was a swipe gesture, and in which cardinal it was made.
     *
     * @param e1 the start point of the fling gesture
     * @param e2 the end point of the fling gesture
     * @param velocityX the horizontal velocity of the fling gesture
     * @param velocityY the the vertical velocity of the fling gesture
     * @return whether the gesture should be consumed or registered by any other possible listeners
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean consumeEvent;
        float horizontalDistance  = e2.getX() - e1.getX();
        float verticalDistance    = e2.getY() - e1.getY();


        try{
            // if a fling passed the swipe threshold
            if(Math.abs(horizontalDistance) >= SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) >= SWIPE_VELOCITY_THRESHOLD ||
                    Math.abs(verticalDistance) >= SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) >= SWIPE_VELOCITY_THRESHOLD) {
                // swipe was horizontal
                if(Math.abs(horizontalDistance) > Math.abs(verticalDistance)) {
                    if(horizontalDistance > 0) {

                        OnSwipeRightEvent();
                    } else {
                        OnSwipeLeftEvent();
                    }
                }

                // swipe was vertical
                else {
                    // swipe was upwards
                    if(verticalDistance >  0) {
                        OnSwipeDownEvent();

                    }
                    // swipe was downwards
                    else {
                        OnSwipeUpEvent();
                    }
                }
                consumeEvent = true;
            } else {
                consumeEvent = super.onFling(e1, e2, velocityX, velocityY);
            }

        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "could not recognize fling gesture");
            e.printStackTrace();
            consumeEvent = false;
        }

        return consumeEvent;


    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return super.onSingleTapConfirmed(e);
    }

    abstract public void OnSwipeLeftEvent();

    abstract public void OnSwipeRightEvent();

    abstract public void OnSwipeUpEvent();

    abstract public void OnSwipeDownEvent();
}
