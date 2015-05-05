package team11.desiredish.TakePhoto;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import team11.desiredish.ProgressListener;
import team11.desiredish.ProgressOutEntity;
import team11.desiredish.R;

/**
 * Created by Yanjing on 10/18/14.
 */
public class PhotoIntentActivity extends Activity{
    public final static String EXTRA_MESSAGE = "team11.desiredish.MESSAGE";

    private static final String DEBUG_TAG = "yanjing-debug";

        private static final int ACTION_TAKE_PHOTO_B = 1;

        private static final String BITMAP_STORAGE_KEY = "viewbitmap";
        private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";

        private ImageView mImageView;
        private Bitmap mImageBitmap;
        private Bitmap thumbBitmap;

        private String mCurrentPhotoPath;

        private static final String JPEG_FILE_PREFIX = "IMG_";
        private static final String JPEG_FILE_SUFFIX = ".jpg";

        private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

        /* Photo album for this application */
        private String getAlbumName() {
            return getString(R.string.album_name);
        }

        private File getAlbumDir() {
            File storageDir = null;

            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

                if (storageDir != null) {
                    if (! storageDir.mkdirs()) {
                        if (! storageDir.exists()){
                            Log.d("CameraSample", "failed to create directory");
                            return null;
                        }
                    }
                }

            } else {
                Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
            }

