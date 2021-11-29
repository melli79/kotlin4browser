package math

import kotlin.test.*

class Matrix24Tester {
    @Test fun invertIdentity() {
        val s = Matrix24(Vector.ex, Vector.ey,  SVector.ex, SVector.ey)
        val result = s.linSolve(SPoint.origin, Point.origin)
        assertEquals(Matrix23(0.0, 1.0, 0.0, 0.0, 0.0, 1.0), result)
    }

    @Test fun invertTranslation() {
        val s = Matrix24(Vector.ex, Vector.ey,  SVector.ex, SVector.ey)
        val result = s.linSolve(SPoint(1.0, 2.0), Point.origin)
        assertEquals(Matrix23(1.0, 1.0, 0.0, 2.0, 0.0, 1.0), result)
    }

    @Test fun invertOffset() {
        val s = Matrix24(Vector.ex, Vector.ey,  SVector.ex, SVector.ey)
        val result = s.linSolve(SPoint.origin, Point(1.0, 2.0))
        assertEquals(Matrix23(-1.0, 1.0, 0.0, -2.0, 0.0, 1.0), result)
    }

    @Test fun rotate() {
        val s = Matrix24(Vector.ex, Vector.ey,  SVector.ey.minus(), SVector.ex)
        val result = s.linSolve(SPoint.origin, Point.origin)
        assertEquals(Matrix23(0.0, 0.0, 1.0, 0.0, -1.0, 0.0),
            result, epsilon)
    }

    @Test fun rotateOffset() {
        val s = Matrix24(Vector.ex, Vector.ey,  SVector.ey.minus(), SVector.ex)
        val result = s.linSolve(SPoint.origin, Point(1.0, 2.0))
        assertEquals(Matrix23(-2.0, 0.0, 1.0, 1.0, -1.0, 0.0),
            result, epsilon)
    }

    @Test fun flipped() {
        val s = Matrix24(Vector.ey, Vector.ex,  SVector.ex.minus(), SVector.ey)
        val result = s.linSolve(SPoint.origin, Point.origin)
        assertEquals(Matrix23(0.0, 0.0, -1.0, 0.0, 1.0, 0.0),
            result, epsilon)
    }

    @Test fun flippedOffset() {
        val s = Matrix24(Vector.ey, Vector.ex,  SVector.ex.minus(), SVector.ey)
        val result = s.linSolve(SPoint.origin, Point(1.0, 2.0))
        assertEquals(Matrix23(2.0, 0.0, -1.0, -1.0, 1.0, 0.0),
            result, epsilon)
    }

    @Test fun rotate2() {
        val s = Matrix24(Vector.ex, Vector.ey,  SVector.ey, SVector.ex.minus())
        val result = s.linSolve(SPoint.origin, Point.origin)
        assertEquals(Matrix23(0.0, 0.0, -1.0, 0.0, 1.0, 0.0),
            result, epsilon)
    }

    @Test fun rotate2Offset() {
        val s = Matrix24(Vector.ex, Vector.ey,  SVector.ey, SVector.ex.minus())
        val result = s.linSolve(SPoint.origin, Point(1.0, 2.0))
        assertEquals(Matrix23(2.0, 0.0, -1.0, -1.0, 1.0, 0.0),
            result, epsilon)
    }
}

fun assertEquals(ex :Matrix23, ac :Matrix23, epsilon :Double) {
    assertEquals(ex.ax, ac.ax, epsilon, "expected $ex and actual $ac differ at ax")
    assertEquals(ex.xx, ac.xx, epsilon, "expected $ex and actual $ac differ at xx")
    assertEquals(ex.xy, ac.xy, epsilon, "expected $ex and actual $ac differ at xy")
    assertEquals(ex.ay, ac.ay, epsilon, "expected $ex and actual $ac differ at ay")
    assertEquals(ex.yx, ac.yx, epsilon, "expected $ex and actual $ac differ at xy")
    assertEquals(ex.yy, ac.yy, epsilon, "expected $ex and actual $ac differ at yy")
}
