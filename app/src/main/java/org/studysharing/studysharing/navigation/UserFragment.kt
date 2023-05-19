package org.studysharing.studysharing.navigation

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.studysharing.studysharing.LoginActivity
import org.studysharing.studysharing.MyApplication.Companion.auth
import org.studysharing.studysharing.MyApplication.Companion.userId
import org.studysharing.studysharing.MyApplication.Companion.userUid
import org.studysharing.studysharing.R
import org.studysharing.studysharing.adapter.AccountPhotoAdapter
import org.studysharing.studysharing.databinding.FragmentUserBinding
import org.studysharing.studysharing.viewmodel.FollowViewModel
import org.studysharing.studysharing.viewmodel.PhotoContentViewModel
import org.studysharing.studysharing.viewmodel.ProfileViewModel

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private lateinit var photoContentVm: PhotoContentViewModel
    private lateinit var profileVm: ProfileViewModel
    private lateinit var followVm: FollowViewModel
    private lateinit var accountPhotoAdapter: AccountPhotoAdapter

    private var curUid: String? = null

    override fun onStop() {
        super.onStop()
        photoContentVm.remove()
        profileVm.remove()
        followVm.remove()
    }

    override fun onResume() {
        super.onResume()
        curUid?.let {
            photoContentVm.getAllWhereUid(it)
            profileVm.getAllWhereUid(it)
            followVm.getAllWhereUid(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        curUid = arguments?.getString("uid")
        photoContentVm = ViewModelProvider(requireActivity())[PhotoContentViewModel::class.java]
        profileVm = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
        followVm = ViewModelProvider(requireActivity())[FollowViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUi()
        observePhotoContent()
        observeProfile()
        observeProfileResult()
        observeFollow()
    }

    private fun setUi() {
        // My page
        binding.userIdTv.text = userId

        lifecycleScope.launch {
            val level = profileVm.getLevel()
            binding.accountImgBorder.setImageDrawable(ColorDrawable(level.color or 0xFF000000.toInt()))
        }

        Glide.with(this)
            .load(R.drawable.ic_profile)
            .apply(RequestOptions().circleCrop())
            .into(binding.accountImg)

        binding.followOrSignoutBtn.apply {
            text = getString(R.string.signout)
            setOnClickListener {
                // 로그아웃
                activity?.finish()
                auth?.signOut()
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
        binding.accountImg.setOnClickListener {
            // Change profile image
            binding.progressBar.visibility = View.VISIBLE

            val photoPickerIntent = Intent(Intent.ACTION_PICK)
                .apply { type = "image/*" }
            photoPickerResult.launch(photoPickerIntent)
        }
        accountPhotoAdapter = AccountPhotoAdapter(requireContext())
            .apply {
                setHasStableIds(true)
            }
        binding.accountRv.apply {
            adapter = accountPhotoAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    fun observePhotoContent() {
        photoContentVm.uidContentLiveData.observe(viewLifecycleOwner) {
            println("xxx observePhotoContent() from UserFragment")
            binding.postCountTv.text = it.size.toString()
            accountPhotoAdapter.contents = it.reversed()
            accountPhotoAdapter.notifyDataSetChanged()
        }
    }

    fun observeProfile() {
        profileVm.profileLiveData.observe(viewLifecycleOwner) {
            println("xxx observeProfile() from UserFragment")
            binding.progressBar.visibility = View.GONE

            Glide.with(requireActivity())
                .load(it.photoUri ?: R.drawable.ic_profile)
                .apply(RequestOptions().circleCrop())
                .into(binding.accountImg)
        }
    }

    fun observeProfileResult() {
        profileVm.resultLiveData.observe(viewLifecycleOwner) {
            println("xxx observeProfileResult() from UserFragment")
            if (it) {
                Snackbar.make(binding.accountImg, getString(R.string.upload_success), 700).show()
                profileVm.initResult()
                profileVm.getAllWhereUid(userUid!!)
            }
        }
    }

    fun observeFollow() {
        followVm.followLiveData.observe(viewLifecycleOwner) {
            println("xxx observeFollow() from UserFragment")
            binding.followerCountTv.text = it.followerCount.toString()
            binding.followingCountTv.text = it.followingCount.toString()

            if (curUid != userUid) {
                if (it.followers.containsKey(userUid)) {
                    // 팔로우중
                    binding.followOrSignoutBtn.text = getString(R.string.follow_cancel)
                } else {
                    // 아직 팔로우하지 않음
                    binding.followOrSignoutBtn.text = getString(R.string.follow)
                }
            }
        }
    }

    private val photoPickerResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { ar ->
        if (ar.resultCode == RESULT_OK) {
            ar.data?.data?.let { uri ->
                profileVm.insert(userUid!!, uri)
            }
        } else {
            binding.progressBar.visibility = View.GONE
            Snackbar.make(binding.accountImg, getString(R.string.upload_fail), 1500).show()
        }
    }

}