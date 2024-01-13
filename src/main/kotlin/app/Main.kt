package app

import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import app.routes.root
import app.routes.toDoRoutes
import app.todo.ToDoRepository

fun main() {
    val toDoRepository = ToDoRepository.new()

    val app = routes(
        root(toDoRepository),
        toDoRoutes(toDoRepository)
    )

    app.asServer(Undertow(3000)).start()
}
