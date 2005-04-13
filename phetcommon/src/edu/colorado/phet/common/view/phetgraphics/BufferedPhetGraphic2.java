/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.GraphicsSetup;
import edu.colorado.phet.common.view.util.GraphicsState;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * BufferedPhetGraphic
 * <p/>
 *
 * @author ?
 * @version $Revision$
 */
public class BufferedPhetGraphic2 extends GraphicLayerSet {
    private PhetImageGraphic phetImageGraphic;
    private Paint background;
    private int imageType = BufferedImage.TYPE_INT_RGB;
    private Rectangle clip;
    private GraphicsSetup graphicsSetup = new BasicGraphicsSetup();

    private RepaintStrategy.FalseComponent falseComponent = new RepaintStrategy.FalseComponent( new RepaintStrategy() {
        public void repaint( int x, int y, int width, int height ) {
        }

        public void paintImmediately() {
        }
    } );

    public BufferedPhetGraphic2( Component component, Paint background ) {
        super( component );
        this.background = background;
        this.phetImageGraphic = new PhetImageGraphic( component );
    }

    public void addGraphic( PhetGraphic graphic ) {
        addGraphic( graphic, 0 );
    }

    public void addGraphic( PhetGraphic graphic, double layer ) {
        super.addGraphic( graphic, layer );
        graphic.setComponent( falseComponent );//redirect paint calls to here.
    }

    public void repaintBuffer() {
        repaintBuffer( 0, 0, getImage().getWidth(), getImage().getHeight() );
    }

    public BufferedImage getImage() {
        return phetImageGraphic.getImage();
    }

    public void setSize( int width, int height ) {
        if( width > 0 && height > 0 ) {
            phetImageGraphic.setImage( new BufferedImage( width, height, imageType ) );
        }
    }

    protected Rectangle determineBounds() {
        if( getImage() == null ) {
            return null;
        }
        return getNetTransform().createTransformedShape( new Rectangle( getImage().getWidth(), getImage().getHeight() ) ).getBounds();
    }

    public void paint( Graphics2D g ) {
        GraphicsState state = new GraphicsState( g );
        g.transform( getNetTransform() );
        phetImageGraphic.paint( g );
        state.restoreGraphics();
    }

    /**
     * Draw the specified graphic into a PhetImageGraphic.
     *
     * @param phetGraphic
     * @param graphicsSetup
     * @param imageType
     * @param background
     * @return
     */
    public static PhetImageGraphic createBuffer( PhetGraphic phetGraphic, GraphicsSetup graphicsSetup, int imageType, Paint background ) {
        PhetImageGraphic phetImageGraphic = new PhetImageGraphic( phetGraphic.getComponent() );
        Rectangle bounds = phetGraphic.getBounds();
        BufferedImage im = new BufferedImage( bounds.width, bounds.height, imageType );
        Graphics2D g2 = im.createGraphics();
        graphicsSetup.setup( g2 );
        g2.setPaint( background );
        g2.translate( -bounds.x, -bounds.y );
        g2.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );
        phetGraphic.paint( g2 );
        phetImageGraphic.setImage( im );
        return phetImageGraphic;
    }

    public void repaintBuffer( Rectangle rect ) {
        repaintBuffer( rect.x, rect.y, rect.width, rect.height );
    }

    public void repaintBuffer( int x, int y, int width, int height ) {
        Graphics2D g2 = getImage().createGraphics();
        if( background != null ) {
            g2.setPaint( background );
            g2.fillRect( x, y, width, height );
        }

        graphicsSetup.setup( g2 );
        super.paint( g2 );
        setBoundsDirty();
        autorepaint();
    }
}
