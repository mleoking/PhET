// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.common.view;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas for "Fractions Intro" sim, used in each of the tabs.
 *
 * @author Sam Reid
 */
public class AbstractFractionsCanvas extends PhetPCanvas {

    public static final Font CONTROL_FONT = new PhetFont( 20 );

    //Stage where nodes are added and scaled up and down
    protected final PNode rootNode;

    //Size for the stage, should have the right aspect ratio since it will always be visible
    //The dimension was determined by running on Windows and inspecting the dimension of the canvas after menubar and tabs are added
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );

    //Default inset between edges, etc.
    public static final double INSET = 10;

    public AbstractFractionsCanvas() {

        setWorldTransformStrategy( new CenteredStage( this, STAGE_SIZE ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBorder( null );
    }

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected void addChildren( PNode... nodes ) {
        for ( PNode node : nodes ) {
            addChild( node );
        }
    }
}