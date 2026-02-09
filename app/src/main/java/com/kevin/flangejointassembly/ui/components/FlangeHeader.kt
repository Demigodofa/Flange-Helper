package com.kevin.flangejointassembly.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kevin.flangejointassembly.ui.FlangeColors

@Composable
fun FlangeHeader(
    onBack: () -> Unit,
    logoResName: String = "flange_helper_512"
) {
    val context = LocalContext.current
    val logoResId = remember(context, logoResName) {
        val id = context.resources.getIdentifier(
            logoResName,
            "drawable",
            context.packageName
        )
        if (id != 0) id else android.R.drawable.sym_def_app_icon
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .size(60.dp)
                .shadow(9.dp, CircleShape)
                .clickable(onClick = onBack),
            shape = CircleShape,
            color = FlangeColors.HeaderButton
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        Image(
            painter = painterResource(id = logoResId),
            contentDescription = "Flange Helper Logo",
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp)
        )
    }
}
