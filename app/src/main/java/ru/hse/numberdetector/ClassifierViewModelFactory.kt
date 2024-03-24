package ru.hse.numberdetector

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClassifierViewModelFactory(
    private val context: Context
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        DigitClassifierViewModel(context) as T
}