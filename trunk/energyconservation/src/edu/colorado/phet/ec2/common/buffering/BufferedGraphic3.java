package edu.colorado.phet.ec2.common.buffering;

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
public class BufferedGraphic3 implements Graphic {
    BufferSource source;
    ImageObserver observer;
    private Paint background;
    CompositeGraphic compositeGraphic = new CompositeGraphic();
    int x;
    int y;
    private int width;
    private int height;
    private BufferedImage image;
    private Graphics2D graphics;
    boolean dimensionsSet = false;
    boolean changed = true;

    public BufferedGraphic3( Component target ) {
        this( target, Color.white );
    }

    public BufferedGraphic3( Component target, Paint background ) {
        this.observer = target;
        this.background = background;
        this.source = new ComponentBufferSource( target );
    }

    public void updateBuffer() {
        if( ( changed || graphics == null ) && dimensionsSet ) {
            this.image = source.getBuffer( width, height );
            if( image != null ) {
                this.graphics = image.createGraphics();
            }
            changed = false;
        }
        if( graphics != null ) {
            graphics.setPaint( background );
            graphics.fillRect( x, y, width, height );
            compositeGraphic.paint( graphics );
        }
    }

    public void setSize( int width, int height ) {
        this.width = width;
        this.height = height;
        graphics = null;
        dimensionsSet = true;
        changed = true;
        updateBuffer();
    }

    public void removeGraphic( Graphic graphic ) {
        compositeGraphic.removeGraphic( graphic );
//        graphics = null;
        changed = true;
    }

    public void removeAllGraphics() {
        compositeGraphic.removeAllGraphics();
//        graphics = null;
        changed = true;
    }

    public void addGraphic( Graphic graphic, int level ) {
        compositeGraphic.addGraphic( graphic, level );
//        graphics = null;
        changed = true;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    public void paint( Graphics2D graphics2D ) {
        if( image != null ) {
            graphics2D.drawImage( image, x, y, this.observer );
        }
    }

}
