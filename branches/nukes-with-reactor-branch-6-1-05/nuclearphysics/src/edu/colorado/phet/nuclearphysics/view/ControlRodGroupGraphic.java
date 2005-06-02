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
import edu.colorado.phet.nuclearphysics.model.ControlRod;
import edu.colorado.phet.nuclearphysics.model.Vessel;
import edu.colorado.phet.nuclearphysics.controller.ControlledFissionModule;

import java.awt.*;
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

    public ControlRodGroupGraphic( Component component, ControlRod[] controlRods, Vessel vessel ) {
        super( null );
        this.vessel = vessel;
        rep = new Rep( component, controlRods );
        setBoundedGraphic( rep );
        addCursorHandBehavior();
        addTranslationBehavior( new Translator() );
        orientation = controlRods[0].getOrientation();
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
                                                                      (Color)rodGraphics[0].getFill() );
            this.addGraphic( connectorGraphic );
            update();
        }

        private void update() {
            double connectorWidth = rodGraphics[rodGraphics.length - 1].getShape().getBounds().getX()
                                    - rodGraphics[0].getShape().getBounds().getX()
                                    + rodGraphics[rodGraphics.length - 1].getShape().getBounds().getWidth();
            location.setLocation( controlRods[0].getBounds().getMinX(),
                                  controlRods[0].getBounds().getMaxY() );
            connector.setRect( location.getX(), location.getY(), connectorWidth, 20 );
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
         * @param dx
         * @param dy
         */
        public void translate( double dx, double dy ) {
            switch( orientation ) {
                case ControlledFissionModule.HORIZONTAL:
                    dy = 0;
                    if( rep.location.getX() + dx <= vessel.getX() + vessel.getWidth() ) {
                        dx = 0;
                    }
                case ControlledFissionModule.VERTICAL:
                    dx = 0;
                    if( rep.location.getY() + dy <= vessel.getY() + vessel.getHeight() ) {
                        dy = 0;
                    }
            }
            rep.translate( dx, dy );
        }
    }
}
