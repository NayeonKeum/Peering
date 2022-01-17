package com.awesomesol.peering.activity

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

//import com.kakao.auth.KakaoSDK 이거 아이니니니ㅣ니임


class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "be78d09b4140cf769f40fba0f8cab07f")
    }
}

//class GlobalApplication : Application()
//{
//    companion object
//    {
//        var instance: GlobalApplication? = null
//    }
//
//    override fun onCreate()
//    {
//        super.onCreate()
//        KakaoSDK.init(this, "be78d09b4140cf769f40fba0f8cab07f")

//        instance = this
//        KakaoSDK.init(KakaoSDKAdapter())
//    }
//
//    override fun onTerminate()
//    {
//        super.onTerminate()
//        instance = null
//    }
//
//    fun getGlobalApplicationContext(): GlobalApplication
//    {
//        checkNotNull(instance) { "this application does not inherit com.kakao.GlobalApplication" }
//        return instance!!
//    }

//}