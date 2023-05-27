package com.android.wallapp.ui.topicdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.wallapp.R
import com.android.wallapp.adapter.PhotoAdapter
import com.android.wallapp.adapter.PhotoLoadStateAdapter
import com.android.wallapp.adapter.TopicsAdapter
import com.android.wallapp.databinding.FragmentTopicDetailBinding
import com.android.wallapp.databinding.FragmentTopicsBinding
import com.android.wallapp.models.PhotoResponse
import com.android.wallapp.ui.home.HomeFragmentDirections
import com.android.wallapp.ui.photodetails.PhotoDetailsFragmentArgs
import com.android.wallapp.ui.topics.TopicsViewModel
import com.android.wallapp.utlis.collectWhileStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class TopicDetailFragment : Fragment(), PhotoAdapter.OnItemClickListener {
    private val args: TopicDetailFragmentArgs by navArgs()

    private var _binding: FragmentTopicDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TopicsViewModel by viewModels()
    lateinit var photosAdapter: PhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTopicDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val topics = args.topicsData
        setupPhotosAdapter()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.topicWisePics(topics!!).collectLatest {
                photosAdapter.submitData(it)
            }
        }

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
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        binding.apply {
            rvGallery.layoutManager = staggeredGridLayoutManager
            rvGallery.setHasFixedSize(true)
            rvGallery.adapter = concatAdapter
            // rvGallery.addItemDecoration(GalleryGridSpacingItemDecoration(16.dpToPx(requireContext())))
//            btnRetry.setOnClickListener {
//                viewModel.getTopics()
//                photosAdapter.retry()
//            }
        }
    }

    override fun onPhotoClick(photo: PhotoResponse) {
        val action = HomeFragmentDirections.actionHomeFragmentToPhotoDetailsFragment(photo)
        findNavController().navigate(action)
    }

}