package polytechnice.si3.ihm.android.Incidents;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.List;

import polytechnice.si3.ihm.android.R;
import polytechnice.si3.ihm.android.SinglePlayVideoView;
import polytechnice.si3.ihm.android.database.model.Issue;

public class IssueAdapter extends ArrayAdapter<Issue> {
    private final static String TAG = "IssueAdapter";
    private List<Issue> issues;
    private Context context;

    private VideoView playing;

    public IssueAdapter(@NonNull Context context, @NonNull List<Issue> issues) {
        super(context, 0, issues);
        Log.d("IncidentAdapter", "Create incident adapter with " + issues.toString());
        this.issues = issues;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int index, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) view = LayoutInflater.from(context).inflate(R.layout.incident_item, null);

        setUpView(view, index);

        return view;
    }

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
            ImageView showMoreImg = (ImageView) view.findViewById(R.id.showMoreImg);
            showMoreImg.setVisibility(View.INVISIBLE);
        }


        ImageView imageView = view.findViewById(R.id.inc_thumbnail);
        SinglePlayVideoView videoPreview = view.findViewById(R.id.videoPreview);

        if (issue.getLinkToPreview() != null) {
            if (isImg(issue.getLinkToPreview())) {
                //region ==== Image ====
                //If this is an image to preview
                videoPreview.setVisibility(View.INVISIBLE);
                if (imageView.getDrawable() == null)
                    imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder));
                new ThumbnailLoader(imageView, context).execute(issue.getLinkToPreview());
                //endregion
            } else {
                //region ==== Video ====

                //If the link point a video, we load a video component
                Log.d(TAG + "_loadVideo", "Video : " + issue.getLinkToPreview());
                imageView.setVisibility(View.INVISIBLE);

                ImageView placeholder = view.findViewById(R.id.loadingVideo);
                placeholder.setVisibility(View.VISIBLE);

                //region ========== VideoView ==========
                videoPreview.setVideoPath(issue.getLinkToPreview());
                videoPreview.setMediaController(new MediaController(context));
                videoPreview.requestFocus();
                //we set an setOnPreparedListener in order to know when the video file is ready for playback

                videoPreview.setPlayPauseListener(new SinglePlayVideoView.PlayPauseListener() {
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

                //endregion
                //endregion
            }
        }

        //region ========== View event handler ======

        Intent intent = new Intent(this.getContext(), IssueDetailsView.class);
        issue.feedIntent(intent);
        view.findViewById(R.id.inc_description).setOnClickListener(
                v -> view.getContext().startActivity(intent));
        title.setOnClickListener(
                v -> view.getContext().startActivity(intent));

        //endregion
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

    public void setIssues(List<Issue> issues){
        this.clear();
        this.addAll(issues);
    }
}
