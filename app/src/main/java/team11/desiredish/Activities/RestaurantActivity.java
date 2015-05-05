package team11.desiredish.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import team11.desiredish.ListView.ListViewAdapter;
import team11.desiredish.ListView.ListViewItem;
import team11.desiredish.R;
import team11.desiredish.TakePhoto.PhotoIntentActivity;

/**
 * Created by Yanjing on 10/23/14.
 */
public class RestaurantActivity extends Activity{
    public final static String EXTRA_MESSAGE = "team11.desiredish.MESSAGE";
    private static final String DEBUG_TAG = "yanjing-debug";

    private RestDetailTask resttask;
    private TextView mrestaurant;
    private TextView maddress;
    private ImageView mdishImage;
    private Bitmap mImageBitmap;
    private String mrestid;
    private Button orderButton;
    private Button callButton;
    private Button directionButton;
    private String mPhone;
    private String latlon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Intent intent = getIntent();
        String[] arguments = intent.getStringExtra(RestaurantActivity.EXTRA_MESSAGE).split(":");
        String uploadid=arguments[1];
        mrestid=arguments[0];
        latlon=arguments[2];
        Log.i(DEBUG_TAG,"RestAct: Uploadid="+uploadid+"Restid="+mrestid);
        Log.i(DEBUG_TAG,"RestAct: latlon="+latlon);
        mrestaurant = (TextView) findViewById(R.id.RestaurantName);
        maddress = (TextView) findViewById(R.id.Address);
        mdishImage= (ImageView) findViewById(R.id.image);
        orderButton=(Button) findViewById(R.id.order);
        callButton=(Button) findViewById(R.id.call);
        directionButton=(Button)findViewById(R.id.direction);
        resttask=new RestDetailTask(mrestid,uploadid);
        resttask.execute();
        // Set OnItemClickListener so we can be notified on button clicks
    }


    public class RestDetailTask extends AsyncTask<Void, Void, String[]> {

        private final String mRestID;
        private final String mUploadid;

        RestDetailTask(String restID,String uploadid) {
            mRestID = restID;
            mUploadid=uploadid;
        }

        @Override
        protected String[] doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost("http://54.165.111.90/desireddish/restaurant");

            BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair("RestaurantID", mRestID);
            BasicNameValuePair usernameBasicNameValuePair2 = new BasicNameValuePair("UploadID", mUploadid);


            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(usernameBasicNameValuePair);
            nameValuePairList.add(usernameBasicNameValuePair2);


            try {
                // Simulate network access.
                //       Thread.sleep(2000);

                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                httpPost.setEntity(urlEncodedFormEntity);
                Log.i(DEBUG_TAG, "Restaurant: request " + httpPost.getRequestLine());

                try {
                    // HttpResponse is an interface just like HttpPost.
                    //Therefore we can't initialize them
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    Log.i(DEBUG_TAG, "Restaurant: response " + httpResponse.getStatusLine().toString());
                    // According to the JAVA API, InputStream constructor do nothing.
                    //So we can't initialize InputStream although it is not an interface
                    InputStream inputStream = httpResponse.getEntity().getContent();

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder stringBuilder = new StringBuilder();

                    String bufferedStrChunk = null;

                    while((bufferedStrChunk = bufferedReader.readLine()) != null){
                        stringBuilder.append(bufferedStrChunk);
                    }
                    stringBuilder.append(bufferedReader.readLine());
                    Log.i(DEBUG_TAG,"Restaurant: result is " + stringBuilder.toString());
                    JSONObject Object=new JSONObject(stringBuilder.toString());
                    String thumbImgDir=Object.getString("thumb_img");

                    InputStream is = null;
                    try {
                        is = (InputStream) new URL("http://54.165.111.90/desireddish/"+thumbImgDir).getContent();
                    } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mImageBitmap = BitmapFactory.decodeStream(is);

                  //      Drawable d = Drawable.createFromStream(is, "src name");
                        // initialize the items list
                    if(!mrestid.equals("null")) {
                        JSONObject restObject = Object.getJSONObject("0");

                        String restName = restObject.getString("RName");
                        String address = restObject.getString("Address");
                        String city = restObject.getString("City");
                        String state = restObject.getString("State");
                        String reserve_url = restObject.getString("mobile_reserve_url");
                        mPhone = restObject.getString("phone");

                        String []result={restName,address+", "+city+", "+state,reserve_url};
                        return result;

                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }catch (ClientProtocolException cpe) {
                    Log.i(DEBUG_TAG, "FoodFrag: First Exception caz of HttpResponese :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    Log.i(DEBUG_TAG,"FoodFrag: Second Exception caz of HttpResponse :" + ioe);
                    ioe.printStackTrace();
                }

            } catch (UnsupportedEncodingException uee) {
                Log.i(DEBUG_TAG,"FoodFrag: An Exception given because of UrlEncodedFormEntity argument :" + uee);
                uee.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String[] result) {
            //mDownloadTask = null;

            mdishImage.setImageBitmap(mImageBitmap);

            if(mrestid.equals("null")||mrestid.equals("0")){
                Log.i(DEBUG_TAG,"********"+mrestid);
                mrestaurant.setText("Not available to order");
                orderButton.setVisibility(View.GONE);
                callButton.setVisibility(View.GONE);
                directionButton.setVisibility(View.GONE);
            }
            else {
                Log.i(DEBUG_TAG,"---------------"+mrestid);

                mrestaurant.setText(result[0]);
                maddress.setText(result[1]);
                orderButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(result[2]);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }});
                callButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:"+mPhone));
                            startActivity(callIntent);
                        } catch (ActivityNotFoundException activityException) {
                            Log.e("Calling a Phone Number", "Call failed", activityException);
                        }
                    }});
                directionButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("http://54.164.121.5/doctorpatient/desiredish.html?latlon="+latlon+","+result[1]);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }});
            }
            // initialize and set the list adapter

        }

        @Override
        protected void onCancelled() {
            // mDownloadTask = null;
        }
    }










}
