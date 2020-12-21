package atomsandbots.android.sweepsnatch.user.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;

import atomsandbots.android.sweepsnatch.user.R;
import atomsandbots.android.sweepsnatch.user.Registration.RegisterActivity;
import atomsandbots.android.sweepsnatch.user.UI.MainActivity;

public class HoneyWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //I went for a simple function here if user is logged in get Main else RegisterActivity
        for (int appWidgetId : appWidgetIds) {

            Intent intent;
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                intent = new Intent(context, MainActivity.class);
            } else {
                intent = new Intent(context, RegisterActivity.class);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}
