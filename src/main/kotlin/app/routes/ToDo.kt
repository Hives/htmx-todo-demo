package app.routes

import kotlinx.html.div
import kotlinx.html.span
import kotlinx.html.stream.appendHTML
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.FormField
import org.http4k.lens.Header
import org.http4k.lens.Validator
import org.http4k.lens.webForm
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import app.todo.ToDo
import app.todo.ToDoRepository
import app.templates.toDo
import app.templates.toDoList
import app.templates.unfinishedCount

fun toDoRoutes(
    toDoRepository: ToDoRepository
) = routes(
    createToDoRoute(toDoRepository),
    toggleToDoRoute(toDoRepository),
    deleteToDoRoute(toDoRepository)
)

fun createToDoRoute(
    toDoRepository: ToDoRepository
): RoutingHttpHandler {
    val textField = FormField.required("text")
    val bodyLens = Body.webForm(Validator.Strict, textField).toLens()

    return "/todo" bind Method.POST to { req ->
        val form = bodyLens(req)
        val text = form.fields.getValue("text").first()
        toDoRepository.add(ToDo.new(text))

        Response(Status.OK)
            .with(Header.CONTENT_TYPE of ContentType.TEXT_HTML)
            .body(buildString {
                appendHTML().apply {
                    span {
                        unfinishedCount(toDoRepository.countIncomplete(), true)
                    }
                    div {
                        toDoList(toDoRepository.getAll())
                    }
                }
            })
    }
}

fun toggleToDoRoute(
    toDoRepository: ToDoRepository
) = "/todo/{id}/toggle" bind Method.POST to { req ->
    val id = req.path("id")!!

    val toDo = toDoRepository.get(id)!!
    toDo.toggle()

    Response(Status.OK)
        .with(Header.CONTENT_TYPE of ContentType.TEXT_HTML)
        .body(buildString {
            appendHTML().apply {
                span {
                    unfinishedCount(toDoRepository.countIncomplete(), true)
                }
                div {
                    toDo(toDo)
                }
            }
        })
}

fun deleteToDoRoute(
    toDoRepository: ToDoRepository
) = "/todo/{id}" bind Method.DELETE to { req ->
    val id = req.path("id")!!

    toDoRepository.delete(id)

    Response(Status.OK)
        .with(Header.CONTENT_TYPE of ContentType.TEXT_HTML)
        .body(buildString {
            appendHTML().apply {
                span {
                    unfinishedCount(toDoRepository.countIncomplete(), true)
                }
                div {
                    toDoList(toDoRepository.getAll())
                }
            }
        })
}

