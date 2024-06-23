package manager.manager.model.enums

class FileType {
    companion object {
        fun getFileTypes(): List<Pair<SnippetLanguage, String>> {
            return SnippetLanguage.entries.map { language ->
                Pair(language, getFileType(language))
            }
        }

        private fun getFileType(language: SnippetLanguage): String {
            return when(language){
                SnippetLanguage.PRINTSCRIPT -> "ps"
            }
        }
    }
}