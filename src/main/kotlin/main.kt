import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.TimeUnit


fun main() = Window(title = "Compose for Desktop", size = IntSize(500, 500)) {

    MaterialTheme(colors = lightColors()) {
        loadUi()
    }
}

@Composable
fun loadUi() {

    val email = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }

    val signInMessage = remember {
        mutableStateOf("")
    }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp,vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp)),
            Arrangement.spacedBy(8.dp)
        )
        {
            Text(modifier = Modifier
                .align(Alignment.CenterHorizontally),
                text = "Sign In",
                color = MaterialTheme.colors.primary,
            fontSize = 24.sp)


            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                value = email.value,
                onValueChange = {
                    email.value = it
                },
                label = { Text("Enter your email") }
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                value = password.value,
                onValueChange = {
                    password.value = it
                },
                label = { Text("Enter your password") }
            )

            if (signInMessage.value.length>5){
            Text(color = MaterialTheme.colors.secondaryVariant,text = signInMessage.value)}

            Button(modifier =Modifier
                .background(color = MaterialTheme.colors.primary)
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.CenterHorizontally),
                onClick = {signInMessage.value = signIn(email.value,password.value)} )
            { Text("Login")}
        }
    }


fun validateEmail(email: String):Boolean {
    return email.length>5
}

fun signIn(email:String,password:String):String{
    val userJson = Gson().toJson(User(email,password))
    println(userJson)

    if (validateEmail(email))
    {
        val client = HttpClient.newHttpClient()

        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/login"))
            .POST(HttpRequest.BodyPublishers.ofString(userJson))
            .header("Content-Type", "application/json; charset=utf8")
            .build()

        val response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse<String>::body)
            .get(10,TimeUnit.SECONDS)

        val res = Gson().fromJson(response,Response::class.java) ?: return "Login Error"

        println(res)

        return res.message
    }
    else
        return "Failed to connect"


}



