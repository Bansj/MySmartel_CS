package com.smartelmall.mysmartel_ver_1

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class LargeWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    // onReceive() 메서드는 브로드캐스트 메시지를 받아 처리하는 함수입니다.
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // MyInfoFragment에서 보낸 데이터를 받아옵니다.
        if (intent.action == "android.appwidget.action.APPWIDGET_UPDATE") {
            val widgetData = intent.getStringExtra("widgetData")
            val widgetCall = intent.getStringExtra("widgetCall")
            val widgetMessage = intent.getStringExtra("widgetMessage")

            Log.d("ReceivedData", "LargeWidgert <- MyInfoFragment (SKT): $widgetData")
            Log.d("ReceivedCall", "LargeWidgert <- MyInfoFragment (SKT): $widgetCall")
            Log.d("ReceivedMessage", "LargeWidgert <- MyInfoFragment (SKT): $widgetMessage")

            // 데이터 업데이트 함수 호출
            updateAppWithNewData(context, widgetData, widgetCall, widgetMessage)
        }
        // Update 버튼 클릭 시 동작 정의
        if(intent.action == ACTION_UPDATE_CLICK){
            // TODO: 실제 앱에 맞게 변경해야 합니다.
            val newData = "Updated Data"
            val newCall = "Updated Call"
            val newMessage = "Updated Message"

            updateAppWithNewData(context, newData, newCall, newMessage)
            Log.d(TAG,"Update button clicked!")
        }

        // Widget 클릭 시 앱으로 이동하게 하는 동작 정의
        if(intent.action == ACTION_WIDGET_CLICK){
            val i = Intent(context , MainActivity::class.java).apply{
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(i)
            Log.d(TAG,"Going to MainActivity!")
        }

    }

    private fun updateAppWithNewData(context: Context, data: String?, call: String?, message: String?) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val remoteViews = RemoteViews(context.packageName, R.layout.large_widget)

        // 위젯의 TextView 업데이트
        remoteViews.setTextViewText(R.id.wg_data, data)
        remoteViews.setTextViewText(R.id.wg_call, call)
        remoteViews.setTextViewText(R.id.wg_M, message)

        // 위젯을 클릭하면 앱이 열리도록 PendingIntent 설정
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0 , intent , 0)

        remoteViews.setOnClickPendingIntent(R.id.wg_layout,pendingIntent)

        // Update 버튼을 클릭하면 데이터가 즉시 갱신되도록 설정
        val updateIntent = Intent(ACTION_UPDATE_CLICK).apply{
            component = ComponentName(context,LargeWidget::class.java)
        }
        val updatePendingIntent = PendingIntent.getBroadcast(context , 0 ,updateIntent,PendingIntent.FLAG_UPDATE_CURRENT )

        remoteViews.setOnClickPendingIntent(R.id.btn_update ,updatePendingIntent )

        appWidgetManager.updateAppWidget(ComponentName(context,LargeWidget::class.java), remoteViews)
    }

    companion object{
        const val ACTION_UPDATE_CLICK = "ACTION_UPDATE_CLICK"
        const val ACTION_WIDGET_CLICK = "ACTION_WIDGET_CLICK"
    }

    // 위젯이 처음 생성될 때 호출되는 메소드
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    // 마지막 위젯이 제거될 때 호출되는 메소드
    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
   // val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.large_widget)


    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}