package todo

import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.SPAN
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.label
import kotlinx.html.li
import kotlinx.html.ul

fun SPAN.unfinishedCount(oob: Boolean = false) {
    id = "unfinished-count"
    if (oob) {
        attributes["hx-swap-oob"] = "true"
    }
    val count = items.count { !it.complete }
    val optionalS = if (count == 1) "" else "s"
    +"$count item$optionalS left"
}

fun FlowContent.renderItems() =
    ul {
        items.mapIndexed { index, item ->
            li {
                render(item, index)
            }
        }
    }

val items = mutableListOf(
    Item("Do the shopping", false),
    Item("Organise stamp collection", false)
)

fun FlowContent.render(item: Item, index: Int) {
    div {
        id = "todo-$index"
        classes = listOfNotNull(
            if (item.complete) "complete" else null
        ).toSet()
        label {
            +item.text
        }
        button {
            attributes["hx-post"] = "/todo/${index}/toggle"
            attributes["hx-target"] = "#todo-$index"
            val text = if (item.complete) "Uncomplete" else "Complete"
            +text
        }
        button {
            attributes["hx-delete"] = "/todo/${index}"
            attributes["hx-target"] = "#todos"
            +"Delete"
        }
    }
}

data class Item(var text: String, var complete: Boolean)
