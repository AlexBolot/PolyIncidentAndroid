package polytechnice.si3.ihm.android.Incidents;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.InputStream;
import java.util.List;

import polytechnice.si3.ihm.android.CustomViewPager;
import polytechnice.si3.ihm.android.R;
import polytechnice.si3.ihm.android.SinglePlayVideoView;
import polytechnice.si3.ihm.android.SinglePlayVideoView.PlayPauseListener;
import polytechnice.si3.ihm.android.database.model.Issue;
import polytechnice.si3.ihm.android.database.model.User;
import polytechnice.si3.ihm.android.database.viewmodel.IssueViewModel;

public class IssueAdapter extends ArrayAdapter<Issue> {
    private final static String TAG = "IssueAdapter";
    private final IssueViewModel issueViewModel;
    private List<Issue> issues;
    private Context context;

    private VideoView playing;

    private View swippedDesc;
    private float xStart;
    private float yStart;
    private boolean canBeClic = false;

    private float totalDx;
    private float totalDy;

    private CustomViewPager viewPager;
    private User userConnected;


    public IssueAdapter(@NonNull Context context, @NonNull List<Issue> issues, CustomViewPager viewPager,
                        IssueViewModel issueViewModel, User userConnected) {
        super(context, 0, issues);
        this.userConnected = userConnected;
        Log.d(TAG, "Create incident adapter with " + issues.toString());
        this.issues = issues;
        this.context = context;
        this.viewPager = viewPager;
        this.issueViewModel = issueViewModel;
    }

