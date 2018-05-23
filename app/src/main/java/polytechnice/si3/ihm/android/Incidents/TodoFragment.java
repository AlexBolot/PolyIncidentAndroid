package polytechnice.si3.ihm.android.Incidents;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

@SuppressWarnings("ConstantConditions")
public class TodoFragment extends Fragment {

    private static final String PROGRESS_TO_DISPLAY = "todo";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TodoFragment newInstance(int progressToDisplay) {
        TodoFragment fragment = new TodoFragment();
        Bundle args = new Bundle();
        args.putInt(PROGRESS_TO_DISPLAY, progressToDisplay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.todo_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() == null)
            throw new IllegalArgumentException("You must set the progress to display");

        GridView gridView = getActivity().findViewById(R.id.todo_gridView);
        IssueViewModel ivm = ViewModelProviders.of(this).get(IssueViewModel.class);
        IssueAdapter issueAdapter = new IssueAdapter(this.getContext(), new ArrayList<>());

        int progress = getArguments().getInt(PROGRESS_TO_DISPLAY);
        LiveData<List<Issue>> liveIssues = ivm.getAll();

        liveIssues.observeForever(issueList -> {
            if (issueList == null || issueList.isEmpty()) return;

            List<Issue> collect = issueList.stream().filter(issue -> issue.getProgressID() == progress).collect(Collectors.toList());

            checkIfPermissionRequired(collect);
            issueAdapter.setIssues(collect);
        });

        gridView.setAdapter(issueAdapter);
    }

    private void checkIfPermissionRequired(List<Issue> issues){
        boolean hasLocalImage = issues.stream().anyMatch(issue -> issue.getLinkToPreview().startsWith("content://"));

    }

    public void requirePermission(String permissionName) {
        if (ContextCompat.checkSelfPermission(getContext(), permissionName) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{permissionName}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        if (requestCode != 1) return;
        if (grantResults.length <= 0) return;
        if (grantResults[0] != PERMISSION_GRANTED) return;
    }
}
