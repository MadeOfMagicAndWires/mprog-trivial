package online.madeofmagicandwires.trivial.listeners;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * A simple gesture detector that recognizes swipe and click gestures
 */
abstract public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

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
        Log.d(getClass().getSimpleName(), "OnFling was called!");
        boolean consumeEvent;
        float horizontalDistance  = e2.getX() - e1.getX();
        float verticalDistance    = e2.getY() - e1.getY();

        // if a fling passed the swipe threshold
        if((Math.abs(horizontalDistance) >= SWIPE_DISTANCE_THRESHOLD || Math.abs(verticalDistance) >= SWIPE_DISTANCE_THRESHOLD) ||
                (velocityX >= SWIPE_VELOCITY_THRESHOLD || velocityY >= SWIPE_VELOCITY_THRESHOLD)) {
            // swipe was horizontal
            if(Math.abs(horizontalDistance) > Math.abs(verticalDistance)) {
                if(horizontalDistance > 0) {
                    consumeEvent = OnSwipeRightEvent();
                } else {
                    consumeEvent = OnSwipeLeftEvent();
                }
            }

            // swipe was vertical
            else {
                // swipe was upwards
                if(verticalDistance >  0) {
                    consumeEvent = OnSwipeDownEvent();

                }
                // swipe was downwards
                else {
                    consumeEvent = OnSwipeUpEvent();
                }
            }
        } else {
            consumeEvent = super.onFling(e1, e2, velocityX, velocityY);
        }

        return consumeEvent;


    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return super.onSingleTapConfirmed(e);
    }

    abstract public boolean OnSwipeLeftEvent();

    abstract public boolean OnSwipeRightEvent();

    abstract public boolean OnSwipeUpEvent();

    abstract public boolean OnSwipeDownEvent();
}
