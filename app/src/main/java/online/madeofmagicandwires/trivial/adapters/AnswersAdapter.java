package online.madeofmagicandwires.trivial.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import online.madeofmagicandwires.trivial.adapters.AnswerViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import online.madeofmagicandwires.trivial.R;
import online.madeofmagicandwires.trivial.fragments.GameFragment;

public class AnswersAdapter extends RecyclerView.Adapter<AnswerViewHolder> {

    private @Nullable GameFragment fragment;
    private LayoutInflater inflater;
    private @LayoutRes int layoutRes;

    private List<String> answers;

    /**
     * The standard constructor
     * @param c the activity context needed for a {@link LayoutInflater}
     * @param layoutRes the resource id of the layout file for all child elements
     * @param answers the  trivia question answer to represent as child elements
     */
    public AnswersAdapter(Context c, @LayoutRes int layoutRes, List<String> answers) {
        this(LayoutInflater.from(c), layoutRes, answers);
    }


    /**
     * Alternative constructor requiring a {@link LayoutInflater} instead of a {@link Context}
     * @param inflater the layout inflator to use within this adapter
     * @param layoutRes the resource id of the layout file for all child elements
     * @param answers the  trivia question answer to represent as child elements
     */
    public AnswersAdapter(LayoutInflater inflater, @LayoutRes int layoutRes, List<String> answers) {
        this.inflater = inflater;
        this.layoutRes = layoutRes;
        this.answers = answers;
    }


    public AnswersAdapter(GameFragment fragment, @LayoutRes int layoutRes, List<String> answers) {
        this(LayoutInflater.from(fragment.getContext()), layoutRes, answers);
        this.fragment = fragment;
    }

    /**
     * Creates an AnswerViewHolder object with the correct root view inflated
     * @param parent the parent view the viewholder will eventually be attached to
     * @param i the position of the viewholder in the order of things
     * @return returns a new ViewHolder containing the correct (empty) view
     */
    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View vH;
        if(layoutRes != 0) {
            vH = inflater.inflate(layoutRes, parent, false);
        } else {
            vH = inflater.inflate(R.layout.question_answer_tile, parent, false);
        }
        if(fragment != null) {
            return new AnswerViewHolder(vH, fragment);
        } else  {
            return new AnswerViewHolder(vH);
        }
    }

    /**
     * Binds the answer data to the view
     * @param answerViewHolder the viewholder object to bind data to;
     *                         must contain a view with the id {@link R.id#answer}
     * @param i the array index of the answer to bind to the viewholder
     */
    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder answerViewHolder, int i) {
        Button root = answerViewHolder.itemView.findViewById(R.id.answer);
        if(root != null && answers != null && i <= getItemCount()) {
            answerViewHolder.setText(answers.get(i));
            answerViewHolder.setTag(R.id.answer_holder_tag, answers.get(i));
            answerViewHolder.setClickListener();
        }
    }

    /**
     * Returns the amount of items
     * @return the total amount of items to represent as child elements
     *         or 0 if there are no items to represent
     */
    @Override
    public int getItemCount() {
        if(answers != null) {
            return answers.size();
        } else {
            return 0;
        }
    }

    /**
     * Updates the answer list to represent
     * @param newAnswers the list of new answers to represent
     */
    public void setAnswers(List<String> newAnswers) {
        if(this.answers != newAnswers) {
            this.answers = newAnswers;
            notifyDataSetChanged();
        }
    }
}
