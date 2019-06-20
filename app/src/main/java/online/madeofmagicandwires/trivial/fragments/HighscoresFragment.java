package online.madeofmagicandwires.trivial.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import online.madeofmagicandwires.trivial.R;


public class HighscoresFragment extends Fragment {

    public static HighscoresFragment newInstance() {
        return new HighscoresFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.highscores_fragment, container, false);
    }

}
