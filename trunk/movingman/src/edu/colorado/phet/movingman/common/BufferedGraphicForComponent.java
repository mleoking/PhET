package edu.colorado.phet.common.view.graphics;

import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 5, 2003
 * Time: 10:11:52 PM
 * To change this template use Options | File Templates.
 */
public class BufferedGraphicForComponent implements Graphic {
    BufferedImage image;
    CompositeGraphic compositeGraphic = new CompositeGraphic();
    int x;
    int y;
    private int width;
    private int height;
    private Color backgroundColor;
    private Component target;
    private Image tempimage;
    private boolean inited = false;
    private AlphaComposite alphaComposite;

    public BufferedGraphicForComponent( int x, int y, int width, int height, Color backgroundColor, Component target ) {
        this.backgroundColor = backgroundColor;
        this.target = target;
        tempimage = target.createImage( target.getWidth(), target.getHeight() );
        if( tempimage != null ) {
            this.image = (BufferedImage)tempimage;//GraphicsUtil.toBufferedImage(tempimage);
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void paintBufferedImage() {
        if( image == null ) {
            return;
        }
        Graphics2D graphics = image.createGraphics();
        GraphicsState renderState = new GraphicsState( graphics );
        graphics.setColor( backgroundColor );
        graphics.fillRect( 0, 0, width, height );
        graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        compositeGraphic.paint( graphics );
        renderState.restoreGraphics();
    }

    public void setSize( int width, int height ) {
        if( height < 0 ) {
            throw new RuntimeException( "Negative height." );
        }
        BufferedImage newed = new BufferedImage( target.getWidth(), target.getHeight(), BufferedImage.TYPE_INT_RGB );
        this.image = newed;
        this.width = width;
        this.height = height;
    }

    public void addGraphic( Graphic graphic, int level ) {
        compositeGraphic.addGraphic( graphic, level );
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public void setX( int x ) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY( int y ) {
        this.y = y;
    }

    public void paint( Graphics2D graphics2D ) {
        GraphicsState graphicsState = new GraphicsState( graphics2D );
        if( !inited ) {
            setSize( width, height );
            inited = true;
        }

        if( image != null ) {
            if( alphaComposite != null ) {
                graphics2D.setComposite( alphaComposite );
            }
            graphics2D.drawImage( image, x, y, target );
        }
        graphicsState.restoreGraphics();
    }

    public void setAlphaComposite( AlphaComposite ac ) {
        this.alphaComposite = ac;
    }

    public void paintBufferedImage( Rectangle rect ) {
        if( image == null ) {
            return;
        }
        Graphics2D graphics = image.createGraphics();
        GraphicsState renderState = new GraphicsState( graphics );
        graphics.setColor( backgroundColor );
        graphics.fillRect( 0, 0, width, height );
        graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        graphics.setClip( rect );
        compositeGraphic.paint( graphics );
        renderState.restoreGraphics();
    }
}


