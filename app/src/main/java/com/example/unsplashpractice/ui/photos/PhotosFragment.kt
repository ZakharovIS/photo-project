package com.example.unsplashpractice.ui.photos

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.unsplashpractice.R
import com.example.unsplashpractice.data.models.PreviewPhoto
import com.example.unsplashpractice.databinding.FragmentPhotosBinding
import com.example.unsplashpractice.domain.DBAdapter
import com.example.unsplashpractice.domain.PhotosAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PhotosFragment : Fragment() {

    private var _binding: FragmentPhotosBinding? = null
    private val binding get() = _binding!!
    private val photosViewModel: PhotosViewModel by viewModels()
    private val currentPhoto = MutableStateFlow<PreviewPhoto?>(null)
    private val pagedAdapter = PhotosAdapter(
        onClick = { String -> onItemClick(String) },
        onLikeClick = { PreviewPhoto -> onLikeClick(PreviewPhoto) },
        currentPhoto
    )
    private val dbPagedAdapter = DBAdapter{String -> onItemClick(String)}
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Если вошли в приложение по ссылке для просмотра фото

        if (Intent.ACTION_VIEW == requireActivity().intent.action && requireActivity().intent.data != null) {
            val photoID = requireActivity().intent.data!!.lastPathSegment
            if (photoID != null) {
                requireActivity().intent.data = null
                onItemClick(photoID)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentPhotosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.photoList.adapter = pagedAdapter

        binding.swipeRefresh.setOnRefreshListener {
            pagedAdapter.refresh()
            dbPagedAdapter.refresh()
        }

        pagedAdapter.loadStateFlow.onEach {
            binding.swipeRefresh.isRefreshing = it.refresh == LoadState.Loading
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                currentPhoto.collect {
                    if(it != null) {
                        photosViewModel.cachePhotos(it)
                    }
                }
            }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                photosViewModel.dbPagedPhotos
                    .collect {
                        dbPagedAdapter.submitData(it)
                    }
            }

        job = viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                photosViewModel.pagedPhotos
                    .collect {
                        pagedAdapter.submitData(it)
                    }
            }

        pagedAdapter.addLoadStateListener { loadStates ->
            when (loadStates.refresh) {
                is LoadState.Loading -> {}
                is LoadState.NotLoading -> {
                    binding.photoList.adapter = pagedAdapter
                }
                is LoadState.Error -> {
                    snackbarShow(getString(R.string.error_loading_local_data))
                    binding.photoList.adapter = dbPagedAdapter
                }
            }
        }

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        createMenu()

    }

    private fun createMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                menuInflater.inflate(R.menu.search_menu, menu)
                val searchView = menu.findItem(R.id.action_search).actionView as SearchView
                searchView.isSubmitButtonEnabled = true
                searchView.setOnQueryTextListener(
                    object : SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(newText: String): Boolean {
                            return false
                        }

                        override fun onQueryTextSubmit(query: String): Boolean {
                            viewLifecycleOwner.lifecycleScope
                                .launch { job.cancelAndJoin() }
                            job = viewLifecycleOwner.lifecycleScope
                                .launch {
                                    photosViewModel.searchPhotos(searchView.query.toString())
                                    photosViewModel.searchPhotos
                                        .collect {
                                            pagedAdapter.submitData(it)
                                        }
                                }
                            return true
                        }
                    }
                )
                searchView.setOnCloseListener {
                    viewLifecycleOwner.lifecycleScope
                        .launch { job.cancelAndJoin() }
                    job = viewLifecycleOwner.lifecycleScope
                        .launch {
                            photosViewModel.pagedPhotos
                                .collect {
                                    pagedAdapter.submitData(it)
                                }
                        }
                    false
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner)
    }

    @Suppress("DEPRECATION")
    private fun snackbarShow(text: String) {
        Snackbar.make(
            binding.myCoordinatorLayout,
            text,
            Snackbar.LENGTH_LONG
        ).setTextColor(resources.getColor(R.color.snackbar_text))
            .setBackgroundTint(resources.getColor(R.color.snackbar_background))
            .show()
    }


    private fun onItemClick(item: String) {
        val action = PhotosFragmentDirections.actionNavigationPhotosToNavigationPhotoDetail(
            item
        )
        findNavController().navigate(action)
    }

    private fun onLikeClick(item: PreviewPhoto) {
        if (item.liked_by_user != null) {
            if (item.liked_by_user!!) photosViewModel.removeLike(item.id!!)
            else photosViewModel.setLike(item.id!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}