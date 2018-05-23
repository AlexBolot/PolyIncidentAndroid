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

public class IssueAdapter extends ArrayAdapter<Issue> {
    private final static String TAG = "IssueAdapter";
    private List<Issue> issues;
    private Context context;

    private VideoView playing;

    private View swippedDesc;
    private float xStart;

    private float totalDx;

    private CustomViewPager viewPager;


    public IssueAdapter(@NonNull Context context, @NonNull List<Issue> issues, CustomViewPager viewPager) {
        super(context, 0, issues);
        Log.d("IncidentAdapter", "Create incident adapter with " + issues.toString());
        this.issues = issues;
        this.context = context;
        this.viewPager = viewPager;
    }

    @NonNull
    @Override
    public View getView(int index, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) view = LayoutInflater.from(context).inflate(R.layout.incident_item, null);

        setUpView(view, index);

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
        } else {
            description.setText(issue.getDescription());
            ImageView showMoreImg = view.findViewById(R.id.showMoreImg);
            showMoreImg.setVisibility(View.GONE);
        }

        //region ===== Init the swiping action menu ====

        //Listener for admin

        View adminsButton = view.findViewById(R.id.adminButtonsLayout);

        description.setOnTouchListener((v, event) -> {
            if (event.getAction() != MotionEvent.ACTION_MOVE)
                Log.d(TAG + "_test", event.toString());

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
                if (Math.abs(event.getX() - xStart) < 10) {
                    Log.d(TAG + "_swipeMenu", "Just clicked on desc");
                    if (swippedDesc == v) {
                        Log.d(TAG + "_swipeMenu", "Reset the swippedDesc");
                        swippedDesc.animate().x(0).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
                        return true;
                    } else {
                        //region ========== Clic handler ======
                        Intent intent = new Intent(this.getContext(), IssueDetailsView.class);
                        issue.feedIntent(intent);
                        view.getContext().startActivity(intent);
                        //endregion
                    }
                }
                swippedDesc = null;
                return false;
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                viewPager.setPagingEnabled(false);
                if (swippedDesc != null && swippedDesc != v)
                    swippedDesc.animate().x(0)
                            .setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
                xStart = event.getX();
                totalDx = 0;
                Log.d(TAG + "_swipeMenu", "x start : " + xStart);
                swippedDesc = v;
            }
            //if this is a move, we do the translation
            else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float dx = event.getX() - xStart;
                Log.d(TAG + "_swipeMenu", "Moved, swipe ?");
                totalDx += dx;
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


        ImageView imageView = view.findViewById(R.id.inc_thumbnail);
        SinglePlayVideoView videoPreview = view.findViewById(R.id.videoPreview);

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
                    placeholder.setVisibility(View.INVISIBLE);
                });
                videoPreview.start();
                slideDown(title);

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
