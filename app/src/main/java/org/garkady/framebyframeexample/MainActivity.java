package org.garkady.framebyframeexample;

import android.content.res.AssetFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "f2f_example";
    private VideoDecoder decoder;
    private boolean pause = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //prepare a preview window
        Preview preview = new Preview(this,640,360);


        //prepare play/pause and playNext buttons
        Button playFull = new Button(this);
        playFull.setText("play/pause");
        playFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decoder.pause(pause);
                pause = !pause;
            }
        });


        Button playNext = new Button(this);
        playNext.setText("playNext");
        playNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decoder.playNextFrame();
            }
        });

        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);

        l.addView(playFull);
        l.addView(playNext);
        l.addView(preview);

        //add all views
        addContentView(l,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT));

        //get file from assets
        AssetFileDescriptor fileDescriptor = getResources().openRawResourceFd(R.raw.big_buck_bunny);

        if (fileDescriptor == null){
            Toast.makeText(getApplicationContext(),"Can not access file",Toast.LENGTH_LONG).show();
            return;
        }

        decoder = new VideoDecoder();
        decoder.init(fileDescriptor,640,360,preview);
        decoder.start();

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (decoder!= null){
            decoder.stop();
            decoder = null;
        }
    }
}
