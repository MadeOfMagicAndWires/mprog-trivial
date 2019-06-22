package online.madeofmagicandwires.trivial.adapters;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import online.madeofmagicandwires.trivial.R;
import online.madeofmagicandwires.trivial.fragments.GameFragment;
import online.madeofmagicandwires.trivial.models.MultipleChoiceQuestion;
import online.madeofmagicandwires.trivial.models.TriviaQuestion;

public class AnswerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private @Nullable GameFragment fragment;

    /**
     * Constructor
     * @param itemView the root view of the viewholder. should contain {@link Button} of
     *                 id {@link R.id#answer}.
     */
    public AnswerViewHolder(View itemView) {
        super(itemView);
    }

    public AnswerViewHolder(View root, GameFragment fragment) {
        super(root);
        this.fragment = fragment;
    }

    /**
     * Called when an answer viewholder has been clicked and lets the parent fragment know
     * of this interaction.
     *
     * @param v the answer viewholder that has been clicked
     */
    @Override
    public void onClick(View v) {
        View answerBtn = v.findViewById(R.id.answer);
        if(answerBtn != null) {
            String answer = (String) answerBtn.getTag(R.id.answer_holder_tag);
            // get the chosen answer from the tag
            if(answer != null) {
                // if fragment is available either answer the question if it is not answered already,
                // or unceremoniously move on to the next question
                if(fragment != null) {
                    if(!fragment.getQuestion().isAnswered()) {
                        fragment.userPickedAnswer(answer);
                    } else {
                        fragment.requestNextQuestion();
                    }
                }
            }

        } else {
            Log.e(getClass().getSimpleName(), "Could not find view of id R,id.answer!");
        }

    }

    /**
     * Sets the text of the MaterialButton representing the answer, capitalizing the first letter
     * @param text
     */
    public void setText(String text) {
        text = capitaliseString(text);
        if(itemView.findViewById(R.id.answer) != null) {
            ((Button) itemView.findViewById(R.id.answer)).setText(text);
        }
    }

    /**
     * Capitalizes the first letter of a string
     * @param text the string in all lowercaps
     * @return string containing the same data as was input, but now the first letter is capitalized
     */
    private static String capitaliseString(String text) {
        if(text != null && text.length() != 0) {
            String initialChar = text.substring(0,1).toUpperCase();
            text = initialChar + text.substring(1);
        }
        return text;
    }

    /**
     * Sets the click listener of the MaterialButton representing the answer
     */
    public void setClickListener() {
        if(itemView.findViewById(R.id.answer) != null) {
            itemView.findViewById(R.id.answer).setOnClickListener(this);
        }
    }

    /**
     * Sets the tag of the MaterialButton representing the answer
     * @param tag the object to link to the answer view
     */
    public void setTag(Object tag) {
        if(itemView.findViewById(R.id.answer) != null) {
            itemView.findViewById(R.id.answer).setTag(tag);
        }
    }

    /**
     * Sets the tag of the MaterialButton representing the answer, under a specific key
     * @param key the specific key to save the tag object under
     * @param value the specific object to link to the answer view
     */
    public void setTag(int key, Object value) {
        if(itemView.findViewById(R.id.answer) != null) {
            itemView.findViewById(R.id.answer).setTag(key, value);
        }
    }

}
