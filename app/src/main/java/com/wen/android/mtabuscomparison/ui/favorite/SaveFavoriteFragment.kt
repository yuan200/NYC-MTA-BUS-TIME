package com.wen.android.mtabuscomparison.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

/**
 * A simple [Fragment] subclass.
 * Use the [SaveFavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SaveFavoriteFragment : Fragment() {
    private val saveFavoriteViewModel by viewModels<SaveFavoriteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        saveFavoriteViewModel.isSaved.observe(this, {
            if (it) activity?.onBackPressed()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_save_favorite, container, false)
        return ComposeView(requireContext()).apply {
            setContent {
                MyApp(saveFavoriteViewModel)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SaveFavoriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            SaveFavoriteFragment()
    }
}

//@Preview(showBackground = true, name = "preview")
//@Composable
//fun DefaultPreview() {
//    MyApp(viewModel = SaveFavoriteViewModel())
//}

@Composable
fun MyApp(saveFavoriteViewModel: SaveFavoriteViewModel) {
//    Surface(color = MaterialTheme.colors.primaryVariant) {
    Surface() {
        Column(modifier = Modifier.fillMaxHeight()) {

            val stopIdText = saveFavoriteViewModel.stopId.observeAsState("")
            TextField(
                value = stopIdText.value, {
                    saveFavoriteViewModel.onStopIdChange(it)
                },
                label = { Text("STOP ID") },
                modifier = Modifier.padding(start = 24.dp)
            )
            Divider(color = Color.Black)
            val descriptionText = saveFavoriteViewModel.description.observeAsState("")
            TextField(
                value = descriptionText.value, {
                    saveFavoriteViewModel.onDescriptionChange(it)
                },
                label = { Text("description") },
                modifier = Modifier.padding(24.dp)
            )
            Save(saveFavoriteViewModel)
        }

    }
}

@Composable
fun Save(saveFavoriteViewModel: SaveFavoriteViewModel) {
    Button(onClick = { saveFavoriteViewModel.onSaveFavorite() }, modifier = Modifier.padding(start = 24.dp)) {
        Text("Save")
    }
}