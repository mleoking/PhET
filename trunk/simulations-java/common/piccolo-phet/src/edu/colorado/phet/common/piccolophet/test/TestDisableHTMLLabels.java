
package edu.colorado.phet.common.piccolophet.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * 
 * Demonstrates that HTML text of disabled Swing components are not grayed out.
 * This problem is not specific to PSwing, it's a general Swing issue.
 * Observed on both Mac and Windows.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestDisableHTMLLabels extends JFrame {

    public TestDisableHTMLLabels() {
        super();
        setResizable( false );

        // panel with check boxes on a canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 200, 200 ) );
        final JPanel pswingPanel = new MyPanel( "PSwing" );
        PSwing pswingWrapper = new PSwing( pswingPanel );
        canvas.getLayer().addChild( pswingWrapper );
        pswingWrapper.setOffset( 50, 50 );

        // panel with check boxes outside the canvas
        final JPanel swingPanel = new MyPanel( "Swing" );
        final JCheckBox enableCheckBox = new JCheckBox( "enable controls" );
        enableCheckBox.setForeground( Color.RED );
        enableCheckBox.setSelected( true );
        enableCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pswingPanel.setEnabled( enableCheckBox.isSelected() );
                swingPanel.setEnabled( enableCheckBox.isSelected() );
            }
        } );
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.Y_AXIS ) );
        controlPanel.add( swingPanel );
        controlPanel.add( Box.createVerticalStrut( 20 ) );
        controlPanel.add( enableCheckBox );

        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( controlPanel, BorderLayout.EAST );

        getContentPane().add( mainPanel );
        pack();
    }

    /*
     * A panel with 2 check boxes.
     * Disabling the panel disables the check boxes.
     */
    public static class MyPanel extends JPanel {

        private final JCheckBox plainTextCheckBox;
        private final JCheckBox htmlCheckBox;

        public MyPanel( String title ) {
            super();
            setBorder( new TitledBorder( title ) );
            plainTextCheckBox = new JCheckBox( "plain text" );
            htmlCheckBox = new JCheckBox( "<html><b>HTML</b> text</html>" );
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            add( plainTextCheckBox );
            add( htmlCheckBox );
        }

        public void setEnabled( boolean enabled ) {
            super.setEnabled( enabled );
            plainTextCheckBox.setEnabled( enabled );
            htmlCheckBox.setEnabled( enabled );
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new TestDisableHTMLLabels();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

}
