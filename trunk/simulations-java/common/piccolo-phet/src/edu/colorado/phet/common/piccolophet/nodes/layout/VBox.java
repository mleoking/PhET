// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.layout;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Layout the nodes in a vertical fashion, centered horizontally with the specified vertical padding between nodes.
 * The layout doesn't update when children bounds change, layout is only performed when new children are added (sufficient for bending light usage).
 *
 * @author Sam Reid
 */
public class VBox extends Box {
    //Creates a VBox with the default spacing and specified children to add
    public VBox( PNode... children ) {
        this( 10, children );
    }

    //Creates a VBox which lays out nodes vertically.  This constructor invocation is meant to be read with 'code folding' on and a good healthy right margin (like 200)
    public VBox( int spacing,
                 PNode... children//List of children to be added on initialization
    ) {
        super( spacing,
               //Specify the width of the node which is used in determining the overall width of the VBox
               new Function1<PBounds, Double>() {
                   public Double apply( PBounds bounds ) {
                       return bounds.getWidth();
                   }
               },
               //Specify the height of the node, for spacing the nodes vertically
               new Function1<PBounds, Double>() {
                   public Double apply( PBounds bounds ) {
                       return bounds.getHeight();
                   }
               },
               //Determine the position to place the node, given its center line, bounds and spaced position
               new PositionStrategy() {
                   public Point2D getRelativePosition( PNode node, double maxSize, double location ) {
                       return new Point2D.Double( maxSize / 2 - node.getFullBounds().getWidth() / 2, location );
                   }
               },
               children
        );
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