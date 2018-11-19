package io.fedcuit.github.futurePattern

class RealData(param: String) : Data<String> {
    private var result: String

    init {
        this.result = (0..10).joinToString {
            try {
                Thread.sleep(100)
            } catch (e: Exception) {
                e.printStackTrace()
            }; param
        }
    }


    override fun get(): String = this.result

}