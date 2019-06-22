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
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        View answerBtn = v.findViewById(R.id.answer);
        if(answerBtn != null) {
            String answer = (String) answerBtn.getTag(R.id.answer_holder_tag);
            // get the chosen answer from the tag
            if(answer != null) {
                // if fragment and fragment question are available, notify the fragment
                if(fragment != null) {
                    fragment.userPickedAnswer(answer);
                }
            }

        } else {
            Log.e(getClass().getSimpleName(), "Could not find view of id R,id.answer!");
        }

    }

    public void setText(String text) {
        text = capitaliseString(text);
        if(itemView.findViewById(R.id.answer) != null) {
            ((Button) itemView.findViewById(R.id.answer)).setText(text);
        }
    }

    private static String capitaliseString(String text) {
        if(text != null && text.length() != 0) {
            String initialChar = text.substring(0,1).toUpperCase();
            text = initialChar + text.substring(1);
        }
        return text;
    }

    public void setClickListener() {
        if(itemView.findViewById(R.id.answer) != null) {
            itemView.findViewById(R.id.answer).setOnClickListener(this);
        }
    }

    public void setTag(Object tag) {
        if(itemView.findViewById(R.id.answer) != null) {
            itemView.findViewById(R.id.answer).setTag(tag);
        }
    }

    public void setTag(int key, Object value) {
        if(itemView.findViewById(R.id.answer) != null) {
            itemView.findViewById(R.id.answer).setTag(key, value);
        }
    }

}
