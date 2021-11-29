package math

import kotlinx.serialization.Serializable
import kotlin.math.pow

import kotlin.math.roundToInt

data class Matrix22(val xx :Double, val xy :Double,  val yx :Double, val yy :Double) {
    constructor(v :Vector, w :Vector) :this(v.x, w.x,  v.y, w.y)
    constructor(v :SVector, w :SVector) :this(v.x, w.x,  v.y, w.y)

    fun dot(v :Vector) = SVector(xx*v.x+xy*v.y, yx*v.x+yy*v.y)
    fun dot(m :Matrix22) = Matrix22(dot(Vector(m.xx, m.yx)), dot(Vector(m.xy, m.yy)))
}

fun Matrix22.inv() :Matrix22 {
    val d = xx*yy -xy*yx
    val f = 1/d // inverse of a 2x2 matrix
    return Matrix22(f*yy, -f*xy,  -f*yx, f*xx)
}

@Serializable
data class Matrix23(
    val ax :Double, val xx :Double, val xy :Double,
    val ay :Double, val yx :Double, val yy :Double) {

    override fun toString() = toString(4)
    fun toString(prec :Byte) :String {
        val e = 10.0.pow(prec.toInt())
        val f = 10.0.pow(-prec)
        return """Matrix {
            | ${(ax*e).roundToInt()*f}, ${(xx*e).roundToInt()*f}, ${(xy*e).roundToInt()*f};
            | ${(ay*e).roundToInt()*f}, ${(yx*e).roundToInt()*f}, ${(yy*e).roundToInt()*f}
            |}""".trimMargin()
    }

    fun dot(p :Point) = SPoint(ax +xx*p.x +xy*p.y, ay +yx*p.x +yy*p.y)
    fun dot(p :SPoint) = Point(ax +xx*p.x +xy*p.y, ay +yx*p.x +yy*p.y)
}

data class Matrix24(val p :Vector, val q :Vector, val v :SVector, val w :SVector) { }

fun Matrix23.invert() :Matrix23 {
    val i = Matrix22(xx, xy, yx, yy).inv()
    val a1 = i.dot(Vector(-ax, -ay))
    return Matrix23(a1.x, i.xx, i.xy, a1.y, i.yx, i.yy)
}

fun Matrix24.linSolve(org :SPoint, p0 :Point) :Matrix23 {
    val i = Matrix22(p, q).inv()
    val m = Matrix22(v, w).dot(i)
    val a = org.plus(m.dot(Point.origin.minus(p0)))
    return Matrix23(a.x, m.xx, m.xy, a.y, m.yx, m.yy)
}
