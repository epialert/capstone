package com.dicoding.capstoneprojek.ui.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.capstoneprojek.data.di.Injection
import com.dicoding.capstoneprojek.data.repository.Repository
import com.dicoding.capstoneprojek.ui.login.ViewModelLogin
import com.dicoding.capstoneprojek.ui.main.ViewModelMain
import com.dicoding.capstoneprojek.ui.register.ViewModelRegister

class FactoryViewModel(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {


            modelClass.isAssignableFrom(ViewModelMain::class.java) -> {
                ViewModelMain(repository) as T
            }
            modelClass.isAssignableFrom(ViewModelLogin::class.java) -> {
                ViewModelLogin(repository) as T
            }
            modelClass.isAssignableFrom(ViewModelRegister::class.java) -> {
                ViewModelRegister(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: FactoryViewModel? = null
        @JvmStatic
        fun getInstance(context: Context): FactoryViewModel {
            if (INSTANCE == null) {
                synchronized(FactoryViewModel::class.java) {
                    INSTANCE = FactoryViewModel(Injection.provideRepository(context))
                }
            }
            return INSTANCE as FactoryViewModel
        }
    }
}