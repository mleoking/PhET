/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.test;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.umd.cs.piccolo.PNode;


public class PiccoloTestFrame extends JFrame {

    private PhetPCanvas canvas;

    public PiccoloTestFrame(String title) {
        super(title);

        canvas = new BufferedPhetPCanvas();
        setContentPane(canvas);
        setSize(800, 600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public PiccoloTestFrame() {
        // Get the name of the class that called this method and use it for
        // the title.
        this(new Exception().getStackTrace()[1].getClassName());
    }

    public void addNode(PNode node) {
        canvas.getLayer().addChild(node);
    }
}
