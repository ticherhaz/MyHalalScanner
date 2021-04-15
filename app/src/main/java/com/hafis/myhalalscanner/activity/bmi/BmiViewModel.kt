package com.hafis.myhalalscanner.activity.bmi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToInt

class BmiViewModel : ViewModel() {
    private val _weight: MutableLiveData<Double> = MutableLiveData()
    val weight: LiveData<Double> get() = _weight

    private val _height: MutableLiveData<Double> = MutableLiveData()
    val height: LiveData<Double> get() = _height

    private val _calculate: MutableLiveData<Double> = MutableLiveData()
    val calculate: LiveData<Double> get() = _calculate

    fun calculate(weight: Double, height: Double) = viewModelScope.launch {
        val result = weight / ((height / 100).pow(2.0))
        val number2digits: Double = (result * 100.0).roundToInt() / 100.0
        _calculate.value = number2digits
    }
}