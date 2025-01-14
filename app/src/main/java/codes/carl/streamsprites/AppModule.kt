package codes.carl.streamsprites

import codes.carl.streamsprites.viewmodels.ConnectionViewModel
import org.koin.dsl.module

val appModule = module {
    single { ConnectionViewModel() }
}