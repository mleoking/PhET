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

    private class Rep extends CompositePhetGraphic {
        public Rep( Component component, ControlRod[] controlRods ) {
            super( component );
            ControlRodGraphic controlRodGraphic = null;
            for( int i = 0; i < controlRods.length; i++ ) {
                ControlRod controlRod = controlRods[i];
                controlRodGraphic = new ControlRodGraphic( component, controlRod );
                this.addGraphic( controlRodGraphic );
            }
            Rectangle2D connector = new Rectangle2D.Double( controlRods[0].getShape().getBounds().getX(),
                                                 controlRods[0].getShape().getBounds().getY(),
                                                 controlRods[controlRods.length].getShape().getBounds().getX() - controlRods[0].getShape().getBounds().getX(),
                                                 20 );
            PhetShapeGraphic connectorGraphic = new PhetShapeGraphic( component, connector,
                                                                      (Color)controlRodGraphic.getFill() );
            this.addGraphic( connectorGraphic );
        }

        private void translate( double dx, double dy ) {
            for( int i = 0; i < this.numGraphics(); i++ ){
                ((ControlRodGraphic)graphicAt( i )).translate( dx, dy );
            }
        }
    }

    private class Translator implements Translatable {
        public void translate( double dx, double dy ) {
            dx = orientation == ControlledFissionModule.HORIZONTAL ? dx : 0;
            dy = orientation == ControlledFissionModule.VERTICAL ? dy : 0;
            double repMaxX = rep.getBounds().getMaxX();
            double repMinX = rep.getBounds().getMinX();
            double repMaxY = rep.getBounds().getMaxY();
            double repMinY = rep.getBounds().getMinY();
            dy = Math.min( repMinY - vessel.getY(), dy  );
            rep.translate( dx, dy );
        }
    }
}
