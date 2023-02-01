package com.gourav.competrace.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Surface
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchAppBar(
    query: String,
    onValueChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    modifier: Modifier = Modifier,
    placeHolderText: String? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val trailingIcon: (@Composable () -> Unit)? = if (query.isNotBlank()) {
        { NormalIconButton(iconId = R.drawable.ic_close_24px, onClick = { onValueChange("") }) }
    } else null

    val placeHolder: (@Composable () -> Unit)? = placeHolderText?.let {
        {
            Text(
                it,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.alpha(ContentAlpha.medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        TextField(
            modifier = modifier.fillMaxWidth(),
            value = query,
            onValueChange = onValueChange,
            placeholder = placeHolder,
            textStyle = MaterialTheme.typography.bodyLarge,
            singleLine = true,
            leadingIcon = {
                NormalIconButton(iconId = R.drawable.ic_arrow_back_24px, onClick = onCloseClicked)
            },
            trailingIcon = trailingIcon,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { keyboardController?.hide() }
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
        )
    }
}