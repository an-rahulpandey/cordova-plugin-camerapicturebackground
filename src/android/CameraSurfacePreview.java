package me.rahul.plugins.camerapicturebackground;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CameraSurfacePreview extends Service {
	private static Camera camera = null;
	private static String imageName;
	private static int camType;
	private static String dirName;
	private static int rotation;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public int onStartCommand (Intent intent, int flags, int startId){
			
		imageName = intent.getStringExtra("filename");
		debugMessage("Image Name = "+imageName);
		camType = intent.getIntExtra("camType", 0);
		debugMessage("Camera Type ="+camType);
		dirName = intent.getStringExtra("dirName");
		debugMessage("Dir Name = "+dirName);
		rotation = intent.getIntExtra("orientation", 0);
		debugMessage("Rotation = "+rotation);
		takePhoto(this);
        return START_NOT_STICKY;

	}

	@SuppressWarnings("deprecation")
	private static void takePhoto(final Context context) {
		
		final SurfaceView preview = new SurfaceView(context);
		SurfaceHolder holder = preview.getHolder();
		// deprecated setting, but required on Android versions prior to 3.0
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		holder.addCallback(surfaceCallback);

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(1,
				1, // Must be at least 1x1
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, 0,
				// Don't know if this is a safe default
				PixelFormat.UNKNOWN);

		// Don't set the preview visibility to GONE or INVISIBLE
		wm.addView(preview, params);
	}

	static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

		public void surfaceCreated(SurfaceHolder holder) {

			final CameraPictureBackground cpb = new CameraPictureBackground();
			try {
				camera = Camera.open(camType);
				try {
					camera.setPreviewDisplay(holder);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				camera.setDisplayOrientation(rotation);
				Camera.Parameters params = camera.getParameters();
    				List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
    				Log.d("CordovaLog","preview sizes = "+previewSizes);
    				Camera.Size previewSize =  previewSizes.get(0);
				params.setPreviewSize(previewSize.width, previewSize.height);
    				
				params.setJpegQuality(100);
				if (params.getSceneMode() != null) {
				    params.setSceneMode(Parameters.SCENE_MODE_STEADYPHOTO);
				}
				List<String> focusModes = params.getSupportedFocusModes();
				if (focusModes.contains(Parameters.FOCUS_MODE_FIXED))
					params.setFocusMode(Parameters.FOCUS_MODE_FIXED);
				params.setRotation(rotation);
				camera.setParameters(params);
				camera.startPreview();
				camera.takePicture(null, null, new PictureCallback() {

					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						
						FileOutputStream outStream = null;
						File sdDir = Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
						File pictureFileDir = new File(sdDir,dirName);

						debugMessage("pictureFileDir = "+pictureFileDir);

						if (!pictureFileDir.exists())
							pictureFileDir.mkdir();
						
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
						String date = dateFormat.format(new Date());
						
						String filepath = pictureFileDir.getPath()
								+ File.separator +imageName+"-"+date+".jpg";


						File pictureFile = new File(filepath);

						try {
							outStream = new FileOutputStream(pictureFile);
							outStream.write(data);
							debugMessage("Picture Saved Successfully");
							outStream.close();
							cpb.sendJavascript(filepath);
						} catch (FileNotFoundException e) {
							debugMessage(e.getMessage());
						} catch (IOException e) {
							debugMessage(e.getMessage());
						}
						if (camera != null) {
							camera.stopPreview();
							camera.release();
							camera = null;
						}
					}
				});
			} catch (Exception e) {
				if (camera != null) {
					camera.stopPreview();
					camera.release();
					camera = null;
				}
				throw new RuntimeException(e);
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {
				camera.stopPreview();
				camera.release();
				camera = null;
			}
		}
	};

	private static void debugMessage(String message) {
		Log.d("CameraPictureBackground", message);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy(){
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}
}
