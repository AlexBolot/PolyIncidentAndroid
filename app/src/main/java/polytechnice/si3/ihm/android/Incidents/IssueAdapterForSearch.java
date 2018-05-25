package polytechnice.si3.ihm.android.Incidents;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import polytechnice.si3.ihm.android.R;
import polytechnice.si3.ihm.android.database.model.Category;
import polytechnice.si3.ihm.android.database.model.Importance;
import polytechnice.si3.ihm.android.database.model.Issue;
import polytechnice.si3.ihm.android.database.model.Progress;
import polytechnice.si3.ihm.android.database.model.User;
import polytechnice.si3.ihm.android.database.viewmodel.CategoryViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.ImportanceViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.IssueViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.ProgressViewModel;

import static android.view.View.GONE;

public class IssueAdapterForSearch extends ArrayAdapter<Issue> {
    private final static String TAG = "IssueAdapterForSearch";
    private List<Issue> issues;
    private Context context;
    private final ProgressViewModel progressViewModel;
    private final ImportanceViewModel importanceViewModel;
    private CategoryViewModel categoryViewModel;

    private View swippedTitle;
    private float xStart;
    private float yStart;
    private boolean canBeClic = false;

    private float totalDx;
    private float totalDy;

    private User userConnected;
    private IssueViewModel issueViewModel;


    public IssueAdapterForSearch(@NonNull Context context, @NonNull List<Issue> issues,
                                 IssueViewModel issueViewModel, User userConnected,
                                 ProgressViewModel progressViewModel, ImportanceViewModel importanceViewModel,
                                 CategoryViewModel categoryViewModel) {
        super(context, 0, issues);
        this.issueViewModel = issueViewModel;
        this.userConnected = userConnected;
        this.issues = issues;
        this.context = context;
        this.progressViewModel = progressViewModel;
        this.importanceViewModel = importanceViewModel;
        this.categoryViewModel = categoryViewModel;
    }

    @NonNull
    @Override
    public View getView(int index, @Nullable View view, @NonNull ViewGroup parent) {
        if (userConnected == null && view != null)
            view.setVisibility(GONE);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.incident_item_search, null);
            setUpView(view, index);
        }
        return view;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void setUpView(View view, int indexOfInc) {
        Issue issue = this.getItem(indexOfInc);
        if (issue == null) return;

        TextView title = view.findViewById(R.id.inc_title2);
        title.setText(issue.getTitle());

        TextView progressT = view.findViewById(R.id.progress);
        Progress progress = progressViewModel.getByID(issue.getProgressID()).orElse(null);
        if (progress != null)
            progressT.setText(progress.getLabel());
        else
            progressT.setText("Progression inconnue");

        TextView importanceT = view.findViewById(R.id.importance);
        Importance importance = importanceViewModel.getByID(issue.getImportanceID()).orElse(null);
        if (importance != null)
            importanceT.setText(importance.getLabel());
        else
            importanceT.setText("Progression inconnue");

        TextView categoryT = view.findViewById(R.id.category);
        Category category = categoryViewModel.getByID(issue.getCategoryID()).orElse(null);
        if (category != null)
            categoryT.setText(category.getLabel());
        else
            categoryT.setText("Progression inconnue");


        //region ===== Init action depending on if you're admin or not ====
        View adminsButton = view.findViewById(R.id.adminButtonsLayout);
        if (userConnected.isAdmin()) {
            //Listener for admin

            //region ==== Swipe ====
            title.setOnTouchListener((v, event) -> {
                if (v == null)
                    return false;

                //if we up the touch, we determine if this is a clic or a swipe
                if (event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_HOVER_EXIT
                        || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    if (totalDx < 50)
                        swippedTitle.animate().x(0).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
                    Log.d(TAG + "_swipeMenu", "Swipe stopped");
                    if (Math.abs(event.getX() - xStart) < 30) {
                        Log.d(TAG + "_swipeMenu", "Just clicked on desc");
                        if (swippedTitle == v && !canBeClic) {
                            Log.d(TAG + "_swipeMenu", "Reset the swippedTitle");
                            swippedTitle.animate().x(0).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
                            return true;
                        } else if (canBeClic) {
                            //region ========== Clic handler ======
                            Intent intent = new Intent(this.getContext(), IssueDetailsView.class);
                            issue.feedIntent(intent);
                            view.getContext().startActivity(intent);
                            //endregion
                        }
                    }
                    swippedTitle = null;
                    return false;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (swippedTitle != null && swippedTitle != v)
                        swippedTitle.animate().x(0)
                                .setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
                    xStart = event.getX();
                    yStart = event.getY();
                    totalDx = 0;
                    totalDy = 0;
                    Log.d(TAG + "_swipeMenu", "x start : " + xStart);
                    swippedTitle = v;
                    canBeClic = true;
                }
                //if this is a move, we do the translation
                else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (canBeClic && (totalDx > 50 || totalDy > 10) || v.getX() > 100)
                        canBeClic = false;
                    float dx = event.getX() - xStart;
                    Log.d(TAG + "_swipeMenu", "Moved, swipe ?");
                    totalDx += dx;
                    totalDy += Math.abs(event.getY() - yStart);
                    //if we go to right
                    if (dx > 3 && v.getX() + dx <= adminsButton.getWidth()) {
                        if (totalDx < 50) {
                            v.animate().x(v.getX() + dx).setDuration(0).start();
                            Log.d(TAG + "_swipeMenu", "swipe done, dx = " + dx);
                        } else if (totalDx > 0) {
                            v.animate().x(adminsButton.getWidth())
                                    .setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
                        } else {
                            Log.d(TAG + "_swipeMenu", "TRIGGGGGGGGGGGGGGGER");
                        }
                    }
                    //if we go to left
                    else if (dx < -3 && v.getX() + dx >= 0) {
                        if (v.getX() + dx >= 0 && totalDx < -20) {
                            v.animate().x(v.getX() + dx).setDuration(0).start();
                        } else {
                            v.animate().x(0)
                                    .setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
                        }
                    }
                }
                return true;
            });

            //endregion

            //region ==== Buttons ==
            Button delete = view.findViewById(R.id.deleteButton_search);
            Button edit = view.findViewById(R.id.editButton_search);

            delete.setOnClickListener(v -> {
                Log.d(TAG, "Delete elem : " + issue);
                issueViewModel.delete(issue);
                this.remove(issue);
            });
            edit.setOnClickListener(v -> {
                Log.d(TAG, "Edit elem : " + issue);
            });

            //endregion
        } else {
            adminsButton.setVisibility(View.GONE);
            title.setOnClickListener(v -> {
                Intent intent = new Intent(this.getContext(), IssueDetailsView.class);
                issue.feedIntent(intent);
                view.getContext().startActivity(intent);
            });
        }
        //endregion
    }

    @Override
    public Issue getItem(int index) {
        return issues.get(index);
    }

    public void setIssues(List<Issue> issues) {
        this.clear();
        this.addAll(issues);
    }
}
