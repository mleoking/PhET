//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.tests;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.control.GeneralLayoutNode;
import edu.colorado.phet.buildamolecule.control.GeneralLayoutNode.HorizontalAlignMethod.Align;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * A few tests for GeneralLayoutNode
 */
public class GeneralLayoutTest {
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "GeneralLayoutTest" );
        frame.setContentPane( new PhetPCanvas() {{
            Dimension size = new Dimension( 480, 640 );
            setPreferredSize( size );

            setWorldTransformStrategy( new CenteredStage( this, size ) );

            addWorldChild( new GeneralLayoutNode() {{
                LayoutMethod method = new CompositeLayoutMethod( new VerticalLayoutMethod(), new HorizontalAlignMethod( Align.Centered ) );
                addChild( new LayoutElement( PhetPPath.createRectangle( 0, 0, 100, 50 ), method ) );
                addChild( new LayoutElement( PhetPPath.createRectangle( 0, 0, 50, 30 ), method ) );
                addChild( new LayoutElement( PhetPPath.createRectangle( 0, 0, 100, 30 ), method, 20, 0, 10, 0 ) );
                addChild( new LayoutElement( PhetPPath.createRectangle( 0, 0, 70, 50 ), method ) );
                addChild( new LayoutElement( PhetPPath.createRectangle( 0, 0, 70, 50 ),
                                             new CompositeLayoutMethod( new VerticalLayoutMethod(), new HorizontalAlignMethod( Align.Left ) ) ) );
                addChild( new LayoutElement( PhetPPath.createRectangle( 0, 0, 70, 50 ),
                                             new CompositeLayoutMethod( new VerticalLayoutMethod(), new HorizontalAlignMethod( Align.Right ) ) ) );
            }} );
        }} );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );
    }
}
