package com.mafazaa.ainaa.services

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class FakeSavedStateRegistryOwner(
): SavedStateRegistryOwner {
    private val registry = SavedStateRegistryController.create(this)

    override val savedStateRegistry: SavedStateRegistry= registry.savedStateRegistry
    init {
        registry.performAttach()
        registry.performRestore(null)
    }
    override val lifecycle: Lifecycle=LifecycleRegistry(this).apply {
        currentState = Lifecycle.State.RESUMED
    }


}
