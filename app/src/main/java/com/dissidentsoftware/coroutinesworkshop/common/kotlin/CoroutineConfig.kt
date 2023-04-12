package com.dissidentsoftware.coroutinesworkshop.common.kotlin

import kotlinx.coroutines.CoroutineDispatcher

data class CoroutineConfig(
    val id: String,
    val dispatcher: CoroutineDispatcher,
    val workDurationInMillis: Long,
    val shouldPerformBlockingWork: Boolean
)
