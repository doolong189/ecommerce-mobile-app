package com.freshervnc.ecommerceapplication.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.databinding.ActivityMainBinding
import com.freshervnc.ecommerceapplication.ui.findmap.MapEcommerceActivity
import com.freshervnc.ecommerceapplication.ui.main.shopping.ShoppingFragment
import com.freshervnc.ecommerceapplication.ui.mapbox.MapboxActivity
import com.freshervnc.ecommerceapplication.ui.messaging.MessageActivity
import com.freshervnc.ecommerceapplication.ui.notification.NotificationActivity
import com.freshervnc.ecommerceapplication.ui.order.OrderActivity
import com.freshervnc.ecommerceapplication.ui.user.UserActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView(savedInstanceState != null)
        setAction()
    }

    private fun setView(isRestore: Boolean){
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        setupActionBarWithNavController(navHostFragment.navController)

        if (isRestore) {
            val fragments = supportFragmentManager.fragments
            if (fragments.isNotEmpty() && (fragments[0] is ShoppingFragment).not()) {
                binding.mainBottomNav.selectedItemId = R.id.navShopping
            }
        }
    }

    private fun setAction(){
        binding.mainBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navShopping -> {
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.shoppingFragment)
                }
                R.id.navCategory ->{
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.menuFragment2)
                }
                R.id.navOrder -> {
                    startActivity(Intent(this,OrderActivity::class.java))
                }
                R.id.navUser -> {
                    startActivity(Intent(this,UserActivity::class.java))
                }
                else -> {}
            }
            false
        }

    }

    fun GONE(){
        binding.mainBottomNav.visibility = View.GONE
    }

    fun Visiable(){
        binding.mainBottomNav.visibility = View.VISIBLE
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }

//    class MainActivity : AppCompatActivity() {
//        private val PICK_IMAGE = 1
//        private var imageUri: Uri? = null
//        private var imageList = ArrayList<Uri>()
//        private lateinit var progressDialog: ProgressDialog
//        private lateinit var urlStrings: ArrayList<String>
//        private lateinit var binding: ActivityMainBinding
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//            binding = ActivityMainBinding.inflate(layoutInflater)
//            setContentView(binding.root)
//            progressDialog = ProgressDialog(this@MainActivity)
//            binding.chooseImage.setOnClickListener {
//                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                    type = "image/*"
//                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//                }
//                startActivityForResult(intent, PICK_IMAGE)
//            }
//
//            binding.uploadImage.setOnClickListener {
//                urlStrings = ArrayList()
//                progressDialog.show()
//                binding.alert.text = "If Loading Takes too long press button again"
//                val imageFolder = FirebaseStorage.getInstance().getReference().child("ImageFolder")
//                for (uploadCount in imageList.indices) {
//                    val individualImage = imageList[uploadCount]
//                    val imageName = imageFolder.child("Images" + individualImage.lastPathSegment)
//                    imageName.putFile(individualImage).addOnSuccessListener { taskSnapshot ->
//                        imageName.downloadUrl.addOnSuccessListener { uri ->
//                            urlStrings.add(uri.toString())
//                            if (urlStrings.size == imageList.size) {
//                                storeLink(urlStrings)
//                                updateTextView(urlStrings)
//                            }
//                        }
//                    }
//                }
//
//            }
//        }
//
//        fun storeLink(urlStrings: ArrayList<String>) {
//            val hashMap = HashMap<String, String>()
//            for (i in urlStrings.indices) {
//                hashMap["ImgLink$i"] = urlStrings[i]
//            }
//            val databaseReference = FirebaseDatabase.getInstance().getReference().child("User")
//            databaseReference.push().setValue(hashMap).addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
//                }
//            }.addOnFailureListener { e ->
//                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
//            }
//            progressDialog.dismiss()
//            binding.alert.text = "Uploaded Successfully"
//            binding.uploadImage.visibility = View.GONE
//            imageList.clear()
//        }
//
//        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//            super.onActivityResult(requestCode, resultCode, data)
//            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
//                data?.clipData?.let { clipData ->
//                    val countClipData = clipData.itemCount
//                    val selectedImages = ArrayList<Uri>()
//                    for (i in 0 until countClipData) {
//                        val imageUri = clipData.getItemAt(i).uri
//                        selectedImages.add(imageUri)
//                    }
//                    binding.alert.visibility = View.VISIBLE
//                    binding.alert.text = "You have selected $countClipData Images"
//                    binding.chooseImage.visibility = View.GONE
//                    imageList.clear()
//                    imageList.addAll(selectedImages)
//
//                } ?: run {
//                    data?.data?.let { uri ->
//                        imageUri = uri
//                        binding.alert.visibility = View.VISIBLE
//                        binding.alert.text = "You have selected 1 Image"
//                        binding.chooseImage.visibility = View.GONE
//                        imageList.clear()
//                        imageList.add(uri)
//                    } ?: run {
//                        Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//
//
//        private fun updateTextView(urlStrings: ArrayList<String>) {
//            val urlListString = urlStrings.joinToString(separator = "\n") { it }
//            binding.urlTextView.text = urlListString
//        }
//    }
}