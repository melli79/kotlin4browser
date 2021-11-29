package math

import kotlin.math.roundToInt
import kotlin.math.sqrt

data class SPoint(val x :Double, val y :Double) {
    fun translate(v :Vector) = Point(x + v.x, y + v.y)
    fun minus(p :SPoint) = SVector(x-p.x, y-p.y)
    fun plus(v :SVector) = Point(x+v.x, y+v.y)
    override fun toString() = """SPoint(${x.roundToInt()}, ${y.roundToInt()})"""

    companion object {
        val origin = SPoint(0.0, 0.0)
    }
}

data class SVector(val x :Double, val y :Double) {
    fun scale(f :Double) = SVector(x*f, y*f)
    fun normed() = scale(1/sqrt(norm2()))
    fun norm2() = x*x +y*y
    fun rotate(c :Double, s :Double) = SVector(c*x-s*y, s*x +c*y)
    fun plus(v :SVector) = SVector(x+v.x, y+v.y)
    fun minus() = SVector(-x, -y)
    override fun toString() = """SVector(${x.roundToInt()}, ${y.roundToInt()})"""

    companion object {
        val ex = SVector(1.0, 0.0)
        val ey = SVector(0.0, 1.0)
    }
}
