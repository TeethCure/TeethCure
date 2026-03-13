package com.teethcure.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MouthZone { LEFT, CENTER, RIGHT }

enum class AppTab(val label: String) {
    WorldMap("월드맵"),
    Brushing("양치"),
    Collection("도감"),
}

data class BrushingState(
    val targetCharacterId: String,
    val leftStrokes: Int = 0,
    val centerStrokes: Int = 0,
    val rightStrokes: Int = 0,
    val elapsedSec: Int = 0,
    val active: Boolean = false,
    val resultMessage: String? = null,
) {
    val leftDone: Boolean get() = leftStrokes >= GameCatalog.STROKES_PER_ZONE
    val centerDone: Boolean get() = centerStrokes >= GameCatalog.STROKES_PER_ZONE
    val rightDone: Boolean get() = rightStrokes >= GameCatalog.STROKES_PER_ZONE
    val purification: Int get() = listOf(leftDone, centerDone, rightDone).count { it }
    val complete: Boolean get() = purification == 3
}

data class ProfileSelectionState(
    val isLoading: Boolean = true,
    val profiles: List<ProfileSummary> = emptyList(),
    val errorMessage: String? = null,
) {
    val canCreateMore: Boolean get() = profiles.size < MAX_PROFILE_COUNT

    companion object {
        const val MAX_PROFILE_COUNT = 3
    }
}

data class AppUiState(
    val selectedTab: AppTab = AppTab.WorldMap,
    val unlockedCharacterIds: Set<String> = emptySet(),
    val brushing: BrushingState = BrushingState(targetCharacterId = GameCatalog.characters.first().id),
    val profileSelection: ProfileSelectionState = ProfileSelectionState(),
    val selectedProfile: ProfileSummary? = null,
) {
    val targetCharacter: CharacterInfo =
        GameCatalog.characters.first { it.id == brushing.targetCharacterId }

    val needsProfileSelection: Boolean
        get() = selectedProfile == null
}

class TeethCureViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        loadProfiles()
    }

    fun loadProfiles() {
        _uiState.update {
            it.copy(
                profileSelection = it.profileSelection.copy(
                    isLoading = true,
                    errorMessage = null,
                ),
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            runCatching { profileRepository.fetchProfiles() }
                .onSuccess { profiles ->
                    _uiState.update {
                        it.copy(
                            profileSelection = ProfileSelectionState(
                                isLoading = false,
                                profiles = profiles.take(ProfileSelectionState.MAX_PROFILE_COUNT),
                            ),
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            profileSelection = it.profileSelection.copy(
                                isLoading = false,
                                errorMessage = throwable.message ?: "프로필 목록을 불러오지 못했습니다.",
                            ),
                        )
                    }
                }
        }
    }

    fun selectProfile(profile: ProfileSummary) {
        _uiState.update { it.copy(selectedProfile = profile) }
    }

    fun resetSelectedProfile() {
        _uiState.update { it.copy(selectedProfile = null) }
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun startSession() {
        _uiState.update { state ->
            if (state.selectedProfile == null) return@update state

            state.copy(
                selectedTab = AppTab.Brushing,
                brushing = BrushingState(
                    targetCharacterId = state.brushing.targetCharacterId,
                    active = true,
                ),
            )
        }
    }

    fun tickSession() {
        _uiState.update { state ->
            val brushing = state.brushing
            if (!brushing.active) return@update state

            val next = brushing.copy(
                elapsedSec = (brushing.elapsedSec + 1).coerceAtMost(GameCatalog.SESSION_DURATION_SEC),
            )
            if (next.complete) {
                return@update onSuccess(state, next)
            }
            if (next.elapsedSec >= GameCatalog.SESSION_DURATION_SEC) {
                return@update onFailure(state, next)
            }
            state.copy(brushing = next)
        }
    }

    fun applyStroke(zone: MouthZone) {
        _uiState.update { state ->
            val brushing = state.brushing
            if (!brushing.active) return@update state
            val expectedZone = currentExpectedZone(brushing)
            if (zone != expectedZone) return@update state

            val updated = when (zone) {
                MouthZone.LEFT -> brushing.copy(leftStrokes = (brushing.leftStrokes + 1).coerceAtMost(50))
                MouthZone.CENTER -> brushing.copy(centerStrokes = (brushing.centerStrokes + 1).coerceAtMost(50))
                MouthZone.RIGHT -> brushing.copy(rightStrokes = (brushing.rightStrokes + 1).coerceAtMost(50))
            }

            if (updated.complete) onSuccess(state, updated) else state.copy(brushing = updated)
        }
    }

    fun resetResultMessage() {
        _uiState.update { it.copy(brushing = it.brushing.copy(resultMessage = null)) }
    }

    private fun onSuccess(state: AppUiState, brushing: BrushingState): AppUiState {
        val unlocked = state.unlockedCharacterIds + brushing.targetCharacterId
        val nextTarget = nextLockedCharacter(unlocked) ?: brushing.targetCharacterId
        return state.copy(
            selectedTab = AppTab.WorldMap,
            unlockedCharacterIds = unlocked,
            brushing = brushing.copy(
                active = false,
                resultMessage = "정화 성공! 완료 단계 ${brushing.purification}/3",
                targetCharacterId = nextTarget,
            ),
        )
    }

    private fun onFailure(state: AppUiState, brushing: BrushingState): AppUiState {
        return state.copy(
            selectedTab = AppTab.WorldMap,
            brushing = brushing.copy(
                active = false,
                resultMessage = "정화 실패. 제한 시간 안에 다시 도전해 보세요.",
            ),
        )
    }

    private fun nextLockedCharacter(unlocked: Set<String>): String? {
        return GameCatalog.characters.firstOrNull { it.id !in unlocked }?.id
    }

    private fun currentExpectedZone(brushing: BrushingState): MouthZone {
        return when {
            !brushing.leftDone -> MouthZone.LEFT
            !brushing.centerDone -> MouthZone.CENTER
            else -> MouthZone.RIGHT
        }
    }
}