            return storageDir;
        }

        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
            File albumF = getAlbumDir();
            File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
            return imageF;
        }

        private File setUpPhotoFile() throws IOException {

            File f = createImageFile();
            mCurrentPhotoPath = f.getAbsolutePath();

            return f;
        }

        private void setPic() {

		/* Get the size of the ImageView */
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();

		/* Get the size of the image */
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
            int scaleFactor = 6;
        /*    if ((targetW > 0) || (targetH > 0)) {
                scaleFactor = Math.min(photoW/targetW, photoH/targetH);
            }

		/* Set bitmap options to scale the image decode target */
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;



		/* Decode the JPEG file into a Bitmap */
            thumbBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            Log.i(DEBUG_TAG,"mCurrentPhotoPath:"+mCurrentPhotoPath);


		/* Associate the Bitmap to the ImageView */
            mImageView.setImageBitmap(thumbBitmap);
            mImageView.setVisibility(View.VISIBLE);
        }

        private void galleryAddPic() {
            Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }

        private void dispatchTakePictureIntent(int actionCode) {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File f = null;

                    try {
                        f = setUpPhotoFile();
                        mCurrentPhotoPath = f.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    } catch (IOException e) {
                        e.printStackTrace();
                        f = null;
                        mCurrentPhotoPath = null;
                    }

            startActivityForResult(takePictureIntent, actionCode);
        }

        private void handleBigCameraPhoto() {

            if (mCurrentPhotoPath != null) {
                Log.i(DEBUG_TAG,"Enter handle big cameraPhoto");
                setPic();
                galleryAddPic();
    //            mCurrentPhotoPath = null;
            }

        }

        Button.OnClickListener mTakePicOnClickListener =
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                    }
                };

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
   //         getActionBar().setDisplayHomeAsUpEnabled(true);
            setContentView(R.layout.activity_photo);
            Intent intent = getIntent();

            //Deal with Take picture
            mImageView = (ImageView) findViewById(R.id.imageView1);
            mImageBitmap = null;
            ImageButton picBtn = (ImageButton) findViewById(R.id.btnIntend);
            setBtnListenerOrDisable(
                    picBtn,
                    mTakePicOnClickListener,
                    MediaStore.ACTION_IMAGE_CAPTURE
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
            } else {
                mAlbumStorageDirFactory = new BaseAlbumDirFactory();
            }
            //Deal with submit button
            Button submitButton = (Button) findViewById(R.id.submit);
            submitButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.i(DEBUG_TAG,"ONCREATE: "+mCurrentPhotoPath);
                    new UploadTask(mCurrentPhotoPath,thumbBitmap).execute();

                    }
            });
        }



        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

              if (resultCode == RESULT_OK) {
                  handleBigCameraPhoto();
              }

        }

        // Some lifecycle callbacks so that the image can survive orientation change
        @Override
        protected void onSaveInstanceState(Bundle outState) {
            outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
            outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
            super.onSaveInstanceState(outState);
        }

        @Override
        protected void onRestoreInstanceState(Bundle savedInstanceState) {
            super.onRestoreInstanceState(savedInstanceState);
            mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
            mImageView.setImageBitmap(mImageBitmap);
            mImageView.setVisibility(
                    savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                            ImageView.VISIBLE : ImageView.INVISIBLE
            );
        }

        /**
         * Indicates whether the specified action can be used as an intent. This
         * method queries the package manager for installed packages that can
         * respond to an intent with the specified action. If no suitable package is
         * found, this method returns false.
         * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
         *
         * @param context The application's environment.
         * @param action The Intent action to check for availability.
         *
         * @return True if an Intent with the specified action can be sent and
         *         responded to, false otherwise.
         */
        public static boolean isIntentAvailable(Context context, String action) {
            final PackageManager packageManager = context.getPackageManager();
            final Intent intent = new Intent(action);
            List<ResolveInfo> list =
                    packageManager.queryIntentActivities(intent,
                            PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }

        private void setBtnListenerOrDisable(
                ImageButton btn,
                ImageButton.OnClickListener onClickListener,
                String intentName
        ) {
            if (isIntentAvailable(this, intentName)) {
                btn.setOnClickListener(onClickListener);
            } else {
                btn.setClickable(false);
            }
        }

    private class UploadTask extends AsyncTask<Void, Integer, String> {



        private final String mImagePath;
        private final Bitmap mBitmap;
        private long totalSize;
        private ProgressDialog pd;


        UploadTask( String imagePath, Bitmap bitmap) {
            mImagePath=imagePath;
            mBitmap=bitmap;


        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(PhotoIntentActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMessage("Uploading....");
            pd.setCancelable(false);
            pd.show();
        }
        @Override
        protected String doInBackground(Void... params) {

            setProgress(0);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); // convert Bitmap to ByteArrayOutputStream
            byte[] imageBytes= stream.toByteArray();

            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpPost httppost = new HttpPost(
                        "http://54.165.111.90/desireddish/picbuy"); // server


                MultipartEntityBuilder reqEntity=MultipartEntityBuilder.create();
                File f=new File(mImagePath);
                FileBody fileBody = new FileBody(f);
                Log.i(DEBUG_TAG,"new file: "+ f.getName()+f.getTotalSpace());
                reqEntity.addPart("f_file", fileBody);
                Log.i(DEBUG_TAG,"Upload: bitmap.getByteCount():"+mBitmap.getByteCount());

                HttpEntity httpEntity = reqEntity.build();
                totalSize = httpEntity.getContentLength();
                ProgressOutEntity progressHttpEntity = new ProgressOutEntity(
                        httpEntity, new ProgressListener() {
                    @Override
                    public void transferred(long transferedBytes) {
                        publishProgress((int) (100 * transferedBytes / totalSize));
                    }
                });

                httppost.setEntity(progressHttpEntity);

                Log.i(DEBUG_TAG, "request " + httppost.getRequestLine());
                Log.i(DEBUG_TAG, "request " + "HEADER!!"+httppost.getHeaders("enctype").toString());

                HttpResponse response = null;
                try {
                    response = httpclient.execute(httppost);
                    InputStream inputStream = response.getEntity().getContent();

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder stringBuilder = new StringBuilder();

                    String bufferedStrChunk = null;

                      while((bufferedStrChunk = bufferedReader.readLine()) != null){
                        stringBuilder.append(bufferedStrChunk);
                    }
                    stringBuilder.append(bufferedReader.readLine());
                    Log.i(DEBUG_TAG,"result is " + stringBuilder.toString());
                    return stringBuilder.toString();


                }catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    if (response != null)
                        Log.i(DEBUG_TAG, "response " + response.getStatusLine().toString());

                } finally {

                }
        //    }catch (final FileNotFoundException e) {

            }finally {

            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pd.setProgress((int) (progress[0]));
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pd.dismiss();
            Toast.makeText(PhotoIntentActivity.this, "Successfully uploaded", Toast.LENGTH_LONG).show();
            String url = result;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
           // finish();


        }
    }

}
