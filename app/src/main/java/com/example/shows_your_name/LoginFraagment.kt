package com.example.shows_your_name

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.example.shows_your_name.databinding.FragmentLoginFraagmentBinding

class LoginFraagment : Fragment() {
    private var _binding: FragmentLoginFraagmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginFraagmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val bundle = bundleOf("Username" to binding.emailTexttxt.text.toString().substringBefore("@"))

        binding.loginbtn.setOnClickListener{
            findNavController().navigate(R.id.to_showsFragment,bundle)
            //val intent = ShowsActivity.buildIntent(this)
            //intent.putExtra("Username",binding.emailTexttxt.text.toString().substringBefore("@"))
            //startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}