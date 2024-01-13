package app.templates

import kotlinx.html.BODY
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.script
import kotlinx.html.stream.appendHTML
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe

fun page(content: BODY.() -> Unit) =
    buildString {
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
                content()
            }
        }
    }
