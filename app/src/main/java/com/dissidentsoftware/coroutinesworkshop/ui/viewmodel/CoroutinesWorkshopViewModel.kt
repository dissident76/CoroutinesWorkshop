package com.dissidentsoftware.coroutinesworkshop.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dissidentsoftware.coroutinesworkshop.common.kotlin.CoroutineConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToLong

private const val DELAY_ITERATION_MILLISECONDS = 1000L

class CoroutinesWorkshopViewModel: ViewModel() {

    private val mainFlowId = "MAIN_FLOW"
    private val mainDispatcher = Dispatchers.Main
    private val mainWorkDuration = 3000L

    private val coroutinesWorkTracker: MutableMap<Pair<String, CoroutineDispatcher>, Int> = mutableMapOf()
    private val coroutinesConfigs: List<CoroutineConfig> = listOf(
        CoroutineConfig("A", Dispatchers.Main, 5000, true),
        CoroutineConfig("B", Dispatchers.Unconfined, 5000,false),
        CoroutineConfig("C", Dispatchers.Unconfined, 5000,false),
    ).onEach { coroutinesWorkTracker[it.id to it.dispatcher] = 0 }
        .also { coroutinesWorkTracker[mainFlowId to mainDispatcher] = 0 }


    fun startPlayingWithCoroutines() {

        println("CHECKPOINT -> START ***************************************")

        viewModelScope.launch {
            coroutinesConfigs.forEach { coroutineConfig ->
                launch {
                    val coroutineId = coroutineConfig.id
                    val dispatcher = coroutineConfig.dispatcher
                    val duration = coroutineConfig.workDurationInMillis
                    val shouldBlock = coroutineConfig.shouldPerformBlockingWork

                    withContext(dispatcher) {
                        if (shouldBlock) blockingWork(duration, coroutineId, dispatcher)
                        else suspendingWork(duration, coroutineId, dispatcher)
                    }

                    println("CHECKPOINT -> '$coroutineId' FINISHED in $dispatcher (${Thread.currentThread().name} thread)")
                }
            }
        }

        println("MAIN FLOW -> RESUMED")
        blockingWork(mainWorkDuration, mainFlowId, mainDispatcher)
        println("CHECKPOINT -> '$mainFlowId' FINISHED in $mainDispatcher (${Thread.currentThread().name} thread)")
        println("CHECKPOINT -> FINISH ***************************************")
    }

    private suspend fun suspendingWork(
        timeMillis: Long,
        coroutineId: String,
        dispatcher: CoroutineDispatcher
    ) {
        for (i in 0..getNumberOfIterations(timeMillis)) {
            println("'$coroutineId' -> Working (suspend) in $dispatcher (${Thread.currentThread().name} thread) for $i seconds")
            delay(DELAY_ITERATION_MILLISECONDS)

            coroutinesWorkTracker[coroutineId to dispatcher] = (coroutinesWorkTracker[coroutineId to dispatcher] ?: 0) + 1
        }
        coroutinesWorkTracker[coroutineId to dispatcher] = Int.MAX_VALUE
    }

    private fun blockingWork(
        timeMillis: Long,
        coroutineId: String,
        dispatcher: CoroutineDispatcher
    ) {
        var hasPreviousBlockingWarning = false
        val previousIterations = coroutinesWorkTracker.toMutableMap()
        for (i in 0..getNumberOfIterations(timeMillis)) {
            println("'$coroutineId' -> Working (non-suspend) in $dispatcher (${Thread.currentThread().name} thread) for $i seconds")

            Thread.sleep(DELAY_ITERATION_MILLISECONDS)

            coroutinesWorkTracker[coroutineId to dispatcher] = (coroutinesWorkTracker[coroutineId to dispatcher] ?: 0) + 1

            if (coroutinesWorkTracker.filterNot { it.key == coroutineId to dispatcher
                        || it.key.second != dispatcher
                        || it.value == Int.MAX_VALUE }
                    .takeIf { it.isNotEmpty() }
                    ?.all { it.value == previousIterations[it.key] } == true
            ) {
                if (hasPreviousBlockingWarning) { println("*'$coroutineId'* -> *** BLOCKING $dispatcher (${Thread.currentThread().name} thread) *** !!") }
                else { hasPreviousBlockingWarning = true}
            } else {
                hasPreviousBlockingWarning = false
            }
        }
        coroutinesWorkTracker[coroutineId to dispatcher] = Int.MAX_VALUE
    }

    private fun getNumberOfIterations(timeMillis: Long) = (timeMillis.toFloat() / DELAY_ITERATION_MILLISECONDS).roundToLong()
}