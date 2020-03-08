//Jeffrey Knight
// MDF 3
// Canvas drawing CE07
package com.example.knightjeffrey_ce07_canvasdrawing.models;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import com.example.knightjeffrey_ce07_canvasdrawing.R;
import java.util.ArrayList;

public class GameDrawingSurface extends SurfaceView implements SurfaceHolder.Callback {

    private Rect mDimensions;

    private Bitmap mBackground;
    private Bitmap mHole;
    private Bitmap mStarfish;

    private ItemsManager itemManager;

    private Paint mBlankPaint;
    private Paint mHolePaint;
    private Paint mItemPaint;
    private Paint mTextPaint;
    private int myItemCount;

    private ArrayList<Point> mPoints;

    public GameDrawingSurface(Context context) {
        super(context);
    }

    public GameDrawingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameDrawingSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GameDrawingSurface(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    // this is where the view is fully ready to start interaction, do setup here
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setWillNotDraw(false);

        getHolder().addCallback(this);

        Resources res = getResources();

        // set bitmaps to image resources
        mBackground = BitmapFactory.decodeResource(res, R.drawable.beach_wave_br);
        mHole  =BitmapFactory.decodeResource(res,R.drawable.hole);
        mStarfish = BitmapFactory.decodeResource(res,R.drawable.star_fish_red);

        mBlankPaint = new Paint();
        mHolePaint = new Paint();
        mItemPaint = new Paint();
        mItemPaint.setColor(Color.WHITE);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLUE);
        mTextPaint.setTextSize(80.0f);
        mPoints = new ArrayList<>();


    }

    // This is where all of the drawing will get handled
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        itemManager = ItemsManager.getInstance();

        if(itemManager.getClickPoints() != null){
            mPoints = itemManager.getClickPoints();
        }
        myItemCount = itemManager.getMyItems().size();
        // clear the canvas
        canvas.drawColor(Color.BLACK);

        // draw the background
        canvas.drawBitmap(mBackground,null,mDimensions, mBlankPaint);

        for(Point p: mPoints){

            canvas.drawBitmap(mHole,p.x,p.y,mHolePaint);

        }

        if(itemManager.getMyItemPoints().size() > 0) {

            for (Point p : itemManager.getMyItemPoints()) {
                canvas.drawBitmap(mStarfish,p.x -50,p.y-40,mItemPaint);
//                canvas.drawCircle(
//                        p.x,
//                        p.y,
//                        20.0f,
//                        mItemPaint
//                );
            }
        }
        String counterStr = "";
        counterStr += myItemCount + " / 171";
        canvas.drawText(counterStr, (mDimensions.width() / 2.0f) - 95,150,mTextPaint);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        itemManager = ItemsManager.getInstance();



        if(event.getAction() == MotionEvent.ACTION_DOWN){
            float x = event.getX() - (((float)mHole.getWidth()) / 2);
            float y = event.getY() - (((float)mHole.getHeight()) / 2);
            Point clickPoint = new Point((int)x,(int)y);
            mPoints.add(clickPoint);

            if(itemManager.checkMatch(clickPoint)){
                Toast.makeText(getContext(),"Found " +
                        itemManager.getMyItems().get(itemManager.getMyItems().size() - 1).getName(),Toast.LENGTH_SHORT).show();
            }
        }
        myItemCount = itemManager.getMyItems().size();
        itemManager.setClickPoints(mPoints);
        postInvalidate();

        return super.onTouchEvent(event);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        storeDimensions(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void storeDimensions(SurfaceHolder holder){
        Canvas canvas = holder.lockCanvas();

        mDimensions = new Rect(0,0, canvas.getWidth(), canvas.getHeight());
        itemManager = ItemsManager.getInstance();
        itemManager.generateItemLocations(canvas.getWidth(),canvas.getHeight());
        holder.unlockCanvasAndPost(canvas);
    }


}
