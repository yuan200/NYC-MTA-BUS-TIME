package com.wen.android.mtabuscomparison.feature.stoproute

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.wen.android.mtabuscomparison.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StopRouteFragment : Fragment() {

    private val viewModel: StopRouteViewModel by viewModels()

    private val args: StopRouteFragmentArgs by navArgs()

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        fun newInstance() = StopRouteFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, StopRouteFragment::class.java.simpleName)
        }

        viewModel.getStopRoute(args.route, getString(R.string.mta_bus_api_key))

    }

    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    val busDirection = viewModel.busDirection.value
                    val direction = viewModel.direction.value
                    val errorMessage = viewModel.errorMessage.value
                    if (errorMessage.isNotEmpty()) {
                        SnackbarScreen(message = errorMessage)
                    }
                    Column {
                        busDirection?.let {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (direction == 0) Color.Gray else MaterialTheme.colors.primary
                                ),
                                onClick = {
                                    viewModel.direction.value = 0
                                }) {
                                Text(text = busDirection.direction0[0].busDirection)
                            }
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (direction == 1) Color.Gray else MaterialTheme.colors.primary
                                ),
                                onClick = {
                                    viewModel.direction.value = 1
                                }) {
                                Text(text = busDirection.direction1[0].busDirection)
                            }
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                itemsIndexed(
                                    if (direction == 0) busDirection.direction0
                                    else busDirection.direction1
                                ) { index, stopInfo ->
                                    StopRouteCard(
                                        stopName = stopInfo.intersections,
                                        stopCode = stopInfo.stopCode,
                                        rulerDrawable = R.drawable.horizontal_rule_black_24dp,
                                        circleDrawable = R.drawable.circle_black_24dp,
                                        hideFirstLine = (index == 0),
                                        hideLastLine = (direction == 0 && index == busDirection.direction0.size - 1)
                                                || (direction == 1 && index == busDirection.direction1.size - 1),
                                        onClickCard = { stopCode ->
                                            findNavController().navigate(
                                                StopRouteFragmentDirections.actionStopRouteFragmentToStopMonitoringFragment(
                                                    stopCode
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}