    @NonNull
    @Override
    public View getView(int index, @Nullable View view, @NonNull ViewGroup parent) {
        if (userConnected == null && view != null)
            view.setVisibility(View.GONE);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.incident_item, null);
            setUpView(view, index);
        }
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpView(View view, int indexOfInc) {
        Issue issue = this.getItem(indexOfInc);
        if (issue == null) return;
        TextView meta = view.findViewById(R.id.inc_categorie);
        StringBuilder metas = new StringBuilder(issue.getCategoryID());
        meta.append(" ");
        metas.append(issue.getDate());
        meta.setText(metas);

        TextView title = view.findViewById(R.id.inc_title);
        title.setText(issue.getTitle());

        TextView description = view.findViewById(R.id.inc_description);

        //Shorten description and display the "show more"
        int maxLength = 100;
        if (issue.getDescription().length() > maxLength) {
            int i = maxLength;
            while (issue.getDescription().charAt(i) != ' '
                    && issue.getDescription().charAt(i) != '\n'
                    && issue.getDescription().charAt(i) != '\t')
                ++i;
            StringBuilder desc =
                    new StringBuilder(issue.getDescription().substring(0, i)).append(" ...");
            description.setText(desc);
            ImageView showMoreImg = view.findViewById(R.id.showMoreImg);
            showMoreImg.setVisibility(View.VISIBLE);
        } else {
            description.setText(issue.getDescription());
        }

        //region ===== Init action depending on if you're admin or not ====
        View adminsButton = view.findViewById(R.id.adminButtonsLayout);
        if (userConnected.isAdmin()) {
            //Listener for admin

            //region ==== Swipe ====
            description.setOnTouchListener((v, event) -> {
                if (v == null)
                    return false;

                //if we up the touch, we determine if this is a clic or a swipe
                if (event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_HOVER_EXIT
                        || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    viewPager.setPagingEnabled(true);
                    if (totalDx < 50)
                        swippedDesc.animate().x(0).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
                    Log.d(TAG + "_swipeMenu", "Swipe stopped");
                    if (Math.abs(event.getX() - xStart) < 30) {
                        Log.d(TAG + "_swipeMenu", "Just clicked on desc");
                        if (swippedDesc == v && !canBeClic) {
                            Log.d(TAG + "_swipeMenu", "Reset the swippedDesc");
                            swippedDesc.animate().x(0).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
                            return true;
                        } else if (canBeClic) {
                            //region ========== Clic handler ======
                            Intent intent = new Intent(this.getContext(), IssueDetailsView.class);
                            issue.feedIntent(intent);
                            view.getContext().startActivity(intent);
                            //endregion
                        }
                    }
                    return false;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    viewPager.setPagingEnabled(false);
                    if (swippedDesc != null && swippedDesc != v)
                        swippedDesc.animate().x(0)
                                .setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
                    xStart = event.getX();
                    yStart = event.getY();
                    totalDx = 0;
                    totalDy = 0;
                    Log.d(TAG + "_swipeMenu", "x start : " + xStart);
                    swippedDesc = v;
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
            ImageButton moveBackward = view.findViewById(R.id.moveBackwardButton);
            ImageButton moveForward = view.findViewById(R.id.moveForwardButton);
            ImageButton delete = view.findViewById(R.id.deleteButton);
            ImageButton edit = view.findViewById(R.id.editButton);

            if (issue.getProgressID() == 1) {
                moveBackward.setVisibility(View.GONE);
            } else {
                moveBackward.setOnClickListener(v -> {
                    Log.d(TAG, "Backward elem : " + issue);
                    issueViewModel.updateProgress(issue, issue.getProgressID() - 1);
                    issue.setProgressID(issue.getProgressID() - 1);
                });
            }

            if (issue.getProgressID() == 3) {
                moveForward.setVisibility(View.GONE);
            } else {
                moveForward.setOnClickListener(v -> {
                    Log.d(TAG, "Forward elem : " + issue);
                    issueViewModel.updateProgress(issue, issue.getProgressID() + 1);
                    issue.setProgressID(issue.getProgressID() + 1);
                });
            }

            delete.setOnClickListener(v -> {
                Log.d(TAG, "Delete elem : " + issue);
                issueViewModel.delete(issue);
                this.remove(issue);
//            issues.remove(issue);
            });
            edit.setOnClickListener(v -> {
                Log.d(TAG, "Edit elem : " + issue);
            });

            //endregion
        } else {
            adminsButton.setVisibility(View.GONE);
            description.setOnClickListener(v -> {
                Intent intent = new Intent(this.getContext(), IssueDetailsView.class);
                issue.feedIntent(intent);
                view.getContext().startActivity(intent);
            });
        }
        //endregion
        ImageView imageView = view.findViewById(R.id.inc_thumbnail);
        SinglePlayVideoView videoPreview = view.findViewById(R.id.videoPreview);

        ImageView placeHolderPlay = view.findViewById(R.id.placeHolderPlay);
        videoPreview.setPlaceHolderPlay(placeHolderPlay);

        String link = issue.getLinkToPreview();

        if (link != null && !link.isEmpty()) {
            if (isImg(link)) {
                //region ==== Image ====

                //If this is an image to preview
                videoPreview.setVisibility(View.INVISIBLE);

                //Load default placeholder
                if (imageView.getDrawable() == null)
                    imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder));

                //If image is from local or web
                if (link.startsWith("content://")) {
                    imageView.setImageBitmap(loadImage(link));
                } else {
                    new ThumbnailLoader(imageView).execute(link);
                }

                //region ===== Clic handler =====
                imageView.setOnClickListener(v -> {
                    Intent intent = new Intent(this.getContext(), IssueDetailsView.class);
                    issue.feedIntent(intent);
                    view.getContext().startActivity(intent);
                });

                //endregion
                //endregion
            } else {
                //region ==== Video ====

                //If the link point a video, we load a video component
                Log.d(TAG + "_loadVideo", "Video : " + link);
                imageView.setVisibility(View.INVISIBLE);

                ImageView placeholder = view.findViewById(R.id.loadingVideo);
                placeholder.setVisibility(View.VISIBLE);

                //region ========== VideoView ==========
                videoPreview.setVideoPath(link);
                MediaController mediaController = new MediaController(context);
                videoPreview.setMediaController(mediaController);
                videoPreview.requestFocus();
                //we set an setOnPreparedListener in order to know when the video file is ready for playback

                videoPreview.setPlayPauseListener(new PlayPauseListener() {
                    @Override
                    public void onPlay() {
                        Log.d(TAG + "_videoPlayer", "play");
                        if (playing != null && playing.isPlaying())
                            playing.pause();
                        playing = videoPreview;

                        slideUp(title);
                    }

                    @Override
                    public void onPause() {
                        Log.d(TAG + "_videoPlayer", "plause");
                        playing = null;
                        slideDown(title);
                    }
                });

                videoPreview.setOnPreparedListener(mediaPlayer -> {
                    // hide the place holder
                    placeholder.setVisibility(View.GONE);
                    placeHolderPlay.setVisibility(View.VISIBLE);
                    mediaController.hide();

                });
                videoPreview.seekTo(200);
                slideDown(title);
                mediaController.hide();

                //Prevent media player from displaying when we don't want it to
                viewPager.addMediaController(mediaController);


                //endregion
                //endregion
            }
        }
    }

    private boolean isImg(String linkToPreview) {
        return !(linkToPreview.startsWith("https://youtu.be") || linkToPreview.endsWith(".mp4"));
    }

    @Override
    public Issue getItem(int index) {
        return issues.get(index);
    }

    //do what slideUp jquery function does
    private void slideUp(View view) {
        view.animate().scaleY(0).translationY(-view.getHeight() / 2).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
    }

    //do what slideDown jquery function does
    private void slideDown(View view) {
        view.animate().scaleY(1).translationY(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
    }

    public void setIssues(List<Issue> issues) {
        this.clear();
        this.addAll(issues);
    }

    private Bitmap loadImage(String localPath) {
        try (InputStream inputStream = getContext().getContentResolver().openInputStream(Uri.parse(localPath))) {
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.d("Debug.catch", e.getMessage());
        }

        return null;
    }
}
