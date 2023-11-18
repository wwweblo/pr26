package com.example.pr26

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var showNotificationButton: Button

    /*
    * CHANNEL_ID позволяет анстраивать поведение уведомлений: выключать звук, блокировать их (добавли в adroid 8)
    * NOTIFICATION_ID присваивает каждому уведомлению id чтобы android мог с ним взаимодействовать, например обновлять
    * REQUEST_NOTIFICATION_PERMISSION позволяет android отличать какое конкретно приложение запросило резрешение на показ уведомлений
    */

    private val CHANNEL_ID = "MyChannelID"
    private val NOTIFICATION_ID = 1
    private val REQUEST_NOTIFICATION_PERMISSION = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)
        showNotificationButton = findViewById(R.id.button)

        createNotificationChannel()

        // Проверка и запрос разрешения на уведомления
        if (!isNotificationPermissionGranted()) {
            Toast.makeText(this, "Уведомления отключены. Включите их в настройках приложения.", Toast.LENGTH_LONG).show()
            requestNotificationPermission()
        }

        showNotificationButton.setOnClickListener {
            val inputText = editText.text.toString()
            showNotification(inputText)
        }
    }

    private fun createNotificationChannel() {
        /*
        *   if проверяет, что версия андройд 8, или новее, так как на 8 версии добавили больше функционала в настройках уведомлений
        *   Канал в уведомлениях позволяет делить уведомненият на категории
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MyChannelName"
            val descriptionText = "MyChannelDescription"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(text: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(text)
            .setContentText("🥵")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(this)

        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } else {
            Toast.makeText(this, "Уведомления отключены. Включите их в настройках приложения.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return NotificationManagerCompat.from(this).areNotificationsEnabled()
    }

    private fun requestNotificationPermission() {
        // Запрос разрешения на уведомления
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        startActivityForResult(intent, REQUEST_NOTIFICATION_PERMISSION)
    }
}