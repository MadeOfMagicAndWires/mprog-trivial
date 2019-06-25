package online.madeofmagicandwires.trivial.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import online.madeofmagicandwires.trivial.fragments.GameFragment;
import online.madeofmagicandwires.trivial.R;
import online.madeofmagicandwires.trivial.helpers.TriviaRequestHelper;
import online.madeofmagicandwires.trivial.listeners.SwipeGestureListener;
import online.madeofmagicandwires.trivial.models.TriviaGame;
import online.madeofmagicandwires.trivial.models.TriviaQuestion;




public class GameActivity extends AppCompatActivity implements
        GameFragment.OnUserFeedbackListener,
        TriviaRequestHelper.SessionTokenResponseListener,
        TriviaRequestHelper.QuestionResponseListener {


    // TODO: implement forward button/swipe
    private static class GameSwipeGestureListener extends SwipeGestureListener {

        private GameActivity activity;

        /**
         * Standard constructor
         * @param activity the activity handling the game
         */
        public GameSwipeGestureListener(@NonNull GameActivity activity) {
            this.activity = activity;
        }

        @Override
        public void OnSwipeLeftEvent() {
            Log.d(getClass().getSimpleName(), "Swipe left!");
            activity.askNextQuestion();
        }

        @Override
        public void OnSwipeRightEvent() {
            Log.d(getClass().getSimpleName(), "Swipe right!");
            activity.showPreviousQuestion();

        }

        @Override
        public void OnSwipeUpEvent() {
            // do nothing
        }

        @Override
        public void OnSwipeDownEvent() {
            // do nothing
        }
    }


    private TriviaGame game;
    private TriviaRequestHelper request;
    private GameFragment currentFragment;
    private boolean startedGame;

    private GestureDetectorCompat swipeDetector;

    /**
     * Called when the activity is started
     * @param savedInstanceState Bundle of data containing objects saved from the previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        OnCreateGameFragment();
        initTriviaGame();
        swipeDetector = new GestureDetectorCompat(getApplicationContext(),
                new GameSwipeGestureListener(this));
    }


    /**
     * Initiates the TriviaGame and TriviaRequestHelper instances
     */
    public void initTriviaGame(){
        game = new TriviaGame(10, TriviaGame.Difficulty.ANY, TriviaGame.QuestionType.ANY);
        request = TriviaRequestHelper.getInstance(getApplicationContext(), game);
        request.requestSessionToken(this);
    }


    /**
     * Creates a {@link GameFragment} and adds it to this activity in View {@link R.id#game_fragment}
     */
    public void OnCreateGameFragment() {
        currentFragment = GameFragment.newInstance();

        // replace fragment
        FragmentTransaction changes = getSupportFragmentManager().beginTransaction();
        changes.replace(R.id.game_fragment, currentFragment, GameFragment.GAME_FRAGMENT_TAG);
        changes.commit();

        // make sure changes are committed NOW, not "later".
        getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * Actually start playing the game
     */
    public void startGame() {
        if(!startedGame) {
            // if no fragment is running, add it, then ask the first question
            Fragment gameFrag = getSupportFragmentManager().findFragmentById(R.id.game_fragment);
            if(!(gameFrag instanceof GameFragment)) {
                OnCreateGameFragment();
                if(currentFragment != null) {
                    startGame();
                }
            } else {
                ((GameFragment) gameFrag).setQuestion(game.getQuestion(0));
                Log.d(getClass().getSimpleName(), "Started game!");
                startedGame = true;
            }
        }
    }


    /**
     * Asks the next question to the user, updating the fragment or requesting more
     * questions as needed
     */
    @SuppressWarnings("ConstantConditions")
    public void askNextQuestion() {
        // next question is last; either get more or end the game after the next one
        Log.d(getClass().getSimpleName(), "Question " + (game.getQuestionIndex()+2) + "/" + game.getQuestionAmount());

        // update fragment, requesting more questions as needed
        if(game != null && currentFragment != null) {
            try {
                game.nextQuestion();
                currentFragment.setQuestion(game.getCurrentQuestion());
            } catch (IndexOutOfBoundsException e) {
                // not enough questions retrieved yet, request more
                if(game.getQuestionIndex() != game.getQuestionAmount()) {
                    currentFragment.togglePlaceholderView(View.VISIBLE);
                    int questionsLeft = (game.getQuestionAmount()) - game.getQuestionIndex();
                    // request missing question amount, max amount of 50
                    request.requestQuestions((questionsLeft < 50) ? questionsLeft : 50, this);
                } else {
                    if(currentFragment.isAdded()) {
                        Snackbar sb = Snackbar.make(currentFragment.getView(),
                                R.string.no_next_question_error_msg, Snackbar.LENGTH_LONG);
                        sb.show();
                    }
                    Log.w(getClass().getSimpleName(), getString(R.string.no_next_question_error_msg));
                }
            } catch (NullPointerException e) {
                currentFragment.togglePlaceholderView(View.VISIBLE);
                request.requestQuestions(game.getQuestionAmount(), this);
            }
        }
    }

    /**
     * Displays the previous question in the current GameFragment
     */
    public void showPreviousQuestion() {
        // TODO: add transition
        if(startedGame && game != null && currentFragment != null) {
            if(game.getQuestionIndex() != 0) {
                game.setQuestionIndex(game.getQuestionIndex()-1);
                currentFragment.setQuestion(game.getCurrentQuestion());
            } else {
                if(currentFragment != null && currentFragment.getView() != null) {
                    Snackbar sb = Snackbar.make(
                            currentFragment.getView(),
                            R.string.no_prev_question_error_msg,
                            Snackbar.LENGTH_LONG);
                    sb.show();
                }
                Log.w(getClass().getSimpleName(), getString(R.string.no_prev_question_error_msg));
            }
        }
    }

    @Override
    public void OnRequestNextQuestion() {
        askNextQuestion();
    }

    /**
     * Called when user has inputted an answer to a question
     * @param gotRightAnswer true when the user picked the correct answer to the question,
     *                       false if not
     */
    @Override
    public void OnUserPickedAnswer(boolean gotRightAnswer) {
        double score = calculateScore(gotRightAnswer);
        game.addScore(score);

        // game is in progress
        if(!game.isGameOver()) {
            if(game.getQuestionIndex() == game.getQuestionAmount()-2) {
                // ARCADE MODE; request more questions
                if(game.getQuestionAmount() == 0) {
                    currentFragment.togglePlaceholderView(View.VISIBLE);
                    request.requestQuestions(50, this);
                }
                // last question; update game state
                else {
                    // set game state to game over
                    // (game will be ended only after this last question has been answered)
                    game.setGameOver(true);
                }
            }

            // ask the next question
            askNextQuestion();
        }

        // Game over, close fragment and open highscore activity
        else {
            Intent intent = new Intent(this, HighscoresActivity.class);
            intent.putExtra(HighscoresActivity.HIGHSCORE_INTENT_SCORE_TAG, game.getScore());
            startActivity(intent);
        }
    }

    public double calculateScore(boolean correctAnswer) {
        double score;
        // add a score modifier based on difficulty,
        // rewarding or punishing more/less for higher difficulties
        // it's therefore more rewarding to answer higher difficulty questions
        switch (game.getCurrentQuestion().getDifficulty()) {
            case TriviaGame.Difficulty.EASY:
                score = (correctAnswer) ? 5 : -5;
                break;
            case TriviaGame.Difficulty.MEDIUM:
                score = (correctAnswer) ? 10 : -2.5;
                break;
            case TriviaGame.Difficulty.HARD:
                score = (correctAnswer) ? 20 : -0.75;
                break;
            default:
                score = 0;
                break;
        }
        //  score is then multiplied by questionIndex if a combo is running to encourage
        //  it's therefore more rewarding to answer answers correctly consecutively
        //  and doing so for longer (thus favouring longer games doubly).
        //  It also increases the risk however since when you break a combo, the combo amount will
        //  now be multiplied with a negative difficulty modifier.
        score *= (game.getCombo() > 0) ? game.getCombo() : 1;
        if(game.hasCombo() && correctAnswer) {
            game.increaseCombo();
        } else {
            if(game.hasCombo()) {
                game.breakCombo();
            }
        }

        return score;
    }


    /**
     * called when a TriviaDB session request has been successfully requeste
     *
     * @param token the session token retrieved
     */
    @Override
    public void OnRequestTokenResponse(String token) {
        // token is saved in instance, just log it
        Log.i(getClass().getSimpleName(), "Successfully retrieved session token '" + token + "'");

        // once we've got a token, request questions
        request.requestQuestions(game.getQuestionAmount(), this);
    }

    @Override
    public void OnResetTokenResponse(String token) {
        Log.i(getClass().getSimpleName(), "Successfully reset session token '" + token + "'");

    }

    /**
     * Called when a TriviaDB question request successfully resolved
     *
     * @param questions a {@link List} of {@link TriviaQuestion} objects
     *                  representing the trivia questions that were retrieved from the TriviaDB
     * @see TriviaRequestHelper#requestQuestions(int, TriviaRequestHelper.QuestionResponseListener)
     */
    @Override
    public void OnQuestionsResponse(List<TriviaQuestion> questions) {
        Log.d(getClass().getSimpleName(), "Resolved question request");
        game.setQuestions(questions);

        // if the game hasn't started yet, start it
        if(!startedGame) {
            startGame();
        }
    }

    /**
     * called when an error occurs during a request to the OpenTrivia API
     *
     * @param lastRequest the endpoint of the request;
     *                    note that this might not be entirely accurate due to async requests
     * @param errorMsg    the error message included.
     */
    @Override
    public void OnErrorResponse(String lastRequest, @Nullable String errorMsg) {
        Log.e(getClass().getSimpleName(), "request to " + lastRequest + " failed!");
        if(errorMsg != null) {
            Log.e(getClass().getSimpleName(), errorMsg);
        }
        // TODO: switch case based on lastRequest
        switch (lastRequest) {
            case TriviaRequestHelper.EndPoint.TRIVIA:
                // something went wrong!
                // TODO: properly convey the error to the user.
                if(!startedGame) {
                    FragmentTransaction changes = getSupportFragmentManager().beginTransaction();
                    changes.remove(currentFragment);
                    changes.commit();
                }
                default:
                    Log.e(lastRequest, errorMsg);
        }
    }

    /**
     * Shows the previous question when user navigates back;
     * or closes the application when on the first question
     */
    @Override
    public void onBackPressed() {
        if(game.getQuestionIndex() != 0) {
            showPreviousQuestion();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Called when a touch screen event was not handled by any of the views
     * under it.  This is most useful to process touch events that happen
     * outside of your window bounds, where there is no view to receive it.
     *
     * @param event The touch screen event being processed.
     * @return Return true if you have consumed the event, false if you haven't.
     * The default implementation always returns false.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.swipeDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
