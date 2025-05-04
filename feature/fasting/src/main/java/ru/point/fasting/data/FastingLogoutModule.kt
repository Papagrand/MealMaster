package ru.point.fasting.data

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.point.core.LogoutHandler

@Module
abstract class FastingLogoutModule {

    @Binds @IntoSet
    abstract fun bindFastingLogoutHandler(
        impl: FastingLogoutHandler
    ): LogoutHandler
}