package org.garkady.framebyframeexample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class Preview extends SurfaceView implements
        SurfaceHolder.Callback {

    private SurfaceHolder mHolder;

    //this flag allow the frame to be mirrored. frequntlly used when working with front facing camera
    private boolean mirror = true;
    //helper vars for image mirroring
    private Matrix m;
    private Bitmap proceesedFrame = null;
    private int rescaledWidth = 0;
    private int rescaledHeight = 0;

    private Activity activity;


    //constructor
    public Preview(Activity activity, int screenWidth, int screenHeight) {
        super(activity);
        rescaledWidth = screenWidth;
        rescaledHeight = screenHeight;
        init();
        this.activity = activity;
    }

    //constructor fox xml
    public Preview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /*
        init the view with holder, and prepare the rotation matrix
     */
    private void init() {
        setFocusable(true);

        mHolder = getHolder();
        mHolder.addCallback(this);

        //prepare rotation matrix
        m = new Matrix();
        m.preScale(-1, 1);
    }

    /*
    Draws given bitmap on a canvas. rotate the bitmap if needed
     */
    private void drawOnCanvas(Bitmap videoBitmap){

        Bitmap rescaledFrame;// = videoBitmap;
        if (rescaledHeight != 0 && rescaledWidth != 0) {
            rescaledFrame = Bitmap.createScaledBitmap(videoBitmap, rescaledWidth, rescaledHeight, false);

            Canvas canvas = mHolder.lockCanvas();
            if(canvas != null) {
                drawOnCanvas(canvas, rescaledFrame);
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }



    protected void drawOnCanvas(Canvas canvas, Bitmap videoBitmap) {
        canvas.drawBitmap(videoBitmap,0,0,null);
    }


    //implementing SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(MainActivity.TAG, "Surface created");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(MainActivity.TAG, "Surface destroyed");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int winWidth,
                               int winHeight) {

    }

    /**
     * Provides a new frame to be drawn on the view
     * @param bitmap
     */
    public void onFrameAvailable(final Bitmap bitmap) {

        activity.runOnUiThread(new Runnable() {
            public void run() {
                drawOnCanvas(bitmap);
            }
        });


    }

}
