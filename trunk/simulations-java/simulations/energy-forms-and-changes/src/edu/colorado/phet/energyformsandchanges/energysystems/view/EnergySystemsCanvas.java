// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.energysystems.model.EnergySystemsModel;
import edu.colorado.phet.energyformsandchanges.intro.view.NormalAndFastForwardTimeControlPanel;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.piccolophet.PhetPCanvas.CenteredStage.DEFAULT_STAGE_SIZE;

/**
 * Piccolo canvas for the "Energy Systems" tab of the Energy Forms and Changes
 * simulation.
 *
 * @author John Blanco
 */
public class EnergySystemsCanvas extends PhetPCanvas {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final Dimension2D STAGE_SIZE = CenteredStage.DEFAULT_STAGE_SIZE;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final BooleanProperty normalSimSpeed = new BooleanProperty( true );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public EnergySystemsCanvas( final EnergySystemsModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this ) );

        // Set up the model-canvas transform. IMPORTANT NOTES: The multiplier
        // factors for the 2nd point can be adjusted to shift the center right
        // or left, and the scale factor can be adjusted to zoom in or out
        // (smaller numbers zoom out, larger ones zoom in).
        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( DEFAULT_STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( DEFAULT_STAGE_SIZE.getHeight() * 0.5 ) ),
                1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( new Color( 245, 246, 247 ) );

        //------- Node Creation ----------------------------------------------

        // Create the clock controls. TODO: i18n
        PNode clockControl = new NormalAndFastForwardTimeControlPanel( normalSimSpeed, model.getClock() );
        {
            normalSimSpeed.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean normalSimSpeed ) {
                    ConstantDtClock clock = (ConstantDtClock) model.getClock();
                    clock.setDt( normalSimSpeed ? EFACConstants.SIM_TIME_PER_TICK_NORMAL : EFACConstants.SIM_TIME_PER_TICK_FAST_FORWARD );
                }
            } );
        }

        // Create the back drop for the clock controls.
        PNode clockControlBackground = new PhetPPath( new Rectangle2D.Double( 0, 0, STAGE_SIZE.getWidth() * 2, clockControl.getFullBoundsReference().getHeight() ),
                                                      Color.LIGHT_GRAY,
                                                      new BasicStroke( 1 ),
                                                      Color.BLACK );

        //------- Node Layering -----------------------------------------------

        PNode rootNode = new PNode();
        addWorldChild( rootNode );

        rootNode.addChild( clockControlBackground );
        rootNode.addChild( clockControl );

        //------- Node Layout -------------------------------------------------

        clockControlBackground.setOffset( STAGE_SIZE.getWidth() / 2 - clockControlBackground.getFullBoundsReference().getWidth() / 2,
                                          STAGE_SIZE.getHeight() - clockControlBackground.getFullBoundsReference().getHeight() );
        clockControl.setOffset( STAGE_SIZE.getWidth() / 2 - clockControl.getFullBoundsReference().getWidth() / 2,
                                STAGE_SIZE.getHeight() - clockControl.getFullBoundsReference().height );

    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------
}
