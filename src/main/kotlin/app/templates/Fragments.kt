package app.templates

import app.todo.ToDo
import kotlinx.html.FlowContent
import kotlinx.html.SPAN
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.label
import kotlinx.html.li
import kotlinx.html.ul

fun SPAN.unfinishedCount(count: Int, oob: Boolean = false) {
    id = "unfinished-count"
    if (oob) {
        attributes["hx-swap-oob"] = "true"
    }
    val optionalS = if (count == 1) "" else "s"
    +"$count item$optionalS left"
}

fun FlowContent.toDoList(toDos: List<ToDo>) =
    div {
        id = "todos"
        ul {
            toDos.map { item ->
                li {
                    id = "todo-${item.id}"
                    toDo(item)
                }
            }
        }
    }

fun FlowContent.toDo(toDo: ToDo) {
    div {
        if (toDo.isComplete) {
            classes = setOf("complete")
        }
        label {
            +toDo.text
        }
        button {
            attributes["hx-post"] = "/todo/${toDo.id}/toggle"
            attributes["hx-target"] = "#todo-${toDo.id}"
            val text = if (toDo.isComplete) "Uncomplete" else "Complete"
            +text
        }
        button {
            attributes["hx-delete"] = "/todo/${toDo.id}"
            attributes["hx-target"] = "#todos"
            +"Delete"
        }
    }
}
