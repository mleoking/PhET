package edu.colorado.phet.common.view.graphics;

import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.movingman.common.GraphicsSetup;

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

    GraphicsSetup renderSetup = new GraphicsSetup();

    public void paintBufferedImage() {
        if( image == null ) {
            return;
        }
        Graphics2D graphics = image.createGraphics();
        renderSetup.saveState( graphics );
        graphics.setColor( backgroundColor );
        graphics.fillRect( 0, 0, width, height );
        graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        compositeGraphic.paint( graphics );
        renderSetup.restoreState( graphics );
    }

    public void setSize( int width, int height ) {
//        BufferedImage trash = (BufferedImage)target.createImage( target.getWidth(), target.getHeight() );
//        System.out.println( "trash = " + trash );
//        tempimage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
//        tempimage=new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB);
//        tempimage=new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB_PRE);
//        tempimage = target.createImage( target.getWidth(), target.getHeight() );
        BufferedImage created = (BufferedImage)target.createImage( target.getWidth(), target.getHeight() );
        BufferedImage newed = new BufferedImage( target.getWidth(), target.getHeight(), BufferedImage.TYPE_INT_RGB );
        System.out.println( "Target.createImage returned: = " + created );
        System.out.println( "new() returned: = " + newed );
//        this.image = (BufferedImage)tempimage;//GraphicsUtil.toBufferedImage(tempimage);
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

//    public void setImage( BufferedImage image ) {
//        this.image = image;
//    }

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

    GraphicsSetup graphicsSetup = new GraphicsSetup();

    public void paint( Graphics2D graphics2D ) {
        graphicsSetup.saveState( graphics2D );
//        this.compositeGraphic.paint( graphics2D );
        if( !inited ) {
            setSize( width, height );
            inited = true;
        }
        Composite c = graphics2D.getComposite();
        if( c instanceof AlphaComposite ) {
            AlphaComposite composite = (AlphaComposite)c;
//            System.out.println( "alpha=" + composite.getAlpha() + ", rule=" + composite.getRule() + ", code=" + composite.hashCode() );
        }

        if( image != null ) {
//            long time = System.currentTimeMillis();
//            Rectangle bounds = graphics2D.getClipBounds();
//            graphics2D.setComposite( AlphaComposite.SrcOver );
            if( alphaComposite != null ) {
                graphics2D.setComposite( alphaComposite );
            }
            else {
//                graphics2D.setComposite( AlphaComposite.SrcAtop );
            }

//            System.out.println( "image.getType() = " + image.getType() );

            graphics2D.drawImage( image, x, y, target );
//            long now = System.currentTimeMillis();
//            int size = bounds.width * bounds.height;
//            Shape clip = graphics2D.getClip();
//
//            graphics2D.setColor( Color.blue );
//            graphics2D.setStroke( new BasicStroke( 7 ) );
//            graphics2D.draw( clip );
//            System.out.println( "paint time=" + ( now - time ) + ", clip area=" + size + ",  clip bounds=" + bounds );
//            graphics2D.drawRenderedImage( image, AffineTransform.getTranslateInstance( x,y) );
        }
        graphicsSetup.restoreState( graphics2D );
    }

    public void setAlphaComposite( AlphaComposite ac ) {
        this.alphaComposite = ac;
    }
}


