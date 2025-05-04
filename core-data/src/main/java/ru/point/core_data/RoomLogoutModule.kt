package ru.point.core_data

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.point.core.LogoutHandler

@Module
abstract class RoomLogoutModule {

    @Binds
    @IntoSet
    abstract fun bindRoomLogoutHandler(
        impl: RoomLogoutHandler
    ): LogoutHandler
}