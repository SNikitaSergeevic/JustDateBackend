package com.example

import com.example.feauteres.controllers.ChatController
import org.koin.core.scope.get
import org.koin.dsl.module

val mainModule = module {
    single {
        ChatController()
    }
}