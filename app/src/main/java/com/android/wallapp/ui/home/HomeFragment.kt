package com.android.wallapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.wallapp.adapter.PhotoAdapter
import com.android.wallapp.adapter.PhotoLoadStateAdapter
import com.android.wallapp.adapter.TopicsAdapter
import com.android.wallapp.databinding.FragmentHomeBinding
import com.android.wallapp.models.PhotoResponse

import com.android.wallapp.utlis.collectWhileStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class HomeFragment : Fragment(),
    PhotoAdapter.OnItemClickListener {

    private var _binding: FragmentHomeBinding? = null

    private val viewModel: HomeViewModel by viewModels()

    private val binding get() = _binding!!
    private lateinit var photosAdapter: PhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupPhotosAdapter()
        callApiPhotos()

        return root
    }


    private fun setupPhotosAdapter() {
        photosAdapter = PhotoAdapter(this)
        val headerAdapter = PhotoLoadStateAdapter { photosAdapter.retry() }
        val footerAdapter = PhotoLoadStateAdapter { photosAdapter.retry() }
        val concatAdapter = photosAdapter.withLoadStateHeaderAndFooter(
            header = headerAdapter,
            footer = footerAdapter,
        )
        //  val staggeredGridLayoutManager =LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        binding.apply {
            rvPhotos.layoutManager = staggeredGridLayoutManager
            rvPhotos.setHasFixedSize(true)
            rvPhotos.adapter = concatAdapter
            btnRetry.setOnClickListener {
                viewModel.getAllPhotos
                photosAdapter.retry()
            }
            binding.includefailedlyt.failedRetry.setOnClickListener {
                viewModel.getAllPhotos
                photosAdapter.retry()
            }
        }
    }


    private fun photosLoadStateListener() {
        photosAdapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                rvPhotos.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading
                progressCircularLoading.isVisible =
                    combinedLoadStates.source.refresh is LoadState.Loading
                binding.btnRetry.isVisible = combinedLoadStates.source.refresh is LoadState.Error
                tvErrorMessage.isVisible = combinedLoadStates.source.refresh is LoadState.Error
            }
        }
    }


    override fun onPhotoClick(photo: PhotoResponse) {
        //   viewModel.onPhotoSelected(photo)
        Log.d("Dinesh", "Photoresponse - $photo")
        val action = HomeFragmentDirections.actionHomeFragmentToPhotoDetailsFragment(photo)
        findNavController().navigate(action)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun callApiPhotos() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getAllPhotos.collectLatest {
                photosAdapter.submitData(it)
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}