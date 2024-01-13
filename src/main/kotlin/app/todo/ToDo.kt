package app.todo

data class ToDo(val id: String, var text: String, var isComplete: Boolean) {
    fun toggle() {
        isComplete = !isComplete
    }

    companion object {
        fun new(text: String) =
            ToDo(id = randomString(10), text = text, isComplete = false)
    }
}

private val allTheChars = listOf('a'..'z', 'A'..'Z', 0..9).flatMap { it.toList() }

private fun randomString(length: Int = 16) =
    generateSequence { allTheChars.random() }
        .take(length)
        .toList()
        .joinToString("")
