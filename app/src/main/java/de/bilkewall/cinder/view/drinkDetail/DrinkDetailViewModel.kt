package de.bilkewall.cinder.view.drinkDetail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.bilkewall.cinder.database.drink.Drink
import de.bilkewall.cinder.database.drinkIngredientCrossRef.DrinkIngredientCrossRef
import de.bilkewall.cinder.di.DependencyProvider
import de.bilkewall.cinder.dto.AppDrinkDto
import de.bilkewall.cinder.util.toAppDrinkDto
import kotlinx.coroutines.launch

class DrinkDetailViewModel : ViewModel() {
    private val drinkIngredientCrossRefRepository = DependencyProvider.drinkIngredientCrossRefRepository
    private val drinkRepository = DependencyProvider.drinkRepository

    var drink: AppDrinkDto by mutableStateOf(AppDrinkDto())
    var ingredients: List<DrinkIngredientCrossRef> by mutableStateOf(listOf())

    private var errorMessage: String by mutableStateOf("")
    var loading: Boolean by mutableStateOf(false)

    fun setDrinkById(id: String) {
        viewModelScope.launch {
            loading = true
            try {
                ingredients = drinkIngredientCrossRefRepository.getIngredientsForDrink(id.toInt())
                drink = drinkRepository.getDrinkById(id.toInt()).toAppDrinkDto(
                    ingredients.map { it.ingredientName },
                    ingredients.map { it.unit }
                )
            } catch (e: Exception) {
                errorMessage = e.message ?: "Unknown error"
                Log.e("DrinkDetailViewModel.setDrinkById", errorMessage)
            } finally {
                loading = false
            }
        }
    }
}