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

import edu.colorado.phet.common.math.ModelViewTx1D;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.lasers.coreadditions.VisibleColor;
import edu.colorado.phet.lasers.model.atom.AtomicState;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class EnergyLevelGraphic extends DefaultInteractiveGraphic implements AtomicState.EnergyLevelChangeListener {
    private AtomicState atomicState;
    private Color color;
    private double xLoc;
    private double yLoc;
    private double width;
    private EnergyLevelRep energyLevelRep;
    private Rectangle bounds = new Rectangle();
    private ModelViewTx1D energyYTx = new ModelViewTx1D( AtomicState.maxEnergy, AtomicState.minEnergy, 0, 0 );

    public EnergyLevelGraphic( final Component component, AtomicState atomicState, Color color, double xLoc, double width ) {
        super( null );
        this.atomicState = atomicState;
        this.color = color;
        this.xLoc = xLoc;
        this.width = width;
        energyLevelRep = new EnergyLevelRep( component );
        setBoundedGraphic( energyLevelRep );

        addCursorBehavior( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
        addTranslationBehavior( new EnergyLevelTranslator() );

        atomicState.addEnergyLevelChangeListener( this );

        // Add a listener that will recompute, when the component in which we are
        // placed changes size, our graphic bounds and the transform between
        // energy values from the model and graphic coordinates
        component.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                Rectangle compRect = component.getBounds();
                bounds.setRect( compRect.getMinX(), compRect.getMinY() + compRect.getHeight() * 0.1,
                                compRect.getWidth(), compRect.getHeight() * 0.8 );
                energyYTx = new ModelViewTx1D( AtomicState.maxEnergy, AtomicState.minEnergy,
                                               (int)bounds.getMinY(), (int)bounds.getMaxY() );
            }
        } );
    }

    public void setBasePosition( double x, double y ) {
        xLoc = x;
        yLoc = y;
    }

    public void energyLevelChangeOccurred( AtomicState.EnergyLevelChangeEvent event ) {
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
        private int minPixelsBetweenLevels = 50;

        public void translate( double dx, double dy ) {
            double energyChange = energyYTx.viewToModelDifferential( (int)dy );
            // Don't let one level get closer than a certain number of pixels to the one above or below
            double minEnergyDifference = energyYTx.viewToModelDifferential( -minPixelsBetweenLevels );
            double newEnergy = Math.max( Math.min( atomicState.getNextHigherEnergyState().getEnergyLevel() - minEnergyDifference,
                                                   atomicState.getEnergyLevel() + energyChange ),
                                         atomicState.getNextLowerEnergyState().getEnergyLevel() + minEnergyDifference );
            atomicState.setEnergyLevel( newEnergy );
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
            color = VisibleColor.wavelengthToColor( atomicState.getWavelength() );
            // We need to create a new color that can't be transparent. VisibleColor will return
            // an "invisible" color with RGB = 0,0,0 if the wavelenght is not visible. And since our
            // ground state has a wavelength that is below visible, and we want a black line, this
            // is the best hack to use.
            color = new Color( color.getRed(), color.getGreen(), color.getBlue() );
            int y = (int)Math.pow( energyYTx.modelToView( atomicState.getEnergyLevel() ), .98 );
            levelLine.setRect( xLoc, y, width, thickness );
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
