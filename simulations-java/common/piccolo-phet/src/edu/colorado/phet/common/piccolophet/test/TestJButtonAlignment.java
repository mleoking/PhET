package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * I noticed that it was impossible to left-align JButtons on Mac.
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
        
        // a PNode rectangle, for alignment reference
        PPath rectNode = new PPath( new Rectangle2D.Double( 0, 0, 100, 50 ) );
        rectNode.setPaint( Color.RED );
        canvas.getLayer().addChild( rectNode );
        rectNode.setOffset( 50, 50 );
        
        // JLabel left-aligns OK
        PSwing labelNode = new PSwing( new JLabel( "PSwing JLabel" ) );
        canvas.getLayer().addChild( labelNode );
        labelNode.setOffset( rectNode.getXOffset(), rectNode.getFullBoundsReference().getMaxY() + 10 );
        
        // JButton doesn't left-align perfectly, there is a bit of blank space to the left of the button
        JButton button = new JButton( "PSwing JButton" );
        PSwing buttonNode = new PSwing( button );
        canvas.getLayer().addChild( buttonNode );
        buttonNode.setOffset( rectNode.getXOffset(), labelNode.getFullBoundsReference().getMaxY() + 10 );
        
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
        pswing.setOffset( rectNode.getXOffset(), buttonNode.getFullBoundsReference().getMaxY() + 10 );
        
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
