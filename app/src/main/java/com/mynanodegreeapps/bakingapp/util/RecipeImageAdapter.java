package com.mynanodegreeapps.bakingapp.util;

import android.app.Application;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.mynanodegreeapps.bakingapp.R;
import com.mynanodegreeapps.bakingapp.activity.BakingDetailActivity;
import com.mynanodegreeapps.bakingapp.model.Recipe;
import com.mynanodegreeapps.bakingapp.widget.BakingAppRemoteViewService;
import com.mynanodegreeapps.bakingapp.widget.BakingAppWidgetProvider;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.mynanodegreeapps.bakingapp.activity.BakingActivity.recipeListForWidget;

public class RecipeImageAdapter extends RecyclerView.Adapter<RecipeImageAdapter.ViewHolder> {

    private Context c;
    List<Recipe> recipeList;
    private int source = 1;

    public static final int SOURCE_NETWORK = 1;
    public static final int SOURCE_DB = 2;

    private static final String LOG_TAG = RecipeImageAdapter.class.getSimpleName();

    public RecipeImageAdapter(Context c, List<Recipe> recipeList, int source){
        this.c=c;
        this.recipeList = recipeList;
        this.source = source;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View recipeImageView = inflater.inflate(R.layout.bakingrecipe_image,parent,false);

        // Return a new holder instance
        RecipeImageAdapter.ViewHolder viewHolder = new RecipeImageAdapter.ViewHolder(recipeImageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Recipe selectedRecipe = recipeList.get(position);

        // Set item views based on your views and data model
        ImageView imageView = viewHolder.recipeImage;
        TextView recipeText = viewHolder.recipeText;
        MaterialFavoriteButton favoriteButton = viewHolder.favButton;

        if(source == SOURCE_DB) {
            /* Todo : Get details from Database !
            */
        }else{
            // Get Details from Network
            String imageUrl = selectedRecipe.getImage();
            //Log.d(LOG_TAG,"imageUrl is" + imageUrl);

            //Automatically creates background thread and loads image
            try {
                Picasso.with(c).load(imageUrl).into(imageView);
            }catch (Throwable t) {
                Picasso.with(c).load(R.drawable.baking_junkfood).into(imageView);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(c,BakingDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("recipe",selectedRecipe);
                    c.startActivity(intent);
                }
            });

            String recipeName = selectedRecipe.getName();
            recipeText.setText(recipeName);
            recipeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(c,BakingDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("recipe", selectedRecipe);
                    c.startActivity(intent);
                }
            });

            favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    if(favorite){
                       recipeListForWidget.add(selectedRecipe);
                    }else{
                        recipeListForWidget.remove(selectedRecipe);
                    }
                    updateWidget();
                }
            });

        }
    }

    private  void updateWidget(){
        Intent intent = new Intent(c,BakingAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int ids[] = AppWidgetManager.getInstance(c).getAppWidgetIds(new ComponentName(c, BakingAppWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        c.sendBroadcast(intent);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        ImageView recipeImage;
        TextView recipeText;
        MaterialFavoriteButton favButton;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            recipeImage = (ImageView) itemView.findViewById(R.id.recipeImage);
            recipeText =  (TextView) itemView.findViewById(R.id.recipeText);
            favButton = (MaterialFavoriteButton) itemView.findViewById(R.id.favorite);

        }
    }


}
