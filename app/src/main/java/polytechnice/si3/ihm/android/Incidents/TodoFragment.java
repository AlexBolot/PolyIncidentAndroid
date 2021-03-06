package polytechnice.si3.ihm.android.Incidents;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import polytechnice.si3.ihm.android.CustomViewPager;
import polytechnice.si3.ihm.android.R;
import polytechnice.si3.ihm.android.database.model.Issue;
import polytechnice.si3.ihm.android.database.model.User;
import polytechnice.si3.ihm.android.database.viewmodel.IssueViewModel;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

@SuppressWarnings("ConstantConditions")
public class TodoFragment extends Fragment {

    private static final String PROGRESS_TO_DISPLAY = "todo";
    private static final String TAG = "TodoFragment";
    private static Runnable globalUpdateRunnable;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TodoFragment newInstance(int progressToDisplay, User userConnected, Runnable globalUpdateRunnable) {
        TodoFragment.globalUpdateRunnable = globalUpdateRunnable;
        TodoFragment fragment = new TodoFragment();
        Bundle args = new Bundle();
        args.putInt(PROGRESS_TO_DISPLAY, progressToDisplay);
        userConnected.feedArgs(args);
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

        CustomViewPager viewPager = getActivity().findViewById(R.id.container);
        GridView gridView = getActivity().findViewById(R.id.todo_gridView);
        IssueViewModel ivm = ViewModelProviders.of(this).get(IssueViewModel.class);

        User userConnected = new User(getArguments());
        IssueAdapter issueAdapter = new IssueAdapter(this.getContext(), new ArrayList<>(), viewPager, ivm, userConnected, globalUpdateRunnable);

        int progress = getArguments().getInt(PROGRESS_TO_DISPLAY);
        LiveData<List<Issue>> liveIssues = ivm.getByProgress(progress);

        liveIssues.observeForever(issueList -> {
            if (issueList == null || issueList.isEmpty()) return;

            List<Integer> collect = issueList.stream().map(Issue::getId).collect(Collectors.toList());

            Log.d("LIST_TODO", collect.toString());
            checkPermission(issueList);
            issueAdapter.setIssues(issueList);
        });

        gridView.setAdapter(issueAdapter);
    }

    private void checkPermission(List<Issue> issues) {
        boolean hasLocalImage = issues.stream().anyMatch(issue -> issue.getLinkToPreview().startsWith("content://"));

        if (hasLocalImage && ActivityCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{READ_EXTERNAL_STORAGE}, 1);
        }
    }
}
