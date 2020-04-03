package dpr.svich.mywallet.model

import java.sql.Timestamp

data class Transaction(var comment: String? = null,
                       var price: String? = null, var timestamp: Long? = null)