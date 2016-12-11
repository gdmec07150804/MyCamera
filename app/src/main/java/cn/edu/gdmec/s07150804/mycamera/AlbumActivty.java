package cn.edu.gdmec.s07150804.mycamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.util.Vector;

public class AlbumActivty extends AppCompatActivity {
    private ViewFlipper flipper;
    private Bitmap[] mBglist;
    private long startTime=0;
    private SensorManager sm;
    private SensorEventListener sel;
    public String[] loadAlbum(){
    String pathName=android.os.Environment.getExternalStorageDirectory().getPath()+"/mycamera";
        File file=new File(pathName);
        Vector<Bitmap> fileName=new Vector<Bitmap>();
        if (file.exists() && file.isDirectory()){
            String[] str=file.list();
            for (String s : str){
                if (new File(pathName+"/"+s).isFile()){
                    fileName.addElement(loadImage(pathName+"/"+s));
                }
            }
            mBglist=fileName.toArray(new Bitmap[]{});
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_activty);
        flipper=(ViewFlipper)this.findViewById(R.id.ViewFlipper01);
        loadAlbum();
        if (mBglist==null){
            Toast.makeText(this,"相册无照片",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }else {
            for (int i=0;i<=mBglist.length;i++){
                flipper.addView(addImage(mBglist[i]),i,
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        sm=(SensorManager)this.getSystemService(SEARCH_SERVICE);
        Sensor sensor=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sel=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x=event.values[SensorManager.DATA_X];
                if (x>10 && System.currentTimeMillis()>startTime+1000){
                   startTime=System.currentTimeMillis();
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivty.this,
                            R.anim.push_right_in));
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivty.this,
                            R.anim.push_right_out));
                    flipper.showPrevious();
                }else if (x<-10&&System.currentTimeMillis()>startTime+1000){
                    startTime=System.currentTimeMillis();
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivty.this,
                            R.anim.push_left_in));
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivty.this,
                            R.anim.push_left_out));
                    flipper.showNext();

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sm.registerListener(sel,sensor,SensorManager.SENSOR_DELAY_GAME);
    }
    protected void onDestroy(){
        super.onDestroy();
        sm.unregisterListener(sel);
    }
    public Bitmap loadImage(String pathName){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        Bitmap bitmap=BitmapFactory.decodeFile(pathName,options);
        WindowManager manage=getWindowManager();
        Display display=manage.getDefaultDisplay();
        int screenWidth=display.getWidth();
        options.inSampleSize=options.outWidth/screenWidth;
        options.inJustDecodeBounds=false;
        bitmap=BitmapFactory.decodeFile(pathName,options);
        return bitmap;
    }
    private View addImage(Bitmap bitmap){
        ImageView img=new ImageView(this);
        img.setImageBitmap(bitmap);
        return img;
    }
}
