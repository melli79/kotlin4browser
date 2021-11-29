package math

import kotlin.test.Test
import kotlin.test.assertEquals

class matrixIntegrationTester {
    @Test fun fixedPoints() {
        val p0 = Point.origin
        val p1 = Point(0.0, 1.0)
        val p2 = Point(-1.0, 1.0)
        val s0 = SPoint(120.0, 30.0)
        val s1 = SPoint(115.0, 40.0)
        val s2 = SPoint(95.0, 42.0)
        val input = Matrix24(p1.minus(p0), p2.minus(p0), s1.minus(s0), s2.minus(s0))
        val result = input.linSolve(s0, p0)
        assertEquals(s0, result.dot(p0))
        assertEquals(s1, result.dot(p1))
        assertEquals(s2, result.dot(p2))
    }
}
