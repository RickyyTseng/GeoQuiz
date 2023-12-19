package com.bignerdranch.android.geoquiz

import QuizViewModel
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater = result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        binding.trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
            disableAnswerButton()
        }
        binding.falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
            disableAnswerButton()
        }
        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
        binding.cheatButton.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }
        // Challenge 1, creating back button
        binding.backButton.setOnClickListener {
            quizViewModel.moveToBack()
            updateQuestion()
        }
        updateQuestion()
    }
    //Challenge 2, no repeating answers
    private fun disableAnswerButton() {
        binding.trueButton.isEnabled = false
        binding.falseButton.isEnabled = false
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG,"onPause() called")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG,"onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)

        val currentQuestionIndex = quizViewModel.currentIndex
        val isQuestionAnswered = quizViewModel.isQuestionAnswered(currentQuestionIndex)

        binding.trueButton.isEnabled = !isQuestionAnswered
        binding.falseButton.isEnabled = !isQuestionAnswered
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        var currentIndex = quizViewModel.currentIndex

        if (quizViewModel.isQuestionCheated(currentIndex)) {
            Toast.makeText(this, R.string.judgment_toast, Toast.LENGTH_SHORT).show()
        }
        else if (userAnswer == correctAnswer) {
            quizViewModel.incrementCorrectAnswers()
            Toast.makeText(this, R.string.correct_toast, Toast.LENGTH_SHORT).show()
            quizViewModel.setQuestionAnswered(currentIndex)
        }
        else{
            Toast.makeText(this, R.string.incorrect_toast, Toast.LENGTH_SHORT).show()
            quizViewModel.setQuestionAnswered(currentIndex)
        }

        if (quizViewModel.isAllQuestionsAnswered()) {
            val quizScore = quizViewModel.getQuizScore()
            Toast.makeText(this, getString(R.string.score, quizScore), Toast.LENGTH_LONG).show()
        }
    }
}
