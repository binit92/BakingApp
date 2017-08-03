package com.mynanodegreeapps.bakingapp.widget;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.mynanodegreeapps.bakingapp.R;
import com.mynanodegreeapps.bakingapp.model.Ingredient;
import com.mynanodegreeapps.bakingapp.model.Recipe;
import com.mynanodegreeapps.bakingapp.util.ResponseReader;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.mynanodegreeapps.bakingapp.activity.BakingActivity.recipeListForWidget;

public class BakingAppRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    JsonArrayRequest recipeListRequest;
    RequestQueue recipeListRequestQueue;

    public  BakingAppRemoteViewFactory(Context c){
        mContext = c;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        System.out.println("--> onDataSetChanged()");
        getData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return recipeListForWidget.size();
    }

    @Override
    public RemoteViews getViewAt(int pos) {

        System.out.println("--> get View At " + pos);
        Log.v(mContext.getClass().getSimpleName(), "pos: "+pos);

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.baking_widget_item);
        String name = "<b>" + recipeListForWidget.get(pos).getName() + "</b>";
        rv.setTextViewText(R.id.recipeName, Html.fromHtml(name));

        int id = recipeListForWidget.get(pos).getRecipeId();

        List<Ingredient> ingredients = getIngredients(id);
        StringBuffer buffer = new StringBuffer();
        for(int i=0; i<ingredients.size(); i++){
            buffer.append(ingredients.get(i).getIngredient() + ",");
        }
        rv.setTextViewText(R.id.ingredients,buffer.toString());
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void getData(){

        recipeListRequestQueue =  Volley.newRequestQueue(mContext);
        Uri requestUri = Uri.parse(mContext.getString(R.string.SERVER_URL));
        recipeListRequest = new JsonArrayRequest(Request.Method.GET
                ,requestUri.toString()
                , null
                ,new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {

                ResponseReader reader = new ResponseReader();
                List<Recipe> recipes = reader.parseJSON(response);
                recipeListForWidget = recipes;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        recipeListRequest.setTag(BakingAppRemoteViewFactory.class.getSimpleName());
        recipeListRequestQueue.add(recipeListRequest);
    }

    public List<Ingredient> getIngredients(int recipeId){
        System.out.println("--> getIngredients , recipeId " + recipeId);
        /*if(recipeListForWidget.isEmpty()){

            Uri requestUri = Uri.parse(mContext.getString(R.string.SERVER_URL));
            RequestQueue recipeListRequestQueue  =  Volley.newRequestQueue(mContext);;
            JsonArrayRequest recipeListRequest = new JsonArrayRequest(Request.Method.GET
                    ,requestUri.toString()
                    , null
                    ,new Response.Listener<JSONArray>(){
                @Override
                public void onResponse(JSONArray response) {

                    ResponseReader reader = new ResponseReader();
                    List<Recipe> recipes = reader.parseJSON(response);
                    recipeListForWidget = recipes;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }*/
        for(Recipe recipe : recipeListForWidget){
            if(recipe.getRecipeId() == recipeId){
                return recipe.getIngredients();
            }
        }
        return null;

    }
}
