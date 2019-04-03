package app

import com.uchuhimo.konf.ConfigSpec

object Base: ConfigSpec() {
    object Database: ConfigSpec(){
        val url by optional("localhost")
        val user by optional("")
        val password by optional("")
    }
    object Mqtt: ConfigSpec() {
        val url by optional("localhost")
        val user by optional("")
        val password by optional("")
    }
}