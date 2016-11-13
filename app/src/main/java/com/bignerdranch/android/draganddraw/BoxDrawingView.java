package com.bignerdranch.android.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoxDrawingView extends View {

    private static final String TAG = "BoxDrawingView";

    private Box currentBox;
    private List<Box> boxen = new ArrayList<>();
    private Paint boxPaint;
    private Paint backgroundPaint;

    // Used in code
    public BoxDrawingView(Context context) {
        this(context, null);
    }

    // Used in XML
    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        boxPaint = new Paint();
        boxPaint.setColor(0x22ff0000); // 50% red

        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xfff8efe0); // white-gray
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "";

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                // reset current state
                currentBox = new Box(current);
                boxen.add(currentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if (currentBox != null) {
                    currentBox.setCurrent(current);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                currentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                currentBox = null;
                break;
        }
        Log.i(TAG, "onTouchEvent: " + action + " at x=" + current.x + " y=" + current.y);

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(backgroundPaint);

        for (Box box : boxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
            canvas.drawRect(left, top, right, bottom, boxPaint);
        }
    }

    /**
     * Obligatory ID in layout for this view!
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putSerializable("boxen", boxen.toArray(new Box[]{}));
        bundle.putParcelable("super", superState);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            Box[] boxenArray = (Box[]) bundle.getSerializable("boxen");
            if (boxenArray != null) {
                boxen.addAll(Arrays.asList(boxenArray));
            }
            Parcelable superState = bundle.getParcelable("super");
            super.onRestoreInstanceState(superState);
        }
    }
}
