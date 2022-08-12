package com.example.shows_your_name

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shows_your_name.databinding.DialogRegistrationStateBinding
import com.example.shows_your_name.databinding.FragmentLoginFraagmentBinding
import com.example.shows_your_name.newtworking.ApiModule
import com.example.shows_your_name.viewModels.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class LoginFraagment : Fragment() {
    private var _binding: FragmentLoginFraagmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private val args by navArgs<LoginFraagmentArgs>()

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        ApiModule.initRetrofit(requireContext())

        sharedPreferences = requireContext().getSharedPreferences(getString(R.string.sharedPreferences), Context.MODE_PRIVATE)

        viewModel.initLoginViewModel(
            getString(R.string.sharedPreferences),
            getString(R.string.ct_username),
            getString(R.string.ACCESS_TOKEN),
            getString(R.string.CLIENT),
            getString(R.string.UID),
            getString(R.string.TOKEN_TYPE),
            getString(R.string.ct_access_token),
            getString(R.string.ct_bearer),
            getString(R.string.REMEMBERED_USERR)
        )

        viewModel.getLoginResultsLiveData().observe(this){ loginSuccess ->
            displayLoginMessage(loginSuccess)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginFraagmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animateLogo()
        animateLogoText()



        val isRemembered = sharedPreferences.getBoolean(getString(R.string.IS_REMEMBERED), false)
        binding.cbRememberMe.isChecked = isRemembered
        if(!arguments?.getString(getString(R.string.ct_email)).isNullOrBlank()){
            binding.loginText.text = getString(R.string.registration_success_message)
            binding.registerbtn.isVisible = false
        }

        if(isRemembered){
            findNavController().navigate(R.id.to_showsFragment)
        }

        binding.loginbtn.isEnabled = false
        var emailCorrect = false
        var passCorrect = false

        if(args.email != "null"){
            binding.emailTexttxt.setText(args.email)
            emailCorrect = true
        }

        binding.emailTexttxt.doAfterTextChanged {
            emailCorrect =
                android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailTexttxt.text.toString()).matches()

            if(emailCorrect && passCorrect){
                changeLoginBtn()
            }
        }
        binding.passwordTexttxt.doAfterTextChanged {
            passCorrect = binding.passwordTexttxt.text.toString() != ""

            if(emailCorrect && passCorrect){
                changeLoginBtn()
            }
        }


        binding.loginbtn.setOnClickListener{

            viewModel.onLoginButtonClicked(
                binding.emailTexttxt.text.toString(),
                binding.passwordTexttxt.text.toString()
            )

        }

        binding.cbRememberMe.setOnCheckedChangeListener{
            _, isChecked ->
            sharedPreferences.edit {
                putBoolean(getString(R.string.IS_REMEMBERED), isChecked)
                putString(getString(R.string.REMEMBERED_USER), binding.emailTexttxt.text.toString().substringBefore("@"))
            }
        }

        binding.registerbtn.setOnClickListener{
            findNavController().navigate(R.id.to_registerFragment)
        }
    }

    private fun displayLoginMessage(isSuccessful: Boolean) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = DialogRegistrationStateBinding.inflate(layoutInflater)

        if (isSuccessful) {
            bottomSheetBinding.registrationMessage.text = getString(R.string.login_success_message)
            findNavController().navigate(R.id.to_showsFragment)
        } else {
            bottomSheetBinding.registrationMessage.text = getString(R.string.login_failure_message)
        }
        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeLoginBtn(){
        binding.loginbtn.isEnabled = true
        binding.loginbtn.alpha = 1F
    }

    fun animateLogo() = with(binding.playicon){
        translationY = -200f
        animate()
            .translationY(0f)
            .setDuration(1000)
            .setInterpolator(BounceInterpolator())
            .start()
    }

    fun animateLogoText() = with(binding.loginTitleText){
        scaleX = 0f
        scaleY = 0f
        animate()
            .setStartDelay(1000)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1000)
            .setInterpolator(OvershootInterpolator())
            .start()
    }
}
