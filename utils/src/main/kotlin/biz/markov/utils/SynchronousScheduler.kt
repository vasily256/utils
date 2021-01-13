package biz.markov.utils

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * SynchronousScheduler.
 * A scheduler that supports synchronous operation of all its instances.
 *
 * @author Vasily_Markov
 */
open class SynchronousScheduler(
    private val period: Long,
    private val timeUnit: TimeUnit = TimeUnit.SECONDS,
    private val threadName: String = "sync-scheduler",
    private val task: () -> Unit
) {

    companion object {
        const val MILLI_SCALE = 1
        const val SECOND_SCALE = 1_000
        const val MINUTE_SCALE = 60 * SECOND_SCALE
        const val HOUR_SCALE = 60 * MINUTE_SCALE
    }

    private lateinit var scheduler: ScheduledExecutorService

    fun start() {
        scheduler = Executors.newScheduledThreadPool(1) { Thread(it, threadName) }
        (period * getScaleFactor(timeUnit)).also { period ->
            val delayMillis = period - System.currentTimeMillis() % period
            scheduler.scheduleAtFixedRate(
                task,
                delayMillis,
                period,
                TimeUnit.MILLISECONDS
            )
        }
    }

    fun stop() {
        scheduler.shutdownNow()
    }

    private fun getScaleFactor(timeUnit: TimeUnit): Int =
        when (timeUnit) {
            TimeUnit.MILLISECONDS -> MILLI_SCALE
            TimeUnit.SECONDS -> SECOND_SCALE
            TimeUnit.MINUTES -> MINUTE_SCALE
            TimeUnit.HOURS -> HOUR_SCALE
            else -> throw IllegalArgumentException("Illegal TimeUnit")
        }
}}