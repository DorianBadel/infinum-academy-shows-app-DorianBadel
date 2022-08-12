package com.example.shows_your_name

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.shows_your_name.databinding.DialogRegistrationStateBinding
import com.example.shows_your_name.databinding.FragmentRegisterFragmentBinding
import com.example.shows_your_name.newtworking.ApiModule
import com.example.shows_your_name.viewModels.RegistrationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        ApiModule.initRetrofit(requireContext())

        viewModel.getRegistrationResultLiveData().observe(this){ registrationSuccess ->
            displayRegistrationMessage(registrationSuccess)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                binding.emailTexttxt.text.toString(),
                binding.passwordTexttxt.text.toString(),
                binding.passwordRepeatTexttxt.text.toString()
            )

        }
    }

    private fun displayRegistrationMessage(isSuccessful: Boolean) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = DialogRegistrationStateBinding.inflate(layoutInflater)

        if (isSuccessful) {
            bottomSheetBinding.registrationMessage.text = getString(R.string.registration_success_message)
        } else {
            bottomSheetBinding.registrationMessage.text = getString(R.string.registration_failure_message)
        }
        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()


        val action = RegisterFragmentDirections.regToLogin(binding.emailTexttxt.text.toString())
        findNavController().navigate(action)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}