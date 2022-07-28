package com.example.shows_your_name

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
import com.example.shows_your_name.databinding.DialogRegistrationStateBinding
import com.example.shows_your_name.databinding.FragmentLoginFraagmentBinding
import com.example.shows_your_name.newtworking.ApiModule
import com.example.shows_your_name.viewModels.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class LoginFraagment : Fragment() {
    private var _binding: FragmentLoginFraagmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences


    //Constants
    private val IS_REMEMBERED = "IS_REMEMBERED"
    private val REMEMBERED_USER = "REMEMBERED_USER"
    private val ctUser = "User"
    private val ctUsername = "Username"
    private val ctEmail = "Email"

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        ApiModule.initRetrofit(requireContext())

        sharedPreferences = requireContext().getSharedPreferences(ctUser, Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(ctUsername, Context.MODE_PRIVATE)
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

        val isRemembered = sharedPreferences.getBoolean(IS_REMEMBERED, false)
        binding.cbRememberMe.isChecked = isRemembered
        if(!arguments?.getString(ctEmail).isNullOrBlank()){
            binding.loginText.text = "Registration successfull!"
            binding.registerbtn.isVisible = false
        }

        val username = sharedPreferences.getString(REMEMBERED_USER, "")

        if(isRemembered){
            val bundle = bundleOf(ctUsername to username)
            findNavController().navigate(R.id.to_showsFragment,bundle)
        }

        binding.loginbtn.isEnabled = false
        var emailCorrect = false;
        var passCorrect = false;

        binding.emailTexttxt.doAfterTextChanged {
            emailCorrect =
                android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailTexttxt.text.toString()).matches()

            binding.loginbtn.isEnabled = emailCorrect && passCorrect
        }
        binding.passwordTexttxt.doAfterTextChanged {
            passCorrect = binding.passwordTexttxt.text.toString() != ""

            binding.loginbtn.isEnabled = emailCorrect && passCorrect
        }


        binding.loginbtn.setOnClickListener{
            viewModel.onLoginButtonClicked(this,binding)

            /*val bundle = bundleOf(ctUsername to binding.emailTexttxt.text.toString().substringBefore("@"))

            findNavController().navigate(R.id.to_showsFragment,bundle)*/

        }

        binding.cbRememberMe.setOnCheckedChangeListener{
            _, isChecked ->
            sharedPreferences.edit {
                putBoolean(IS_REMEMBERED, isChecked)
            }
            sharedPreferences.edit{
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}