package com.android.wallapp.ui.photodetails

import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import com.android.wallapp.ActivityCropWallpaper
import com.android.wallapp.R
import com.android.wallapp.databinding.FragmentPhotoDetailsBinding
import com.android.wallapp.models.PhotoResponse
import com.android.wallapp.utlis.ConstantMethods
import com.android.wallapp.utlis.ConstantMethods.Companion.BOTH
import com.android.wallapp.utlis.ConstantMethods.Companion.HOME_SCREEN
import com.android.wallapp.utlis.ConstantMethods.Companion.LOCK_SCREEN
import com.android.wallapp.utlis.WallpaperHelper
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


@AndroidEntryPoint
class PhotoDetailsFragment : Fragment(R.layout.fragment_photo_details) {
    private val args: PhotoDetailsFragmentArgs by navArgs()
    private var _binding: FragmentPhotoDetailsBinding? = null
    private val binding get() = _binding!!
    var wallpaperHelper: WallpaperHelper? = null
    var progressDialog: ProgressDialog? = null
    private var single_choice_selected: String? = null
    var TAG = "PhotoDetailsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPhotoDetailsBinding.bind(view)
        wallpaperHelper = WallpaperHelper(requireActivity() as AppCompatActivity?)
        progressDialog = ProgressDialog(requireActivity())

        val photo = args.photo
        initView(photo)
    }


    private fun initView(photo: PhotoResponse) = with(binding) {
        ivPhoto.load(photo.urls.full)
        Log.e("TAG_IMAGES_OF", "initView: " + photo.urls.full)
        /*
                ivPhoto.setOnClickListener {
                    val action =
                        PhotoDetailsFragmentDirections.actionDetailsFragmentToPhotoZoomFragment(photo)
                    findNavController().navigate(action)
                }
        */
        binding.lytAction.btnSave.setOnClickListener(View.OnClickListener { view: View? ->
            if (!verifyPermissions()!!) {

            }

            val wallpaperName: String =
                photo.id.toLowerCase().replace(" ", "_") + "_" + photo.id
            wallpaperHelper!!.downloadWallpaper(
                requireView(),
                progressDialog,
                wallpaperName,
                photo.urls.full
            )
        })
        binding.lytAction.btnShare.setOnClickListener(View.OnClickListener { view: View? ->
            if (!verifyPermissions()!!) {
                Toast.makeText(requireContext(), "Need Check permission", Toast.LENGTH_SHORT).show()
            }
            val wallpaperName: String =
                photo.id.toLowerCase().replace(" ", "_") + "_" + photo.id
            wallpaperHelper?.shareWallpaper(progressDialog, wallpaperName, photo.urls.full)
        })

        binding.lytAction.btnSetWallpaper.setOnClickListener(View.OnClickListener { view: View? ->
            if (!verifyPermissions()!!) {
                Toast.makeText(requireContext(), "Need Check permission", Toast.LENGTH_SHORT).show()

            }
            loadFile(progressDialog, photo.urls.full, photo)
        })

    }

    private fun loadFile(
        progressDialog: ProgressDialog?,
        wallpaperUrl: String,
        photo: PhotoResponse,
    ) {
        progressDialog!!.show()
        try {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val url = URL(photo.urls.full)
            val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            val mimeType = getMimeType(photo.urls.full)
            if (mimeType == "image/gif" || mimeType == "image/GIF") {
                val imageName: String = photo.id.replace(" ", "_") + "_" + photo.id
                wallpaperHelper!!.setGif(binding.root, progressDialog, imageName, wallpaperUrl)
            } else {
                if (Build.VERSION.SDK_INT >= 24) {
                    val ogBitmap: Bitmap = getBitmapFromURL(photo.urls.full)!!
                    Log.e(TAG, "loadFile: " + ogBitmap)
                    dialogOptionSetWallpaper(ogBitmap, photo)
                } else {
                    wallpaperHelper!!.setWallpaper(
                        binding.root,
                        progressDialog,
                        wallpaperUrl
                    )
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            progressDialog!!.dismiss()
        }
    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection =
                url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun getMimeType(url: String?): String {
        url?.lowercase()
        var result: String = ""
        if (url?.contains("jpg") == true) {
            result = "jpg"
        } else if (url?.contains("png") == true) {
            result = "png"
        } else if (url?.contains("gif") == true) {
            result = "gif"
        } else if (url?.contains("jpeg") == true) {
            result = "jpeg"

        }
        return result
    }

    private fun verifyPermissions(): Boolean? {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.TIRAMISU) {
            val permissionExternalMemory =
                ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
                val STORAGE_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(requireActivity(), STORAGE_PERMISSIONS, 1)
                return false
            }
            return true
        }
        return true
    }

    private fun dialogOptionSetWallpaper(bitmap: Bitmap, photo: PhotoResponse) {
        Log.e("TAG_PHotodetailsFragment", "dialogOptionSetWallpaper: $bitmap")
        val items = resources.getStringArray(R.array.dialog_set_wallpaper)
        single_choice_selected = items[0]
        val itemSelected = 0
        val alertDialog = AlertDialog.Builder(requireActivity())
        alertDialog.setTitle(R.string.dialog_set_title)
            ?.setSingleChoiceItems(
                items, itemSelected
            ) { dialogInterface: DialogInterface?, i: Int ->
                single_choice_selected = items[i]
            }
            ?.setPositiveButton(R.string.dialog_option_ok) { dialogInterface, i ->
                if (single_choice_selected == resources.getString(R.string.set_home_screen)) {
                    wallpaperHelper?.setWallpaper(
                        requireActivity().findViewById(android.R.id.content),
                        progressDialog,
                        bitmap,
                        HOME_SCREEN
                    )
                    progressDialog!!.setMessage(getString(R.string.msg_apply_wallpaper))
                } else if (single_choice_selected == resources.getString(R.string.set_lock_screen)) {
                    wallpaperHelper?.setWallpaper(
                        requireView(),
                        progressDialog,
                        bitmap,
                        LOCK_SCREEN
                    )
                    progressDialog!!.setMessage(getString(R.string.msg_apply_wallpaper))
                } else if (single_choice_selected == resources.getString(R.string.set_both)) {
                    wallpaperHelper?.setWallpaper(
                        requireView(),
                        progressDialog,
                        bitmap,
                        BOTH
                    )
                    progressDialog!!.setMessage(getString(R.string.msg_apply_wallpaper))
                } else if (single_choice_selected == resources.getString(R.string.set_crop)) {
                    val intent = Intent(requireActivity(), ActivityCropWallpaper::class.java)
                    intent.putExtra("image_url", photo.urls.full)
                    startActivity(intent)
                    ConstantMethods.bitmap = bitmap
                    progressDialog!!.dismiss()
                } else if (single_choice_selected == resources.getString(R.string.set_with)) {
                    wallpaperHelper!!.setWallpaperFromOtherApp(photo.urls.full)
                    progressDialog!!.dismiss()
                }
            }
            ?.setNegativeButton(R.string.dialog_option_cancel) { dialogInterface, i -> progressDialog!!.dismiss() }
            ?.setCancelable(false)
            ?.show()
    }

}