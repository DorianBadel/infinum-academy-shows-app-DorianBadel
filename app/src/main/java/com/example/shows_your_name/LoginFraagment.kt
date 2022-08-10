package com.example.shows_your_name

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.os.bundleOf
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


    //Constants
    private val sharedPrefs = "SHARED_STORAGE"
    private val IS_REMEMBERED = "IS_REMEMBERED"
    private val REMEMBERED_USER = "REMEMBERED_USER"
    private val ctUsername = "Username"
    private val ctEmail = "Email"

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        ApiModule.initRetrofit(requireContext())

        sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)

        viewModel.getLoginResultsLiveData().observe(this){ loginSuccess ->
            displayLoginMessage(loginSuccess)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginFraagmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(args.email != "null"){
            binding.emailTexttxt.setText(args.email)
        }

        val isRemembered = sharedPreferences.getBoolean(IS_REMEMBERED, false)
        binding.cbRememberMe.isChecked = isRemembered
        if(!arguments?.getString(ctEmail).isNullOrBlank()){
            binding.loginText.text = "Registration successfull!"
            binding.registerbtn.isVisible = false
        }

        if(isRemembered){
            findNavController().navigate(R.id.to_showsFragment)
        }

        binding.loginbtn.isEnabled = false
        var emailCorrect = false;
        var passCorrect = false;

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
                putBoolean(IS_REMEMBERED, isChecked)
                putString(REMEMBERED_USER, binding.emailTexttxt.text.toString().substringBefore("@"))
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
            bottomSheetBinding.registrationMessage.text = "Login succesful"
        } else {
            bottomSheetBinding.registrationMessage.text = "Login not successful"
        }
        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()

        findNavController().navigate(R.id.to_showsFragment)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeLoginBtn(){
        binding.loginbtn.isEnabled = true
        binding.loginbtn.alpha = 1F
    }
}