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
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * An interactive graphic that represents an energy level for a type of atom. It can be moved up and down with the
 * mouse, and it's range of movement is limited by the energy levels of the next higher and next lower energy states.
 */
public class EnergyLevelGraphic extends DefaultInteractiveGraphic implements AtomicState.Listener {
    private AtomicState atomicState;
    private boolean isAdjustable;
    private Color color;
    private double xLoc;
    private double width;
    private EnergyLevelRep energyLevelRep;
    private Rectangle bounds = new Rectangle();
    private ModelViewTx1D energyYTx;

    /**
     * @param component
     * @param atomicState
     * @param color
     * @param xLoc
     * @param width
     * @param isAdjustable
     */
    public EnergyLevelGraphic( final Component component, AtomicState atomicState, Color color, double xLoc, double width,
                               boolean isAdjustable ) {
        super( null );

        this.atomicState = atomicState;
        this.isAdjustable = isAdjustable;
        atomicState.addListener( this );
        this.color = color;
        this.xLoc = xLoc;
        this.width = width;
        energyLevelRep = new EnergyLevelRep( component );
        setBoundedGraphic( energyLevelRep );

        if( isAdjustable ) {
            addCursorHandBehavior();
            addTranslationBehavior( new EnergyLevelTranslator() );
        }
    }

    public void energyLevelChanged( AtomicState.Event event ) {
        energyLevelRep.update();
    }

    public void meanLifetimechanged( AtomicState.Event event ) {
        //noop
    }

    public void update( ModelViewTx1D tx ) {
        this.energyYTx = tx;
        energyLevelRep.update();
    }

    public Point2D getPosition() {
        return energyLevelRep.getBounds().getLocation();
    }

    public Point2D getLinePosition() {
        return energyLevelRep.getLinePosition();
    }


    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Inner class that handles translation of the graphic
     */
    private class EnergyLevelTranslator implements Translatable {
        private int minPixelsBetweenLevels = EnergyLevelMonitorPanel.EnergyLifetimeSlider.sliderHeight;

        public void translate( double dx, double dy ) {
            double energyChange = energyYTx.viewToModelDifferential( (int)dy );
            // Don't let one level get closer than a certain number of pixels to the one above or below
            double minEnergyDifference = energyYTx.viewToModelDifferential( -minPixelsBetweenLevels );
            double newEnergy = Math.max( Math.min( atomicState.getNextHigherEnergyState().getEnergyLevel() - minEnergyDifference,
                                                   atomicState.getEnergyLevel() + energyChange ),
                                         atomicState.getNextLowerEnergyState().getEnergyLevel() + minEnergyDifference );
            double newWavelength = Photon.energyToWavelength( newEnergy );
            // The +1 and -1 are needed to make sure that floating point errors don't put the energy
            // and wavelength out of range
            newWavelength = Math.min( Math.max( newWavelength, LaserConfig.MIN_WAVELENGTH + 1 ),
                                      LaserConfig.MAX_WAVELENGTH - 1 );
            atomicState.setEnergyLevel( Photon.wavelengthToEnergy( newWavelength ) );
            atomicState.determineEmittedPhotonWavelength();
        }
    }

    /**
     * The graphic class itself
     */
    private class EnergyLevelRep extends PhetGraphic {

        private Rectangle2D levelLine = new Rectangle2D.Double();
        private double thickness = 2;
        private Arrow arrow1;
        private Arrow arrow2;
        private Rectangle boundingRect;

        protected EnergyLevelRep( Component component ) {
            super( component );
            color = VisibleColor.wavelengthToColor( atomicState.getWavelength() );
        }

        private void update() {
            color = VisibleColor.wavelengthToColor( atomicState.getWavelength() );

            // We need to create a new color that can't be transparent. VisibleColor will return
            // an "invisible" color with RGB = 0,0,0 if the wavelenght is not visible. And since our
            // ground state has a wavelength that is below visible, and we want a black line, this
            // is the best hack to use.
            color = new Color( color.getRed(), color.getGreen(), color.getBlue() );
            int y = (int)energyYTx.modelToView( atomicState.getEnergyLevel() );
            levelLine.setRect( xLoc, y, width, thickness );

            if( isAdjustable ) {
                double xOffset = width - 50;
                int arrowHt = 16;
                int arrowHeadWd = 10;
                int tailWd = 3;
                arrow1 = new Arrow( new Point2D.Double( xLoc + xOffset, y ),
                                    new Point2D.Double( xLoc + xOffset, y + arrowHt ),
                                    arrowHeadWd, arrowHeadWd, tailWd );
                arrow2 = new Arrow( new Point2D.Double( xLoc + xOffset, y ),
                                    new Point2D.Double( xLoc + xOffset, y - arrowHt ),
                                    arrowHeadWd, arrowHeadWd, tailWd );
            }
            boundingRect = determineBoundsInternal();
            setBoundsDirty();
            repaint();
        }

        protected Rectangle determineBounds() {
            return boundingRect;
        }

        private Rectangle determineBoundsInternal() {
            if( arrow1 != null ) {
                Area a = new Area( arrow1.getShape() );
                a.add( new Area( arrow2.getShape() ) );
                a.add( new Area( levelLine ) );
                return a.getBounds();
            }
            else {
                return levelLine.getBounds();
            }
        }

        public void paint( Graphics2D g ) {
            saveGraphicsState( g );
            if( isAdjustable ) {
                g.setColor( Color.DARK_GRAY );
                g.draw( arrow1.getShape() );
                g.draw( arrow2.getShape() );
            }
            g.setColor( color );
            g.fill( levelLine );
            restoreGraphicsState();
        }

        public Point2D getLinePosition() {
            return levelLine.getBounds().getLocation();
        }
    }
}
