package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * User: Sam Reid
 * Date: Dec 13, 2006
 * Time: 7:20:46 AM
 * Copyright (c) Dec 13, 2006 by Sam Reid
 */

public class CCKBackground extends PNode {
    private CCKModel model;
    private CCKSimulationPanel cckSimulationPanel;
    private PPath path;

    public CCKBackground( final CCKModel model, CCKSimulationPanel cckSimulationPanel ) {
        this.model = model;
        this.cckSimulationPanel = cckSimulationPanel;
        path = new PhetPPath( ICCKModule.BACKGROUND_COLOR );
        addChild( path );
        cckSimulationPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                update();
            }

            public void componentShown( ComponentEvent e ) {
                update();
            }
        } );
        update();
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                System.out.println( "CCKBackground.mousePressed" );
                model.clearSelection();
            }
        } );
    }

    private void update() {
        path.setPathToRectangle( 0, 0, cckSimulationPanel.getWidth(), cckSimulationPanel.getHeight() );
    }
}
