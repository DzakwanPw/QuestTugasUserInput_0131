package com.example.pamp5bagian2

import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val ZONA_UTC = "UTC"

@Composable
private fun spResource(@DimenRes id: Int): TextUnit =
    with(LocalDensity.current) { dimensionResource(id).toSp() }

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = colorResource(R.color.purple_primary),
    unfocusedBorderColor = colorResource(R.color.outline_gray),
    focusedTextColor = colorResource(R.color.text_input),
    unfocusedTextColor = colorResource(R.color.text_input),
    focusedLabelColor = colorResource(R.color.purple_primary),
    unfocusedLabelColor = colorResource(R.color.text_hint),
    cursorColor = colorResource(R.color.purple_primary)
)

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes label: Int,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        shape = RoundedCornerShape(dimensionResource(R.dimen.field_radius)),
        label = { Text(stringResource(label)) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = fieldColors()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormBiodataScreen() {
    var namaDepan by remember { mutableStateOf("") }
    var namaBelakang by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    val skillTerpilih = remember { mutableStateListOf<String>() }

    val experienceOptions = stringArrayResource(R.array.experience_list)
    val skillOptions = stringArrayResource(R.array.skill_list)
    val durasiPopup = integerResource(R.integer.durasi_popup_ms)

    val formLengkap = namaDepan.isNotBlank() &&
            namaBelakang.isNotBlank() &&
            email.isNotBlank() &&
            tanggal.isNotBlank() &&
            experience.isNotBlank() &&
            skillTerpilih.isNotEmpty()

    val fieldShape = RoundedCornerShape(dimensionResource(R.dimen.field_radius))
    val cardShape = RoundedCornerShape(dimensionResource(R.dimen.card_radius))

    // klik di kolom Tgl Lahir juga membuka kalender
    val tanggalSource = remember { MutableInteractionSource() }
    LaunchedEffect(tanggalSource) {
        tanggalSource.interactions.collect {
            if (it is PressInteraction.Release) showDatePicker = true
        }
    }

    // pop up hasil tertutup otomatis setelah 5 detik
    LaunchedEffect(showResult) {
        if (showResult) {
            delay(durasiPopup.toLong())
            showResult = false
        }
    }

    Scaffold(containerColor = colorResource(R.color.screen_bg)) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {

            // Gambar header
            Image(
                painter = painterResource(R.drawable.header_neon),
                contentDescription = stringResource(R.string.cd_header),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.header_height))
            )

            // Indikator titik
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(dimensionResource(R.dimen.dot_padding)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dot_spacing))
            ) {
                repeat(integerResource(R.integer.dot_count)) { index ->
                    Box(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.dot_size))
                            .clip(CircleShape)
                            .background(
                                colorResource(
                                    if (index == 0) R.color.dot_active else R.color.dot_inactive
                                )
                            )
                    )
                }
            }

            // BOX MENGAPUNG di atas Scaffold
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxSize()
                    .padding(
                        top = dimensionResource(R.dimen.card_top_margin),
                        bottom = innerPadding.calculateBottomPadding() +
                                dimensionResource(R.dimen.card_bottom_margin)
                    )
                    .shadow(dimensionResource(R.dimen.card_elevation), cardShape)
                    .background(colorResource(R.color.card_bg), cardShape)
                    .imePadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            horizontal = dimensionResource(R.dimen.card_padding_horizontal),
                            vertical = dimensionResource(R.dimen.card_padding_vertical)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
                ) {
                    Text(
                        text = stringResource(R.string.judul_form),
                        color = colorResource(R.color.text_title),
                        fontSize = spResource(R.dimen.title_size),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.subjudul_form),
                        color = colorResource(R.color.text_subtitle),
                        fontSize = spResource(R.dimen.subtitle_size)
                    )

                    // Nama depan & belakang
                    Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))) {
                        FormField(
                            value = namaDepan,
                            onValueChange = { namaDepan = it },
                            label = R.string.hint_nama_depan,
                            modifier = Modifier.weight(1f)
                        )
                        FormField(
                            value = namaBelakang,
                            onValueChange = { namaBelakang = it },
                            label = R.string.hint_nama_belakang,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Email
                    FormField(
                        value = email,
                        onValueChange = { email = it },
                        label = R.string.hint_email,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardType = KeyboardType.Email
                    )

                    // Tgl lahir & Experience
                    Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))) {
                        OutlinedTextField(
                            value = tanggal,
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            interactionSource = tanggalSource,
                            modifier = Modifier.weight(1f),
                            shape = fieldShape,
                            label = { Text(stringResource(R.string.hint_tgl_lahir)) },
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = stringResource(R.string.cd_kalender)
                                    )
                                }
                            },
                            colors = fieldColors()
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = experience,
                                onValueChange = {},
                                readOnly = true,
                                singleLine = true,
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                shape = fieldShape,
                                label = { Text(stringResource(R.string.hint_experience)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = fieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                experienceOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            experience = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Skill
                    Text(
                        text = stringResource(R.string.label_skill),
                        color = colorResource(R.color.text_input),
                        fontSize = spResource(R.dimen.label_size),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        skillOptions.forEach { skill ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = skill in skillTerpilih,
                                    onCheckedChange = { dicentang ->
                                        if (dicentang) skillTerpilih.add(skill)
                                        else skillTerpilih.remove(skill)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = colorResource(R.color.purple_primary),
                                        uncheckedColor = colorResource(R.color.checkbox_unchecked)
                                    )
                                )
                                Text(
                                    text = skill,
                                    color = colorResource(R.color.text_input),
                                    fontSize = spResource(R.dimen.checkbox_text_size)
                                )
                            }
                        }
                    }

                    // Tombol Proceed (aktif kalau semua terisi)
                    Button(
                        onClick = { showResult = true },
                        enabled = formLengkap,
                        shape = RoundedCornerShape(dimensionResource(R.dimen.button_radius)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(R.dimen.button_height)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.purple_primary),
                            contentColor = colorResource(R.color.white),
                            disabledContainerColor = colorResource(R.color.button_disabled_bg),
                            disabledContentColor = colorResource(R.color.button_disabled_text)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.tombol_proceed),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Dialog kalender
    if (showDatePicker) {
        val dateState = rememberDatePickerState()
        val formatTanggal = stringResource(R.string.format_tanggal)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat(formatTanggal, Locale.getDefault())
                        sdf.timeZone = TimeZone.getTimeZone(ZONA_UTC)
                        tanggal = sdf.format(Date(millis))
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.tombol_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.tombol_batal))
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }

    // Pop up hasil (otomatis tertutup 5 detik, atau tekan OK)
    if (showResult) {
        AlertDialog(
            onDismissRequest = { showResult = false },
            confirmButton = {
                TextButton(onClick = { showResult = false }) {
                    Text(stringResource(R.string.tombol_ok))
                }
            },
            title = { Text(stringResource(R.string.judul_hasil)) },
            text = {
                Text(
                    stringResource(
                        R.string.format_hasil,
                        namaDepan,
                        namaBelakang,
                        email,
                        tanggal,
                        experience,
                        skillTerpilih.joinToString(separator = stringResource(R.string.pemisah_skill))
                    )
                )
            }
        )
    }
}