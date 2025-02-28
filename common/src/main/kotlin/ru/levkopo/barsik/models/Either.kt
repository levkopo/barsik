package ru.levkopo.barsik.models

sealed class Either<out A, out B> {

    /**
     * The left side of the disjoint union, as opposed to the [Right] side.
     */
    class Left<out A>(val value: A) : Either<A, Nothing>()

    /**
     * The right side of the disjoint union, as opposed to the [Left] side.
     */
    class Right<out B>(val value: B) : Either<Nothing, B>()
}