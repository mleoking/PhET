// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.test;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

public class PiccoloTestFrame extends JFrame {

    private PhetPCanvas canvas;

    public PiccoloTestFrame( String title ) {
        super( title );

        canvas = new BufferedPhetPCanvas();
        setContentPane( canvas );
        setSize( 800, 600 );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public void addNode( PNode node ) {
        canvas.getLayer().addChild( node );
    }
}
