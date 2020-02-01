package dpr.svich.mywallet.enums

enum class Status(val code: Int) {
    UNUSED(0),
    LOGIN_INVALID(1),
    PASWD_INVALID(2),
    PASWD_MISMATCH(3),
    SUCCESS(4)
}