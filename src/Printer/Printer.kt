package Printer

class Printer {
    companion object {
        private var DEBUG = true
        fun d(s: String) {
            if (DEBUG) {
                println(s)
            }
        }
    }
}