package ru.point.core_data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.point.core.LogoutHandler
import javax.inject.Inject

class RoomLogoutHandler @Inject constructor(
    private val db: AppDatabase
) : LogoutHandler {

    override suspend fun onLogout() {
        withContext(Dispatchers.IO) {
            db.clearAllTables()
        }
    }
}
