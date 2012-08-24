// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.common.view.stove.HeaterCoolerWithLimitsNode;
import edu.colorado.phet.energyformsandchanges.intro.model.Burner;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.intro.view.EnergyChunkNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.energyformsandchanges.common.view.stove.HeaterCoolerWithLimitsNode.HeatCoolMode;

/**
 * Piccolo node that represents a burner in the view.
 *
 * @author John Blanco
 */
public class BurnerNode extends PNode {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final Stroke BURNER_STAND_STROKE = new BasicStroke( 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Color BURNER_STAND_STROKE_COLOR = Color.BLACK;
    private static final double PERSPECTIVE_ANGLE = Math.PI / 4; // Positive is counterclockwise, a value of 0 produces a non-skewed rectangle.
    private static final double BURNER_EDGE_LENGTH = 30;
    private static final double MODE_CHANGE_LOCKOUT_TIME = 3; // In seconds.

    // For debug purposes.
    private static final boolean SHOW_2D_RECT = false;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    protected final HeaterCoolerWithLimitsNode heaterCoolerNode;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public BurnerNode( final Burner burner, final ModelViewTransform mvt ) {

        if ( SHOW_2D_RECT ) {
            addChild( new PhetPPath( mvt.modelToViewRectangle( burner.getOutlineRect() ), new BasicStroke( 1 ), Color.RED ) );
        }

        // Get a version of the rectangle that defines the burner size and
        // location in the view.
        final Rectangle2D burnerViewRect = mvt.modelToView( burner.getOutlineRect() ).getBounds2D();

        // Add the heater-cooler node to the center bottom.
        // TODO: i18n
        heaterCoolerNode = new HeaterCoolerWithLimitsNode( burner.heatCoolLevel, "Heat", "Cool" ) {{
            setScale( mvt.modelToViewDeltaX( burner.getOutlineRect().getWidth() ) * 0.7 / getFullBoundsReference().width );
            setOffset( burnerViewRect.getX() + burnerViewRect.getWidth() / 2 - getFullBoundsReference().width / 2,
                       burnerViewRect.getMaxY() - getFullBoundsReference().height * 0.9 );
        }};
        addChild( heaterCoolerNode );

        // Set up the lockout timer that prevents the heat/cool mode from
        // changing too rapidly.
        final Timer heatCoolModeChangeLockoutTimer = new Timer( (int) ( MODE_CHANGE_LOCKOUT_TIME * 1000 ), null );
        heatCoolModeChangeLockoutTimer.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateHeatCoolMode( burner.heatCoolLevel.getBoundsProperty().get() );
                heatCoolModeChangeLockoutTimer.stop();
            }
        } );

        // Update the mode of the heater cooler based on the limits on the burner.
        burner.heatCoolLevel.getBoundsProperty().addObserver( new VoidFunction1<DoubleRange>() {
            public void apply( DoubleRange allowedRange ) {
                if ( !heatCoolModeChangeLockoutTimer.isRunning() ) {

                    // Change is not locked out, so an update is allowed.
                    HeatCoolMode modeBeforeUpdate = heaterCoolerNode.heatCoolMode.get();
                    updateHeatCoolMode( allowedRange );
                    if ( heaterCoolerNode.heatCoolMode.get() != modeBeforeUpdate ) {

                        // A mode change occurred, so start the lockout timer.
                        heatCoolModeChangeLockoutTimer.restart();
                    }
                }
                // else ignore the change and let the timer handle it.
            }
        } );

        // Update heat/cool mode when elements are added to or removed from burner.
        burner.getIsSomethingOnTopProperty().addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean isSomethingOnBurner ) {
                if ( !isSomethingOnBurner ) {
                    // Whatever was on burner was removed, so clear limits.
                    heatCoolModeChangeLockoutTimer.stop();
                    updateHeatCoolMode( burner.heatCoolLevel.getBoundsProperty().get() );
                }
            }
        } );

        // Add the stand that goes around and above the burner.
        addChild( new BurnerStandNode( burnerViewRect, burnerViewRect.getHeight() * 0.2 ) );

        // Watch for energy chunks coming and going and add/remove nodes accordingly.
        burner.energyChunkList.addElementAddedObserver( new VoidFunction1<EnergyChunk>() {
            public void apply( final EnergyChunk addedEnergyChunk ) {
                final PNode energyChunkNode = new EnergyChunkNode( addedEnergyChunk, mvt );
                addChild( energyChunkNode );
                burner.energyChunkList.addElementRemovedObserver( new VoidFunction1<EnergyChunk>() {
                    public void apply( EnergyChunk removedEnergyChunk ) {
                        if ( removedEnergyChunk == addedEnergyChunk ) {
                            removeChild( energyChunkNode );
                            burner.energyChunkList.removeElementRemovedObserver( this );
                        }
                    }
                } );
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    private void updateHeatCoolMode( DoubleRange allowedRange ) {
        if ( allowedRange.getMin() == 0 && heaterCoolerNode.heatCoolMode.get() != HeatCoolMode.HEAT_ONLY ) {
            heaterCoolerNode.heatCoolMode.set( HeatCoolMode.HEAT_ONLY );
        }
        else if ( allowedRange.getMax() == 0 && heaterCoolerNode.heatCoolMode.get() != HeatCoolMode.COOL_ONLY ) {
            heaterCoolerNode.heatCoolMode.set( HeatCoolMode.COOL_ONLY );
        }
        else if ( allowedRange.getMin() < 0 && allowedRange.getMax() > 0 && heaterCoolerNode.heatCoolMode.get() != HeatCoolMode.HEAT_AND_COOL ) {
            heaterCoolerNode.heatCoolMode.set( HeatCoolMode.HEAT_AND_COOL );
        }
    }
}
