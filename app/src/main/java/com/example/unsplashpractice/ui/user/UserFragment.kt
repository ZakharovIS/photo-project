package com.example.unsplashpractice.ui.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import com.example.unsplashpractice.R
import com.example.unsplashpractice.auth.AuthorizationActivity
import com.example.unsplashpractice.data.models.PreviewPhoto
import com.example.unsplashpractice.databinding.FragmentUserBinding
import com.example.unsplashpractice.domain.CollectionsPhotoAdapter
import com.example.unsplashpractice.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null

    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()

    private val pagedAdapter = CollectionsPhotoAdapter(
        onClick = { String -> onItemClick(String) },
        onLikeClick = { PreviewPhoto -> onLikeClick(PreviewPhoto) }
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.userListPhotos.adapter = pagedAdapter

        binding.buttonYes.setOnClickListener {
            val intent = Intent(requireContext(), AuthorizationActivity::class.java)
            startActivity(intent)
            binding.logoutFrame.visibility = View.GONE
            binding.maskLayoutTransparent.visibility = View.GONE
            activity?.finish()

        }

        binding.buttonNo.setOnClickListener {
            binding.logoutFrame.visibility = View.GONE
            binding.maskLayoutTransparent.visibility = View.GONE
        }

        viewLifecycleOwner.lifecycleScope
            .launch {
                userViewModel.userData
                    .collect() {
                        if (it != null) {
                            userViewModel.loadMyFavoritePhotos(it.username!!)
                            loadPhotos()
                            binding.userName.text = it.name
                            binding.userId.text = """@""" + it.username
                            binding.email.text = it.email
                            binding.about.text = it.bio
                            if (it.location != null) {
                                binding.location.text = "${it.location}"
                                binding.locationButton.setOnClickListener { view ->
                                    val gmmIntentUri = Uri.parse(
                                        "geo:0,0?q=${it.location}"
                                    )
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    val packageManager = requireContext().packageManager
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    mapIntent.resolveActivity(packageManager)?.let {
                                        startActivity(mapIntent)
                                    }
                                }
                            } else binding.location.text = getString(R.string.Undefined_location)

                            Glide.with(binding.root.context)
                                .load(it.profile_image?.medium)
                                .circleCrop()
                                .into(binding.userPhoto)
                        }
                    }

            }



        binding.refreshButton.setOnClickListener {
            pagedAdapter.refresh()
            if (userViewModel.userData.value == null) {
                userViewModel.loadMyData()
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            pagedAdapter.refresh()
            if (userViewModel.userData.value == null) {
                userViewModel.loadMyData()
            }
        }

        pagedAdapter.loadStateFlow.onEach {
            binding.swipeRefresh.isRefreshing = it.refresh == LoadState.Loading
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                userViewModel.state
                    .collect() {
                        when(it) {
                            State.Error -> {
                                binding.maskLayoutSolid.visibility = View.VISIBLE
                                binding.errorLayout.visibility = View.VISIBLE
                                binding.progressCircular.visibility = View.GONE
                            }
                            State.Loading -> {
                                binding.errorLayout.visibility = View.GONE
                                binding.maskLayoutSolid.visibility = View.VISIBLE
                                binding.progressCircular.visibility = View.VISIBLE
                            }
                            State.Success -> {
                                binding.errorLayout.visibility = View.GONE
                                binding.maskLayoutSolid.visibility = View.GONE
                                binding.userListPhotos.visibility = View.VISIBLE
                                binding.userFrame.visibility = View.VISIBLE
                                binding.progressCircular.visibility = View.GONE
                            }
                        }
                    }
            }


        pagedAdapter.addLoadStateListener { loadStates ->
            when (loadStates.refresh) {
                is LoadState.Loading -> {
                    binding.errorLayout.visibility = View.GONE
                    binding.maskLayoutSolid.visibility = View.GONE
                    binding.userListPhotos.visibility = View.GONE

                }
                is LoadState.NotLoading -> {
                    binding.errorLayout.visibility = View.GONE
                    binding.maskLayoutSolid.visibility = View.GONE
                    binding.userListPhotos.visibility = View.VISIBLE
                    binding.userFrame.visibility = View.VISIBLE

                }
                is LoadState.Error -> {
                    binding.errorLayout.visibility = View.VISIBLE
                    binding.maskLayoutSolid.visibility = View.VISIBLE
                    binding.userListPhotos.visibility = View.GONE
                    binding.userFrame.visibility = View.GONE
                }
            }
        }




        return root
    }


    private fun onItemClick(item: String) {
        val action = UserFragmentDirections.actionNavigationUserToNavigationPhotoDetail(
            item
        )
        findNavController().navigate(action)
    }

    private fun loadPhotos() {
        viewLifecycleOwner.lifecycleScope
            .launch {
                userViewModel.pagedPhotos
                    .collectLatest() {
                        pagedAdapter.submitData(it)
                    }
            }
    }

    private fun onLikeClick(item: PreviewPhoto) {
         if (item.liked_by_user != null) {
             if (item.liked_by_user!!) userViewModel.removeLike(item.id!!)
             else userViewModel.setLike(item.id!!)
         }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.logout_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> findNavController().popBackStack()
                    R.id.logout -> {
                        binding.maskLayoutTransparent.visibility = View.VISIBLE
                        binding.logoutFrame.visibility = View.VISIBLE
                    }

                }

                return true
            }

        }, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}