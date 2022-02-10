package com.awesomesol.peering.activity

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.awesomesol.peering.R
import com.awesomesol.peering.calendar.CalendarInfo
import com.awesomesol.peering.character.UserInfo
import com.awesomesol.peering.databinding.ActivityLoginBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kakao.network.ApiErrorCode
import com.kakao.network.ErrorResult
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.user.UserApiClient
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.UnLinkResponseCallback
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class LoginActivity : AppCompatActivity(){

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityLoginBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    private val TAG="로그인"

    private val fs= FirebaseFirestore.getInstance()
    var storage: FirebaseStorage= FirebaseStorage.getInstance()

    lateinit var uid:String
    lateinit var email:String
    lateinit var nickname:String
    lateinit var profileImagePath:String
    var mfriendList: HashMap<String, Int> = hashMapOf()

    private lateinit var dataList4: HashMap<String, ArrayList<HashMap<String, Any>>>
    private lateinit var contentList:HashMap<String, String>
    private lateinit var feedList:HashMap<String, String>


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        // 바인딩
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        
        // 자동로그인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Toast.makeText(this, "[자동 로그인] 토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
            }
            else if (tokenInfo != null) {
                Toast.makeText(this, "[자동 로그인] 토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()

                UserApiClient.instance.me { user, error ->
                    uid = user?.id.toString()
                    nickname = user?.kakaoAccount?.profile?.nickname.toString()
                    profileImagePath = user?.kakaoAccount?.profile?.profileImageUrl.toString()
                    email = user?.kakaoAccount?.email.toString()

                    TalkApiClient.instance.friends { friends, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡 친구 목록 가져오기 실패", error)
                        }
                        else if (friends != null) {
                            Log.i(TAG, "카카오톡 친구 목록 가져오기 성공 \n${friends.elements.joinToString("\n")}")
                            Log.d(TAG, friends.toString())
                            Log.d(TAG, "uid: $uid")

                            for (i in friends.elements.indices) {
                                mfriendList.put(friends.elements[i].id.toString(), 0) // 이거 일단 다 0으로 넣음
                            }
                            // 친구의 UUID 로 메시지 보내기 가능
                            fs.collection("users").whereEqualTo("uid", uid).get()
                                .addOnSuccessListener { documents ->
                                    // 이미 있는 유저

                                    // 유저 친구/프로필/닉네임 등은 수정이 필요할 수도 있잖슴
                                    // 친구는 근데 친구인지 아닌지가 있으니까.. 일단 이 기능은 보류.. ㅜㅜ

                                    for (document in documents) {
                                        var mergeable: HashMap<String, Any> = document.data as HashMap<String, Any>
                                        mergeable["nickName"] = nickname
                                        mergeable["profileUrl"] = profileImagePath
                                        mergeable["friendList"] = mfriendList
                                        fs.collection("users").document(uid).set(mergeable)
                                                .addOnSuccessListener { it ->
                                                    Log.d(TAG, "fs 에 유저 정보 수정 쨘")
                                                }
                                                .addOnFailureListener { exception ->
                                                    Log.d(TAG, "fs 에 유저 정보 수정 실패")
                                                }
                                    }
                                }
                                .addOnFailureListener { exception -> }

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        }
                    }
                }
            }
        }

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                        getAppKeyHash()
                        // 기타에러가 뜨면 로그캣에서 [Hash Key] 검색 후 그 옆에 나온 값을 카톡방에 공유해주세요~
                    }
                }
            }
            else if (token != null) {
                Toast.makeText(this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show()

                UserApiClient.instance.me { user, error ->
                    uid = user?.id.toString()
                    nickname = user?.kakaoAccount?.profile?.nickname.toString()
                    profileImagePath = user?.kakaoAccount?.profile?.profileImageUrl.toString()
                    email = user?.kakaoAccount?.email.toString()
                    // 카카오톡 친구 목록 가져오기 (기본)
                    TalkApiClient.instance.friends { friends, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡 친구 목록 가져오기 실패", error)
                        }
                        else if (friends != null) {
                            Log.i(TAG, "카카오톡 친구 목록 가져오기 성공 \n${friends.elements.joinToString("\n")}")
                            Log.d(TAG, friends.toString())

                            for (i in friends.elements.indices){
                                mfriendList.put(friends.elements[i].id.toString(), 0) // 이거 일단 다 0으로 넣음
                            }
                            // 친구의 UUID 로 메시지 보내기 가능

                            // 유저 데이터가 null 일 때(초기 방문) 갤러리에 있는 데이터들 전부 파베에 저장!

                            galleryDataCallback{ dataList4->
                                Log.d(TAG, "loginClear(dataList4)")
                                Log.d(TAG, "얘가 있니? $uid")
                                fs.collection("users").whereEqualTo("uid", uid).get()
                                    .addOnSuccessListener { documents ->

                                        ////// 여기서부턴 멤버들 한 번씩 넣고 나서는 제거
                                        binding.pBarLoginActivity.visibility= View.VISIBLE
                                        Log.d(TAG, "이미 있는 유저")
                                        val calID = "calendar" + Random().nextInt(1000000)

                                        val user = UserInfo(
                                                uid,
                                                nickname,
                                                profileImagePath,
                                                email,
                                                mfriendList,
                                                hashMapOf<String, String>("myCalendar" to calID)
                                        )

                                        // 유저 저장
                                        fs.collection("users").document(uid).set(user)
                                                .addOnSuccessListener {
                                                    Log.d(TAG, "fs 에 유저 정보 저장 쨘")
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.d(TAG, "유저 저장 에러 났음 쨘", e)
                                                    Toast.makeText(
                                                            this@LoginActivity,
                                                            "Peering에 오신 것을 환영합니다!",
                                                            Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                        // 내 캘린더 저장

                                        // HashMap<String, ArrayList<HashMap<String, Any>>>
                                        val dateList: MutableSet<String> = dataList4.keys
                                        var i=0
                                        for (date in dateList){
                                            // content 초기화
                                            contentList[date]=""
                                            feedList[date]=""
                                            // 그외
                                            val imgsOfDate:ArrayList<HashMap<String, Any>>? = dataList4[date]
                                            if (imgsOfDate != null) {
                                                for (img in imgsOfDate){
                                                    val imgUri:String= img["imageUri"] as String
                                                    val fileName="myCal$i"
                                                    img["imageUri"]=fileName
                                                    storage.reference.child(uid).child(calID).child(fileName)
                                                            .putFile(imgUri.toUri())
                                                            .addOnSuccessListener {
//                                                                taskSnapshot -> // 업로드 정보를 담는다
//                                                                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { it ->
//                                                                    // 업로드 한 url
//                                                                    var imageUrl = it.toString()
//                                                                    imgsOfDateReturn[idx]["imageUri"] = imageUrl
//                                                                    imgsOfDateReturn[idx]["used"] = imgsOfDate[idx]["used"] as Long
//                                                                }
//                                                                Log.d(TAG, "imgsOfDate[idx][\"imageUri\"]111 ${imgsOfDate[idx]["imageUri"]}")
//                                                                imgsOfDate[idx]["imageUri"]=fileName
//                                                                Log.d(TAG, "imgsOfDate[idx][\"imageUri\"]222 ${imgsOfDate[idx]["imageUri"]}")
                                                            }
                                                            .addOnFailureListener{
                                                            }

                                                    i+=1
                                                }
                                                dataList4[date]=imgsOfDate
                                            }
                                        }

                                        Log.d(TAG, "dataList4 $dataList4")

                                        // fs 저장
                                        val calData=
                                                CalendarInfo(arrayListOf(uid), calID, "내 캘린더", dataList4, contentList, feedList)
                                        fs.collection("calendars").document(calID).set(calData)
                                                .addOnSuccessListener {
                                                    Log.d(TAG, "캘린더 저장 성공")
                                                    binding.pBarLoginActivity.visibility= View.INVISIBLE
                                                }
                                                .addOnFailureListener{ e-> Log.d(TAG, "캘 저장 에러 났음", e)}

                                            /////////

//                                        // 이미 있는 유저
//
//                                        Log.d(TAG, "이미 있는 유저 $uid")
//                                        for (document in documents){
//                                            Log.d(TAG, document.data.toString())
//                                        }
//
//                                        // 유저 친구/프로필/닉네임 등은 수정이 필요할 수도 있잖슴
//                                        // 친구는 근데 친구인지 아닌지가 있으니까.. 일단 이 기능은 보류.. ㅜㅜ
//
//                                        for (document in documents){
//                                            var mergeable:HashMap<String, Any> = document.data as HashMap<String, Any>
//                                            mergeable["nickName"] = nickname
//                                            mergeable["profileUrl"] = profileImagePath
//                                            mergeable["friendList"] = mfriendList
//
//                                            fs.collection("users").document(uid).set(mergeable)
//                                                .addOnSuccessListener { it ->
//                                                    Log.d(TAG, "fs 에 유저 정보 수정 쨘")
//                                                }
//                                                .addOnFailureListener {
//                                                    Log.d(TAG, "fs 에 유저 정보 수정 실패")
//                                                }
//                                        }
                                    }
                                    .addOnFailureListener {
                                        // 첫 방문

                                        binding.pBarLoginActivity.visibility= View.VISIBLE
                                        Log.d(TAG, "이미 있는 유저")
                                        val calID = "calendar" + Random().nextInt(1000000)

                                        val user = UserInfo(
                                                uid,
                                                nickname,
                                                profileImagePath,
                                                email,
                                                mfriendList,
                                                hashMapOf<String, String>("myCalendar" to calID)
                                        )

                                        // 유저 저장
                                        fs.collection("users").document(uid).set(user)
                                                .addOnSuccessListener {
                                                    Log.d(TAG, "fs 에 유저 정보 저장 쨘")
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.d(TAG, "유저 저장 에러 났음 쨘", e)
                                                    Toast.makeText(
                                                            this@LoginActivity,
                                                            "Peering에 오신 것을 환영합니다!",
                                                            Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                        // 내 캘린더 저장

                                        // HashMap<String, ArrayList<HashMap<String, Any>>>
                                        val dateList: MutableSet<String> = dataList4.keys
                                        var i=0
                                        for (date in dateList){
                                            // content 초기화
                                            contentList[date]=""
                                            feedList[date]=""
                                            // 그외
                                            val imgsOfDate:ArrayList<HashMap<String, Any>>? = dataList4[date]
                                            if (imgsOfDate != null) {
                                                for (img in imgsOfDate){
                                                    val imgUri:String= img["imageUri"] as String
                                                    val fileName="myCal$i"
                                                    img["imageUri"]=fileName
                                                    storage.reference.child(uid).child(calID).child(fileName)
                                                            .putFile(imgUri.toUri())
                                                            .addOnSuccessListener {
//                                                                taskSnapshot -> // 업로드 정보를 담는다
//                                                                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { it ->
//                                                                    // 업로드 한 url
//                                                                    var imageUrl = it.toString()
//                                                                    imgsOfDateReturn[idx]["imageUri"] = imageUrl
//                                                                    imgsOfDateReturn[idx]["used"] = imgsOfDate[idx]["used"] as Long
//                                                                }
//                                                                Log.d(TAG, "imgsOfDate[idx][\"imageUri\"]111 ${imgsOfDate[idx]["imageUri"]}")
//                                                                imgsOfDate[idx]["imageUri"]=fileName
//                                                                Log.d(TAG, "imgsOfDate[idx][\"imageUri\"]222 ${imgsOfDate[idx]["imageUri"]}")
                                                            }
                                                            .addOnFailureListener{
                                                            }

                                                    i+=1
                                                }
                                                dataList4[date]=imgsOfDate
                                            }
                                        }


                                        // fs 저장
                                        val calData=
                                                CalendarInfo(arrayListOf(uid), calID, "내 캘린더", dataList4, contentList, feedList)
                                        fs.collection("calendars").document(calID).set(calData)
                                                .addOnSuccessListener {
                                                    Log.d(TAG, "캘린더 저장 성공")
                                                    binding.pBarLoginActivity.visibility= View.INVISIBLE
                                                }
                                                .addOnFailureListener{ e-> Log.d(TAG, "캘 저장 에러 났음", e)}
                                    }
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }

        binding.kakaoLogin.setOnClickListener{
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            LoginClient.instance.run {
                if (isKakaoTalkLoginAvailable(this@LoginActivity)) {
                    loginWithKakaoTalk(this@LoginActivity, callback = callback)
                } else {
                    loginWithKakaoAccount(this@LoginActivity, callback = callback)
                }
            }
        }


//        UserManagement.getInstance()
//                .requestUnlink(object : UnLinkResponseCallback() {
//                    override fun onSessionClosed(errorResult: ErrorResult) {
//                        Toast.makeText(applicationContext, "에러: " + errorResult + "로그인 세션이 닫혔습니다.", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onFailure(errorResult: ErrorResult) {
//                        val error = errorResult.errorCode
//                        if (error == ApiErrorCode.CLIENT_ERROR_CODE) {
//                            Toast.makeText(applicationContext, "에러: $error\n네트워크 연결이 불안정합니다.", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(applicationContext, "에러: $error\n회원 탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    override fun onSuccess(result: Long) {
//                        Toast.makeText(applicationContext, "성공: $result\n회원 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                })

        binding.kakaoLogoutButton.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
            }
        }

        binding.kakaoUnlinkButton.setOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Toast.makeText(this, "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                }
            }
        }


    }

    private fun calendarCallback(calID: String, fileName: String, imgUri: String, callback: (Uri?) -> Unit){
        storage.reference.child(uid).child(calID).child(fileName)
                .putFile(imgUri.toUri())
                .addOnSuccessListener { taskSnapshot -> // 업로드 정보를 담는다
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { it->
                        callback(it)
                    }
                }
    }

    fun galleryDataCallback(callback: (HashMap<String, ArrayList<HashMap<String, Any>>>) -> Unit){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 200)
        } else {
            // 갤러리 연동 분기점
            //initView()
            try {
                val cursor = getImageData()
                //getImages(cursor)
                if (cursor != null) {
                    dataList4= hashMapOf()
                    contentList=hashMapOf()
                    feedList= hashMapOf()
                    while (cursor.moveToNext()) {
                        // 날짜별 이미지 리스트 초기화

                        //1. 각 컬럼의 열 인덱스를 취득한다.
                        val idColNum = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
                        val titleColNum = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.TITLE)
                        val dateTakenColNum =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN)

                        //2. 인덱스를 바탕으로 데이터를 Cursor로부터 취득하기
                        val id = cursor.getLong(idColNum) // 0
                        val title = cursor.getString(titleColNum) // 1
                        val dateTaken = cursor.getLong(dateTakenColNum) // 2
//                val imageUri =
//                        withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                        var uri= ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                        )

                        //3. 데이터를 View로 설정
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = dateTaken
                        val date = DateFormat.format("yyyy-MM-dd", calendar).toString() // "yyyy-MM-dd (E) kk:mm:ss"

                        // 일단 2021.10.1이전 것들은 다 안 넣음
                        val targetDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
                        val standardDate = LocalDate.of(2021, 10, 1)

                        if (targetDate.isBefore(standardDate)){
                            continue
                        }

                        // Log.d(TAG, date)
                        // 앞에꺼 하나만 사용, 뒤에껀 안 사용!
                        if (date in dataList4.keys){
                            // 날짜가 이미 있다면
                            var hmap:HashMap<String, Any> = hashMapOf()
                            hmap["imageUri"]=uri.toString()
                            val ln:Long=0
                            hmap["used"]=ln
                            dataList4[date]?.add(hmap)
                        }
                        else{
                            // 날짜가 없음!
                            var hmap:HashMap<String, Any> = hashMapOf()
                            hmap["imageUri"]=uri.toString()
                            // 2는 대표사진
                            val ln:Long=2
                            hmap["used"]=ln
                            dataList4[date] = arrayListOf()
                            dataList4[date]?.add(hmap)

                        }

//                textView.text = "촬용일시: $text"
//                imageView.setImageURI(imageUri)

//                Log.d(TAG, "DATE: "+date)
//                Log.d(TAG, "ID: "+id)
//                Log.d(TAG, "TITLE: "+title)
//                Log.d(TAG, "URI: "+uri)
                    }
                    cursor.close()
                    Log.d(TAG + " 뭐 들었니", dataList4.toString())

                }
                callback(dataList4)

            } catch (e: SecurityException) {
                Toast.makeText(this, "스토리지에 접근 권한을 허가해주세요", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "스토리지에 접근 권한을 허가해주세요")
                // finish()
            }
        }
    }

    //해시 키 값 구하기
    private fun getAppKeyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                var md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something: String = String(Base64.encode(md.digest(), 0))
                Log.e("Hash key", something)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("name not found", e.toString())
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }
//
//    // 내 gallery 연동
//    private fun checkPermission() {
//        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 200)
//        } else {
//            // 갤러리 연동 분기점
//            initView()
//        }
//    }
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            200 -> {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    initView()
//                } else {
//                    Toast.makeText(this, "스토리지에 접근 권한을 허가해주세요", Toast.LENGTH_SHORT).show()
//                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 200)
//                }
//            }
//        }
//    }
//
//    private fun initView() {
//        try {
//            val cursor = getImageData()
//            getImages(cursor)
//
//        } catch (e: SecurityException) {
//            Toast.makeText(this, "스토리지에 접근 권한을 허가해주세요", Toast.LENGTH_SHORT).show()
//            Log.d(TAG, "스토리지에 접근 권한을 허가해주세요")
//            // finish()
//        }
//    }
//
    private fun getImageData(): Cursor {

        val resolver = contentResolver
        var queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        //가져올 컬럼명
        val what = arrayOf(
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.TITLE,
                MediaStore.Images.ImageColumns.DATE_TAKEN
        )

        //정렬
        val orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " ASC"

        //1건만 가져온다.
        //queryUri = queryUri.buildUpon().appendQueryParameter("limit", "1").build()

        return resolver!!.query(queryUri, what, null, null, orderBy)!!
    }


//    private fun getImages(cursor: Cursor){
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                // 날짜별 이미지 리스트 초기화
//
//                //1. 각 컬럼의 열 인덱스를 취득한다.
//                val idColNum = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
//                val titleColNum = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.TITLE)
//                val dateTakenColNum =
//                    cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN)
//
//                //2. 인덱스를 바탕으로 데이터를 Cursor로부터 취득하기
//                val id = cursor.getLong(idColNum) // 0
//                val title = cursor.getString(titleColNum) // 1
//                val dateTaken = cursor.getLong(dateTakenColNum) // 2
////                val imageUri =
////                        withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
//
//                var uri= ContentUris.withAppendedId(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    id
//                )
//
//                //3. 데이터를 View로 설정
//                val calendar = Calendar.getInstance()
//                calendar.timeInMillis = dateTaken
//                val date = DateFormat.format("yyyy-MM-dd", calendar).toString() // "yyyy-MM-dd (E) kk:mm:ss"
//                // Log.d(TAG, date)
//                // 앞에꺼 하나만 사용, 뒤에껀 안 사용!
//                if (date in dataList4.keys){
//                    // 날짜가 이미 있다면
//                    dataList4.get(date)?.add(GalleryData(uri.toString(), 0))
//                }
//                else{
//                    // 날짜가 없음!
//                    dataList4.put(date, arrayListOf())
//                    dataList4.get(date)?.add(GalleryData(uri.toString(), 1))
//
//                }
//
////                textView.text = "촬용일시: $text"
////                imageView.setImageURI(imageUri)
//
////                Log.d(TAG, "DATE: "+date)
////                Log.d(TAG, "ID: "+id)
////                Log.d(TAG, "TITLE: "+title)
////                Log.d(TAG, "URI: "+uri)
//            }
//            cursor.close()
//            Log.d(TAG + " 뭐 들었니", dataList4.toString())
//
//        }
//        // view?.findViewById<ImageView>(R.id.iv_CalendarFragment_test)?.setImageURI(calendarImages.get(targetDate)?.get(0)?.toUri())
//
//    }


}

