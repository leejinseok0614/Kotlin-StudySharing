package org.studysharing.studysharing.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.studysharing.studysharing.adapter.DetailViewAdapter
import org.studysharing.studysharing.adapter.DetailViewAdapterCallback
import org.studysharing.studysharing.databinding.FragmentDetailViewBinding
import org.studysharing.studysharing.viewmodel.PhotoContentViewModel

class DetailViewFragment : Fragment() {

    private lateinit var binding: FragmentDetailViewBinding
    private lateinit var detailViewAdapter: DetailViewAdapter

    private val photoContentVm: PhotoContentViewModel by activityViewModels()

    override fun onStop() {
        super.onStop()
        photoContentVm.remove()
        detailViewAdapter.profileRepository.remove()
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
        binding = FragmentDetailViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUi()
        observePhotoContent()
    }

    private fun setUi() {
        detailViewAdapter = DetailViewAdapter(requireContext())
            .apply {
                setHasStableIds(true)
                setDetailViewAdapterCallback(object : DetailViewAdapterCallback {
                    override fun onChangeThumbStatus(position: Int, status: Boolean?) {
                        val documentId =
                            photoContentVm.contentIdLiveData.value?.reversed()?.getOrNull(position)
                                ?: return

                        photoContentVm.updateFavorite(documentId, status)
                    }
                })
            }

        binding.detailRv.apply {
            adapter = detailViewAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observePhotoContent() {
        photoContentVm.contentLiveData.observe(viewLifecycleOwner) { items ->
            println("xxx observePhotoContent(contentLiveData) from DetailViewFragment")
            if (items.isNotEmpty()) {
                detailViewAdapter.apply {
                    contents = items.reversed()
                    notifyDataSetChanged()
                }
            }
        }

        photoContentVm.contentIdLiveData.observe(viewLifecycleOwner) { items ->
            println("xxx observePhotoContent(contentIdLiveData) from DetailViewFragment")
            if (items.isNotEmpty()) {
                detailViewAdapter.contentUids = items.reversed()
            }
        }
    }

}