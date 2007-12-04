package edu.colorado.phet.rotation;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
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

    public RotationIntroSimulationPanel( RotationIntroModule introModule,JFrame frame ) {
        super( (JComponent) frame.getContentPane(),introModule);
        this.introModule = introModule;
        playAreaNode = new RotationSimPlayAreaNode( introModule.getRotationModel(), introModule.getVectorViewModel(), introModule.getAngleUnitModel() );
        playAreaNode.setOriginNodeVisible( false );
        addScreenChild( playAreaNode );

        introSimControlPanel = new RotationIntroControlPanel( introModule,playAreaNode );
        introSimControlPanelPSwing = new PSwing( introSimControlPanel );
        addScreenChild( introSimControlPanelPSwing );

        init( playAreaNode, introSimControlPanelPSwing, introModule.getRotationModel().getRotationPlatform() );

        updateLayout();
        setBackground( AbstractRotationSimulationPanel.PLAY_AREA_BACKGROUND_COLOR );
        
        final Timer timer=new Timer( introModule.getRotationClock().getDelay(), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                doPaintImm();
            }
        } );
        introModule.addListener( new Module.Listener() {
            public void activated() {
                timer.start();
            }

            public void deactivated() {
                timer.stop();
            }
        } );
    }
    public void doPaintImm(){
        super.paintImmediately( 0,0,getWidth(), getHeight() );
//        System.out.println( "RotationIntroSimulationPanel.doPaintImm" );
    }

    public void paintImmediately() {
//        super.paintImmediately();
    }

    public void paintImmediately( int x, int y, int w, int h ) {
//        super.paintImmediately( x, y, w, h );
    }

    public void paintImmediately( Rectangle r ) {
//        super.paintImmediately( r );
    }

    public RotationSimPlayAreaNode getPlayAreaNode() {
        return playAreaNode;
    }
}