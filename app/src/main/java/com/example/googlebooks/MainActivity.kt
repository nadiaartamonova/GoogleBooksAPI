package com.example.googlebooks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.googlebooks.viewModel.search.BooksScreen
import com.example.googlebooks.viewModel.search.BooksViewModel
import com.example.googlebooks.ui.theme.GoogleBooksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoogleBooksTheme {
                val vm: BooksViewModel = viewModel()
                BooksScreen(viewModel = vm)
            }
        }
    }
}