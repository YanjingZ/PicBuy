package team11.desiredish.Fragment;


import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

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
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import team11.desiredish.Activities.RestaurantActivity;
import team11.desiredish.ListView.ListViewAdapter;
import team11.desiredish.ListView.ListViewItem;
import team11.desiredish.R;
import team11.desiredish.TakePhoto.PhotoIntentActivity;


/**
 * Created by Yanjing on 10/18/14.
 */
public class MyFoodFragment extends ListFragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener{
    public final static String EXTRA_MESSAGE = "team11.desiredish.MESSAGE";
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final String DEBUG_TAG = "yanjing-debug";

    private DownloadTask mDownloadTask=null;
    Location mCurrentLocation;
    LocationClient mLocationClient;
    // JSON Node names
    private static final String TAG_REST_ID = "restaurant_id";
    private static final String TAG_REST_NAME = "restaurant_name";
    private static final String TAG_TIME = "dining_time";
    private static final String TAG_DESC = "description";
    private static final String TAG_RATING = "rating";
    private static final String TAG_IMG = "img_dir";
    private static final String TAG_THUMB_IMG = "thumbimg_dir";
    private static final String TAG_UPLOAD = "upload_id";


    private String UserID;

    // contacts JSONArray
    JSONArray DishItems = null;

    // Hashmap for ListView
    ArrayList<ListViewItem> DishItemList;

 /*   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
        return (LinearLayout)inflater.inflate(R.layout.tab_myfood_layout, container, false);
    }*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserID=this.getArguments().getString("UserID");
        Log.i(DEBUG_TAG,"MYFoodFrag: UserID:"+UserID);

        DishItemList = new ArrayList<ListViewItem>();
      /*  DownloadTask asyncTask = new DownloadTask(UserID);
        mDownloadTask = new WeakReference<DownloadTask>(asyncTask );
        asyncTask.execute();*/
        mLocationClient = new LocationClient(getActivity(), this, this);
        mLocationClient.connect();

        mDownloadTask=new DownloadTask(UserID);
        mDownloadTask.execute();

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        mCurrentLocation = mLocationClient.getLastLocation();
        Log.i(DEBUG_TAG,"___"+mCurrentLocation.getLatitude()+","+mCurrentLocation.getLongitude());
        ListViewItem item = DishItemList.get(position);
        Intent intent = new Intent(getActivity(), RestaurantActivity.class);
        intent.putExtra(EXTRA_MESSAGE, item.restID+":"+item.uploadID+":"+mCurrentLocation.getLatitude()+","+mCurrentLocation.getLongitude());
        startActivity(intent);

        // do something
//        Toast.makeText(getActivity(), item.restID + item.uploadID, Toast.LENGTH_SHORT).show();
    }

    public class DownloadTask extends AsyncTask<Void, Void, String> {

        private final String mUserID;

        DownloadTask(String username) {
            mUserID = username;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost("http://54.165.111.90/desireddish/mylist");

            BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair("UserId", UserID);

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(usernameBasicNameValuePair);

            try {
                // Simulate network access.
                //       Thread.sleep(2000);

                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                httpPost.setEntity(urlEncodedFormEntity);
                Log.i(DEBUG_TAG, "FoodFrag: Login request " + httpPost.getRequestLine());

                try {
                    // HttpResponse is an interface just like HttpPost.
                    //Therefore we can't initialize them
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    Log.i(DEBUG_TAG, "FoodFrag: Login response " + httpResponse.getStatusLine().toString());
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
                    Log.i(DEBUG_TAG,"FoodFrag: My Food result is " + stringBuilder.toString());
                    DishItems = new JSONArray(stringBuilder.toString());
                    Log.i(DEBUG_TAG,"FoodFrag: My Food len: "+ DishItems.length());
                    for (int i = 0; i < DishItems.length(); i++) {
                        JSONObject c = DishItems.getJSONObject(i);

                        String restId = c.getString(TAG_REST_ID);
                        String RestName = c.getString(TAG_REST_NAME);
                        String rating = c.getString(TAG_RATING);
                        String desc = c.getString(TAG_DESC);
                        String imgDir = c.getString(TAG_IMG);
                        String thumbImgDir=c.getString(TAG_THUMB_IMG);
                        String time = c.getString(TAG_TIME).split(" ")[0];
                        String uploadID=c.getString(TAG_UPLOAD);

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
                        Drawable d = Drawable.createFromStream(is, "src name");

                        // initialize the items list

                        DishItemList.add(new ListViewItem(d, RestName, desc, time,rating,restId,uploadID,null));

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
        protected void onPostExecute(final String result) {
            mDownloadTask = null;
            // initialize and set the list adapter
            setListAdapter(new ListViewAdapter(getActivity(), DishItemList));

        }

        @Override
        protected void onCancelled() {
           // mDownloadTask = null;
        }

    }

    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
       // Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();

    }
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
    //    Toast.makeText(getActivity(), "Disconnected. Please re-connect.",
     //           Toast.LENGTH_SHORT).show();
    }
    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
           // showErrorDialog(connectionResult.getErrorCode());
        }
    }
}
