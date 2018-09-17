package drawer.view;

import drawer.model.MyImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

import static drawer.model.MyImage.SIZE;

public class ImageDrawer extends Region {

    private static final String BACKGR_COLOR = "-fx-background-color: ";
    private MyImage image;
    private Region[][] pixels = new Region[SIZE][SIZE];
    private double sensitiveRadius;
    private double pixelSize;

    public ImageDrawer(int size) {
        image = new MyImage();
        setPrefSize(size, size);
        sensitiveRadius = size / SIZE;
        getStylesheets().add("/drawer/view/style.css");
        getStyleClass().add("drawer");
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                pixels[i][j] = createPixel(i, j);
                getChildren().add(pixels[i][j]);
            }
        }
        setOnMouseDragged(d -> blurPixels(d));

    }

    public void clear() {
        for (int i = 0; i < SIZE; ++i)
            for (int j = 0; j < SIZE; ++j)
                pixels[i][j].setStyle(BACKGR_COLOR + getRGBTripple(0));
    }

    private Region createPixel(int i, int j) {
        Region pixel = new Region();
        pixelSize = getPrefHeight() / SIZE;
        pixel.setPrefSize(pixelSize, pixelSize);
        pixel.setLayoutY(i * pixelSize);
        pixel.setLayoutX(j * pixelSize);
        pixel.setStyle("-fx-background-color: rgb(0, 0, 0)");
        return pixel;
    }

    private void blurPixels(MouseEvent d) {
        if (outOfBounds(d))
            return;
        double x = d.getX();
        double y = d.getY();
        checkTopLeft(x, y);
        checkTopRight(x, y);
        checkBottomLeft(x, y);
        checkBottomRight(x, y);
    }

    private boolean outOfBounds(MouseEvent d) {
        if (d.getX() < 0 ||
                d.getX() >= getPrefWidth() ||
                d.getY() < 0 ||
                d.getY() >= getPrefHeight()
        )
            return true;
        return false;
    }

    private void checkTopLeft(double middleX, double middleY) {
        double tlX = middleX - pixelSize / 2;
        double tlY = middleY - pixelSize / 2;
        int i = (int) (tlY / pixelSize);
        int j = (int) (tlX / pixelSize);

        if(isOutOfBounds(i, j))
            return;

        double thisBRX = j * pixelSize + pixelSize;
        double thisBRY = i * pixelSize + pixelSize;

        double square = (thisBRX - tlX) * (thisBRY - tlY);
        double pixelSquare = pixelSize * pixelSize;
        double coef = square / pixelSquare;

        image.setIfBrighter(i, j, coef);
        double newCoef = image.get(i, j);
        pixels[i][j].setStyle(BACKGR_COLOR + getRGBTripple(newCoef));
    }

    private void checkTopRight(double middleX, double middleY) {
        double trX = middleX + pixelSize / 2;
        double trY = middleY - pixelSize / 2;
        int i = (int) (trY / pixelSize);
        int j = (int) (trX / pixelSize);

        if(isOutOfBounds(i, j))
            return;

        double thisBLX = j * pixelSize;
        double thisBLY = i * pixelSize + pixelSize;

        double square = (trX - thisBLX) * (thisBLY - trY);
        double pixelSquare = pixelSize * pixelSize;

        double coef = square / pixelSquare;
        image.setIfBrighter(i, j, coef);
        double newCoef = image.get(i, j);
        pixels[i][j].setStyle(BACKGR_COLOR + getRGBTripple(newCoef));
    }

    private void checkBottomLeft(double middleX, double middleY) {
        double blX = middleX - pixelSize / 2;
        double blY = middleY + pixelSize / 2;
        int i = (int) (blY / pixelSize);
        int j = (int) (blX / pixelSize);

        if (isOutOfBounds(i, j))
            return;

        double thisTRX = j * pixelSize + pixelSize;
        double thisTRY = i * pixelSize;

        double square = (thisTRX - blX) * (blY - thisTRY);
        double pixelSquare = pixelSize * pixelSize;

        double coef = square / pixelSquare;
        image.setIfBrighter(i, j, coef);
        double newCoef = image.get(i, j);
        pixels[i][j].setStyle(BACKGR_COLOR + getRGBTripple(newCoef));
    }

    private void checkBottomRight(double middleX, double middleY) {
        double brX = middleX + pixelSize / 2;
        double brY = middleY + pixelSize / 2;
        int i = (int) (brY / pixelSize);
        int j = (int) (brX / pixelSize);

        if(isOutOfBounds(i, j))
            return;

        double thisTLX = j * pixelSize;
        double thisTLY = i * pixelSize;

        double square = (brX - thisTLX) * (brY - thisTLY);
        double pixelSquare = pixelSize * pixelSize;

        double coef = square / pixelSquare;
        image.setIfBrighter(i, j, coef);
        double newCoef = image.get(i, j);
        pixels[i][j].setStyle(BACKGR_COLOR + getRGBTripple(newCoef));
    }

    private boolean isOutOfBounds(int i, int j){
        return i >= SIZE || i < 0 || j >= SIZE || j < 0;
    }

    private String getRGBTripple(double coef) {
        int rgbVal = (int) (coef * 255);
        return "rgb(" + rgbVal + ", " + rgbVal + ", " + rgbVal + ")";
    }

}
