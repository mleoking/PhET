/**
 * Class: BufferedGraphic2
 * Package: edu.colorado.phet.greenhouse.coreadditions
 * Author: Another Guy
 * Date: Oct 17, 2003
 */
package edu.colorado.phet.greenhouse.coreadditions;
/*Copyright, Sam Reid, 2003.*/


//import edu.colorado.phet.common.view.graphics.CompositeGraphic;

import edu.colorado.phet.common.view.CompositeGraphic;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 5, 2003
 * Time: 10:11:52 PM
 * To change this template use Options | File Templates.
 */
public class BufferedGraphic2 extends CompositeGraphic {
    Component target;
    private int x;
    private int y;
    private int width;
    private int height;
    private BufferedImage image;

    private Graphics2D graphics;

    public BufferedGraphic2(Component target) {
        this(0, 0, target.getWidth(), target.getHeight(), target);
    }

    private BufferedGraphic2(int x, int y, int width, int height, Component target) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.target = target;

        if (target.isShowing())
            rebuildBuffer();
        target.addComponentListener(new ComponentAdapter() {

            public void componentShown(ComponentEvent e) {
                rebuildBuffer();
            }

            public void componentResized(ComponentEvent e) {
                rebuildBuffer();
            }

            public void componentHidden(ComponentEvent e) {
//                graphics.dispose();
//                graphics = null;
            }
        });
    }

    public synchronized void rebuildBuffer() {
        if (graphics != null)
            graphics.dispose();
        this.image = createBuffer(target);
        if (image != null) {
            this.graphics = image.createGraphics();
            paint(graphics);
        }
    }

    private static BufferedImage createBuffer(Component target) {
        //protect against zero size.
        int width = Math.max(1, target.getWidth());
        int height = Math.max(1, target.getHeight());
        BufferedImage tempimage = (BufferedImage) target.createImage(width, height);
        return tempimage;
    }

    public synchronized void updateBuffer() {
        graphics.setColor(Color.black);
        graphics.fillRect(x, y, width, height);
        paint(graphics);
    }

//    public void setSize(int width, int height) {
//        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        this.width = width;
//        this.height = height;
//        rebuildBuffer();
//    }

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

    public synchronized void paint(Graphics2D graphics2D) {
//        graphics.clearRect(x,y,width,height);
//        graphics2D.setColor(Color.white);
//        graphics.fillRect(x,y,width,height);

        if (image != null) {
            AffineTransform at = graphics2D.getTransform();
            graphics2D.setTransform(new AffineTransform());
//            graphics2D.setColor(Color.white);
//            graphics2D.fillRect(0,0,width,height);

            graphics.setTransform(at);//usurp the output graphic's coordinate frame.

            super.paint(graphics);//Fill in the buffer.
//            graphics2D.drawRenderedImage(image, new AffineTransform());//draw the buffer to the requesed graphics device.
            graphics2D.drawImage(image, 0, 0, target); //Following the Java2D Demo
            Toolkit.getDefaultToolkit().sync();

//            graphics2D.drawRenderedImage(ImageLoader.loadBufferedImage("images/firedog2.gif"), new AffineTransform());
            graphics2D.setTransform(at);
        }
    }

}

