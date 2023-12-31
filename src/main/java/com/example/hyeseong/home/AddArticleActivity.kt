package com.example.hyeseong.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.example.hyeseong.DBKey.Companion.DB_ARTICLES
import com.example.hyeseong.R

class AddArticleActivity : AppCompatActivity() {

    private var selectedUri: Uri? = null

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){ uri->

        if (uri != null) {
            // 사진을 정상적으로 가져온 경우;
            findViewById<ImageView>(R.id.photoImageView).setImageURI(uri)
            selectedUri = uri
        } else {
            Toast.makeText(this, " 사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_article)

        // 이미지 추가 버튼;
        initImageAddButton()

        // 게시글 등록하기 버튼;
        initSubmitButton()

    }

    private fun initSubmitButton() {
        findViewById<Button>(R.id.submitButton).setOnClickListener {
            showProgress()
            // 입력된 값 가져오기;
            val title = findViewById<EditText>(R.id.titleEditText).text.toString()
            val price = findViewById<EditText>(R.id.priceEditText).text.toString()
            val sellerId = auth.currentUser?.uid.orEmpty()

            // 중간에 이미지가 있으면 업로드 과정을 추가
            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(photoUri,
                    successHandler = { url -> // 다운로드 url 을 받아서 처리;
                        uploadArticle(sellerId, title, price, url)
                    },
                    errorHandler = {
                        Toast.makeText(this, "사진 업로드 실패.", Toast.LENGTH_SHORT)
                            .show()
                        hideProgress()
                    })
            } else {
                // 이미지가 없는 경우 빈 문자열
                uploadArticle(sellerId, title, price, "")
                hideProgress()
            }

            // 모델 생성;

        }
    }

    private fun initImageAddButton() {
        findViewById<Button>(R.id.imageAddButton).setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> { // 권한을 가지고 있는 경우;
                    startContentProvider()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_IMAGES) -> {
                    // 교육이 필요한 경우;
                    showPermissionContextPop()
                }
                else -> {
                    // 권한 요청;
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                        1010
                    )
                }
            }

        }
    }

    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        var fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("article/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) { // 업로드 과정 완료
                    // 다운로드 url 가져오기
                    storage.reference.child("article/photo").child(fileName).downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    Log.d("sslee", it.exception.toString())
                    errorHandler()
                }
            }
    }

    private fun uploadArticle(sellerId: String, title: String, price: String, imageUrl: String) {
        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), "${price}원", imageUrl)

        // 데이터베이스에 업로드;
        articleDB.push().setValue(model)

        hideProgress()
        finish()
    }

    // 권힌 요청 결과 확인;
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1010 -> {
                // 권한을 허용한 경우;
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startContentProvider()
                } else { // 권한을 거부한 경우;
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun startContentProvider() {
        // 이미지 SAF 기능 실행; 이미지 가져오기;

        // old ver.
        //val intent = Intent(Intent.ACTION_GET_CONTENT)
        //intent.type = "image/*"
        // startActivityForResult(intent, 2020) // deprecated

        // new ver.
        getContent.launch("image/*")
    }

    private fun showProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = true

    }

    private fun hideProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = false
    }


    private fun showPermissionContextPop() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES), 1010)
            }
            .create()
            .show()
    }

}