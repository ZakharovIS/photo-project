package com.example.unsplashpractice.downloader

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

class UnsplashDownloader(
    context: Context
): Downloader {


    private val downloadManager: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    override fun downloadFile(url: String, title: String): Long {

        val request = DownloadManager.Request(url.toUri())
            .setMimeType("image/jpeg")
            //.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
            .setTitle("${title}.jpg")
            .setAllowedOverRoaming(false)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "${title}.jpg")
        return downloadManager.enqueue(request)
    }


}