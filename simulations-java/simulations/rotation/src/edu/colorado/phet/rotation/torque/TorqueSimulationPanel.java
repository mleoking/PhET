package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.view.AbstractRotationSimulationPanel;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:02:17 AM
 */
public class TorqueSimulationPanel extends AbstractRotationSimulationPanel {

    public TorqueSimulationPanel( final AbstractTorqueModule torqueModule, JFrame parentFrame ) {
        super( torqueModule, parentFrame );
    }

//    private void addClearTorqueButton( final AbstractTorqueModule torqueModule ) {
//        final PSwing clearTorqueButton = new PSwing( new JButton( "Clear Torque" ) );
//        torqueModule.getTorqueModel().addListener( new TorqueModel.Adapter() {
//            public void appliedForceChanged() {
//                clearTorqueButton.setVisible( torqueModule.getTorqueModel().getAppliedForceMagnitude() > 0 );
//            }
//        } );
//        addScreenChild( clearTorqueButton );
//        Point2D d = torqueModule.getTorqueModel().getRotationPlatform().getCenter();
//        Point2D loc = new Point2D.Double( d.getX(), d.getY() );
//        getPhetRootNode().worldToScreen( loc );
//        clearTorqueButton.setOffset( loc );
//    }

    protected JComponent createControlPanel( RulerNode rulerNode, JFrame parentFrame ) {
        return new TorqueControlPanel( rulerNode, getRotationGraphSet(), getGraphSetModel(), (AbstractTorqueModule) getAbstractRotationModule(), getVectorViewModel() );//todo: better typing
    }

    protected GraphSuiteSet createRotationGraphSet() {
        return new TorqueGraphSet( this, (TorqueModel) getRotationModel(), getAngleUnitModel() );
    }

    protected RotationPlayAreaNode createPlayAreaNode() {
        return new TorqueSimPlayAreaNode( (TorqueModel) getRotationModel(), getVectorViewModel(), getAngleUnitModel() );
    }
}
