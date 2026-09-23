package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.BirdSkin
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.PixelBirdAvatar
import com.example.ui.components.PixelButton
import com.example.ui.components.PixelCard
import com.example.ui.theme.PixelBorderDark
import com.example.ui.theme.PixelGold
import com.example.ui.theme.RetroFontFamily

@Composable
fun ShopScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.playerProfile.collectAsState()
    val coins = profile?.coins ?: 0
    val equippedSkin = profile?.equippedSkin ?: "classic_yellow"
    val unlockedSkins = remember(profile?.unlockedSkins) {
        profile?.unlockedSkins?.split(",")?.map { it.trim() } ?: listOf("classic_yellow")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    modifier = Modifier
                        .border(2.dp, MaterialTheme.colorScheme.outline)
                        .background(MaterialTheme.colorScheme.surface)
                        .size(38.dp)
                        .testTag("shop_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "ARCADE PIXEL SHOP",
                    fontFamily = RetroFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Coin Counter
                Box(
                    modifier = Modifier
                        .border(2.dp, PixelBorderDark)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🟡 $coins",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = PixelGold
                    )
                }
            }

            // Scrollable Shop Items
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section: Upgrades
                item {
                    Text(
                        text = "PERMANENT ARCADE UPGRADES:",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    val extraHearts = profile?.extraHeartsUnlocked ?: 0
                    UpgradeCard(
                        title = "Extra Starting Heart (+1)",
                        desc = "Begin every run with an additional heart! (Max 2)",
                        icon = Icons.Default.Favorite,
                        iconTint = Color(0xFFFF2252),
                        cost = 350,
                        currentLevel = extraHearts,
                        maxLevel = 2,
                        canAfford = coins >= 350,
                        onBuy = { viewModel.purchaseUpgrade("extra_heart", 350) }
                    )
                }

                item {
                    val hasMagnet = profile?.shieldMagnetUnlocked ?: false
                    UpgradeCard(
                        title = "Coin Shield Magnet",
                        desc = "Pulls coins within 3x radius during flight!",
                        icon = Icons.Default.Security,
                        iconTint = Color(0xFF00F5D4),
                        cost = 450,
                        currentLevel = if (hasMagnet) 1 else 0,
                        maxLevel = 1,
                        canAfford = coins >= 450,
                        onBuy = { viewModel.purchaseUpgrade("shield_magnet", 450) }
                    )
                }

                // Section: Character Skins
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "8-BIT CHARACTER SKINS:",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                items(BirdSkin.ALL_SKINS) { skin ->
                    val isOwned = unlockedSkins.contains(skin.id)
                    val isEquipped = equippedSkin == skin.id
                    SkinShopCard(
                        skin = skin,
                        isOwned = isOwned,
                        isEquipped = isEquipped,
                        canAfford = coins >= skin.price,
                        onEquip = { viewModel.equipSkin(skin.id) },
                        onBuy = { viewModel.purchaseSkin(skin.id, skin.price) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SkinShopCard(
    skin: BirdSkin,
    isOwned: Boolean,
    isEquipped: Boolean,
    canAfford: Boolean,
    onEquip: () -> Unit,
    onBuy: () -> Unit
) {
    PixelCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (isEquipped) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
        borderColor = if (isEquipped) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PixelBirdAvatar(skinId = skin.id, modifier = Modifier.size(46.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = skin.name,
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (skin.id == "cyber_violet") {
                            Text(
                                text = "[EVENT]",
                                fontFamily = RetroFontFamily,
                                fontSize = 9.sp,
                                color = Color(0xFFFF007F),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = skin.description,
                        fontFamily = RetroFontFamily,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            // Action: Equipped / Equip / Buy
            when {
                isEquipped -> {
                    Box(
                        modifier = Modifier
                            .border(1.5.dp, Color(0xFF00F5D4))
                            .background(Color(0xFF052B24))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "EQUIPPED",
                            fontFamily = RetroFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF00F5D4)
                        )
                    }
                }
                isOwned -> {
                    PixelButton(
                        text = "EQUIP",
                        onClick = onEquip,
                        backgroundColor = Color(0xFF0284C7),
                        fontSize = 11
                    )
                }
                else -> {
                    PixelButton(
                        text = "${skin.price} 🟡",
                        onClick = onBuy,
                        enabled = canAfford,
                        backgroundColor = if (canAfford) PixelGold else Color.Gray,
                        textColor = Color.Black,
                        fontSize = 11
                    )
                }
            }
        }
    }
}

@Composable
private fun UpgradeCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    cost: Int,
    currentLevel: Int,
    maxLevel: Int,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    val isMax = currentLevel >= maxLevel

    PixelCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.5.dp, Color.Black)
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint)
                }
                Column {
                    Text(
                        text = title,
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = desc,
                        fontFamily = RetroFontFamily,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Level: $currentLevel / $maxLevel",
                        fontFamily = RetroFontFamily,
                        fontSize = 9.sp,
                        color = iconTint,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isMax) {
                Box(
                    modifier = Modifier
                        .border(1.5.dp, Color(0xFF73BF2E))
                        .background(Color(0xFF1E3A0F))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "MAXED",
                        fontFamily = RetroFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Color(0xFF9CE659)
                    )
                }
            } else {
                PixelButton(
                    text = "$cost 🟡",
                    onClick = onBuy,
                    enabled = canAfford,
                    backgroundColor = if (canAfford) PixelGold else Color.Gray,
                    textColor = Color.Black,
                    fontSize = 11
                )
            }
        }
    }
}
