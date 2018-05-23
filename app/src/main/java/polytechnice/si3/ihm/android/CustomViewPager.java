package polytechnice.si3.ihm.android;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.MediaController;

import java.util.LinkedList;
import java.util.List;

public class CustomViewPager extends ViewPager {

    private boolean enabled;
    private List<MediaController> mediaControllers;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
        mediaControllers = new LinkedList<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            if (event.getAction() == MotionEvent.ACTION_MOVE)
                hideMediaControllers();
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            if (event.getAction() == MotionEvent.ACTION_MOVE)
                hideMediaControllers();
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    private void hideMediaControllers() {
        for (MediaController mediaController : mediaControllers) {
            if (mediaController.isShowing()) mediaController.hide();
        }
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void addMediaController(MediaController mediaController) {
        mediaControllers.add(mediaController);
    }

    public void removeMediaController(MediaController mediaController) {
        mediaControllers.remove(mediaController);
    }


}