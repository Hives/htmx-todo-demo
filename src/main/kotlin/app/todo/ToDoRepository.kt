package app.todo

class ToDoRepository {
    private val toDos = mutableListOf<ToDo>()

    fun add(toDo: ToDo) {
        toDos.add(toDo)
    }
    fun get(id: String) = toDos.find { it.id == id }
    fun getAll() = toDos.toList()
    fun delete(id: String) {
        toDos.removeIf { it.id == id }
    }
    fun countIncomplete() = toDos.count { !it.isComplete }

    companion object {
        fun new(): ToDoRepository {
            val repo = ToDoRepository()
            repo.add(ToDo.new("Do the shopping"))
            repo.add(ToDo.new("Organise stamp collection"))
            return repo
        }
    }
}

