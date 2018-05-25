package polytechnice.si3.ihm.android.Incidents;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.InputStream;

import polytechnice.si3.ihm.android.R;
import polytechnice.si3.ihm.android.SinglePlayVideoView;
import polytechnice.si3.ihm.android.database.model.Category;
import polytechnice.si3.ihm.android.database.model.Importance;
import polytechnice.si3.ihm.android.database.model.Issue;
import polytechnice.si3.ihm.android.database.model.Progress;
import polytechnice.si3.ihm.android.database.model.User;
import polytechnice.si3.ihm.android.database.viewmodel.CategoryViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.ImportanceViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.ProgressViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.UserViewModel;

public class IssueDetailsView extends AppCompatActivity {

    private VideoView playing;

    private String urlPattern = "https://maps.googleapis.com/maps/api/staticmap?zoom=18&size=200x100&maptype=roadmap&format=jpg&visual_refresh=true&markers=size:mid%7Ccolor:0xff0000%7Clabel:Position%7C";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String TAG = "IssueDetailsView";
        Log.d(TAG, "Activity start");
        setContentView(R.layout.incident_details);

        Issue issue = new Issue(getIntent());
        TextView title = findViewById(R.id.title);
        title.setText(issue.getTitle());

        //region ====== Fetch data =====
        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        CategoryViewModel categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        ImportanceViewModel importanceViewModel = ViewModelProviders.of(this).get(ImportanceViewModel.class);
        ProgressViewModel progressViewModel = ViewModelProviders.of(this).get(ProgressViewModel.class);

        User creator = userViewModel.getByID(issue.getCreatorID()).orElse(null);
        User assignee = userViewModel.getByID(issue.getAssigneeID()).orElse(null);
        Category category = categoryViewModel.getByID(issue.getCategoryID()).orElse(null);
        Importance importance = importanceViewModel.getByID(issue.getImportanceID()).orElse(null);
        Progress progress = progressViewModel.getByID(issue.getProgressID()).orElse(null);
        String linkToPreview = issue.getLinkToPreview();
        //endregion

        TextView creatorT = findViewById(R.id.creator);
        creatorT.setText(creator != null ? "Créé par " + creator.getName() : "Créateur inconnu");

        TextView assigneeT = findViewById(R.id.resp);
        assigneeT.setText(assignee != null ? "Assigné à " + assignee.getName() : "Aucun responsable");

        //TODO Listener to start UserActivity on creator or resp

        TextView progressT = findViewById(R.id.progress);
        if (progress != null)
            progressT.setText(progress.getLabel());

        TextView dateT = findViewById(R.id.date);
        dateT.setText(issue.getDate());

        TextView descriptionT = findViewById(R.id.descriptionLayout);
        descriptionT.setText(issue.getDescription());

        TextView importanceT = findViewById(R.id.importance);
        if (importance != null)
            importanceT.setText("Incident " + importance.getLabel().toLowerCase());

        TextView categoryT = findViewById(R.id.category);
        if (category != null)
            categoryT.setText("Catégorie : " + category.getLabel());

        Button callButton = findViewById(R.id.phoneCallButton);
        callButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:(+33)" + issue.getEmergencyPhoneNumber().substring(1)));
            startActivity(callIntent);
        });

        Button smsButton = findViewById(R.id.smsButton);
        smsButton.setOnClickListener(v -> {
            String number = "(+33)" + issue.getEmergencyPhoneNumber().substring(1);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
        });

        ImageView imageView = findViewById(R.id.inc_thumbnail);
        ImageView placeholder = findViewById(R.id.loadingVideo);

        CardView cardView = findViewById(R.id.cardView);

        if (linkToPreview.isEmpty()) {
            cardView.setVisibility(View.GONE);
        } else {
            if (isImg(linkToPreview)) {
                if (linkToPreview.startsWith("content://")) {
                    imageView.setImageBitmap(loadImage(linkToPreview));
                } else {
                    new ThumbnailLoader(imageView).execute(linkToPreview);
                }
                placeholder.setVisibility(View.GONE);
            } else {
                SinglePlayVideoView videoPreview = findViewById(R.id.videoPreview);
                ImageView placeHolderPlay = findViewById(R.id.placeHolderPlay);
                videoPreview.setPlaceHolderPlay(placeHolderPlay);

                imageView.setVisibility(View.INVISIBLE);
                placeholder.setVisibility(View.VISIBLE);

                //region ========== VideoView ==========
                videoPreview.setVideoPath(linkToPreview);
                MediaController mediaController = new MediaController(this);
                videoPreview.setMediaController(mediaController);
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
                    placeholder.setVisibility(View.GONE);
                    placeHolderPlay.setVisibility(View.VISIBLE);
                    mediaController.hide();

                });
                videoPreview.seekTo(200);
                slideDown(title);
                mediaController.hide();
            }
        }

        ImageView mapThumbnail = findViewById(R.id.img_mapThumbnail);

        double lat = issue.getLatitude();
        double lng = issue.getLongitude();

        if (lat != -1 && lng != -1) {
            mapThumbnail.setVisibility(View.VISIBLE);

            String url = urlPattern + lat + ",+" + lng;

            new ThumbnailLoader(mapThumbnail).execute(url);
        } else
            mapThumbnail.setVisibility(View.GONE);

    }

    private Bitmap loadImage(String localPath) {
        try (InputStream inputStream = getContentResolver().openInputStream(Uri.parse(localPath))) {
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.d("Debug.catch", e.getMessage());
        }

        return null;
    }

    private boolean isImg(String linkToPreview) {
        return !(linkToPreview.startsWith("https://youtu.be") || linkToPreview.endsWith(".mp4"));
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

}
