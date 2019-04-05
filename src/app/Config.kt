package app

import com.uchuhimo.konf.ConfigSpec

object Server: ConfigSpec() {

        val mqttUrl by optional("localhost")
        val mqttUser by optional("")
        val mqttPass by optional("")

        val dbUrl by optional("localhost")
        val dbUser by optional("")
        val dbPass by optional("")
}