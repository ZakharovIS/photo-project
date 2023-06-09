package com.example.unsplashpractice.downloader

interface Downloader {
    fun downloadFile(url: String, title: String) :Long
}