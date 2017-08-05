package com.mynanodegreeapps.bakingapp.widget;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mynanodegreeapps.bakingapp.R;
import com.mynanodegreeapps.bakingapp.model.Ingredient;
import com.mynanodegreeapps.bakingapp.model.Recipe;
import java.util.List;

import static com.mynanodegreeapps.bakingapp.activity.BakingActivity.recipeListForWidget;

public class BakingAppRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;

    public  BakingAppRemoteViewFactory(Context c){
        mContext = c;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

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

    public List<Ingredient> getIngredients(int recipeId){
        for(Recipe recipe : recipeListForWidget){
            if(recipe.getRecipeId() == recipeId){
                return recipe.getIngredients();
            }
        }
        return null;

    }
}
