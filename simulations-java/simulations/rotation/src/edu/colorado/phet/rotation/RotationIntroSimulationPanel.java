package edu.colorado.phet.rotation;

import edu.colorado.phet.rotation.view.AbstractRotationSimulationPanel;
import edu.colorado.phet.rotation.view.RotationSimPlayAreaNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Oct 5, 2007
 * Time: 1:51:54 PM
 */
public class RotationIntroSimulationPanel extends AbstractIntroSimulationPanel {
    private RotationIntroModule introModule;
    private RotationSimPlayAreaNode playAreaNode;
    private RotationIntroControlPanel introSimControlPanel;
    private PSwing introSimControlPanelPSwing;

    public RotationIntroSimulationPanel( RotationIntroModule introModule ) {
        this.introModule = introModule;
        playAreaNode = new RotationSimPlayAreaNode( introModule.getRotationModel(), introModule.getVectorViewModel(), introModule.getAngleUnitModel() );
        playAreaNode.setOriginNodeVisible( false );
        addScreenChild( playAreaNode );

        introSimControlPanel = new RotationIntroControlPanel( introModule );
        introSimControlPanelPSwing = new PSwing( introSimControlPanel );
        addScreenChild( introSimControlPanelPSwing );

        init( playAreaNode, introSimControlPanelPSwing, introModule.getRotationModel().getRotationPlatform() );

        updateLayout();
        setBackground( AbstractRotationSimulationPanel.PLAY_AREA_BACKGROUND_COLOR );
    }
}