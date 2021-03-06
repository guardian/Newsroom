package com.theguardian.newsroom.reporter

import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class GoogleAnalyticsReporter : Reporter("Google Analytics") {

    private val compositeDisposables: CompositeDisposable = CompositeDisposable()

    override fun onStart() {
        notifyWhenGaHitsAreSent()
    }

    override fun onStop() {
        compositeDisposables.clear()
    }

    companion object {
        private const val TAG = "Newsroom"
    }

    private fun logcat(options: String): Observable<String> {
        val observable: Observable<String> = Observable.create { emitter ->
            val process = Runtime.getRuntime().exec("logcat $options")
            val bufferedReader = process.inputStream.reader().buffered()

            emitter.setCancellable {
                bufferedReader.close()
                process.destroy()
            }

            // Note: forEachLine closes the reader automatically
            bufferedReader.forEachLine(emitter::onNext)

            process.destroy()
            emitter.onComplete()
        }
        return observable.subscribeOn(Schedulers.io())
    }

    private fun gaLogToMap(logLine: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val regex = Regex(", [a-z0-9_]+=")
        var start = 0
        var match = regex.find(logLine, start)
        while (match != null) {
            val split = logLine.subSequence(start, match.range.start).split("=", limit = 2)
            val key = split[0]
            val value = split[1]
            result[key] = value
            start = match.range.start + 2
            match = regex.find(logLine, start)
        }
        val split = logLine.subSequence(start, logLine.length).split("=", limit = 2)
        val key = split[0]
        val value = split[1]
        result[key] = value
        return result
    }

    private fun gaHitDeliveries(): Observable<String> {
        return logcat("-s GAv4")
                .filter { it.contains("Hit delivery requested") }
                .map { it.split("Hit delivery requested:").last() }
    }

    private fun notifyWhenGaHitsAreSent() {
        compositeDisposables.add(gaHitDeliveries()
                .map { gaLogToMap(it) }
                .subscribe({ map ->
                    if (map["t"] == "event") {
                        reportEvent("GA Event Tracked", mapOf(
                                "Category" to map["ec"],
                                "Action" to map["ea"],
                                "Label" to map["el"]
                        ))
                    }
                }, { err -> Log.w(TAG, err) }))
    }
}
