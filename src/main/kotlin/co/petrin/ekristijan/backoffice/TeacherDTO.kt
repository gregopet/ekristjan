package co.petrin.ekristijan.backoffice

data class TeacherDTO(
    val id: Int,
    val name: String,
    val email: String,
    val enabled: Boolean,
    val backofficeAccess: Boolean,
)