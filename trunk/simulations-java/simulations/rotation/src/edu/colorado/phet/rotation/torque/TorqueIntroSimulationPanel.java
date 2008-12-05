package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.rotation.AbstractIntroSimulationPanel;
import edu.colorado.phet.rotation.view.AbstractRotationSimulationPanel;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Oct 5, 2007
 * Time: 1:51:54 PM
 */
public class TorqueIntroSimulationPanel extends AbstractIntroSimulationPanel {
    private TorqueIntroModule introModule;
    private TorqueSimPlayAreaNode playAreaNode;
    private TorqueIntroControlPanel introSimControlPanel;
    private PSwing introSimControlPanelPSwing;

    public TorqueIntroSimulationPanel( TorqueIntroModule introModule, JFrame phetFrame ) {
        super( (JComponent) phetFrame.getContentPane(), introModule.getConstantDtClock(), introModule );
        this.introModule = introModule;
        playAreaNode = new TorqueSimPlayAreaNode( introModule.getTorqueModel(), introModule.getVectorViewModel(), introModule.getAngleUnitModel() );
        playAreaNode.setOriginNodeVisible( false );
        addScreenChild( playAreaNode );

        introSimControlPanel = new TorqueIntroControlPanel( introModule );
        introSimControlPanelPSwing = new PSwing( introSimControlPanel );
        addScreenChild( introSimControlPanelPSwing );
        init( playAreaNode, introSimControlPanelPSwing, introModule.getTorqueModel().getRotationPlatform() );

        updateLayout();

        setBackground( AbstractRotationSimulationPanel.PLAY_AREA_BACKGROUND_COLOR );
    }


}
