package atomsandbots.android.sweepsnatch.user;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import atomsandbots.android.sweepsnatch.user.UI.MainActivity;


public class HoneyWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        // Intent to launch MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Get the layout for the App Widget and attach an on-click listener
        // to the button
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.honey_widget);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);


        //Update Widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }


}