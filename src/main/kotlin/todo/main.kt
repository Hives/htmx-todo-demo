package todo

import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe
import org.http4k.core.Body
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.Header
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Method
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.lens.FormField
import org.http4k.lens.Validator
import org.http4k.lens.webForm

fun main() {
    val app = routes(
        "/" bind Method.GET to {
            val body = buildString {
                append("<!DOCTYPE html>\n")
                appendHTML().html {
                    head {
                        title {
                            +"HTMX To Do demo"
                        }
                        script {
                            src = "https://unpkg.com/htmx.org@1.9.10"
                        }
                        style {
                            unsafe {
                                raw(
                                    """
                                    .complete label {
                                        text-decoration: line-through;
                                    }
                                """.trimIndent()
                                )
                            }
                        }
                    }
                    body {
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
                        div {
                            id = "todos"
                            renderItems()
                        }
                        div {
                            span { unfinishedCount() }
                        }
                    }
                }
            }
            Response(Status.OK).with(Header.CONTENT_TYPE of TEXT_HTML).body(body)
        },
        "/todo/{index}/toggle" bind Method.POST to { req ->
            val index = req.path("index")!!.toInt()
            val item = items[index]
            item.complete = !item.complete
            val fragment = createHTML().apply {
                span {
                    unfinishedCount(true)
                }
                div {
                    render(item, index)
                }
            }.finalize()
            Response(Status.OK).with(Header.CONTENT_TYPE of TEXT_HTML).body(fragment)
        },
        "/todo/{index}" bind Method.DELETE to { req ->
            val index = req.path("index")!!.toInt()
            items.removeAt(index)
            val fragment = createHTML().apply {
                span { unfinishedCount(true) }
                div { renderItems() }
            }.finalize()
            Response(Status.OK).with(Header.CONTENT_TYPE of TEXT_HTML).body(fragment)
        },
        "/todo" bind Method.POST to { req ->
            val textField = FormField.required("text")
            val bodyLens = Body.webForm(Validator.Strict, textField).toLens()
            val form = bodyLens(req)
            val text = form.fields.getValue("text").first()
            val item = Item(text, false)
            items.add(item)
            val fragment = createHTML().apply {
                span { unfinishedCount(true) }
                div { renderItems() }
            }.finalize()
            Response(Status.OK).with(Header.CONTENT_TYPE of TEXT_HTML).body(fragment)
        }
    )

    app.asServer(Undertow(3000)).start()
}
