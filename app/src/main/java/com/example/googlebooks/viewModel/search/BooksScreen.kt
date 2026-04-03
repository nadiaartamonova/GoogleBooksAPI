package com.example.googlebooks.viewModel.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.googlebooks.domain.model.BookUiModel



import androidx.compose.material3.Card


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksScreen(
    viewModel: BooksViewModel
) {
    val query by viewModel.query.collectAsState()
    val books by viewModel.books.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val recentQueries by viewModel.recentQueries.collectAsState()


    var isSearchOpen by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf<BookUiModel?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        TopAppBar(
            title = { Text("Google Books") },
            actions = {
                IconButton(onClick = { isSearchOpen = !isSearchOpen }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Open search",
                        tint = Color(0xFFFF4500)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color(0xFFFF4500)
            )
        )

        if (isSearchOpen) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = viewModel::onQueryChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Search books") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF4500),
                        unfocusedBorderColor = Color(0xFFFF4500),
                        focusedLabelColor = Color(0xFFFF4500),
                        cursorColor = Color(0xFFFF4500)
                    )
                )

                Button(
                    onClick = { viewModel.searchBooks() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF4500),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                }
            }
        }
        if (recentQueries.isNotEmpty()) {
            Text(
                text = "Recent searches",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelLarge
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentQueries) { recentQuery ->
                    AssistChip(
                        onClick = { viewModel.onRecentQueryClick(recentQuery) },
                        label = { Text(recentQuery) }
                    )
                }
            }
        }
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        if (!isLoading && error == null && books.isEmpty() && query.isNotBlank()) {
            Text(
                text = "Nothing found",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(books) { book ->
                BookItem(
                    book = book,
                    onDetailsClick = { selectedBook = book }
                )
            }
        }

        selectedBook?.let { book ->
            AlertDialog(
                onDismissRequest = { selectedBook = null },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = book.title.ifBlank { "No title" },
                            color = Color(0xFFFF4500),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { selectedBook = null }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close details",
                                tint = Color(0xFFFF4500)
                            )
                        }
                    }
                },
                text = {

                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (book.thumbnailUrl.isNotBlank()) {
                                AsyncImage(
                                    model = book.thumbnailUrl,
                                    contentDescription = book.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            }

                        DetailLine(label = "Author(s)", value = book.authors.ifBlank { "Unknown" })
                        DetailLine(label = "Published", value = book.publishedDate.ifBlank { "-" })
                        DetailLine(label = "Publisher", value = book.publisher.ifBlank { "Unknown" })
                        DetailLine(
                            label = "Pages",
                            value = if (book.pageCount > 0) book.pageCount.toString() else "Unknown"
                        )
                        Text(
                            text = if (book.description.isNotBlank()) book.description else "No description",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                confirmButton = {}
            )
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$label: ")
            }
            append(value)
        },
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun BookItem(
    book: BookUiModel,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {

                if (book.thumbnailUrl.isNotBlank()) {
                    AsyncImage(
                        model = book.thumbnailUrl,
                        contentDescription = book.title,
                        modifier = Modifier
                            .height(140.dp)
                            .fillMaxWidth(0.3f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {

                    Box(
                        modifier = Modifier
                            .height(140.dp)
                            .fillMaxWidth(0.3f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF2F2F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Image",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = book.title.ifBlank { "No title" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF4500)
                    )
                    Text(text = "Author(s): ${book.authors.ifBlank { "Unknown" }}")
                    Text(text = "Published: ${book.publishedDate.ifBlank { "-" }}")
                }
            }

            Button(

                onClick = onDetailsClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF4500),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Text("Details")
            }
        }
    }
}
