import androidx.lifecycle.ViewModel
import com.example.stepcounter.data.stepHistory
import com.example.stepcounter.ui.StepCounterUIState
import com.example.stepcounter.ui.StepHistory
import com.example.stepcounter.ui.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

class StepCounterViewModel : ViewModel() {

    // Holds the app's UI state
    private val _uiState = MutableStateFlow(StepCounterUIState())
    val uiState: StateFlow<StepCounterUIState> = _uiState.asStateFlow()


    init {
        resetStepCounter()
    }

    fun resetStepCounter() {
        _uiState.value = StepCounterUIState()
    }

    // Function to update the step count (e.g., called when steps are detected)
    fun updateStepCount(newSteps: Int) {
        _uiState.update {
                currentState -> currentState.copy(stepCount = newSteps)
        }
    }

    // Function to set a new daily goal
    fun setStepGoal(goal: Int) {
        _uiState.update {
                currentState -> currentState.copy(stepGoal = goal)
        }
    }

    // Function to add a new history entry (e.g., at the end of the day)
    fun addStepHistoryEntry() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val newHistory = stepHistory + StepHistory(date = today, steps = _uiState.value.stepCount)

        //update the history

        // Reset the step count at the end of the day and update history
        resetStepCounter()
    }

    // Register a new user (name, surname, profession)
    fun registerUser(uniqueID: String, name: String, surname: String, profession: String, email: String) {
        val user = User(uniqueID, name, surname, profession, email)
        _uiState.update {
                currentState -> currentState.copy(user = user)
        }
    }




}
