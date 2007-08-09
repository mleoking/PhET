package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.AbstractRotationSimulationPanel;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.graphs.TorqueGraphSet;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.geom.Point2D;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:02:17 AM
 */
public class TorqueSimulationPanel extends AbstractRotationSimulationPanel {

    public TorqueSimulationPanel( final TorqueModule torqueModule, JFrame parentFrame ) {
        super( torqueModule, parentFrame );
//        addClearTorqueButton( torqueModule );
    }

    private void addClearTorqueButton( final TorqueModule torqueModule ) {
        final PSwing clearTorqueButton = new PSwing( new JButton( "Clear Torque" ) );
        torqueModule.getTorqueModel().addListener( new TorqueModel.Listener() {
            public void appliedForceChanged() {
                clearTorqueButton.setVisible( torqueModule.getTorqueModel().getAppliedForceMagnitude() > 0 );
            }
        } );
        addScreenChild( clearTorqueButton );
        Point2D d = torqueModule.getTorqueModel().getRotationPlatform().getCenter();
        Point2D loc = new Point2D.Double( d.getX(), d.getY() );
        getPhetRootNode().worldToScreen( loc );
        clearTorqueButton.setOffset( loc );
    }

    protected JComponent createControlPanel( RulerNode rulerNode, JFrame parentFrame ) {
        return new TorqueControlPanel( getRotationGraphSet(), getGraphSetModel(), (TorqueModule)getAbstractRotationModule() );//todo: better typing
    }

    protected GraphSuiteSet createRotationGraphSet() {
        return new TorqueGraphSet( this, (TorqueModel)getRotationModel(), getAngleUnitModel() );
    }

    protected RotationPlayAreaNode createPlayAreaNode() {
        return new TorqueSimPlayAreaNode( (TorqueModel)getRotationModel(), new VectorViewModel(), getAngleUnitModel() );
    }
}
