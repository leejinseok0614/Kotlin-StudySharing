package org.studysharing.studysharing

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.studysharing.studysharing.adapter.AccountPhotoAdapter
import org.studysharing.studysharing.databinding.FragmentOtherUserBinding
import org.studysharing.studysharing.viewmodel.FollowViewModel
import org.studysharing.studysharing.viewmodel.PhotoContentViewModel
import org.studysharing.studysharing.viewmodel.ProfileViewModel

class OtherUserFragment : Fragment() {

    private lateinit var binding: FragmentOtherUserBinding
    private lateinit var photoContentVm: PhotoContentViewModel
    private lateinit var profileVm: ProfileViewModel
    private lateinit var followVm: FollowViewModel
    private lateinit var accountPhotoAdapter: AccountPhotoAdapter

    private var id: String? = null
    private var uid: String? = null

    override fun onStop() {
        super.onStop()
        photoContentVm.remove()
        profileVm.remove()
        followVm.remove()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtherUserBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id = arguments?.getString("userId")
        uid = arguments?.getString("uid")
        photoContentVm = ViewModelProvider(this)[PhotoContentViewModel::class.java]
        profileVm = ViewModelProvider(this)[ProfileViewModel::class.java]
        followVm = ViewModelProvider(this)[FollowViewModel::class.java]

        initUi()
        observePhotoContent()
        observeProfile()
        observeProfileResult()
        observeFollow()
    }

    private fun initUi() {
        // Other user page
        Glide.with(this)
            .load(R.drawable.ic_profile)
            .apply(RequestOptions().circleCrop())
            .into(binding.accountImg)
        binding.followBtn.apply {
            setOnClickListener {
                uid?.let { uid -> followVm.updateFollow(uid) }
            }
        }

        if (context is MainActivity) {
            (context as MainActivity).setToolbarForOtherUser(id)
        }

        accountPhotoAdapter = AccountPhotoAdapter(requireContext())
            .apply {
                setHasStableIds(true)
            }
        binding.accountRv.apply {
            adapter = accountPhotoAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
        uid?.let {
            photoContentVm.getAllWhereUid(it)
            profileVm.getAllWhereUid(it)
            followVm.getAllWhereUid(it)
        }
    }

    fun observePhotoContent() {
        photoContentVm.uidContentLiveData.observe(viewLifecycleOwner) {
            println("xxx observePhotoContent() from OtherUserFragment")
            binding.postCountTv.text = it.size.toString()
            accountPhotoAdapter.contents = it.reversed()
            accountPhotoAdapter.notifyDataSetChanged()
        }
    }

    fun observeProfile() {
        profileVm.profileLiveData.observe(viewLifecycleOwner) {
            println("xxx observeProfile() from OtherUserFragment")
            Glide.with(this)
                .load(it.photoUri ?: R.drawable.ic_profile)
                .apply(RequestOptions().circleCrop())
                .into(binding.accountImg)
        }
    }

    fun observeProfileResult() {
        profileVm.resultLiveData.observe(viewLifecycleOwner) {
            println("xxx observeProfileResult() from OtherUserFragment")
            if (it) profileVm.getAllWhereUid(MyApplication.userUid!!)
        }
    }

    fun observeFollow() {
        followVm.followLiveData.observe(viewLifecycleOwner) {
            println("xxx observeFollow() from OtherUserFragment")
            binding.followerCountTv.text = it.followerCount.toString()
            binding.followingCountTv.text = it.followingCount.toString()

            if (it.followers.containsKey(MyApplication.userUid)) {
                // 팔로우중
                binding.followBtn.text = getString(R.string.follow_cancel)
                binding.followBtn.setBackgroundColor(Color.parseColor("#FFFFFF"))
                binding.followBtn.setTextColor(Color.parseColor("#000000"))
            } else {
                // 아직 팔로우하지 않음
                binding.followBtn.text = getString(R.string.follow)
                binding.followBtn.setBackgroundColor(Color.parseColor("#2196F3"))
                binding.followBtn.setTextColor(Color.parseColor("#FFFFFF"))
            }
        }
    }

}