package com.mynanodegreeapps.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class BakingAppRemoteViewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        System.out.println("--> onGetViewFactory ");
          return new BakingAppRemoteViewFactory(this.getApplicationContext());

    }
}
