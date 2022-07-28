package com.example.shows_your_name

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.shows_your_name.databinding.DialogRegistrationStateBinding
import com.example.shows_your_name.databinding.FragmentRegisterFragmentBinding
import com.example.shows_your_name.newtworking.ApiModule
import com.google.android.material.bottomsheet.BottomSheetDialog

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel: RegistrationViewModel by viewModels()


    //Constants
    private val IS_REMEMBERED = "IS_REMEMBERED"
    private val REMEMBERED_USER = "REMEMBERED_USER"
    private val ctUser = "User"
    private val ctUsername = "Username"

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(ctUser, Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences(ctUsername, Context.MODE_PRIVATE)

        ApiModule.initRetrofit(requireContext())

        viewModel.getRegistrationResultLiveData().observe(this){ registrationSuccess ->
            displayRegistrationMessage(registrationSuccess)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isRemembered = sharedPreferences.getBoolean(IS_REMEMBERED, false)

        val username = sharedPreferences.getString(REMEMBERED_USER, "")

        if(isRemembered){
            val bundle = bundleOf(ctUsername to username)
            findNavController().navigate(R.id.to_showsFragment,bundle)
        }

        binding.registerButton.isEnabled = false
        var emailCorrect = false
        var passCorrect = false
        var repeatPassCorrect = false

        binding.emailTexttxt.doAfterTextChanged {
            emailCorrect =
                android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailTexttxt.text.toString()).matches()

            binding.registerButton.isEnabled = emailCorrect && passCorrect && repeatPassCorrect
        }
        binding.passwordTexttxt.doAfterTextChanged {
            passCorrect = binding.passwordTexttxt.text.toString() != ""
            if(binding.passwordRepeatTexttxt.text.toString() != ""
                && binding.passwordRepeatTexttxt.text.toString() == binding.passwordTexttxt.text.toString()){
                repeatPassCorrect = true
            }

            binding.registerButton.isEnabled = emailCorrect && passCorrect && repeatPassCorrect
        }

        binding.passwordRepeatTexttxt.doAfterTextChanged {
            if(binding.passwordRepeatTexttxt.text.toString() != ""
                && binding.passwordRepeatTexttxt.text.toString() == binding.passwordTexttxt.text.toString()){
                repeatPassCorrect = true
            }

            binding.registerButton.isEnabled = emailCorrect && passCorrect && repeatPassCorrect
        }


        binding.registerButton.setOnClickListener{

            viewModel.onRegisterButtonClicked(
                this,
                binding
            )

            //val bundle = bundleOf(ctEmail to binding.emailTexttxt.text.toString())

            //findNavController().navigate(R.id.reg_to_loginFraagment,bundle)

        }
    }

    private fun displayRegistrationMessage(isSuccessful: Boolean) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = DialogRegistrationStateBinding.inflate(layoutInflater)

        if (isSuccessful) {
            bottomSheetBinding.registrationMessage.text = "Registration successful"
        } else {
            bottomSheetBinding.registrationMessage.text = "Registration not successful"
        }
        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}