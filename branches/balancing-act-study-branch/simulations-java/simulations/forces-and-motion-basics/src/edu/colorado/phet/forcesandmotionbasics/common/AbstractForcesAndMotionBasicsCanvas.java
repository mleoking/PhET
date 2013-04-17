// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.common;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Abstract canvas class, used in all of the tabs.
 * Contains most of the functionality in #3423 "migrate duplicate canvas subclasses to common code"
 *
 * @author Sam Reid
 */
public class AbstractForcesAndMotionBasicsCanvas extends PhetPCanvas {

    //Font used for most of the labels and controls
    public static final Font DEFAULT_FONT = new PhetFont( 17, true );

    //Stage where nodes are added and scaled up and down
    protected final PNode rootNode;

    //Size for the stage, should have the right aspect ratio since it will always be visible
    //The dimension was determined by running on Windows and inspecting the dimension of the canvas after menubar and tabs are added
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );

    //Default inset between edges, etc.
    public static final double INSET = 10;

    protected AbstractForcesAndMotionBasicsCanvas() {

        setWorldTransformStrategy( new CenteredStage( this, STAGE_SIZE ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBorder( null );
    }

    protected void addChild( PNode node ) { rootNode.addChild( node ); }
}