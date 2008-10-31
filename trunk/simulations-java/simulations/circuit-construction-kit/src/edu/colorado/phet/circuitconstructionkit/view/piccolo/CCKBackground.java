package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * User: Sam Reid
 * Date: Dec 13, 2006
 * Time: 7:20:46 AM
 */

public class CCKBackground extends PNode {
    private CCKModel model;
    private CCKSimulationPanel cckSimulationPanel;
    private PPath path;

    public CCKBackground( final CCKModel model, CCKSimulationPanel cckSimulationPanel ) {
        this.model = model;
        this.cckSimulationPanel = cckSimulationPanel;
        path = new PhetPPath( CCKModule.BACKGROUND_COLOR );
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
                model.clearSelection();
            }
        } );
    }

    private void update() {
        path.setPathToRectangle( 0, 0, cckSimulationPanel.getWidth(), cckSimulationPanel.getHeight() );
    }
}
