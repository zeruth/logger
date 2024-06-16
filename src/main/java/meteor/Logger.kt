package meteor

import meteor.ANSI_COLORS.ANSI_GREEN
import meteor.ANSI_COLORS.ANSI_PURPLE
import meteor.ANSI_COLORS.ANSI_RED
import meteor.ANSI_COLORS.ANSI_RESET
import meteor.ANSI_COLORS.ANSI_WHITE
import meteor.ANSI_COLORS.ANSI_YELLOW
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

class Logger(var name: String) {
    fun info(message: String,
             nameColor: String? = ANSI_WHITE, headerText: String? = null) {
        printMessage(
            messageColor = ANSI_WHITE,
            message = message,
            headerColor = nameColor,
            headerText = headerText
        )
    }

    fun warn(message: String,
             headerColor: String? = ANSI_YELLOW, headerText: String? = null) {
        printMessage(
            messageColor = ANSI_YELLOW,
            message = message,
            headerColor = headerColor,
            headerText = headerText
        )
    }

    fun debug(message: String,
              headerColor: String? = ANSI_GREEN, headerText: String? = null) {
        if (!showDebug) {
            return
        }
        printMessage(
            messageColor = ANSI_GREEN,
            message = message,
            headerColor = headerColor,
            headerText = headerText
        )
    }

    fun error(message: String,
              headerColor: String? = ANSI_RED,
              headerText: String? = null) {
        printMessage(
            messageColor = ANSI_RED,
            message = message,
            headerColor = headerColor,
            headerText = headerText
        )
    }

    fun error(exception: Exception,
              headerColor: String? = ANSI_RED,
              headerText: String? = null) {
        printMessage(
            messageColor = ANSI_RED,
            message = "\n" + exception.stackTraceToString().trimEnd('\n'),
            headerColor = headerColor,
            headerText = headerText
        )
    }

    private fun printMessage(messageColor: String, message: String,
                             headerColor: String?, headerText: String?) {
        var hText = ""
        var headerLength = 0
        if (headerColor != null) {
            hText += headerColor
        }
        val text = if (headerText == null) "[$name]" else "[$name:$headerText]"
        headerLength = text.length
        hText += text
        hText += ANSI_RESET

        val m = "$messageColor$message$ANSI_RESET"

        var finalText = if (hText.isNotEmpty())
            format(hText, headerLength, m)
        else
            m

        logFile.appendText(getCurrentTime() + sanitizeANSIString("$finalText\n"))
        println(finalText)
    }

    fun format(header: String, headerLength: Int, message: String): String {
        var t = header
        var i = 0
        while (i < ((headerLength + 5) - (headerLength % 5)) - headerLength) {
            t += " "
            i++
        }
        return t + message
    }

    private fun sanitizeANSIString(input: String): String {
        val ansiRegex = """\u001B\[[0-9;]*[a-zA-Z]""".toRegex()
        return ansiRegex.replace(input, "")
    }



    companion object {
        lateinit var logDirectory: File
        lateinit var logFile: File
        var showDebug = true
        val logger = Logger("main")
        fun KClass<*>.logger() = Logger(this.java.simpleName)
        private fun getCurrentDay(): String {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return current.format(formatter)
        }

        private fun getCurrentTime(): String {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            return "[${current.format(formatter)}] "
        }

        /**
         * This is purely for quick testing of behavior
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val meteorDir = File(System.getProperty("user.home"), ".meteor")
            logDirectory = File(meteorDir, "logs")
            logDirectory.mkdirs()
            logFile = File(logDirectory, "${getCurrentDay()}.txt")
            val logger = Logger::class.logger()
            val e = RuntimeException("testing")
            logger.error(e)
            logger.info("NH_INFO")
            logger.warn("NH_WARN")
            logger.error("NH_ERROR")
            logger.debug("NH_DEBUG")
            logger.info("INFO", headerText = "1")
            logger.info("INFO", headerText = "HEADER")
            logger.warn("WARN", headerText = "LONGER_HEADER")
            logger.error("ERROR", headerText = "ERROR_LEVEL_HEADER")
            logger.info("INFO", headerText = "HEADER")
            logger.warn("WARN", headerText = "LONGER_HEADER")
            logger.error("COLORED-NAME", headerColor = ANSI_PURPLE, headerText = "PURPLE")
        }
    }
}