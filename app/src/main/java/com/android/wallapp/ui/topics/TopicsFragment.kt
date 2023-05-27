package com.android.wallapp.ui.topics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.android.wallapp.adapter.TopicsAdapter
import com.android.wallapp.databinding.FragmentTopicsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TopicsFragment : Fragment(), TopicsAdapter.OnItemClickListener {

    private var _binding: FragmentTopicsBinding? = null

    private val binding get() = _binding!!

    private val viewModel: TopicsViewModel by viewModels()
    lateinit var topicsAdapter: TopicsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTopicsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.progressCircularLoading.visibility = View.VISIBLE
        intiRecyclerView()
        loadData()
        return root
    }

    private fun loadData() {
        lifecycleScope.launch {
            viewModel.getTopicsList.collect() { response ->
                binding.apply {
                    delay(2000)
                    progressCircularLoading.visibility = View.GONE
                }
                Log.d("TopicsMain", "onCreate: ${response.toString()}")
                topicsAdapter.submitData(viewLifecycleOwner.lifecycle, response)
            }
        }
    }

    private fun intiRecyclerView() {
        topicsAdapter = TopicsAdapter(this)
        binding.apply {
            rvPhotosTags.apply {
                layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                adapter = topicsAdapter
                setHasFixedSize(true)

            }
        }
    }

    override fun onItemTopicClick(category: String?) {
        showToast(category!!)
        val action = TopicsFragmentDirections.actionTopicToTopicDetailsFragment(category)
        findNavController().navigate(action)
    }

    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}