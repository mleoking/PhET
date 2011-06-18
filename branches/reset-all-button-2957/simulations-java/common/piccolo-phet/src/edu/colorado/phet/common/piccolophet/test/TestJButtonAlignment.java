// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * I noticed that it was impossible to left-align JButtons on Mac.
 * (It was verified that this is not a problem on Windows.)
 * There is always a bit of blank space to the left of the button.
 * This program demonstrates that it's not a PSwing issue, it's a general Swing issue.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestJButtonAlignment extends JFrame {

    public TestJButtonAlignment() {
        super();

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 400, 300 ) );

        final double xOffset = 50;

        // a PNode rectangle, for alignment reference
        PPath rectNode = new PPath( new Rectangle2D.Double( 0, 0, 100, 50 ) );
        rectNode.setPaint( Color.RED );
        canvas.getLayer().addChild( rectNode );
        rectNode.setOffset( xOffset, 50 );

        // JLabel left-aligns OK
        PSwing labelNode = new PSwing( new JLabel( "PSwing JLabel" ) );
        canvas.getLayer().addChild( labelNode );
        labelNode.setOffset( xOffset, rectNode.getFullBoundsReference().getMaxY() + 10 );

        // JButton doesn't left-align perfectly, there is a bit of blank space to the left of the button
        JButton button = new JButton( "PSwing JButton" );
        PSwing buttonNode = new PSwing( button );
        canvas.getLayer().addChild( buttonNode );
        buttonNode.setOffset( xOffset, labelNode.getFullBoundsReference().getMaxY() + 10 );

        // Workaround, a little better but still some space.
        // And as a general workaround, this will violate the Aqua style guide for usage of button types.
        // See button types at http://nadeausoftware.com/node/87
        JButton button2 = new JButton( "Workaround JButton" );
        if ( PhetUtilities.isMacintosh() ) {
            button2.putClientProperty( "JButton.buttonType", "bevel" );
        }
        PSwing buttonNode2 = new PSwing( button2 );
        canvas.getLayer().addChild( buttonNode2 );
        buttonNode2.setOffset( xOffset, buttonNode.getFullBoundsReference().getMaxY() + 10 );

        // JPanel with a JLabel and JButton, same space appears to left of JButton
        JPanel panel = new JPanel();
        panel.setBorder( new LineBorder( Color.BLACK ) );
        panel.setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST; // left align
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        panel.add( new JLabel( "JLabel" ), constraints );
        panel.add( new JButton( "JButton" ), constraints );
        PSwing pswing = new PSwing( panel );
        canvas.getLayer().addChild( pswing );
        pswing.setOffset( xOffset, buttonNode2.getFullBoundsReference().getMaxY() + 10 );

        getContentPane().add( canvas );
        pack();
    }

    public static void main( String[] args ) {
        JFrame frame = new TestJButtonAlignment();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
