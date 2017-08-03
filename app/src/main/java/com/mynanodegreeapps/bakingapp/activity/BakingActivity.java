package com.mynanodegreeapps.bakingapp.activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.mynanodegreeapps.bakingapp.model.Recipe;
import com.mynanodegreeapps.bakingapp.util.IVolleyCallback;
import com.mynanodegreeapps.bakingapp.util.RecipeImageAdapter;
import com.mynanodegreeapps.bakingapp.util.ResponseReader;
import com.mynanodegreeapps.bakingapp.widget.BakingAppWidgetProvider;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import com.mynanodegreeapps.bakingapp.R;

public class BakingActivity extends AppCompatActivity implements IVolleyCallback{

    private String LOG_TAG = BakingActivity.class.getSimpleName();

    private JsonArrayRequest recipeListRequest;
    private RequestQueue recipeListRequestQueue;

    private RecyclerView recipeGrid;
    private RecipeImageAdapter recipeImageAdapter;
    public static List<Recipe> recipeArrayList = new ArrayList<>();
    public static List<Recipe> recipeListForWidget = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baking_activity);

        recipeGrid = (RecyclerView) findViewById(R.id.recipeGrid);

        if (getResources().getBoolean(R.bool.isTablet)) {
            recipeGrid.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        } else {
            recipeGrid.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        }

        recipeGrid.setClickable(true);
        recipeListRequestQueue =  Volley.newRequestQueue(getApplicationContext());

        if(isNetworkConnectivityAvailable()) {
            getRecipes(this);
        }else{
            Toast.makeText(getApplicationContext(),"Network in Unavailable",Toast.LENGTH_LONG);
        }

    }

    public boolean isNetworkConnectivityAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return  isConnected;
    }

    public  void getRecipes(final IVolleyCallback callback){
        Uri requestUri = Uri.parse(getString(R.string.SERVER_URL));
        recipeListRequest = new JsonArrayRequest(Request.Method.GET
                ,requestUri.toString()
                , null
                ,new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {

                        // Todo : May be use a 3rdparty GSON library here !
                        ResponseReader reader = new ResponseReader();
                        List<Recipe> recipes = reader.parseJSON(response);
                        recipeArrayList = recipes;


                        if(recipeArrayList.isEmpty()){
                            Toast.makeText(getApplicationContext(),"No Data Available ",Toast.LENGTH_SHORT);
                        }else{
                            recipeImageAdapter = new RecipeImageAdapter(getApplicationContext(), recipeArrayList, RecipeImageAdapter.SOURCE_NETWORK);
                            recipeGrid.setAdapter(recipeImageAdapter);
                            callback.markSuccess();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
            });

        recipeListRequest.setTag(LOG_TAG);
        recipeListRequestQueue.add(recipeListRequest);
    }

    @Override
    public boolean markSuccess() {
       // Log.d(LOG_TAG," Successful reply ! ");
        updateWidget();
        return true;
    }

    public RequestQueue getRequestQueue(){
        return recipeListRequestQueue;
    }


    private  void updateWidget(){
        Intent intent = new Intent(this,BakingAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), BakingAppWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }
}
