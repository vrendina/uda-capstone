/*
 * Copyright (C) 2015-2017 Level Software LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.levelsoftware.carculator.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.ui.SplashActivity;
import io.levelsoftware.carculator.ui.quoteform.QuoteFormActivity;
import timber.log.Timber;

public class QuoteListWidgetIntentService extends IntentService {
    public QuoteListWidgetIntentService() {super("QuoteListWidgetIntentService");}

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager
                .getAppWidgetIds(new ComponentName(this, QuoteListWidgetProvider.class));

        for(int widgetId: appWidgetIds) {
            Timber.d("Updating widget with id " + widgetId);

            RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.widget_quote_list);

            // Create an Intent to launch MainActivity
            Intent homeIntent = new Intent(this, SplashActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, homeIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_list_header, pendingIntent);

            // Set up the collection by providing the remote view service
            views.setRemoteAdapter(R.id.list_view_widget,
                    new Intent(this, QuoteListRemoteViewService.class));

            // Set the intent for each list item
            Intent intentTemplate = new Intent(this, QuoteFormActivity.class);

            PendingIntent pendingIntentTemplate = TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(intentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.list_view_widget, pendingIntentTemplate);

            views.setEmptyView(R.id.list_view_widget, R.id.text_view_widget_empty);

            appWidgetManager.updateAppWidget(widgetId, views);
        }

    }
}
