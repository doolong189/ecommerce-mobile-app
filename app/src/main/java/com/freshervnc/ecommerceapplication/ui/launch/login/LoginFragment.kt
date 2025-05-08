package com.freshervnc.ecommerceapplication.ui.launch.login

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.GraphRequest
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.common.base.BaseFragment
import com.freshervnc.ecommerceapplication.data.enity.LoginRequest
import com.freshervnc.ecommerceapplication.data.enity.LoginResponse
import com.freshervnc.ecommerceapplication.data.enity.RegisterRequest
import com.freshervnc.ecommerceapplication.databinding.FragmentLoginBinding
import com.freshervnc.ecommerceapplication.ui.launch.register.RegisterAccountViewModel
import com.freshervnc.ecommerceapplication.ui.main.MainActivity
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Extensions
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.SocketIOManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.json.JSONException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays


class LoginFragment : BaseFragment() {
    private lateinit var binding : FragmentLoginBinding
    private val viewModel by activityViewModels<LoginViewModel>()
    private val registerViewModel by activityViewModels<RegisterAccountViewModel>()
    override var isVisibleActionBar: Boolean = false
    private lateinit var preferences : PreferencesUtils
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 1
    private val callbackManager = CallbackManager.Factory.create()
    private val EMAIL: String = "email"
    private val PUBLIC_PROFILE : String = "public_profile"
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun initView() {
        preferences = PreferencesUtils(requireContext())
        AppEventsLogger.activateApp(getApplicationContext())
        printHashKey()
    }


    override fun setView() {
//        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
//        if(account != null) {
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            startActivity(intent)
//        }
//
//        val accessToken = AccessToken.getCurrentAccessToken()
//        val isLoggedIn = accessToken != null && !accessToken.isExpired
//        if (isLoggedIn){
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            startActivity(intent)
//        }
    }

    override fun setAction() {
        preferences.clearUserData()
        binding.btnLogin.setOnClickListener {
            if(binding.edEmail.text.toString().isEmpty() || binding.edPassword.text.toString().isEmpty()){
                viewModel.getLogin(LoginRequest(email = "ec2", password = "123"))
            }
            viewModel.getLogin(LoginRequest(email = binding.edEmail.text.toString(), password = binding.edPassword.text.toString()))
        }
        binding.tvRegister.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            findNavController().navigate(R.id.action_loginFragment_to_registerAccountFragment)
        }

        binding.loginWithGoogle.setOnClickListener {
            loginWithGoogle()
        }

        binding.loginWithFacebook.setOnClickListener {
            loginWithFacebook()
        }

        binding.loginTvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    override fun setObserve() {
        viewModel.getLoginResult().observe(viewLifecycleOwner, Observer {
            getLoginResult(it)
        })
    }

    private fun getLoginResult(event: Event<Resource<LoginResponse>>){
        event.getContentIfNotHandled()?.let { response ->
            when (response) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    preferences.saveUserData(user = response.data?.user,token = token)
                    SocketIOManager().join(response.data?.user?._id.toString())
                    startActivity(Intent(requireContext(),MainActivity::class.java))
                    requireActivity().finish()
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    response.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun loginWithGoogle() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val intent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    private fun loginWithFacebook() {
        binding.loginBtnFacebook.setReadPermissions("email")
        binding.loginBtnFacebook.fragment = this
        binding.loginBtnFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                val token = loginResult?.accessToken
                val request = GraphRequest.newMeRequest(
                    token
                ) { obj, response ->
                    try {
                        // Save user email to variable
                        val email = obj.getString("email")
                        val firstName = obj.getString("first_name")
                        val lastName = obj.getString("last_name")
                        val id = obj.getString("id")
                        val picture = Extensions.urlImageFb(id)
                        Glide.with(requireContext())
                            .load(picture)
                            .into(binding.loginWithFacebook)
                        registerViewModel.registerAccount(
                            RegisterRequest(
                                name = firstName + lastName,
                                email = email,
                                image = picture
                            )
                        )
                    } catch (e: JSONException) {
                        Log.e("zzzz", e.localizedMessage.toString())
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "email,first_name,last_name")
                request.parameters = parameters
                request.executeAsync()
            }
            override fun onCancel() {
            }
            override fun onError(exception: FacebookException) {
                Log.e("zzzz1",exception.localizedMessage.toString())
            }
        })
        LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("public_profile","email"))
    }

    private fun printHashKey() {
        try {
            val info: PackageInfo = requireActivity().packageManager.getPackageInfo(
                requireActivity().packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d(
                    "KeyHash:",
                    Base64.encodeToString(
                        md.digest(),
                        Base64.DEFAULT
                    )
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account : GoogleSignInAccount? = task.getResult(ApiException::class.java)
                if (account != null) {
                    registerViewModel.registerAccount(
                        RegisterRequest(
                            name = account.displayName,
                            email = account.email,
                            image = account.photoUrl.toString(),
                            password = "ecommerce"
                        )
                    )
                    Log.e("TAG","signInResult:success code=" + account.email)
                }else {
                    val intent = mGoogleSignInClient!!.signInIntent
                    startActivityForResult(intent, RC_SIGN_IN)
                }
            } catch (e: ApiException) {
                Log.e("TAG","signInResult:failed code=" + e.statusCode)
            }
        }
    }
}