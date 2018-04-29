package polytechnice.si3.ihm.android.Incidents;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

import polytechnice.si3.ihm.android.R;

public class IncidentGridFragment extends Fragment {

    private static final String PROGRESS_TO_DISPLAY = "progress";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static IncidentGridFragment newInstance(int progressToDisplay) {
        IncidentGridFragment fragment = new IncidentGridFragment();
        Bundle args = new Bundle();
        Log.d("IncidentGridFragment", "Set PROGRESS_TO_DISPLAY to " + Integer.toString(progressToDisplay));
        args.putInt(PROGRESS_TO_DISPLAY, progressToDisplay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news_grid, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() == null)
            throw new IllegalArgumentException("You must set the progress to display");

        IncidentDBAccess db = new IncidentDBAccess(getContext());

        List<Incident> incidents;

        int display = getArguments().getInt(PROGRESS_TO_DISPLAY);
        Log.d("IncidentGridFragment",
                "load PROGRESS_TO_DISPLAY : " + getArguments().getInt(PROGRESS_TO_DISPLAY));
        switch (display) {
            case 1:
                incidents = db.getAllIncTODO();
                break;
            case 2:
                incidents = db.getAllIncIP();
                break;
            case 3:
                incidents = db.getAllIncDone();
                break;
            default:
                throw new IllegalArgumentException("The progress should be between 1 and 3 (included)");
        }
        //TODO Determine what progress to display

        Log.d("IncidentGridFragment",
                "PROGRESS_TO_DISPLAY : " + getArguments().getInt(PROGRESS_TO_DISPLAY) + '\n' +
                        "Display list(" + incidents.size() + ") : " + incidents);


        GridView gridView = getActivity().findViewById(R.id.gridView);
        gridView.setAdapter(new IncidentAdapter(this.getContext(), incidents));

        db.close();
    }
}
