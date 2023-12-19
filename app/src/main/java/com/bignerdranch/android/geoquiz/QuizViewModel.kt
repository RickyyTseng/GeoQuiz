import android.media.MediaCodec.QueueRequest
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bignerdranch.android.geoquiz.Question
import com.bignerdranch.android.geoquiz.R

private const val TAG = "QuizViewModel"
const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true),
        Question(R.string.question_mountain, true),
        Question(R.string.question_population, false),
        Question(R.string.question_oceanAmount,false),
        Question(R.string.question_river,true),
        Question(R.string.question_thailand,true),
        Question(R.string.question_taiwan,true)
    )
    private val cheatedQuestions = MutableList(questionBank.size) { false }
    private val answeredQuestions = BooleanArray(questionBank.size) { false }
    private var correctAnswers = 0

    var isCheater: Boolean
        get() = savedStateHandle.get(IS_CHEATER_KEY) ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)

    var currentIndex : Int
        get() = savedStateHandle.get(CURRENT_INDEX_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext(){
        currentIndex = (currentIndex + 1) % questionBank.size
    }
    fun moveToBack(){
        currentIndex = (currentIndex - 1) % questionBank.size
    }
    fun isQuestionAnswered(index: Int): Boolean {
        return answeredQuestions[index]
    }
    fun setQuestionAnswered(index: Int) {
        answeredQuestions[index] = true
    }
    fun incrementCorrectAnswers() {
        correctAnswers += 1
    }
    fun getQuizScore(): Int {
        return (correctAnswers.toDouble() / questionBank.size.toDouble() * 100).toInt()
    }
    fun isAllQuestionsAnswered(): Boolean {
        return answeredQuestions.all { it }
    }
    fun isQuestionCheated(index: Int): Boolean {
        return cheatedQuestions[index]
    }
    fun setQuestionCheated(index: Int, cheated: Boolean) {
        cheatedQuestions[index] = cheated
    }
}