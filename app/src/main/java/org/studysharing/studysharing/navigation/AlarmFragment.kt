package org.studysharing.studysharing.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.studysharing.studysharing.MyApplication.Companion.userUid
import org.studysharing.studysharing.adapter.AlarmAdapter
import org.studysharing.studysharing.databinding.FragmentAlarmBinding
import org.studysharing.studysharing.model.AlarmDTO
import org.studysharing.studysharing.viewmodel.AlarmViewModel

class AlarmFragment : Fragment() {

    private lateinit var binding: FragmentAlarmBinding
    private val alarmsVm by activityViewModels<AlarmViewModel>()
    private lateinit var alarmAdapter: AlarmAdapter

    override fun onStop() {
        super.onStop()
        alarmAdapter.profileRepository.remove()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alarmAdapter = AlarmAdapter(requireContext())
            .apply {
                setHasStableIds(true)
            }
        binding.alarmRv.apply {
            adapter = alarmAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        userUid?.let { alarmsVm.getAll(it) }
        observeAlarms()
    }

    fun observeAlarms() {
        alarmsVm.alarmsLiveData.observe(viewLifecycleOwner) { list ->
            println("xxx observeAlarms() from AlarmFragment")
            if (list.isNotEmpty()) {
                alarmAdapter.alarms = list as ArrayList<AlarmDTO>
                alarmAdapter.notifyDataSetChanged()
            }
        }
    }

}