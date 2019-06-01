package online.madeofmagicandwires.trivial;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment implements GameActivity.GameView {

    private View root;


    public GameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GameFragment.
     */

    public static GameFragment newInstance() {
        GameFragment fragment = new GameFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("OnCreateView", "onCreateView has been called");
        root = inflater.inflate(R.layout.fragment_game, container, false);
        return root;
    }

    @Override
    public void showNextQuestion() {
        if(isAdded()) {
            // use runOnUiThread as this might not be called from the main thread.
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        getView().findViewById(R.id.game_fragment_placeholder).setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Log.e("showNextQuestion", "Fragment was not added to an Activity");
        }
    }
}
