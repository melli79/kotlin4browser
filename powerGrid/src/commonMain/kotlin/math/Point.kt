package org.grutzmann.common.math

data class Point(val x :Double, val y :Double) {
    fun translate(v :Vector) = Point(x + v.x, y + v.y)
    fun minus(p :Point) = Vector(x-p.x, y-p.y)
    fun plus(v :Vector) = Point(x+v.x, y+v.y)
}

fun Collection<Point>.centroid() :Point? {
    val o = firstOrNull()
    return o?.plus(
        fold(Vector(0.0,0.0)){s, p -> s.plus(p.minus(o)) }
            .scale(1.0/size))
}
