package perceptron;

public class MathUtils {

    private static final double E = 2.71828182846;

    public static double sigmoid(double value) {
        double e_x = Math.pow(E, value);
        return e_x / (e_x + 1);
    }

    public static double ReLU(double value) {
        return Math.max(0, value);
    }

    public static double derivative(Function f, double x0){
        double delta = 1e-12;

        double x1 = x0;
        double x2 = x0 + delta;
        double dx = x2 - x1;

        double y1 = f.function(x1);
        double y2 = f.function(x2);
        double dy = y2 - y1;

        return dy / dx;
    }

}
