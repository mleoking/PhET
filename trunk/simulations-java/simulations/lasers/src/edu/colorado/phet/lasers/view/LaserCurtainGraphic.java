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

import java.awt.*;

import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsUtil;
import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.lasers.controller.LasersConfig;
import edu.colorado.phet.lasers.model.LaserModel;

/**
 * LaserCurtainGraphic
 * <p/>
 * An overlay on the laser that fills the laser cavity with color. Alpha is proportional to
 * the amplitude of the lasing
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LaserCurtainGraphic extends PhetShapeGraphic implements AtomicState.Listener, LaserModel.ChangeListener {
    private Rectangle beamBounds = new Rectangle();
    private Color color = Color.white;
    private AtomicState[] atomicStates;
    private double level;
    private int numLasingPhotons;
    private double maxAlpha;
    private double alpha;
    private LaserModel model;

    /**
     * @param component
     * @param beamShape
     * @param laserModel
     * @param atomicStates
     * @param maxAlpha
     */
    public LaserCurtainGraphic( Component component,
                                Shape beamShape,
                                LaserModel laserModel,
                                AtomicState[] atomicStates,
                                double maxAlpha ) {
        super( component, null, null );
        this.model = laserModel;
        this.atomicStates = atomicStates;
        this.maxAlpha = maxAlpha;
        beamBounds.setRect( beamShape.getBounds() );
        setShape( beamShape );
        setColor( color );
        update();

        laserModel.addLaserListener( this );
        atomicStates[1].addListener( this );
        atomicStates[0].addListener( this );
    }

    public void setMaxAlpha( double alpha ) {
        this.maxAlpha = alpha;
    }

    private void update() {
        // Determine the proper opacity of the shape's fill color
        level = numLasingPhotons > LasersConfig.LASING_THRESHOLD ? numLasingPhotons : 0;
        alpha = ( level / LasersConfig.KABOOM_THRESHOLD ) * maxAlpha;

        double de = atomicStates[1].getEnergyLevel() - atomicStates[0].getEnergyLevel();
        Color color = VisibleColor.wavelengthToColor( PhysicsUtil.energyToWavelength( de ) );

        setColor( color );
        setBoundsDirty();
        repaint();
    }

    public void paint( Graphics2D g2 ) {
        if ( alpha > 0 ) {
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
        atomicStates[1].removeListener( this );
        atomicStates[0].removeListener( this );
        atomicStates = new AtomicState[]{model.getGroundState(),
                model.getMiddleEnergyState()};
        atomicStates[1].addListener( this );
        atomicStates[0].addListener( this );
        update();
    }

    public void atomicStatesChanged( LaserModel.ChangeEvent event ) {
        atomicStates[1].removeListener( this );
        atomicStates[0].removeListener( this );
        atomicStates = new AtomicState[]{model.getGroundState(),
                model.getMiddleEnergyState()};
        atomicStates[1].addListener( this );
        atomicStates[0].addListener( this );
        update();
    }

    public void meanLifetimechanged( AtomicState.Event event ) {
    }

    public void lasingPopulationChanged( LaserModel.ChangeEvent event ) {
        numLasingPhotons = event.getLasingPopulation();
        update();
    }
}
