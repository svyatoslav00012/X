package drawer.model;

public class MyImage {

    public static final int SIZE = 28;

    private double[][] image = new double[SIZE][SIZE];
    private int label;

    public double[] toVector() {
        double[] vector = new double[SIZE * SIZE];
        for (int i = 0; i < SIZE; ++i)
            for (int j = 0; j < SIZE; ++j)
                vector[i * SIZE + j] = image[i][j];
        return vector;
    }

    public double get(int x, int y){
        return image[y][x];
    }

    public void setIfBrighter(int x, int y, double coef){
        if(coef > image[y][x])
            set(x, y, coef);
    }

    public void set(int x, int y, double coef){
        image[y][x] = coef;
    }

    public int getLabel() {
        return label;
    }
}
