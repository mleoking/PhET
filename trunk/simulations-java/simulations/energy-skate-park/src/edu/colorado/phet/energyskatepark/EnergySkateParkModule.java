// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkOptions;
import edu.colorado.phet.energyskatepark.model.Floor;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkControlPanel;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class EnergySkateParkModule extends AbstractEnergySkateParkModule {
    private final EnergySkateParkControlPanel energySkateParkControlPanel;

    public EnergySkateParkModule( IUserComponent userComponent, String name, PhetFrame phetFrame, EnergySkateParkOptions options, boolean limitNumberOfTracks ) {
        super( userComponent, name, phetFrame, options, true, true, Floor.DEFAULT_FRICTION, true /* hasZoomControls */, 1.0, limitNumberOfTracks );

        energySkateParkControlPanel = new EnergySkateParkControlPanel( this );
        setControlPanel( energySkateParkControlPanel );

        final HelpBalloon trackHelp = new HelpBalloon( getDefaultHelpPane(), EnergySkateParkResources.getString( "help.grab-a-track" ), HelpBalloon.TOP_CENTER, 20 );
        getDefaultHelpPane().add( trackHelp );
        trackHelp.pointAt( energySkateParkSimulationPanel.getRootNode().getSplineToolbox(), energySkateParkSimulationPanel );

        final HelpBalloon skateHelp = new HelpBalloon( getDefaultHelpPane(), EnergySkateParkResources.getString( "help.drag-the-skater" ), HelpBalloon.RIGHT_CENTER, 20 ) {
            public Point2D mapLocation( PNode node, PCanvas canvas ) {
                if ( node == null || node.getParent() == null ) {
                    return new Point2D.Double( 0, 0 );
                }
                else {
                    return super.mapLocation( node, canvas );
                }
            }
        };
        getDefaultHelpPane().add( skateHelp );
        skateHelp.pointAt( energySkateParkSimulationPanel.getRootNode().getSkaterNode( 0 ), energySkateParkSimulationPanel );
        energyModel.addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void bodyCountChanged() {
                if ( energySkateParkSimulationPanel.getRootNode().numBodyGraphics() > 0 ) {
                    skateHelp.pointAt( energySkateParkSimulationPanel.getRootNode().getSkaterNode( 0 ), energySkateParkSimulationPanel );
                }
            }
        } );

        final HelpBalloon trackClickHelp = new HelpBalloon( getDefaultHelpPane(), EnergySkateParkResources.getString( "help.right-click-for-options" ), HelpBalloon.BOTTOM_CENTER, 20 );
        getDefaultHelpPane().add( trackClickHelp );
        trackClickHelp.pointAt( 0, 0 );

        energyModel.addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void preStep() {//todo: won't update while paused
                if ( energyModel.getNumSplines() > 0 && isHelpEnabled() ) {
                    trackHelp.setVisible( isHelpEnabled() );
                    SerializablePoint2D pt = energyModel.getSpline( 0 ).getParametricFunction2D().evaluate( 0.25 );
                    Point2D w = new Point2D.Double( pt.getX(), pt.getY() );
                    energySkateParkSimulationPanel.getRootNode().worldToScreen( w );
                    trackClickHelp.pointAt( w );
                }
                else {
                    trackHelp.setVisible( false );
                }
            }
        } );
    }

    public boolean hasHelp() {
        return true;
    }

    @Override public void reset() {
        super.reset();
        energySkateParkControlPanel.reset();
    }
}
