/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.AtomicState;

import java.awt.*;

/**
 * LaserCurtainGraphic
 * <p/>
 * An overlay on the laser that fills the laser cavity with color. Alpha is proportional to
 * the amplitude of the lasing
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LaserCurtainGraphic extends PhetShapeGraphic implements AtomicState.Listener, LaserModel.LaserListener {
    private Shape beamShape;
    private Rectangle beamBounds = new Rectangle();
    private Color color = Color.white;
    private AtomicState atomicState;
    private double level;
    private int numLasingPhotons;
    private double maxAlpha;
    private double alpha;

    /**
     * @param component
     * @param beamShape
     * @param laserModel
     * @param atomicState
     * @param maxAlpha
     */
    public LaserCurtainGraphic( Component component, Shape beamShape, LaserModel laserModel, AtomicState atomicState, double maxAlpha ) {
//    public LaserCurtainGraphic( Component component, Rectangle bounds, LaserModel laserModel, AtomicState atomicState, double maxAlpha ) {
        super( component, null, null );
        this.atomicState = atomicState;
        this.maxAlpha = maxAlpha;
        beamBounds.setRect( beamShape.getBounds() );
        setShape( beamShape );
//        setShape( beamBounds );
        setColor( color );

        laserModel.addLaserListener( this );
        atomicState.addListener( this );
    }

    public void setMaxAlpha( double alpha ) {
        this.maxAlpha = alpha;
    }

    private void update() {
        level = numLasingPhotons > LaserConfig.LASING_THRESHOLD ? numLasingPhotons : 0;
        alpha = ( level / LaserConfig.KABOOM_THRESHOLD ) * maxAlpha;
        setColor( VisibleColor.wavelengthToColor( atomicState.getWavelength() ) );
        // The power function here controls the ramp-up of actualColor intensity
        double rampUpExponent = .5;
        level = Math.max( maxAlpha, 255 - (int)( ( 255 - maxAlpha ) * Math.pow( ( level / LaserConfig.LASING_THRESHOLD ), rampUpExponent ) ) );
        this.level = Math.min( level, 255 );
        setBoundsDirty();
    }

    public void paint( Graphics2D g2 ) {
        if( alpha > 0 ) {
            saveGraphicsState( g2 );
            GraphicsUtil.setAlpha( g2, alpha );
            super.paint( g2 );

            restoreGraphicsState();
        }
//        g2.setColor( Color.green );
//        g2.draw( this.getBounds() );
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    public void energyLevelChanged( AtomicState.Event event ) {
        update();
    }

    public void meanLifetimechanged( AtomicState.Event event ) {
    }

    public void lasingPopulationChanged( LaserModel.LaserEvent event ) {
        numLasingPhotons = event.getLasingPopulation();
        update();
    }
}
