package app.routes

import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.input
import kotlinx.html.span
import org.http4k.core.ContentType
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.Header
import org.http4k.routing.bind
import app.todo.ToDoRepository
import app.templates.page
import app.templates.toDoList
import app.templates.unfinishedCount

fun root(
    toDoRepository: ToDoRepository
) = "/" bind Method.GET to {

    val body = page {
        h1 { +"HTMX To Do demo" }
        h2 { +"To Dos" }
        form {
            attributes["hx-post"] = "/todo"
            attributes["hx-target"] = "#todos"
            input {
                type = InputType.text
                name = "text"
                placeholder = "What needs to be done?"
            }
            button {
                type = ButtonType.submit
                +"Submit"
            }
        }
        toDoList(toDoRepository.getAll())
        div {
            span { unfinishedCount(toDoRepository.countIncomplete()) }
        }
    }

    Response(Status.OK)
        .with(Header.CONTENT_TYPE of ContentType.TEXT_HTML)
        .body(body)
}
