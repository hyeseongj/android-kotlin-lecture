package com.example.hyeseong

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.hyeseong.chatList.ChatListFragment
import com.example.hyeseong.home.HomeFragment
import com.example.hyeseong.myPage.MyPageFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val chatListFragment = ChatListFragment()
        val myPageFragment = MyPageFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigationView)

        replaceFragment(homeFragment) // 최초 홈 설정.

        // 네비게이션 버튼 리스너;
        bottomNavigationView.setOnItemSelectedListener { MenuItem->
            when (MenuItem.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.chatList -> replaceFragment(chatListFragment)
                R.id.myPage -> replaceFragment(myPageFragment)
            }
            true
        }
    }

    // fragmet 화면 전환
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction() // 트랜젝션 : 작업을 시작한다고 알려줌;
            .apply {
                replace(R.id.fragmentContainer, fragment)
                commit() // 트랜잭션 끝.
            }
    }
}