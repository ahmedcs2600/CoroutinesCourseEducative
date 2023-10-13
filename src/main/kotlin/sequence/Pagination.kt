package sequence

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun allUsersFlow(
    api: UserApi,
): Flow<List<User>> = flow {
    var page = 0
    do {
        val users = api.takePage(page++)
        emit(users)
    } while(users.isNotEmpty())
}

interface UserApi {
    fun takePage(page: Int): List<User>
}

data class User(val id: String)

fun main() {

}
