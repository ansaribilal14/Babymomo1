package com.babymomo.app.ui.screens.skills
import androidx.lifecycle.ViewModel
import com.babymomo.app.core.skills.SkillRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SkillsViewModel @Inject constructor(
    private val skillRegistry: SkillRegistry
) : ViewModel() {
    fun getSkills() = skillRegistry.getAllSkills().map { it.name to it.triggers }
}
