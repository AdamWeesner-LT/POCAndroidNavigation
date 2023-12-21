package com.example.pocnavigation.decomposeNavigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.example.pocnavigation.FirstScreen
import com.example.pocnavigation.SecondScreen
import com.example.pocnavigation.ThirdScreen
import kotlinx.serialization.Serializable

/*
* ROUTES
*/

@Serializable
sealed interface Route {
    @Serializable
    data object First : ScreenRoute

    @Serializable
    class Second(val argument: String = "randomArgument") : ScreenRoute

    @Serializable
    data object Third : DialogRoute
}

@Serializable
sealed interface ScreenRoute : Route

@Serializable
sealed interface DialogRoute : Route

/*
* VIEWMODELS
*/

interface PlatformAgnosticViewModel

class FirstViewModel(
    // this is the thing that Decompose uses to handle lifecycle and stuff like that
    componentContext: ComponentContext,
) : PlatformAgnosticViewModel, ComponentContext by componentContext

class SecondViewModel(
    // this is the thing that Decompose uses to handle lifecycle and stuff like that
    componentContext: ComponentContext,
) : PlatformAgnosticViewModel, ComponentContext by componentContext

class ThirdViewModel(
    // this is the thing that Decompose uses to handle lifecycle and stuff like that
    componentContext: ComponentContext,
) : PlatformAgnosticViewModel, ComponentContext by componentContext

/*
* NAVIGATION
*/

interface DecomposeNavigation {
    val screenStack: Value<ChildStack<ScreenRoute, ScreenRouteInitializer>>
    val dialogSlot: Value<ChildSlot<DialogRoute, DialogRouteInitializer>>

    fun navigate(route: ScreenRoute)
    fun navigate(route: DialogRoute)
    fun popBackStack()
    fun dismissDialog()

    // this is the thing that connects the dots between the Route and the Compose Screens (or any other frontend that Kotlin works with)
    sealed interface RouteInitializer {
        class First(val viewModel: FirstViewModel) : ScreenRouteInitializer
        class Second(val viewModel: SecondViewModel) : ScreenRouteInitializer
        class Third(val viewModel: ThirdViewModel) : DialogRouteInitializer
    }

    sealed interface ScreenRouteInitializer : RouteInitializer
    sealed interface DialogRouteInitializer : RouteInitializer
}

class DefaultDecomposeNavigation(
    componentContext: ComponentContext,
) : DecomposeNavigation, ComponentContext by componentContext {
    // StackNavigation allows a stack of things to push and pop from
    private val screenNavigator = StackNavigation<ScreenRoute>()

    // SlotNavigation is for a single "slot" at a time, generally you dont want more than 1 dialog at once, as an example
    private val dialogNavigator = SlotNavigation<DialogRoute>()

    override val screenStack: Value<ChildStack<ScreenRoute, DecomposeNavigation.ScreenRouteInitializer>> =
        childStack(
            source = screenNavigator,
            serializer = ScreenRoute.serializer(),
            // this can also be a list, or even work with coming from a deeplink
            initialConfiguration = Route.First,
            handleBackButton = true,
            childFactory = ::childFactoryScreen
        )

    override val dialogSlot: Value<ChildSlot<DialogRoute, DecomposeNavigation.DialogRouteInitializer>> =
        childSlot(
            // you can add keys (optionally) to all navs so you can have as many as you need
            key = "dialogs",
            source = dialogNavigator,
            serializer = DialogRoute.serializer(),
            // this can also be a list, or even work with coming from a deeplink
            handleBackButton = true,
            childFactory = ::childFactoryDialog
        )


    override fun navigate(route: DialogRoute) {
        dialogNavigator.activate(route)
    }

    override fun navigate(route: ScreenRoute) {
        // this adds a new instance of "Route" to the stack, not caring if there is already one
//        screenNavigator.push(route)
        // if the route exists on the backstack it will hoist it to the top, otherwise create it
        screenNavigator.bringToFront(route)
    }

    override fun popBackStack() {
        screenNavigator.pop()
    }

    override fun dismissDialog() {
        dialogNavigator.dismiss()
    }

    private fun childFactoryScreen(
        route: ScreenRoute,
        componentContext: ComponentContext
    ): DecomposeNavigation.ScreenRouteInitializer {
        // this affords the ability to fail at compile time if you dont update this when statement
        return when (route) {
            Route.First -> {
                // this could be injected in via hilt if we wanted
                val viewModel = FirstViewModel(componentContext)
                DecomposeNavigation.RouteInitializer.First(viewModel)
            }

            is Route.Second -> {
                val viewModel = SecondViewModel(componentContext)
                DecomposeNavigation.RouteInitializer.Second(viewModel)
            }
        }
    }

    private fun childFactoryDialog(
        route: DialogRoute,
        componentContext: ComponentContext
    ): DecomposeNavigation.DialogRouteInitializer {
        // this affords the ability to fail at compile time if you dont update this when statement
        return when (route) {
            Route.Third -> {
                val viewModel = ThirdViewModel(componentContext)
                DecomposeNavigation.RouteInitializer.Third(viewModel)
            }
        }
    }
}

/*
* NAVIGATION CONNECTION TO UI
*/

@Composable
fun DecomposeNavigationRoot(
    navigation: DecomposeNavigation,
    // these are only exposed to allow for playing with the other nav types, this would not be a thing normally
    firstScreenOnClick: () -> Unit,
    secondScreenOnClick: () -> Unit,
    showDialogClick: () -> Unit,
) {
    val dialogSlot by navigation.dialogSlot.subscribeAsState()

    // if we had a DialogRoute we can bring up dialogs here
    when (val current = dialogSlot.child?.instance) {
        is DecomposeNavigation.RouteInitializer.Third -> {
            println("viewmodel ${current.viewModel}")
            Dialog(
                onDismissRequest = {
                    navigation.dismissDialog()
                }
            ) {
                ThirdScreen(
                    label = "Decompose Dialog",
                    onClick = {
                        navigation.dismissDialog()
                    },
                )
            }
        }

        null -> { /* do nothing here, we dont have a dialog to show */
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Children(
            stack = navigation.screenStack,
            modifier = Modifier.padding(it)
        ) {
            // it.instance here is the RouteInitializer, it gives us the agnostic viewmodel
            //   and type-safe compile errors if we forget to add them
            when (val current = it.instance) {
                is DecomposeNavigation.RouteInitializer.First -> {
                    println("viewmodel ${current.viewModel}")
                    FirstScreen(
                        label = "Decompose",
                        onClick = firstScreenOnClick
                    )
                }

                is DecomposeNavigation.RouteInitializer.Second -> {
                    println("viewmodel ${current.viewModel}")
                    SecondScreen(
                        label = "Decompose",
                        onClick = secondScreenOnClick,
                        showDialogClick = showDialogClick
                    )
                }
            }
        }
    }
}
