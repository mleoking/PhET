// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Comparator;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

import static java.util.Collections.max;

/**
 * Layout the nodes in a vertical fashion, centered horizontally with the specified vertical padding between nodes.
 * The layout doesn't update when children bounds change, layout is only performed when new children are added (sufficient for bending light usage).
 *
 * @author Sam Reid
 */
public class VBox extends PhetPNode {
    private final int spacing;

    public VBox() {
        this( 0 );
    }

    public VBox( int spacing ) {
        this.spacing = spacing;
    }

    @Override public void addChild( int index, PNode child ) {
        super.addChild( index, child );
        updateLayout();
    }

    //Layout the nodes in a vertical fashion, keeping them centered
    private void updateLayout() {
        //Find the width of the biggest child node so far
        double maxWidth = max( getChildren(), new Comparator<PNode>() {
            public int compare( PNode o1, PNode o2 ) {
                return Double.compare( o1.getFullBounds().getWidth(), o2.getFullBounds().getWidth() );
            }
        } ).getFullBounds().getWidth();
        double y = 0;

        //Move each child 'spacing' below the previous child and center it
        for ( PNode child : getChildren() ) {
            final PBounds bounds = child.getFullBounds();
            double childOriginX = bounds.getX() - child.getOffset().getX();
            double childOriginY = bounds.getY() - child.getOffset().getY();
            child.setOffset( maxWidth / 2 - bounds.getWidth() / 2 - childOriginX, y - childOriginY );
            y += bounds.getHeight() + spacing;
        }
    }

    //Test
    public static void main( String[] args ) {
        new JFrame() {{
            setContentPane( new PhetPCanvas() {{
                addScreenChild( new ControlPanelNode( new VBox( 5 ) {{
                    addChild( new PText( "Testing" ) );
                    addChild( new PImage( new BufferedImage( 100, 100, BufferedImage.TYPE_INT_ARGB_PRE ) {{
                        Graphics2D g2 = createGraphics();
                        g2.setPaint( Color.blue );
                        g2.fillRect( 0, 0, 100, 100 );
                        g2.dispose();
                    }} ) );
                    addChild( new PhetPPath( new Ellipse2D.Double( 0, 0, 100, 100 ) ) );
                    addChild( new PhetPPath( new Ellipse2D.Double( -100, -100, 100, 100 ) ) );
                }} ) );
            }} );
            setSize( 800, 600 );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
        }}.setVisible( true );
    }
}