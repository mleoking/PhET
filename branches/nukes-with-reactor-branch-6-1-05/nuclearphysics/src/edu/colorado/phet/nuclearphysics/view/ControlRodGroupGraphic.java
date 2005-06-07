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
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.nuclearphysics.controller.ControlledFissionModule;
import edu.colorado.phet.nuclearphysics.model.ControlRod;
import edu.colorado.phet.nuclearphysics.model.Vessel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * ControlRodGroupGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlRodGroupGraphic extends DefaultInteractiveGraphic {
    private Rep rep;
    private int orientation;
    private Vessel vessel;
    private PhysicalPanel panel;
    private AffineTransform atx;
    private AffineTransform invAtx;
    private double rodLength;

    public ControlRodGroupGraphic( Component component, ControlRod[] controlRods, Vessel vessel, AffineTransform atx ) {
        super( null );
        this.panel = (PhysicalPanel)component;
        this.vessel = vessel;
        this.atx = atx;
        rep = new Rep( component, controlRods );
        this.rodLength = controlRods[0].getLength();
        setBoundedGraphic( rep );
        addCursorHandBehavior();
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
        private Rectangle2D connector = new Rectangle2D.Double();
        private ControlRodGraphic[] rodGraphics;
        private Point location = new Point();
        private ControlRod[] controlRods;

        public Rep( Component component, ControlRod[] controlRods ) {
            super( component );
            this.controlRods = controlRods;
            rodGraphics = new ControlRodGraphic[controlRods.length];
            for( int i = 0; i < controlRods.length; i++ ) {
                ControlRod controlRod = controlRods[i];
                rodGraphics[i] = new ControlRodGraphic( component, controlRod );
                this.addGraphic( rodGraphics[i] );
            }
            PhetShapeGraphic connectorGraphic = new PhetShapeGraphic( component, connector,
                                                                      (Color)rodGraphics[0].getFill(),
                                                                      new BasicStroke( 5 ), Color.black);
            this.addGraphic( connectorGraphic );
            update();
        }

        private void update() {
            double connectorWidth = rodGraphics[rodGraphics.length - 1].getShape().getBounds().getX()
                                    - rodGraphics[0].getShape().getBounds().getX()
                                    + rodGraphics[rodGraphics.length - 1].getShape().getBounds().getWidth();
            location.setLocation( controlRods[0].getBounds().getMinX(),
                                  controlRods[0].getBounds().getMaxY() );
            connector.setRect( location.getX(), location.getY(), connectorWidth, 100 );
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
                    if( dy < 0 && rep.location.getY() + dy <= vessel.getY() + rodLength ) {
                        dy = 0;
                    }
                    if( dy > 0 && rep.location.getY() + dy >= vessel.getY() + rodLength * 2 ) {
                        dy = 0;
                    }
            }
            // Have to account for the SCALE of the affine transform before telling the model element to move
            rep.translate( dx / atx.getScaleX(), dy / atx.getScaleY() );
        }
    }
}
