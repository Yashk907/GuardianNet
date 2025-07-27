import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardiannetapp.Viewmodels.GurdianViewmodels.connectToPatientVM
import com.example.guardiannetapp.ui.theme.Poppins

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPatientScreen(
    viewModel : connectToPatientVM,
    userId : String,
    modifier : Modifier= Modifier
) {
    var patientCode by remember { mutableStateOf("") }
    val isLoading = viewModel.isLoading.collectAsState()
    val context = LocalContext.current
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            //verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Link Patient",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Patient Code Input
            OutlinedTextField(
                value = patientCode,
                onValueChange = { patientCode = it },
                label = {
                    Text(
                        text = "Patient Code",
                        color = Color(0xFF607D8B),
                        fontFamily = Poppins,
                    )
                },
                placeholder = { Text(text = "",fontFamily = Poppins,) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Helper Text
            Text(
                text = "Enter the unique code provided by the patient’s device to link and track their status.",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF607D8B), // subtle blue-grey
                modifier = Modifier
                    .padding(bottom = 32.dp)
            )

            // Add Patient Button
            Button(
                onClick = {
                    viewModel.connectToPatient (userId = userId,
                        linkCode = patientCode){
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if(isLoading.value){
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                }else{
                    Text(
                        text = "Add Patient",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium
                    )
                }

            }
        }
}
@Composable
fun TopAppBar(
    title: String,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
            onClick = onMoreClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = Color.Black
            )
        }
    }
}