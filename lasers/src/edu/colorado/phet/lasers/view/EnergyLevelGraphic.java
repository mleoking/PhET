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

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class EnergyLevelGraphic extends DefaultInteractiveGraphic implements AtomicState.EnergyLevelChangeListener {
    private AtomicState atomicState;
    private Color color;
    private double xLoc;
    private double yLoc;
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

        addCursorBehavior( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
        addTranslationBehavior( new EnergyLevelTranslator() );

        atomicState.addEnergyLevelChangeListener( this );
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
        public void translate( double dx, double dy ) {
            atomicState.setEnergyLevel( atomicState.getEnergyLevel() - dy );
        }
    }

    /**
     * The graphic class itself
     */
    private static double maxEnergy = 200;
    private static double minEnergy = 0;
    private static double maxBlueLevel = maxEnergy;
    private static double maxRedLevel = maxEnergy / 2;
    private static int numColors = (int)maxEnergy;
    private static Color[] colors = new Color[numColors];

    static {
        for( int i = 0; i < numColors; i++ ) {
            int red = ( numColors / 2 ) - Math.abs( i - numColors / 2 );
            int green = 0;
            int blue = Math.max( 0, ( i - numColors / 2 ) );
            colors[i] = new Color( red, green, blue );
        }
    }

    private class EnergyLevelRep extends PhetGraphic {


        private Rectangle2D levelLine = new Rectangle2D.Double();
        private double thickness = 2;

        protected EnergyLevelRep( Component component ) {
            super( component );
            update();
        }

        private void update() {

            int blueLevel = (int)Math.max( 0, Math.min( 255 * ( atomicState.getEnergyLevel() - maxRedLevel ) / ( maxBlueLevel - maxRedLevel ), 255 ) );
            int redLevel = (int)Math.max( 0, Math.min( 255 * ( atomicState.getEnergyLevel() - minEnergy ) / ( maxRedLevel - minEnergy ), 255 ) );
            color = new Color( redLevel, 0, blueLevel );

            color = colors[(int)Math.min( ( atomicState.getEnergyLevel() / maxEnergy ) * numColors, maxEnergy - 1 )];
            //            color = colors[ (int)Math.min( atomicState.getEnergyLevel(), maxEnergy - 1)];

            double y = yLoc - atomicState.getEnergyLevel();
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
