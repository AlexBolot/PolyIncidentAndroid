package polytechnice.si3.ihm.android;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Sample code found on https://stackoverflow.com/questions/7934556/event-for-videoview-playback-state-or-mediacontroller-play-pause?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
 * Used to override pause and start to add handler on them
 *
 * @author MH
 */
public class SinglePlayVideoView extends VideoView {

    private PlayPauseListener mListener;

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

    @Override
    public void pause() {
        super.pause();
        if (mListener != null) {
            mListener.onPause();
        }
    }

    @Override
    public void start() {
        super.start();
        if (mListener != null) {
            mListener.onPlay();
        }
    }

    public interface PlayPauseListener {
        void onPlay();

        void onPause();
    }

}