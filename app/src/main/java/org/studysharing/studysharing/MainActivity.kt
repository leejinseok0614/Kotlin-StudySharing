package org.studysharing.studysharing

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import org.studysharing.studysharing.MyApplication.Companion.requestPermission
import org.studysharing.studysharing.MyApplication.Companion.userUid
import org.studysharing.studysharing.databinding.ActivityMainBinding
import org.studysharing.studysharing.navigation.AlarmFragment
import org.studysharing.studysharing.navigation.DetailViewFragment
import org.studysharing.studysharing.navigation.GridFragment
import org.studysharing.studysharing.navigation.UserFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
//    private val photoContentVM: PhotoContentViewModel by viewModels()
//    private val profileVm: ProfileViewModel by viewModels()
//    private val alarmsVm: AlarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setBottomNavigation()
        requestPermission(this)
    }

    private fun setToolbarDefault() {
        binding.toolbarImg.visibility = View.VISIBLE
        binding.toolbarBackBtn.visibility = View.GONE
        binding.toolbarUsernameTv.visibility = View.GONE
    }

    fun setToolbarForOtherUser(userId: String?) {
        binding.toolbarImg.visibility = View.GONE
        binding.toolbarBackBtn.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                goUserFragment()
            }
        }
        binding.toolbarUsernameTv.apply {
            visibility = View.VISIBLE
            text = userId
        }
    }

    fun goUserFragment() {
        binding.bottomNavigation.selectTab(binding.bottomNavigation.getTabAt(0))
    }

    private fun setBottomNavigation() {
        val listener = object : TabLayout.OnTabSelectedListener {
            private var selectedPosition = -1

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: return
                if (selectedPosition == position) return

                val transaction = supportFragmentManager.beginTransaction()

                when (position) {
                    0 -> {
                        val detailViewFragment = DetailViewFragment()
                        transaction.replace(
                            R.id.main_container,
                            detailViewFragment,
                            "detailViewFragment"
                        ).commit()

                        selectedPosition = position
                    }
                    1 -> {
                        val gridFragment = GridFragment()
                        transaction.replace(R.id.main_container, gridFragment, "gridFragment")
                            .commit()

                        selectedPosition = position
                    }
                    2 -> {
                        startActivity(
                            Intent(
                                this@MainActivity,
                                CalendarActivity::class.java
                            ).apply {
                                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            })

                        binding.bottomNavigation.selectTab(
                            binding.bottomNavigation.getTabAt(
                                selectedPosition
                            )
                        )
                    }
                    3 -> {
                        addPhotoActivityResult.launch(
                            Intent(
                                this@MainActivity,
                                AddPhotoActivity::class.java
                            )
                        )

                        binding.bottomNavigation.selectTab(
                            binding.bottomNavigation.getTabAt(
                                selectedPosition
                            )
                        )
                    }
                    4 -> {
                        val alarmFragment = AlarmFragment()
                        transaction.replace(R.id.main_container, alarmFragment, "alarmFragment")
                            .commit()

                        selectedPosition = position
                    }
                    5 -> {
                        val userFragment = UserFragment()
                            .apply {
                                arguments = Bundle().apply { putString("uid", userUid) }
                            }
                        transaction.replace(R.id.main_container, userFragment, "userFragment")
                            .commit()

                        selectedPosition = position
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        }

        binding.bottomNavigation.addOnTabSelectedListener(listener)

        val tab = binding.bottomNavigation.getTabAt(0) ?: return
        listener.onTabSelected(tab)
        binding.bottomNavigation.selectTab(tab)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        println("xxx requestCode($requestCode), permissions(${permissions.toList()}, grantResults(${grantResults.toList()})")

        permissions.forEachIndexed { index, permission ->
            if (permission == Manifest.permission.READ_EXTERNAL_STORAGE
                && grantResults[index] == -1
            ) {
                Snackbar.make(binding.toolbar, "앨범 접근을 위해 저장소 권한을 허용해주세요.", 1500).show()
            }
        }
    }

    private val addPhotoActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { ar ->
        if (ar.resultCode == RESULT_OK) Snackbar.make(
            binding.toolbar,
            getString(R.string.upload_success),
            1000
        ).show()
//        else Snackbar.make(binding.toolbar, getString(R.string.upload_fail), 1500).show()
    }

}