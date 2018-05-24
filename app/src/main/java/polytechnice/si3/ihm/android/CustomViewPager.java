package polytechnice.si3.ihm.android;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.MediaController;

import java.util.LinkedList;
import java.util.List;

public class CustomViewPager extends ViewPager {

    private static final String TAG = "CustomViewPager";
    private boolean enabled;
    private List<MediaController> mediaControllers;

    private double xStart;
    private double yStart;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
        mediaControllers = new LinkedList<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                xStart = event.getX();
                yStart = event.getY();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (distance(xStart, event.getX(), yStart, event.getY()) > 20)
                    hideMediaControllers();
            }
            return super.onTouchEvent(event);
        } else {
            Log.d(TAG, "event received, but view disabled");
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                xStart = event.getX();
                yStart = event.getY();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (distance(xStart, event.getX(), yStart, event.getY()) > 20)
                    hideMediaControllers();
            }
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    private double distance(double xStart, float xEnd, double yStart, float yEnd) {
        return Math.sqrt(Math.pow(xEnd - xStart, 2) + Math.pow(yEnd - yStart, 2));
    }

    private void hideMediaControllers() {
        Log.d(TAG, "Hide media controllers");
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
}