package com.example.unsplashpractice.ui.collections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.unsplashpractice.databinding.FragmentCollectionsBinding
import com.example.unsplashpractice.domain.CollectionsAdapter
import com.example.unsplashpractice.domain.CustomLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CollectionsFragment : Fragment() {

    private var _binding: FragmentCollectionsBinding? = null

    private val binding get() = _binding!!

    private val collectionsViewModel: CollectionsViewModel by activityViewModels()

    private val pagedAdapter = CollectionsAdapter() { String -> onItemClick(String) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.collectionList.adapter = pagedAdapter.withLoadStateHeaderAndFooter(
            CustomLoadStateAdapter(),
            CustomLoadStateAdapter()
        )

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                collectionsViewModel.pagedUnsplashCollection
                    .collect() {
                        pagedAdapter.submitData(it)
                    }
            }

        binding.swipeRefresh.setOnRefreshListener {
            pagedAdapter.refresh()
        }
        binding.refreshButton.setOnClickListener {
            pagedAdapter.refresh()
        }

        pagedAdapter.loadStateFlow.onEach {
            binding.swipeRefresh.isRefreshing = it.refresh == LoadState.Loading
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        pagedAdapter.addLoadStateListener { loadStates ->
            when (loadStates.refresh) {
                is LoadState.Loading -> {
                    binding.errorLayout.visibility = View.GONE
                    binding.collectionList.visibility = View.VISIBLE
                }
                is LoadState.NotLoading -> {
                    binding.errorLayout.visibility = View.GONE
                    binding.collectionList.visibility = View.VISIBLE

                }
                is LoadState.Error -> {
                    binding.errorLayout.visibility = View.VISIBLE
                    binding.collectionList.visibility = View.GONE
                }
            }
        }

        return root
    }

    private fun onItemClick(item: String) {
        val action =
            CollectionsFragmentDirections.actionNavigationCollectionsToNavigationCollectionDetail(item)
        findNavController().navigate(action)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}