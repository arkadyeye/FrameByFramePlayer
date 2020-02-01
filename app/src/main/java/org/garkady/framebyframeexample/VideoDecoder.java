package org.garkady.framebyframeexample;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.view.Surface;


/**
 * Created by Arkady G on 05/09/19.
 *
 * Actually, this is a combination of a few examples that I found on the net
 * look here for more https://bigflake.com/mediacodec/
 *
 */

public class VideoDecoder {


    private VideoDecoderThread mVideoDecoder;

    private Handler mBackgroundHandler;


    private AssetFileDescriptor videoFile;
    private ImageReader mImageReader;
    private Surface surface;
    private Preview preview;

    private byte[][] cachedYuvBytes;
    private int[] cachedRgbBytes;

    private Bitmap rgbFrameBitmap;

    private ImageReader.OnImageAvailableListener mImageAvailable = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireLatestImage();

            if (image == null)
                return;

            //------------

            cachedRgbBytes = ImageUtils.convertImageToBitmap(image, cachedRgbBytes, cachedYuvBytes);
            rgbFrameBitmap.setPixels(cachedRgbBytes, 0, image.getWidth(), 0, 0,
                    image.getWidth(), image.getHeight());

            preview.onFrameAvailable(rgbFrameBitmap);

            image.close();


        }
    };

    public void init(AssetFileDescriptor file, int width, int height, Preview preview){

        videoFile = file;

        this.preview = preview;

        this.cachedRgbBytes = new int[width * height];
        this.cachedYuvBytes = new byte[3][];
        this.rgbFrameBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        mImageReader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, 10);

        mImageReader.setOnImageAvailableListener(mImageAvailable,mBackgroundHandler);
        surface = mImageReader.getSurface();

        mVideoDecoder = new VideoDecoderThread();
    }


    public void start(){
        if (mVideoDecoder != null) {
            if (mVideoDecoder.init(surface, videoFile)) {
                mVideoDecoder.start();

            } else {
                mVideoDecoder = null;
            }

        }
    }

    public void stop(){
        if (mVideoDecoder != null) {
            mVideoDecoder.close();
        }

    }

    public void playNextFrame(){
        mVideoDecoder.playNextFrame();
    }

    public void pause(boolean pause){
        mVideoDecoder.pause(pause);
    }

}
