package edu.colorado.phet.coreadditions.graphics;

import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 5, 2003
 * Time: 10:11:52 PM
 * To change this template use Options | File Templates.
 */
public class BufferedGraphic2 implements Graphic {
    ImageObserver observer;
    CompositeGraphic compositeGraphic = new CompositeGraphic();
    int x;
    int y;
    private int width;
    private int height;
    private BufferedImage image;
    private Graphics2D graphics;

    public BufferedGraphic2(int x, int y, int width, int height) {
        this(x, y, width, height, new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));
    }

    public BufferedGraphic2(Component target) {
        this(target.getX(), target.getY(), target.getWidth(), target.getHeight(), target);
    }

    public BufferedGraphic2(int x, int y, int width, int height, Component target) {
        this(x, y, width, height, createBuffer(target));
        this.observer = target;
    }

    public void rebuildBuffer(Component target) {
        this.image = createBuffer(target);
        this.graphics=image.createGraphics();
        this.observer = target;
    }

    private static BufferedImage createBuffer(Component target) {
        int width=Math.max(1,target.getWidth());
        int height=Math.max(1,target.getHeight());
        BufferedImage tempimage = (BufferedImage) target.createImage(width,height);
        return tempimage;
    }

    public BufferedGraphic2(int x, int y, int width, int height, BufferedImage buffer) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = buffer;
        this.graphics=image.createGraphics();
    }
    public void updateBuffer() {
//        graphics = image.createGraphics();//new graphics, same image.
        graphics.setColor(Color.black);
        graphics.fillRect(x,y,width,height);
        compositeGraphic.paint(graphics);
    }

    public void setSize(int width, int height) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.width = width;
        this.height = height;
        graphics=image.createGraphics();
    }

    public void removeGraphic(Graphic graphic) {
        compositeGraphic.removeGraphic(graphic);
    }

    public void removeAllGraphics() {
        compositeGraphic.removeAllGraphics();
    }

    public void addGraphic(Graphic graphic, int level) {
        compositeGraphic.addGraphic(graphic, level);
    }

    public BufferedImage getImage() {
        return image;
    }

    public Graphics2D getGraphics() {
        return graphics;
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
        if (image != null)
            graphics2D.drawImage(image, x, y, this.observer);
    }

}
