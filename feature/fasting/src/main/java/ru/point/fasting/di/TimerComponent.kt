package ru.point.fasting.di

import dagger.Component
import ru.point.api.timer_fasting.data.TimerService
import ru.point.core_data.dao.ScenarioDao
import ru.point.core_data.dao.UserTimerDao
import ru.point.fasting.ui.FastingFragment
import kotlin.properties.Delegates.notNull

@Component(
    dependencies = [TimerFragmentDeps::class],
    modules = [TimerModule::class]
)
interface TimerComponent {
    fun inject(fragment: FastingFragment)

    @Component.Builder
    interface Builder {
        fun deps(deps: TimerFragmentDeps): Builder
        fun build(): TimerComponent
    }
}

interface TimerFragmentDeps {
    val timerService: TimerService
    val userTimerDao: UserTimerDao
    val scenarioDao: ScenarioDao
}

interface TimerFragmentDepsProvider {
    var deps: TimerFragmentDeps

    companion object : TimerFragmentDepsProvider by TimerFragmentDepsStore
}

object TimerFragmentDepsStore : TimerFragmentDepsProvider {
    override var deps: TimerFragmentDeps by notNull()
}
