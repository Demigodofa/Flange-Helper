package com.kevin.flangejointassembly.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import com.kevin.flangejointassembly.R
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun StartScreen(onStart: () -> Unit) {
    Scaffold(
        containerColor = FlangeColors.ScreenBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(FlangeColors.ScreenBackground)
        ) {
            val screenHeight = LocalConfiguration.current.screenHeightDp.dp
            val logoSize = (screenHeight * 0.25f).coerceIn(96.dp, 160.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = R.drawable.flange_helper_512),
                    contentDescription = "Flange Helper Logo",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(logoSize)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.75f)
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FlangeColors.PrimaryButton,
                        contentColor = FlangeColors.PrimaryButtonText
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text(
                        text = "Create New Report",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(
                    color = FlangeColors.Divider,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No reports yet. Create your first report above.",
                        textAlign = TextAlign.Center,
                        color = FlangeColors.TextMuted
                    )
                }
            }
        }
    }
}
