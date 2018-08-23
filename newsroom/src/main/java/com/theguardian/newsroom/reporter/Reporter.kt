package com.theguardian.newsroom.reporter

import com.theguardian.newsroom.Newsroom
import com.theguardian.newsroom.model.Event


abstract class Reporter {

    open val sourceName: String = ""
    private var _newsroom: Newsroom? = null
    private val newsroom: Newsroom
        get() {
        return _newsroom ?: throw IllegalStateException("This reporters gone rogue, give them a newsroom")
    }
    
    fun setNewsroom(newsroom: Newsroom){
        this._newsroom = newsroom
    }

    fun sendEvent(event: Event){
        newsroom.reportEvent(event)
    }

    open fun onStart(){

    }
}