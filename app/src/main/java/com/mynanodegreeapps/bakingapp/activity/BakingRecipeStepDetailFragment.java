package com.mynanodegreeapps.bakingapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.mynanodegreeapps.bakingapp.model.Ingredient;
import com.mynanodegreeapps.bakingapp.model.Step;
import com.mynanodegreeapps.bakingapp.util.IRecipeStepCallback;
import com.squareup.picasso.Picasso;

import com.mynanodegreeapps.bakingapp.R;

import java.util.List;


/*  This Fragment will the show the details of Recipe Step i.e. details of "ingredients"
    and details of each recipe step that includes picture/video, recipe instruction and navigation etc"
 */

public class BakingRecipeStepDetailFragment extends Fragment implements ExoPlayer.EventListener{

    View rootView;
    LinearLayout videoLayout;
    LinearLayout descriptionLayout;
    List<Ingredient> ingredients ;
    List<Step> steps;
    Step step;
    int position;
    Uri currentVideoUrl;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mExoPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    private IRecipeStepCallback recipeStepCallback;

    Button btnPrev;
    Button btnNext;

    private static final String LOG_TAG = BakingRecipeStepDetailFragment.class.getSimpleName();

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
        if(mMediaSession != null){
           mMediaSession.setActive(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
        if (mMediaSession != null) {
            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(currentVideoUrl != null && !currentVideoUrl.toString().isEmpty()) {
            initializeExoPlayer(currentVideoUrl);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)           {
        rootView = inflater.inflate(R.layout.bakingrecipestepdetail_fragment,container,false);
        Bundle b = getArguments();

        if(rootView != null){
            ingredients = b.getParcelableArrayList("ingredients");
            steps = b.getParcelableArrayList("steps");
            position = b.getInt("position");
            setPosition(position);

            if(!getResources().getBoolean(R.bool.isTablet)) {
                btnPrev = (Button) rootView.findViewById(R.id.btn_prev);
                btnNext = (Button) rootView.findViewById(R.id.btn_next);
                buttonClickListeners();
            }
        }
        return rootView;
    }

    public void createLayouts(){

        if(videoLayout == null) {
            videoLayout = (LinearLayout) rootView.findViewById(R.id.videoContainer);
        }

        if(descriptionLayout == null) {
            descriptionLayout = (LinearLayout) rootView.findViewById(R.id.recipestepsdetailscontainer);
        }

        if (ingredients != null && position<0) {
            descriptionLayout.removeAllViews();
            videoLayout.setVisibility(View.GONE);
            createIngredientLayout(ingredients, descriptionLayout);
        }else{
            descriptionLayout.removeAllViews();
            videoLayout.setVisibility(View.VISIBLE);
            createVideoLayout(step, videoLayout,descriptionLayout);
        }
    }

    public void createIngredientLayout(List<Ingredient> ingredients , LinearLayout parentLayout){

        for(Ingredient ingredient : ingredients){
            LinearLayout linearLayout = new LinearLayout(getActivity().getApplicationContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.LEFT);
            linearLayout.setPadding(5,5,5,5);

            TextView quantityTextView = new TextView(getActivity().getApplicationContext());
            TextView measureTextView = new TextView(getActivity().getApplicationContext());
            TextView ingredientTextView = new TextView(getActivity().getApplicationContext());

            quantityTextView.setText(ingredient.getQuantity());
            quantityTextView.setTextColor(getResources().getColor(R.color.colorBackground));
            quantityTextView.setPadding(2,2,2,2);
            measureTextView.setText(ingredient.getMeasure());
            measureTextView.setTextColor(getResources().getColor(R.color.colorBackground));
            measureTextView.setPadding(2,2,2,2);
            ingredientTextView.setText(ingredient.getIngredient());
            ingredientTextView.setTextColor(getResources().getColor(R.color.colorBackground));
            ingredientTextView.setPadding(2,2,2,2);

            linearLayout.addView(quantityTextView);
            linearLayout.addView(measureTextView);
            linearLayout.addView(ingredientTextView);

            parentLayout.addView(linearLayout);
        }

    }

    public void createVideoLayout(Step step, LinearLayout videoLayout, LinearLayout parentLayout){

        // Initialize ExoPlayer
        if(mExoPlayerView == null) {
            mExoPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.video_view1);
        }
        releasePlayer();

        currentVideoUrl = Uri.parse(step.getVideoUrl());
        initializeMediaSession();
        initializeExoPlayer(currentVideoUrl);

        if(step.getDescription() != null) {
            TextView stepDescription = new TextView(getActivity().getApplicationContext());
            parentLayout.addView(stepDescription);
            stepDescription.setText(step.getDescription());
        }

        if(step.getThumbnailUrl() != null || !("").equals(step.getThumbnailUrl())){
            ImageView   stepImageView = new ImageView(getActivity().getApplicationContext());
            parentLayout.addView(stepImageView);
            Picasso.with(getActivity().getApplicationContext()).load(Uri.parse(step.getThumbnailUrl())).into(stepImageView);

        }
    }

    public void buttonClickListeners(){
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(step != null) {
                    setPosition(step.getStepId() -1);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(step != null) {
                     setPosition(step.getStepId()+1);
                }else if(step == null && position <0){
                    setPosition(0);
                }
            }
        });
    }
    public void setPosition(int pos){
        position = pos;
        if(position >=0 && position<steps.size()) {
            step = steps.get(position);
        }
        createLayouts();
    }

    private void initializeExoPlayer(Uri mediaUri){
        if(mExoPlayer == null){
            // create an instance of exoplayer
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(),trackSelector,loadControl);
            mExoPlayerView.setPlayer(mExoPlayer);

            mExoPlayer.addListener(this);

            // prepare the media source
            String userAgent = Util.getUserAgent(getContext(), getContext().getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(getContext(),
                    userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer(){
        if(mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private void initializeMediaSession(){
        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(),LOG_TAG);

        // Enable callbacks from MediaButtons and TransportControls
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when app is not visible
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial playback with ACTION_PLAY, so media buttons can start the player
        mStateBuilder  = new PlaybackStateCompat.Builder()
                        .setActions(
                                    PlaybackStateCompat.ACTION_PLAY |
                                    PlaybackStateCompat.ACTION_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from media controller
        mMediaSession.setCallback(new MySessionCallback());

        // Start the media Session since fragment is active
        mMediaSession.setActive(true);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

}

