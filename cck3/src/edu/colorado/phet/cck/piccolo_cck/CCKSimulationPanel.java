package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.common.SimpleKeyEvent;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Wire;
import edu.colorado.phet.piccolo.PhetPCanvas;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 11:15:22 AM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class CCKSimulationPanel extends PhetPCanvas {
    private CCKModel model;
    private ToolboxNode toolboxNode;
    private CircuitNode circuitNode;
    private MessageNode messageNode;

    public CCKSimulationPanel( CCKModel model ) {
        super( new Dimension( 10, 10 ) );
        this.model = model;
        setBackground( ICCKModule.BACKGROUND_COLOR );

        toolboxNode = new ToolboxNode();
        addScreenChild( toolboxNode );

        circuitNode = new CircuitNode();
        addWorldChild( circuitNode );

        messageNode = new MessageNode();
        addScreenChild( messageNode );

        addKeyListener( new SimpleKeyEvent( KeyEvent.VK_SPACE ) {
            public void invoke() {
                super.invoke();
                addTestCircuit();
            }
        } );
        setWorldScale( 30 );
    }

    private void addTestCircuit() {
        Junction a = new Junction( 5, 5 );
        Junction b = new Junction( 8, 5 );
        Wire component = new Wire( model.getCircuitChangeListener(), a, b );
        model.getCircuit().addBranch( component );
        circuitNode.addGraphic( component );
    }
}
