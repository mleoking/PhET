/**
 * Class: EnergyLevelGraphic
 * Package: edu.colorado.phet.lasers.view
 * Original Author: Ron LeMaster
 * Creation Date: Nov 2, 2004
 * Creation Time: 2:00:59 PM
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.EventRegistry;
import edu.colorado.phet.mechanics.Vector3D;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

public class EnergyLevelGraphic extends DefaultInteractiveGraphic implements AtomicState.EnergyLevelListener {
    private AtomicState atomicState;
    private Color color;
    private double xLoc;
    private double width;
    private EnergyLevelRep energyLevelRep;

    public EnergyLevelGraphic( Component component, AtomicState atomicState, Color color, double xLoc, double width ) {
        super( null );
        this.atomicState = atomicState;
        this.color = color;
        this.xLoc = xLoc;
        this.width = width;
        energyLevelRep = new EnergyLevelRep( component );
        setBoundedGraphic( energyLevelRep );

        addCursorBehavior( Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR ));
        addTranslationBehavior( new EnergyLevelTranslator() );

        EventRegistry.instance.addListener( this );
    }

    public void energyLevelChangeOccurred( AtomicState.EnergyLevelChange event ) {
        energyLevelRep.update();
    }

    public void update() {
        energyLevelRep.update();
    }

    public Point2D getPosition() {
        return energyLevelRep.getBounds().getLocation();
    }


    /**
     * Inner class that handles translation of the graphic
     */
    private class EnergyLevelTranslator implements Translatable {
        public void translate( double dx, double dy ) {
            atomicState.setEnergyLevel( atomicState.getEnergyLevel() - dy );
        }
    }

    /**
     * The graphic class itself
     */
    private class EnergyLevelRep extends PhetGraphic {
        private Rectangle2D levelLine = new Rectangle2D.Double();
        private double thickness = 2;

        protected EnergyLevelRep( Component component ) {
            super( component );
            update();
        }

        private void update() {
            Rectangle frameOfReference = getComponent().getBounds();
            double yLoc = frameOfReference.getY() + frameOfReference.getHeight() - atomicState.getEnergyLevel();
            levelLine.setRect( xLoc, yLoc, width, thickness );
            setBoundsDirty();
            repaint();
        }

        protected Rectangle determineBounds() {
            return levelLine.getBounds();
        }

        public void paint( Graphics2D g ) {
            saveGraphicsState( g );
            g.setColor( color );
            g.fill( levelLine );
            restoreGraphicsState();
        }
    }
}
