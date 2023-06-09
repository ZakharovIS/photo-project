package com.example.unsplashpractice.ui.collections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.example.unsplashpractice.data.models.PreviewPhoto
import com.example.unsplashpractice.databinding.FragmentCollectionDetailBinding
import com.example.unsplashpractice.domain.CollectionsPhotoAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CollectionDetailFragment : Fragment() {

    private var _binding: FragmentCollectionDetailBinding? = null

    private val binding get() = _binding!!

    private val collectionsViewModel: CollectionsViewModel by activityViewModels()

    private val pagedAdapter = CollectionsPhotoAdapter(
        onClick = { String -> onItemClick(String) },
        onLikeClick = { PreviewPhoto -> onLikeClick(PreviewPhoto) }
    )

    private val args: CollectionDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        collectionsViewModel.loadCollection(args.collectionId)
        collectionsViewModel.loadCollectionPhotos(args.collectionId)
        binding.collectionListPhotos.adapter = pagedAdapter

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                collectionsViewModel.collectionDetail
                    .collect() {
                        if (it != null) {
                            var tags = ""
                            binding.collectionTitle.text = it.title
                            binding.collectionDescription.text = it.description
                            it.tags.forEach {
                                tags = "$tags #${it!!.title} "
                            }
                            binding.collectionTags.text = tags
                            binding.authorUsername = it.user?.username ?: ""
                            binding.numberOfImages = it.total_photos

                        }
                    }
            }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                collectionsViewModel.pagedCollectionPhotos
                    .collectLatest {
                        pagedAdapter.submitData(it)
                    }
            }

        binding.refreshButton.setOnClickListener {
            pagedAdapter.refresh()
            if(collectionsViewModel.collectionDetail.value == null) {
                collectionsViewModel.loadCollection(args.collectionId)
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            pagedAdapter.refresh()
            if(collectionsViewModel.collectionDetail.value == null) {
                collectionsViewModel.loadCollection(args.collectionId)
            }
        }

        pagedAdapter.loadStateFlow.onEach {
            binding.swipeRefresh.isRefreshing = it.refresh == LoadState.Loading
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        pagedAdapter.addLoadStateListener { loadStates ->
            when (loadStates.refresh) {
                is LoadState.Loading -> {
                    binding.errorLayout.visibility = View.GONE
                    binding.collectionListPhotos.visibility = View.GONE
                    binding.collectionAuthorAndNumberPhotos.visibility = View.GONE
                }
                is LoadState.NotLoading -> {
                    binding.errorLayout.visibility = View.GONE
                    binding.collectionListPhotos.visibility = View.VISIBLE
                    binding.collectionAuthorAndNumberPhotos.visibility = View.VISIBLE
                }
                is LoadState.Error -> {
                    binding.errorLayout.visibility = View.VISIBLE
                    binding.collectionListPhotos.visibility = View.GONE
                    binding.collectionAuthorAndNumberPhotos.visibility = View.GONE
                }
            }
        }
        return root
    }

    private fun onItemClick(item: String) {
        val action =
            CollectionDetailFragmentDirections.actionNavigationCollectionDetailToNavigationPhotoDetail(
                item
            )
        findNavController().navigate(action)
    }

    private fun onLikeClick(item: PreviewPhoto) {

        if (item.liked_by_user != null) {
            if (item.liked_by_user!!) collectionsViewModel.removeLike(item.id!!)
            else collectionsViewModel.setLike(item.id!!)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}