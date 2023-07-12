package com.smartel.mysmartel_ver_1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val svcNameLiveData: MutableLiveData<String> = MutableLiveData()
    val freeMinUseLiveData: MutableLiveData<String> = MutableLiveData()
}
