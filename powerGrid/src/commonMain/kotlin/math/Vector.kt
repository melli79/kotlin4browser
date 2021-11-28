package org.grutzmann.common.math

data class Vector(val x :Double, val y :Double) {
    fun minus() = Vector(-x, -y)
    fun scale(f :Double) = Vector(f * x, f * y)
    fun rotate(c :Double, s :Double) = Vector(c * x - s * y, s * x + c * y)
    fun plus(v :Vector) = Vector(x+v.x, y+v.y)

    fun norm2() = x*x +y*y
    fun perp() = Vector(-y, x)
    fun dot(v :Vector) = x*v.x +y*v.y
}
