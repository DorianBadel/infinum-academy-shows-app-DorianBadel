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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.example.shows_your_name.databinding.FragmentLoginFraagmentBinding

class LoginFraagment : Fragment() {
    private var _binding: FragmentLoginFraagmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private val IS_REMEMBERED = "IS_REMEMBERED"
    private val REMEMBERED_USER = "REMEMBERED_USER"

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE)
        sharedPreferences = requireContext().getSharedPreferences("Username", Context.MODE_PRIVATE)
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

        val username = sharedPreferences.getString(REMEMBERED_USER, "")

        if(isRemembered){
            val bundle = bundleOf("Username" to username)
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

            val bundle = bundleOf("Username" to binding.emailTexttxt.text.toString().substringBefore("@"))

            findNavController().navigate(R.id.to_showsFragment,bundle)
            //val intent = ShowsActivity.buildIntent(this)
            //intent.putExtra("Username",binding.emailTexttxt.text.toString().substringBefore("@"))
            //startActivity(intent)

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}