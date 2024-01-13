package app

import app.todo.ToDo
import app.todo.ToDoRepository
import gg.jte.ContentType
import gg.jte.TemplateEngine
import gg.jte.output.StringOutput
import gg.jte.resolve.DirectoryCodeResolver
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.FormField
import org.http4k.lens.Validator
import org.http4k.lens.webForm
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import java.nio.file.Path

fun main() {
    val toDoRepository = ToDoRepository.new()

    val textField = FormField.required("text")
    val bodyLens = Body.webForm(Validator.Strict, textField).toLens()

    val codeResolver = DirectoryCodeResolver(Path.of("src/main/resources/kte"))
    val templateEngine = TemplateEngine.create(codeResolver, ContentType.Html)

    val app = routes(
        "/" bind Method.GET to {
            val output = StringOutput()
            templateEngine.render("page.kte", toDoRepository.getAll(), output)
            Response(Status.OK).body(output.toString())
        },
        "/todo" bind Method.POST to { req ->
            val text = bodyLens(req).let(textField)
            toDoRepository.add(ToDo.new(text))

            val toDos = toDoRepository.getAll()
            val outstanding = toDos.count { !it.isComplete }

            val output = StringOutput()
            templateEngine.render("todo-list.kte", toDos, output)
            templateEngine.render(
                "outstanding.kte",
                mapOf("outstanding" to outstanding, "swapOob" to true),
                output
            )
            Response(Status.OK).body(output.toString())
        },
        "/todo/{id}/toggle" bind Method.POST to { req ->
            val id = req.path("id")!!
            val toDo = toDoRepository.get(id)!!
            toDo.toggle()

            val outstanding = toDoRepository.getAll().count { !it.isComplete }

            val output = StringOutput()
            templateEngine.render("todo.kte", toDo, output)
            templateEngine.render(
                "outstanding.kte",
                mapOf("outstanding" to outstanding, "swapOob" to true),
                output
            )
            Response(Status.OK).body(output.toString())
        },
        "/todo/{id}" bind Method.DELETE to { req ->
            val id = req.path("id")!!
            toDoRepository.delete(id)

            val toDos = toDoRepository.getAll()
            val outstanding = toDos.count { !it.isComplete }

            val output = StringOutput()
            templateEngine.render("todo-list.kte", toDoRepository.getAll(), output)
            templateEngine.render(
                "outstanding.kte",
                mapOf("outstanding" to outstanding, "swapOob" to true),
                output
            )
            Response(Status.OK).body(output.toString())
        }
    )

    app.asServer(Undertow(3000)).start()
}

