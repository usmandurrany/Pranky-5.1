package antistatic.spinnerwheel;

/**
 * Created by Usman-Durrani on 04-Nov-15.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

public class StrokeTextView extends TextView {

    // fields
    private int mStrokeColor;
    private int mStrokeWidth;
    private TextPaint mStrokePaint;

    // constructors
    public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StrokeTextView(Context context) {
        super(context);
    }

    // getters + setters
    public void setStrokeColor(int color) {
        mStrokeColor = color;
    }

    public void setStrokeWidth(int width) {
        mStrokeWidth = (int) (getResources().getDimension(R.dimen.stroke_size) / getResources().getDisplayMetrics().density);
    }



    // overridden methods
    @Override
    protected void onDraw(Canvas canvas) {
        mStrokeWidth = (int) (getResources().getDimension(R.dimen.stroke_size) / getResources().getDisplayMetrics().density);
        /*Log.e("Stroke TextView", "Density: "+String.valueOf(getResources().getDisplayMetrics().density));
        Log.e("Stroke TextView", "Stroke Size: "+String.valueOf(mStrokeWidth));*/
        // lazy load
        if (mStrokePaint == null) {
            mStrokePaint = new TextPaint();
        }

        // copy
        mStrokePaint.setTextSize(getTextSize());
        mStrokePaint.setTypeface(getTypeface());
        mStrokePaint.setFlags(getPaintFlags());

        // custom
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStrokeWidth(mStrokeWidth);

        String text = getText().toString();
        canvas.drawText(text, (getWidth() - mStrokePaint.measureText(text)) / 2, getBaseline(), mStrokePaint);
        super.onDraw(canvas);
    }

}