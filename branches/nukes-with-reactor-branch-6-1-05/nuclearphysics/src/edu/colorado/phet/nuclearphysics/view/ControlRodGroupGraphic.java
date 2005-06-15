/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.controller.ControlledFissionModule;
import edu.colorado.phet.nuclearphysics.model.ControlRod;
import edu.colorado.phet.nuclearphysics.model.Vessel;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * ControlRodGroupGraphic
 * <p/>
 * A graphic that includes the control rods, a bar that connects them, and a handle
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlRodGroupGraphic extends DefaultInteractiveGraphic {
    private Rep rep;
    private int orientation;
    private Vessel vessel;
    private AffineTransform atx;
    private AffineTransform invAtx;
    private double rodLength;

    public ControlRodGroupGraphic( Component component, ControlRod[] controlRods, Vessel vessel, AffineTransform atx ) {
        super( null );
        this.vessel = vessel;
        this.atx = atx;
        rep = new Rep( component, controlRods );
        this.rodLength = controlRods[0].getLength();
        setBoundedGraphic( rep );
        addCursorBehavior( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
        addTranslationBehavior( new Translator() );
        orientation = controlRods[0].getOrientation();
    }

    /**
     * Must be overridden because of the transform used to center the graphic
     *
     * @param i
     * @param i1
     * @return
     */
    public boolean contains( int i, int i1 ) {
        Point2D p = new Point2D.Double( i, i1 );
        // Note: if we try to do this in the constructor, atx doesn't work because it is the identity
        // matrix at that time
        if( invAtx == null ) {
            try {
                this.invAtx = atx.createInverse();
            }
            catch( NoninvertibleTransformException e ) {
                e.printStackTrace();
            }
        }
        invAtx.transform( p, p );
        return super.contains( (int)p.getX(), (int)p.getY() );
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * The graphic itself
     */
    private class Rep extends CompositePhetGraphic {
        private Shape connectorShape;
        private Rectangle2D connector = new Rectangle2D.Double();
        private ControlRodGraphic[] rodGraphics;
        private Point location = new Point();
        private ControlRod[] controlRods;
        private PhetImageGraphic handleGraphic;
        private PhetShapeGraphic connectorGraphic;

        public Rep( Component component, ControlRod[] controlRods ) {
            super( component );

            // Graphics for the rods
            this.controlRods = controlRods;
            rodGraphics = new ControlRodGraphic[controlRods.length];
            for( int i = 0; i < controlRods.length; i++ ) {
                ControlRod controlRod = controlRods[i];
                rodGraphics[i] = new ControlRodGraphic( component, controlRod );
                this.addGraphic( rodGraphics[i] );
            }

            // A graphic for the bar connecting the rods
            connectorGraphic = new PhetShapeGraphic( component, connectorShape,
                                                     new Color( 130, 150, 40 ),
//                                                     Color.ORANGE,
//                                                     (Color)rodGraphics[0].getFill(),
                                                     new BasicStroke( 5 ), Color.black );
            this.addGraphic( connectorGraphic );

            // A handle to put on the connector bar
            BufferedImage bi = null;
            try {
                bi = ImageLoader.loadBufferedImage( Config.HANDLE_IMAGE_NAME );
                AffineTransform atx = AffineTransform.getScaleInstance( 1 / ControlledFissionModule.SCALE,
                                                                        1 / ControlledFissionModule.SCALE );
                atx.concatenate( AffineTransform.getRotateInstance( Math.PI, bi.getWidth() / 2, bi.getHeight() / 2 ) );
                AffineTransformOp atxOp = new AffineTransformOp( atx,
                                                                 AffineTransformOp.TYPE_BILINEAR );
                bi = atxOp.filter( bi, null );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            handleGraphic = new PhetImageGraphic( component, bi, 0, 0 );
            this.addGraphic( handleGraphic );

            update();
        }

        private void update() {
            // 150 is for the thickness of the vessel wall
            double connectorWidth = vessel.getBounds().getMaxX()
                                    - rodGraphics[0].getShape().getBounds().getX() + 150;
            location.setLocation( controlRods[0].getBounds().getMinX(),
                                  controlRods[0].getBounds().getMaxY() );
            Rectangle2D horizontalBar = new Rectangle2D.Double( location.getX(), location.getY(), connectorWidth, 150 );
            Rectangle2D verticalBar = new Rectangle2D.Double( location.getX() + connectorWidth,
                                                              controlRods[0].getBounds().getY(), 100,
                                                              horizontalBar.getMaxY() - controlRods[0].getBounds().getY() );
            Area connectorArea = new Area( horizontalBar );
            connectorArea.add( new Area( verticalBar ) );
            connectorShape = connectorArea;
            connectorGraphic.setShape( connectorShape );
            handleGraphic.setPosition( (int)( verticalBar.getBounds().getMaxX() ),
                                       (int)( verticalBar.getBounds().getMinY() + 100 ) );
            setBoundsDirty();
            repaint();
        }

        public void paint( Graphics2D graphics2D ) {
            saveGraphicsState( graphics2D );
            graphics2D.transform( atx );
            super.paint( graphics2D );
            restoreGraphicsState();
        }

        private void translate( double dx, double dy ) {
            for( int i = 0; i < rodGraphics.length; i++ ) {
                rodGraphics[i].translate( dx, dy );
            }
            update();
        }
    }

//    private class ConnectorGraphic extends CompositePhetGraphic {
//        pr
//        public ConnectorGraphic( Component component ) {
//            super( component );
//        }
//    }


    /**
     * Translation handler
     */
    private class Translator implements Translatable {

        /**
         * Constrains the translation of the graphic to not go too far into the vessel
         *
         * @param dx
         * @param dy
         */
        public void translate( double dx, double dy ) {
            switch( orientation ) {
                case ControlledFissionModule.HORIZONTAL:
                    dy = 0;
                    if( rep.location.getX() + dx <= vessel.getX() + rodLength ) {
                        dx = 0;
                    }
                case ControlledFissionModule.VERTICAL:
                    dx = 0;
                    if( dy < 0 && rep.location.getY() - rodLength + ( dy / atx.getScaleY() ) <= vessel.getY() ) {
                        dy = ( vessel.getY() - ( rep.location.getY() - rodLength ) ) * atx.getScaleY();
                    }
                    else if( dy > 0 && rep.location.getY() - rodLength + ( dy / atx.getScaleY() )
                                       >= vessel.getY() + vessel.getHeight() ) {
                        dy = ( vessel.getY() - rep.location.getY() + ( rodLength * 2 ) ) * atx.getScaleY();
                    }
            }
            // Have to account for the SCALE of the affine transform before telling the model element to move
            rep.translate( dx / atx.getScaleX(), dy / atx.getScaleY() );
        }
    }
}
