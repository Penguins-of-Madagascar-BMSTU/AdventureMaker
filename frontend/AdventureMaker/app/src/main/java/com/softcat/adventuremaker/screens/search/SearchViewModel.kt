package com.softcat.adventuremaker.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.domain.entities.City
import com.example.domain.entities.Place
import com.example.domain.usecases.FavouriteUseCase
import com.example.domain.usecases.PlacesUseCase
import com.example.domain.usecases.UserUseCase
import com.softcat.adventuremaker.navigation.NavigationItem
import com.softcat.adventuremaker.screens.search.model.SearchScreenState
import com.softcat.adventuremaker.screens.search.model.SearchViewModelMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class SearchViewModel(
    private val mapper: SearchViewModelMapper,
    private val placesUseCase: PlacesUseCase,
    private val favouriteUseCase: FavouriteUseCase,
    private val userUseCase: UserUseCase
): ViewModel() {

    // Подписка на список любимых мест. Если пользователь меняется, то этот список обновляется.
    @OptIn(ExperimentalCoroutinesApi::class)
    val favouritesFlow = userUseCase.observeLastEnteredUser()
        .flatMapLatest { user ->
            user?.id?.let {
                favouriteUseCase.getFavouriteIds(it)
            } ?: flowOf(emptyList())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // Список городов, из которых можно выбрать город для поиска мест.
    private var availableCities = emptyList<City>()

    // Места, которые нужно отображать на карте в результате поиска.
    private var placesOnMap = emptyList<Place>()

    private val _state = MutableLiveData(initialSearchScreenState())
    val state: LiveData<SearchScreenState>
        get() = _state

    init {
        loadAvailableCities()
    }

    fun changeQuery(newValue: String) {
        _state.value = state.value?.copy(query = newValue)
    }

    fun selectCity(cityName: String) {
        _state.value = state.value?.copy(cityName = cityName)
    }

    fun selectCategory(category: Place.Category) {
        val bottomSheetState = state.value?.bottomSheetState ?: return
        val newBottomSheetState = bottomSheetState.copy(
            // Получаем новый список категорий для BottomSheet.
            categories = mapper.mapToCategoryList(category)
        )
        _state.value = state.value?.copy(bottomSheetState = newBottomSheetState)
    }

    fun searchPlaces() {
        val state = state.value ?: return
        val cityId = availableCities.find {
            it.name == state.cityName
        }?.id ?: return
        val selectedCategory = state.bottomSheetState.categories
            .find { it.isSelected }?.key ?: return
        // Пока не ясна логика определения параметра page, поэтому так :)
        val page = Random.nextInt(1, 5)

        viewModelScope.launch(Dispatchers.IO) {
            placesUseCase.searchPlaces(cityId, state.query, selectedCategory, page)
                .onSuccess {
                    updatePlacesOnMap(it)
                    showBottomSheet()
                }
        }
    }

    fun dismissBottomSheet() {
        val currentState = state.value ?: return
        _state.value = currentState.copy(
            bottomSheetState = currentState.bottomSheetState.copy(
                isSheetVisible = false
            )
        )
    }

    fun showBottomSheet() {
        val currentState = state.value ?: return
        _state.value = currentState.copy(
            bottomSheetState = currentState.bottomSheetState.copy(
                isSheetVisible = true
            )
        )
    }

    fun changeFavouriteStatus(placeId: String) {
        val isFavourite = placeId in favouritesFlow.value

        viewModelScope.launch(Dispatchers.IO) {
            val userId = userUseCase.getLastEnteredUser()?.id ?: return@launch

            if (isFavourite)
                favouriteUseCase.removeFromFavourites(userId, placeId)
            else {
                val place = placesOnMap.find { it.id == placeId } ?: return@launch
                favouriteUseCase.addToFavourite(userId, place)
            }
        }
    }

    fun navigateToPlaceDetails(navController: NavController, placeId: String) {
        val place = placesOnMap.find { it.id == placeId } ?: return
        navController.navigate(NavigationItem.Search.Details(place))
    }

    private fun loadAvailableCities() {
        // Функция отправляет запросы на загрузку городов до тех пор, пока они не будут получены.
        // Ошибки не отображаются, так как это не так важно на данный момент.
        viewModelScope.launch(Dispatchers.IO) {
            while (availableCities.isEmpty()) {
                placesUseCase.getCities().onSuccess { cities ->
                    availableCities = cities
                    withContext(Dispatchers.Main) {
                        _state.value = state.value
                            ?.copy(availableCities = availableCities.map { it.name })
                    }
                }.onFailure {
                    delay(CITIES_LOADING_RETRY_PERIOD)
                }
            }
        }
    }

    private fun updatePlacesOnMap(places: List<Place>) {
        placesOnMap = places
        val currentState = state.value ?: return

        val selectedCategory = currentState.bottomSheetState.categories
            .find { it.isSelected } ?.key ?: Place.Category.Unknown

        // На экране нужно показать только те места, которые относятся к выбранной категории.
        val visiblePlaces = places.filter {
            selectedCategory == Place.Category.Unknown || it.category == selectedCategory
        }
        val newPlaceModels = mapper.mapToPlaceModels(
            places = visiblePlaces,
            favouriteIds = favouritesFlow.value
        )
        val newSheetState = currentState.bottomSheetState.copy(places = newPlaceModels)
        _state.postValue(
            state.value?.copy(bottomSheetState = newSheetState)
        )
    }

    fun initialSearchScreenState() = SearchScreenState(
        bottomSheetState = SearchBottomSheetState(
            places = emptyList(),
            categories = mapper.mapToCategoryList(Place.Category.Unknown),
            isSheetVisible = false
        ),
        query = "",
        cityName = "",
        availableCities = availableCities.map { it.name }
    )

    companion object {
        private const val CITIES_LOADING_RETRY_PERIOD = 1000L
    }
}