package com.rnprojectreact

import android.app.Application
import com.callstack.reactnativebrownfield.OnJSBundleLoaded
import com.callstack.reactnativebrownfield.ReactNativeBrownfield
import com.facebook.react.PackageList
import com.facebook.react.ReactNativeApplicationEntryPoint.loadReactNative
import com.facebook.react.ReactPackage

object ReactNativeHostManager {
    fun initialize(application: Application, externalPackages: List<ReactPackage> = emptyList(), onJSBundleLoaded: OnJSBundleLoaded? = null) {
        loadReactNative(application)

        val packageList = PackageList(application).packages + externalPackages
        ReactNativeBrownfield.initialize(application, packageList, onJSBundleLoaded)
    }
}
