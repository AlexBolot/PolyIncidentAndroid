package polytechnice.si3.ihm.android;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Sample code found on https://stackoverflow.com/questions/7934556/event-for-videoview-playback-state-or-mediacontroller-play-pause?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
 * Used to override pause and start to add handler on them
 *
 * @author MH
 */
public class SinglePlayVideoView extends VideoView {

    private PlayPauseListener mListener;
    private ImageView placeHolderPlay;
    private MediaController mediaControllerWaiting;
    private MediaController mediaController;
    private boolean isMediaControllerSet = false;

    public SinglePlayVideoView(Context context) {
        super(context);
    }

    public SinglePlayVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SinglePlayVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPlayPauseListener(PlayPauseListener listener) {
        mListener = listener;
    }

    public void setPlaceHolderPlay(ImageView placeHolderPlay) {
        this.placeHolderPlay = placeHolderPlay;
        placeHolderPlay.setOnClickListener(v -> {
            this.start();
            if (!isMediaControllerSet) {
                this.mediaController = mediaControllerWaiting;
                super.setMediaController(mediaController);
            }
        });
    }

    @Override
    public void setMediaController(MediaController mediaController) {
        mediaControllerWaiting = mediaController;
    }

    @Override
    public void pause() {
        super.pause();
        if (isMediaControllerSet && mediaController.isShowing()) mediaController.hide();
        if (placeHolderPlay != null) {
            placeHolderPlay.setVisibility(VISIBLE);
            if (isMediaControllerSet && mediaController.isShowing()) mediaController.hide();
        }
        if (mListener != null) {
            mListener.onPause();
        }
    }

    @Override
    public void start() {
        super.start();
        if (isMediaControllerSet && mediaController.isShowing()) mediaController.hide();
        if (placeHolderPlay != null) {
            placeHolderPlay.setVisibility(GONE);
        }
        if (mListener != null) {
            mListener.onPlay();
        }
    }

    public interface PlayPauseListener {
        void onPlay();

        void onPause();
    }

}