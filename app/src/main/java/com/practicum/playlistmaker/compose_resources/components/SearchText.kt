import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R

@Composable
fun SearchText(
    onFocusChanged: (Boolean) -> Unit,
    onTextChanged: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = text,
        onValueChange = {
            text = it
            onTextChanged(text)
        },
        singleLine = true,
        modifier = Modifier.onFocusChanged {
            onFocusChanged(it.isFocused)
        },
        textStyle = MaterialTheme.typography.labelMedium,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 16.dp)
                    .height(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(42.dp)
                        .padding(start = 16.dp)
                        .padding(end = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }

                if (text.isNotEmpty()) {
                    Icon(
                        painterResource(R.drawable.ic_search_clear),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(start = 8.dp)
                            .padding(end = 16.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ) {
                                text = ""
                                onTextChanged(text)
                                focusManager.clearFocus()
                            }
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchText() {
    SearchText(onFocusChanged = {}, onTextChanged = {})
}