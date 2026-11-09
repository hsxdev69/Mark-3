package com.movievortex.android.ui.dns

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.movievortex.android.ui.components.VortexIcons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movievortex.android.controller.DnsOption
import com.movievortex.android.controller.SettingsController
import com.movievortex.android.ui.theme.VortexColors

@Composable
fun DnsIntroScreen(
    settingsController: SettingsController,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by settingsController.uiState.collectAsState()
    var isInstructionsExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VortexColors.PureBlack)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .clip(RoundedCornerShape(20.dp))
                .background(VortexColors.CardSurface)
                .border(1.dp, VortexColors.BorderSubtle, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Network Shield Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0x33E50914)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = VortexIcons.Security,
                    contentDescription = "Secure DNS",
                    tint = VortexColors.CinematicRed,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Secure & Private Streaming",
                color = VortexColors.PrimaryText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select your preferred secure DNS (DoH) to bypass network blocks, eliminate ISP throttling, and ensure optimal streaming performance.",
                color = VortexColors.SecondaryText,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // DNS Option Rows
            settingsController.dnsOptions.forEach { option ->
                DnsOptionRow(
                    option = option,
                    isSelected = state.selectedDnsId == option.id,
                    onSelect = { settingsController.setDns(option.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Collapsible Instructions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isInstructionsExpanded = !isInstructionsExpanded }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Why is secure DNS recommended?",
                    color = VortexColors.CoolBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = if (isInstructionsExpanded) VortexIcons.ChevronUp else VortexIcons.ChevronDown,
                    contentDescription = "Toggle instructions",
                    tint = VortexColors.CoolBlue,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = isInstructionsExpanded) {
                Text(
                    text = "Encrypted DNS prevents your ISP or router from inspecting media requests, reducing buffering issues and bypassing regional domain filters seamlessly.",
                    color = VortexColors.MutedText,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Confirmation Action Button
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Apply & Continue",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DnsOptionRow(
    option: DnsOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (isSelected) VortexColors.CinematicRed else VortexColors.BorderSubtle
    val bgColor = if (isSelected) Color(0x1AE50914) else VortexColors.DarkGraphite

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = VortexColors.CinematicRed,
                unselectedColor = VortexColors.MutedText
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = option.name,
                    color = VortexColors.PrimaryText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (option.id == 2) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0x3310B981), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "FASTEST",
                            color = VortexColors.EmeraldGreen,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = option.description,
                color = VortexColors.MutedText,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}
