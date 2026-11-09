package com.movievortex.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.movievortex.android.ui.theme.VortexColors

@Composable
fun AuthModal(
    actionReason: String = "Sign in to sync your Watchlist and continue watching.",
    onDismiss: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(VortexColors.CardSurface)
                .border(1.dp, VortexColors.GlassBorder, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = VortexIcons.Close,
                            contentDescription = "Close",
                            tint = VortexColors.TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Brand Logo
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                        .border(1.5.dp, VortexColors.NeonCrimsonGlow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = com.lagradost.cloudstream3.R.drawable.vortex_brand_logo,
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isSignUp) "Create Vortex Account" else "Sign In to MovieVortex",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = actionReason,
                    color = VortexColors.TextMuted,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email address", color = VortexColors.TextMuted, fontSize = 13.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = VortexColors.NeonCrimson,
                        unfocusedBorderColor = VortexColors.BorderSubtle,
                        focusedContainerColor = VortexColors.DarkGraphite,
                        unfocusedContainerColor = VortexColors.DarkGraphite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password", color = VortexColors.TextMuted, fontSize = 13.sp) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = VortexColors.NeonCrimson,
                        unfocusedBorderColor = VortexColors.BorderSubtle,
                        focusedContainerColor = VortexColors.DarkGraphite,
                        unfocusedContainerColor = VortexColors.DarkGraphite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Submit button
                Button(
                    onClick = {
                        val user = if (email.isNotBlank()) email.substringBefore("@") else "Vortex User"
                        onLoginSuccess(user)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isSignUp) "Create Account" else "Sign In",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Toggle Sign Up / Sign In
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isSignUp) "Already have an account?" else "Don't have an account?",
                        color = VortexColors.TextMuted,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isSignUp) "Sign In" else "Sign Up",
                        color = VortexColors.NeonCrimson,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { isSignUp = !isSignUp }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = VortexColors.TextSecondary)
                ) {
                    Text(
                        text = "Continue as Guest",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
    }
}
