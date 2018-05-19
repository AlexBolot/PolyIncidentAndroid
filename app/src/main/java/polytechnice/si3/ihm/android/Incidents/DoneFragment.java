package polytechnice.si3.ihm.android.Incidents;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import polytechnice.si3.ihm.android.R;
import polytechnice.si3.ihm.android.database.model.Issue;
import polytechnice.si3.ihm.android.database.viewmodel.IssueViewModel;

@SuppressWarnings("ConstantConditions")
public class DoneFragment extends Fragment {

    private static final String PROGRESS_TO_DISPLAY = "progress";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DoneFragment newInstance(int progressToDisplay) {
        DoneFragment fragment = new DoneFragment();
        Bundle args = new Bundle();
        args.putInt(PROGRESS_TO_DISPLAY, progressToDisplay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.done_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() == null)
            throw new IllegalArgumentException("You must set the progress to display");

        GridView gridView = getActivity().findViewById(R.id.done_gridView);

        int display = getArguments().getInt(PROGRESS_TO_DISPLAY);

        IssueViewModel ivm = ViewModelProviders.of(this).get(IssueViewModel.class);

        LiveData<List<Issue>> liveIssues = ivm.getAll();

        IssueAdapter issueAdapter = new IssueAdapter(this.getContext(), new ArrayList<>());

        liveIssues.observe(this, issueList -> {
            List<Issue> collect = issueList.stream().filter(issue -> issue.getProgressID() == display).collect(Collectors.toList());
            issueAdapter.clear();
            issueAdapter.addAll(collect);
        });

        gridView.setAdapter(issueAdapter);
    }
}
