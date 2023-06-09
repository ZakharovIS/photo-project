package com.example.unsplashpractice.ui.photodetail

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.unsplashpractice.R
import com.example.unsplashpractice.databinding.FragmentPhotoDetailBinding
import com.example.unsplashpractice.downloader.UnsplashDownloader
import com.example.unsplashpractice.utils.CheckInternetConnection
import com.example.unsplashpractice.utils.State
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PhotoDetailFragment : Fragment() {

    private var _binding: FragmentPhotoDetailBinding? = null
    private val binding get() = _binding!!
    val args: PhotoDetailFragmentArgs by navArgs()
    private val photoDetailViewModel: PhotoDetailViewModel by viewModels()
    private var downloadID: Long = -1L
    private lateinit var downloader: UnsplashDownloader

    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            checkPermissionsAndDownload(
                photoDetailViewModel.photoDetail.value!!.urls!!.raw!!,
                args.photoId
            )
        } else {
            Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                getString(R.string.permission_not_granted),
                Snackbar.LENGTH_LONG
            )
                .show()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPhotoDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        downloader = UnsplashDownloader(requireContext())

        photoDetailViewModel.loadPhoto(args.photoId)

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                photoDetailViewModel.state
                    .collect() {
                        when (it) {
                            State.Error -> {
                                binding.maskLayoutSolid.visibility = View.VISIBLE
                                binding.errorLayout.visibility = View.VISIBLE
                                binding.progressCircular.visibility = View.GONE
                            }
                            State.Loading -> {
                                binding.maskLayoutSolid.visibility = View.VISIBLE
                                binding.progressCircular.visibility = View.VISIBLE
                            }
                            State.Success -> {
                                binding.maskLayoutSolid.visibility = View.GONE
                                binding.progressCircular.visibility = View.GONE
                                binding.errorLayout.visibility = View.GONE
                            }
                        }
                    }
            }

        binding.refreshButton.setOnClickListener {
            photoDetailViewModel.loadPhoto(args.photoId)
        }


        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                photoDetailViewModel.photoDetail
                    .collect() {
                        Glide.with(binding.root.context)
                            .load(it?.urls?.regular)
                            .into(binding.photoDetailView)
                        Glide.with(binding.root.context)
                            .load(it?.user?.profile_image?.medium)
                            .into(binding.userPhoto)
                        binding.userName.text = it?.user?.name
                        binding.userId.text = """@""" + it?.user?.username
                        binding.likesCount.text = it?.likes?.toString()

                        val loc =
                            "" + (if (it?.location?.city != null) "${it.location?.city}, " else "") +
                                    (if (it?.location?.country != null) "${it.location?.country}" else "")
                        binding.location.text = loc
                        var tags = ""
                        it?.tags?.forEach {
                            tags = "$tags #${it.title} "
                        }
                        binding.tags.text = tags
                        binding.madeWith.text = it?.exif?.make
                        binding.model.text = it?.exif?.model
                        binding.exposure.text = it?.exif?.exposure_time
                        binding.aperture.text = it?.exif?.aperture
                        binding.focal.text = it?.exif?.focal_length
                        binding.iso.text = it?.exif?.iso.toString()
                        binding.author.text = """@""" + it?.user?.username
                        binding.about.text = it?.user?.bio
                        binding.downloadCount.text = it?.downloads.toString()
                        binding.likeIsSet = it?.liked_by_user

                        binding.downloadButton.setOnClickListener { view ->
                            if (it?.urls?.raw != null) {
                                checkPermissionsAndDownload(it?.urls?.raw!!, it.id!!)
                            }
                        }


                        if (it?.location?.position?.latitude != null && it.location?.position?.longitude != null)
                            binding.locationButton.setOnClickListener { view ->
                                val gmmIntentUri = Uri.parse(
                                    "geo:${it.location!!.position!!.latitude},${it.location!!.position!!.longitude}"
                                )
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                val packageManager = requireContext().packageManager
                                mapIntent.setPackage("com.google.android.apps.maps")
                                mapIntent.resolveActivity(packageManager)?.let {
                                    startActivity(mapIntent)
                                }
                            }
                    }
            }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                photoDetailViewModel.updatedPhoto
                    .collect() {
                        if (it != null) {
                            binding.likeIsSet = it.liked_by_user
                            binding.likesCount.text = it.likes.toString()
                        }

                    }
            }


        binding.like.setOnClickListener {
            if (photoDetailViewModel.photoDetail.value?.urls?.raw != null &&
                CheckInternetConnection.isOnline(context = binding.root.context)
            ) {
                if (binding.likeIsSet != null) {
                    if (binding.likeIsSet!!) photoDetailViewModel.removeLike(args.photoId)
                    else photoDetailViewModel.setLike(args.photoId)
                }
            }
        }


        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.share_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> findNavController().popBackStack()
                    R.id.share -> {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "https://unsplash.com/photos/".plus(args.photoId)
                            )
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        startActivity(shareIntent)
                    }

                }

                return true
            }

        }, viewLifecycleOwner)

        requireActivity().registerReceiver(
            reciever,
            IntentFilter("android.intent.action.DOWNLOAD_COMPLETE")
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkPermissionsAndDownload(url: String, title: String) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            downloadID = downloader.downloadFile(url, title)
        } else {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    REQUIRED_PERMISSION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(REQUIRED_PERMISSION)
            } else {
                downloadID = downloader.downloadFile(url, title)
            }
        }


    }

    val reciever = object : BroadcastReceiver() {

        private lateinit var downloadManager: DownloadManager

        override fun onReceive(context: Context?, intent: Intent?) {
            downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == -1L)
                    return
                val query = DownloadManager.Query()
                query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1))
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    if (cursor.count > 0) {
                        val status =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))

                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            val uri = downloadManager.getUriForDownloadedFile(id)

                            Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                getString(R.string.download_complete),
                                Snackbar.LENGTH_LONG
                            ).setAction(getString(R.string.open_gallery)) {
                                println(uri)
                                val intent = Intent()
                                //intent.action = Intent.ACTION_VIEW
                                intent.action = Intent.ACTION_GET_CONTENT
                                intent.setDataAndType(
                                    uri,
                                    "image/*"
                                )
                                startActivity(intent)
                            }
                                .setTextColor(resources.getColor(R.color.snackbar_text))
                                .setBackgroundTint(resources.getColor(R.color.snackbar_background))
                                .show()

                        }

                    }
                }
                cursor.close()

            }
        }
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(reciever)
        super.onDestroy()
    }

    companion object {
        private val REQUIRED_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

    }

}