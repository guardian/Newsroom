package com.theguardian.newsroom.reporter

import com.theguardian.newsroom.Newsroom
import com.theguardian.newsroom.model.Event

abstract class Reporter(private val sourceName: String) : ReporterTasks {
    private var _newsroom: Newsroom? = null
    private val newsroom: Newsroom
        get() {
            return _newsroom
                    ?: throw IllegalStateException("This reporters gone rogue, give them a newsroom")
        }

    override fun setNewsroom(newsroom: Newsroom) {
        _newsroom = newsroom
    }

    fun reportEvent(title: String, data: Map<String, String?>? = null) {
        newsroom.reportEvent(Event(sourceName, title, data))
    }
}
