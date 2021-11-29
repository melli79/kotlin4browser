package math

data class Vector(val x :Double, val y :Double) {
    fun minus() = Vector(-x, -y)
    fun scale(f :Double) = Vector(f * x, f * y)
    fun rotate(c :Double, s :Double) = Vector(c * x - s * y, s * x + c * y)
    fun plus(v :Vector) = Vector(x+v.x, y+v.y)

    fun norm2() = x*x +y*y
    fun perp() = Vector(-y, x)
    fun dot(v :Vector) = x*v.x +y*v.y

    companion object {
        val ex = Vector(1.0, 0.0)
        val ey = Vector(0.0, 1.0)
    }
}
