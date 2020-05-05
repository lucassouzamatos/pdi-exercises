package com.company;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {
    @FXML ImageView imageViewOne;
    @FXML ImageView imageViewTwo;
    @FXML ImageView imageViewThree;

    private Image imageOne;
    private Image imageTwo;
    private Image imageThree;

    private Image openImage(ImageView imageView) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
            imageView.setFitHeight(image.getHeight());
            imageView.setFitWidth(image.getWidth());

            return image;
        }

        return null;
    }

    @FXML
    public void handleImageOneAction() {
        imageOne = openImage(imageViewOne);
        sendImageToViewThree(columnToPb(imageOne));

        isFullFilled();
    }

    @FXML
    public void handleImageTwoAction() {
        imageTwo = openImage(imageViewTwo);
    }

    private void sendImageToViewThree(Image image) {
        imageViewThree.setImage(image);
        imageViewThree.setFitHeight(image.getHeight());
        imageViewThree.setFitWidth(image.getWidth());

        imageThree = image;
    }

    private Image columnToPb(Image image) {
        try {
            double w = image.getWidth();
            double h = image.getHeight();

            PixelReader pixelReader = image.getPixelReader();
            WritableImage writableImage = new WritableImage((int) w, (int) h);
            PixelWriter pixelWriter = writableImage.getPixelWriter();

            int columnWidth = (int) (w/8);
            boolean isGray = true;

            for (int i = 0; i < w; i++) {
                if ((i+1)%columnWidth == 0) {
                    isGray = !isGray;
                } 

                for (int j = 0; j < h; j++) {
                    Color oldColor = pixelReader.getColor(i, j);

                    if (isGray) {
                        double media = (oldColor.getRed() + oldColor.getGreen() + oldColor.getBlue()) / 3;
                        Color currentColor = new Color(media, media, media, oldColor.getOpacity());
                        pixelWriter.setColor(i, j, currentColor);
                    } else {
                        pixelWriter.setColor(i, j, oldColor);
                    }
                }
            }
            return writableImage;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public boolean isFullFilled() {
        int w = (int) imageOne.getWidth();
        int h = (int) imageOne.getHeight();

        PixelReader pixelReader = imageOne.getPixelReader();
        WritableImage writableImage = new WritableImage((int) w, (int) h);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int originX = -1; 
        int originY = -1;
        int targetX = -1; 
        int targetY = -1;

        for (int x = 0; x < w; x++) {
            int currentY = 0;
            if (originY != -1) {
                currentY = originY;
            }

            for (int y = currentJ; y < h; y++) {
                Color currentColor = pixelReader.getColor(i, y);

                if (currentColor.toString().equals(Color.BLACK.toString())) {
                    if (originX == -1 && originY == -1) {
                        originX = x;
                        originY = y;
                    }
                    
                    if (targetY == -1 && !pixelReader.getColor(i, y+1).toString().equals(Color.BLACK.toString()) && i == originX) {
                        targetY = y;
                    }
                    
                    if (targetX == -1 && !pixelReader.getColor(i+1, y).toString().equals(Color.BLACK.toString()) && y == originY) {
                        targetX = i;
                    }

                } else if (originX != -1 && originY != -1) {
                    if (targetX == -1 && y > originY && y < targetY) {
                        System.out.print("É VAZADO!");
                        break;
                    }
                }
            }
        }

        return true;
    } 


    int originX;
    int originY;

    @FXML
    public void clickImage(MouseEvent mouseEvent) {
        originX = (int) mouseEvent.getX();
        originY = (int) mouseEvent.getY();
    }

    /**
     * Inversão de pedaço da imagem   
     */
    @FXML
    public void releaseImage(MouseEvent mouseEvent) {
        ImageView imageView = (ImageView) mouseEvent.getTarget();        
        int targetX = (int) mouseEvent.getX();
        int targetY = (int) mouseEvent.getY();

        Image image = imageView.getImage();

        int w = (int) image.getWidth();
        int h = (int) image.getHeight();

        PixelReader pixelReader = image.getPixelReader();
        WritableImage writableImage = new WritableImage((int) w, (int) h);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int currentInverseY = 0;
        int currentInverseX = 0;

        for (int i = 0; i < w; i++) {
            currentInverseY = 0;

            for (int j = 0; j < h; j++) {
                Color oldColor = pixelReader.getColor(i, j);
                if (i >= originX && i <= targetX && j >= originY && j <= targetY) {
                    Color currentColor = pixelReader.getColor(targetX-currentInverseX, targetY-currentInverseY);
                    pixelWriter.setColor(i, j, currentColor);
                    currentInverseY++;
                } else {
                    pixelWriter.setColor(i, j, oldColor);
                }
            }

            if (i >= originX && i <= targetX)
                currentInverseX++;
        }        

        imageView.setImage(writableImage);
    } 
}