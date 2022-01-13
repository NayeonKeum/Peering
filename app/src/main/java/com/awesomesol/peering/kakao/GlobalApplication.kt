package com.awesomesol.peering.kakao

import android.app.Application
import com.kakao.auth.KakaoSDK

class GlobalApplication : Application()
{
    companion object
    {
        var instance: GlobalApplication? = null
    }

    override fun onCreate()
    {
        super.onCreate()

        instance = this
        KakaoSDK.init(KakaoSDKAdapter())
    }

    override fun onTerminate()
    {
        super.onTerminate()
        instance = null
    }

    fun getGlobalApplicationContext(): GlobalApplication
    {
        checkNotNull(instance) { "this application does not inherit com.kakao.GlobalApplication" }
        return instance!!
    }

}