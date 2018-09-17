package test;

import org.junit.jupiter.api.Test;
import perceptron.Function;
import perceptron.MathUtils;

public class MathUtilsTest {

    private static final double EPS = 1e-12;

    @Test
    public void testDerivativeSqrReturn2x() {
        Function sqr = new Function() {
            @Override
            public double function(double x) {
                return x * x;
            }
        };

        double actual = MathUtils.derivative(sqr, 3);
        System.out.println(actual);
        double expected = 6.0;

        assertEquals(expected, actual);
    }

    private void assertEquals(double d1, double d2) {
        assert doubleEquals(d1, d2);
    }

    private boolean doubleEquals(double d1, double d2) {
        return Math.abs(d1 - d2) <= EPS;
    }
}
