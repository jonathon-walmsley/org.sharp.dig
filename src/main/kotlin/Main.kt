package org.sharp.dig

import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val instant: Instant = Instant.now()
val zone: ZoneId = ZoneId.systemDefault()
val timestamp_yyyyMMddHHmmssSSSSS = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSS").withZone(zone).format(instant)
val timestamp_MMMM_dd_YYYY = DateTimeFormatter.ofPattern("MMMM dd YYYY").withZone(zone).format(instant)

lateinit var logger: Logger
lateinit var config: Config

fun log(msg: String) {
    println(msg)
    logger.info(msg)
}

fun configureLogger() {
    val logFilename = "files/log/" + timestamp_yyyyMMddHHmmssSSSSS + "_IdCards.log"
    System.setProperty("log4j.filename", logFilename)
    BasicConfigurator.resetConfiguration()
    PropertyConfigurator.configure("Files/config/log4j.properties")
}

fun main(vararg args: String) {

    configureLogger()
    logger = Logger.getLogger("Main")
    log("Job started at: $timestamp_MMMM_dd_YYYY for job: IdCards")
    log("Loading Configuration Properties")

    if (args.isNotEmpty()) {
        args[0].split(",").forEach { process ->
            if (loadConfigurations(process)) {
                when (process) {
                    "idcards" -> IdCards().processIdCards("Files/input")
                    "letters" -> Letters().processLetters("Files/input")
                }
            }
        }
    } else {
        println("No arguments passed!")
    }
}

fun loadConfigurations(process: String): Boolean {
    config = Config()
    var validProcess = false
    File("files/config/config.properties").bufferedReader().use { out ->
        out.forEachLine { line ->
            when {
                line.startsWith("availableProcess=") -> { when { line.contains(process) -> validProcess = true } }
                line.startsWith("idcards.memberExistenceCheckFlag=On") -> config.recard = true
            }
        }
    }
    return validProcess
}

class Config {
    var recard = false
    override fun toString(): String =
        "Config Properties:\n" +
                "recard = $recard"

}
