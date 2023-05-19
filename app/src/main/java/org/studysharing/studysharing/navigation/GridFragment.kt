package org.studysharing.studysharing.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import org.studysharing.studysharing.adapter.AccountPhotoAdapter
import org.studysharing.studysharing.databinding.FragmentGridBinding
import org.studysharing.studysharing.viewmodel.PhotoContentViewModel

class GridFragment : Fragment() {

    private lateinit var binding: FragmentGridBinding
    private lateinit var photoContentVm: PhotoContentViewModel
    private lateinit var photoAdapter: AccountPhotoAdapter

    override fun onStop() {
        super.onStop()
        photoContentVm.remove()
    }

    override fun onResume() {
        super.onResume()
        photoContentVm.getAll()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGridBinding.inflate(
            inflater,
            container,
            false
        )
        photoContentVm = ViewModelProvider(requireActivity())[PhotoContentViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        observePhotoContent()
    }

    private fun initUi() {
        photoAdapter = AccountPhotoAdapter(requireContext())
            .apply { setHasStableIds(true) }
        binding.photoRv.apply {
            adapter = photoAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun observePhotoContent() {
        photoContentVm.contentLiveData.observe(viewLifecycleOwner) {
            println("xxx observePhotoContent() from GridFragment")
            photoAdapter.contents = it.reversed()
            photoAdapter.notifyDataSetChanged()
        }
    }

}