package math

import kotlin.test.Test
import kotlin.test.assertEquals

class matrix23Tester {
    @Test fun identity() {
        val m = Matrix23(0.0, 1.0, 0.0, 0.0, 0.0, 1.0)
        assertEquals(SPoint(1.0, 0.0), m.dot(Point(1.0, 0.0)))
        assertEquals(Point(0.0, 1.0), m.dot(SPoint(0.0, 1.0)))
    }

    @Test fun translation() {
        val m = Matrix23(1.0, 1.0, 0.0, 2.0, 0.0, 1.0)
        assertEquals(SPoint(1.0, 2.0), m.dot(Point(0.0, 0.0)))
        assertEquals(Point(1.0, 2.0), m.dot(SPoint(0.0, 0.0)))
    }

    @Test fun rotation() {
        val o = Point(0.0, 0.0)
        val m = Matrix23(0.0, 0.0, -1.0, 0.0, 1.0, 0.0)
        assertEquals(o.plus(Vector(1.0, 0.0).rotate(0.0, 1.0)), m.dot(SPoint(1.0, 0.0)))
        assertEquals(o.plus(Vector(0.0, 1.0).rotate(0.0, 1.0)), m.dot(SPoint(0.0, 1.0)))
    }

    @Test fun inverseTranslation() {
        val m = Matrix23(1.0, 1.0, 0.0, 2.0, 0.0, 1.0)
        val result = m.invert()
        assertEquals(SPoint(0.0, 0.0), result.dot(m.dot(SPoint(0.0, 0.0))))
        assertEquals(SPoint(1.0, 0.0), result.dot(m.dot(SPoint(1.0, 0.0))))
        assertEquals(SPoint(0.0, 1.0), result.dot(m.dot(SPoint(0.0, 1.0))))
    }
}
