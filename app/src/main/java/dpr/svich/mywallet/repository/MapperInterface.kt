package dpr.svich.mywallet.repository

interface MapperInterface<From, To> {
    fun map(from : From) : To
}