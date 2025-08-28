package com.mafazaa.ainaa.data.repository_impl.data_source.local_data_source

import android.content.SharedPreferences

class LocalData(sharedPreferences: SharedPreferences){
    var apps by sharedPreferences.delegates.stringSet()
    var phoneNum by sharedPreferences.delegates.string()
    var level by sharedPreferences.delegates.protectionLevel()
    var downloadedVersion by sharedPreferences.delegates.int(0)
    /**
     * The last version of the app that was installed.
     * to check if the app was updated, compare this with the current version.
     */
    var lastVersion by sharedPreferences.delegates.int(0)
}
fun Set<String>.add(string:String):Set<String>{
    val m =this.toMutableSet()
    m.add(string)
    return m.toSet()
}
fun Set<String>.remove(string:String):Set<String>{
    val m =this.toMutableSet()
    m.remove(string)
    return m.toSet()
}