package com.awesomesol.peering.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.awesomesol.peering.R
import com.awesomesol.peering.databinding.ActivityLoginBinding
import com.awesomesol.peering.databinding.ActivityMainBinding

import com.kakao.auth.ApiErrorCode
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException



class LoginActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityLoginBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    private lateinit var callback: SessionCallback


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        // 바인딩
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().checkAndImplicitOpen()

        binding.kakaoLogin.setOnClickListener {
            callback = SessionCallback()
            Session.getCurrentSession().addCallback(callback)
            Session.getCurrentSession().checkAndImplicitOpen()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data))
        {
            Log.e("로그인", "onActivityResult()에서 세션 획득!!")
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy()
    {
        mBinding = null
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback)
    }

    inner class SessionCallback : ISessionCallback
    {
        override fun onSessionOpenFailed(exception: KakaoException?)
        {
            Log.e("Log", "Session Call back :: onSessionOpenFailed: ${exception?.message}")
        }

        override fun onSessionOpened()
        {
            UserManagement.getInstance().me(object : MeV2ResponseCallback()
            {

                override fun onFailure(errorResult: ErrorResult?)
                {
                    val result = errorResult?.errorCode
                    if (result == ApiErrorCode.CLIENT_ERROR_CODE)
                    {
                        Toast.makeText(this@LoginActivity, "네트워크 연결이 불안정합니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else
                    {
                        Toast.makeText(this@LoginActivity, "로그인 도중 오류가 발생했습니다 : ${errorResult?.errorMessage}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onSessionClosed(errorResult: ErrorResult?)
                {
                    Toast.makeText(this@LoginActivity, "세션이 닫혔습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(result: MeV2Response?)
                {
                    Log.e("카카오 로그인", "결과 : $result")
                    Log.e("카카오 로그인", "아이디 : ${result!!.id}")
                    Log.e("카카오 로그인", "이메일 : ${result.kakaoAccount.email}")
                    Log.e("카카오 로그인", "프로필 이미지 : ${result.kakaoAccount.profile.profileImageUrl}")
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("id", result.id)
                    intent.putExtra("email", result!!.kakaoAccount.email)
                    intent.putExtra("nickname", result!!.kakaoAccount.profile.nickname)
                    intent.putExtra("profileImagePath", result.kakaoAccount.profile.profileImageUrl.toString())

                    startActivity(intent)
                    finish()
                }
            })
        }
    }
}