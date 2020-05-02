package dpr.svich.mywallet.model

data class Transaction(var id: String? = null,
                       var comment: String? = null,
                       var price: String? = null,
                       var timestamp: Long? = null,
                       var isSpend: Boolean? = null,
                       var category: Int? = null)