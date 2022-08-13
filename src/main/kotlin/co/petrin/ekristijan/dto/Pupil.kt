package co.petrin.ekristijan.dto

class Pupil(val id: Int, val name: String, val fromClass: String) {
    override fun toString(): String = "$id: $name, $fromClass"
}