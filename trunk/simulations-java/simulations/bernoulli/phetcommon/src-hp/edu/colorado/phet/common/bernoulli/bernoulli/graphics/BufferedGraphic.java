package edu.colorado.phet.common.bernoulli.bernoulli.graphics;

import edu.colorado.phet.common.bernoulli.view.CompositeGraphic;
import edu.colorado.phet.common.bernoulli.view.graphics.Graphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 5, 2003
 * Time: 10:11:52 PM
 * To change this template use Options | File Templates.
 */
public class BufferedGraphic implements Graphic {
    BufferedImage image;
    CompositeGraphic compositeGraphic = new CompositeGraphic();
    int x;
    int y;
    private int width;
    private int height;
    private Color backgroundColor;

    public BufferedGraphic(int x, int y, int width, int height, Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void paintBufferedImage() {
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, width, height);
        compositeGraphic.paint(graphics);
    }

    public void setSize(int width, int height) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.width = width;
        this.height = height;
    }

    public void addGraphic(Graphic graphic, int level) {
        compositeGraphic.addGraphic(graphic, level);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void paint(Graphics2D graphics2D) {
        graphics2D.drawImage(image, x, y, null);
    }

}
