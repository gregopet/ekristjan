package co.petrin.ekristijan.dto

/** @property name Obsolete, given & family names are used now */
class Pupil(val id: Int, val name: String, val givenName: String, val familyName: String, val fromClass: String) {
    override fun toString(): String = "$id: $name, $fromClass"
}