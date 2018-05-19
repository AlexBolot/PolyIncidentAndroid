package polytechnice.si3.ihm.android.Incidents;

import android.content.Context;
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
import polytechnice.si3.ihm.android.database.model.Issue;

public class IssueAdapter extends ArrayAdapter<Issue> {
    private final static String TAG = "IssueAdapter";
    private List<Issue> issues;
    private Context context;

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
        VideoView videoPreview = (VideoView) view.findViewById(R.id.videoPreview);

        if (issue.getLinkToPreview() != null)
            if (isImg(issue.getLinkToPreview())) {
                //If this is an image to preview
                videoPreview.setVisibility(View.INVISIBLE);
                if (imageView.getDrawable() == null)
                    imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder));
                new ThumbnailLoader(imageView).execute(issue.getLinkToPreview());
            } else {
                //If the link point a video, we load a video component
                Log.d(TAG + "_loadVideo", "Video : " + issue.getLinkToPreview());
                imageView.setVisibility(View.INVISIBLE);
                ImageView placeholder = view.findViewById(R.id.loadingVideo);

//                placeholder.setVisibility(View.VISIBLE);
                placeholder.setVisibility(View.VISIBLE);

                MediaController mediaController = new MediaController(this.getContext());
                mediaController.setEnabled(true);
                videoPreview.setMediaController(mediaController);


                videoPreview.setVideoPath(issue.getLinkToPreview());
                videoPreview.requestFocus();
                //we set an setOnPreparedListener in order to know when the video file is ready for playback
                videoPreview.setOnPreparedListener(mediaPlayer -> {
                    // hide the place holder
                    placeholder.setVisibility(View.INVISIBLE);
                    videoPreview.start();
                });

            }
    }

    private boolean isImg(String linkToPreview) {
        return !(linkToPreview.startsWith("https://youtu.be") || linkToPreview.endsWith(".mp4"));
    }

    @Override
    public Issue getItem(int index) {
        return issues.get(index);
    }
}
