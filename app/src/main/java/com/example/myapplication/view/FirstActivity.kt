package com.example.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.databinding.ActivityFirstBinding

class FirstActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.btnCheck.setOnClickListener {
            val inputText = binding.etPalindrome.text.toString()
            if (inputText.isBlank()) {
                showDialog("Please enter a valid string.")
                return@setOnClickListener
            }

            val (isPalindrome, cleanedText, reversedText) = isPalindrome(inputText)
            val message = if (isPalindrome) {
                "Palindrome\nOriginal: $inputText\nCleaned: $cleanedText\nReversed: $reversedText"
            } else {
                "Not a palindrome\nOriginal: $inputText\nCleaned: $cleanedText\nReversed: $reversedText"
            }
            showDialog(message)
        }

        binding.btnNext.setOnClickListener {
            val nameText = binding.etName.text.toString()
            if (nameText.isBlank()) {
                showDialog("Please enter your name.")
            } else {
                val intent = Intent(this, SecondActivity::class.java)
                intent.putExtra("EXTRA_NAME", nameText)
                startActivity(intent)
            }
        }
    }

    private fun isPalindrome(text: String): Triple<Boolean, String, String> {
        val cleanedText = text.filter { it.isLetterOrDigit() }.lowercase()
        val reversedText = cleanedText.reversed()
        return Triple(cleanedText == reversedText, cleanedText, reversedText)
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}