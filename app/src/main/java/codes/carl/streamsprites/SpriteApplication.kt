package codes.carl.streamsprites

import android.app.Application
import android.content.Context
import codes.carl.streamsprites.sensors.PhoneSensor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.lang.ref.WeakReference

class SpriteApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SpriteApplication)
            modules(appModule)
        }
    }

    companion object {
        private var phoneSensor: WeakReference<PhoneSensor>? = null

        fun initializePhoneSensor(context: Context) {
            if (phoneSensor?.get() == null) {
                phoneSensor = WeakReference(PhoneSensor(context.applicationContext, context as PhoneSensor.PermissionRequester))
            }
        }

        fun getPhoneSensor(): PhoneSensor {
            return phoneSensor?.get() ?: throw UninitializedPropertyAccessException("PhoneSensor must be initialized in Application.onCreate()")
        }
    }
}