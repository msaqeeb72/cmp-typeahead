package dev.saqeeb.typeahead

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize

@Composable
fun <T> TypeAheadTextField(
    dataList: List<T>,
    initialValue: String = "",
    label: String = "",
    placeholder: String = "",
    modifier: Modifier = Modifier,
    onItemSelected: (T) -> Unit,
    searchKeyExtractor: (T) -> String = { it.toString() },
    itemContent: @Composable (T) -> Unit,
) {
    var searchKey by remember {
        mutableStateOf(initialValue)
    }

    var textFieldSize by remember {
        mutableStateOf(Size.Zero)
    }

    var expanded by remember {
        mutableStateOf(false)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    expanded = false
                }
            )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchKey,
                    onValueChange = {
                        searchKey = it
                        expanded = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        },
                    label = { Text(label) },
                    placeholder = { Text(placeholder) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = "arrow",
                            )
                        }
                    }
                )
            }

            AnimatedVisibility(visible = expanded) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width(textFieldSize.width.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    val focusManager = LocalFocusManager.current

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 150.dp),
                    ) {
                        if (searchKey.isNotEmpty()) {
                            items(
                                dataList.filter {
                                    searchKeyExtractor(it).lowercase()
                                        .contains(searchKey.lowercase())
                                }
                                    .sortedBy { searchKeyExtractor(it) }
                            ) { item ->
                                AutoCompleteItem(item = item, onSelect = {
                                    searchKey = searchKeyExtractor(it)
                                    expanded = false
                                    onItemSelected(it)
                                    focusManager.clearFocus(true)
                                }, itemContent = itemContent)
                            }
                        } else {
                            items(
                                dataList.sortedBy { searchKeyExtractor(it) }
                            ) { item ->
                                AutoCompleteItem(item = item, onSelect = {
                                    searchKey = searchKeyExtractor(it)
                                    expanded = false
                                    onItemSelected(it)
                                }, itemContent = itemContent)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T> AutoCompleteItem(
    item: T,
    onSelect: (T) -> Unit,
    itemContent: @Composable (T) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(item) }
            .padding(10.dp)
    ) {
        itemContent(item)
    }
}