package dev.martins.marcio.stockmgmt.importfiles

enum class B3Types(private val typeName: String) {
    TransferSettlement("Transferência - Liquidação"),
    Dividends("Dividendo"),
    Revenue("Rendimento"),
    InterestOnEquity("Juros Sobre Capital Próprio");

    companion object {
        private val types: Map<String, B3Types> = values().associateBy { it.typeName }

        fun from(name: String): B3Types? = types[name]
    }
}
