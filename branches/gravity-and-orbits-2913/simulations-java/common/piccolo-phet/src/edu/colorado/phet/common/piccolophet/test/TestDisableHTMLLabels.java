// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Disabled Swing components are not grayed out if their text is HTML.
 * Use the red "enable components" check box to demonstrate.
 * This problem is not specific to PSwing, it's a general Swing issue.
 * Observed on both Mac and Windows.
 * <p/>
 * See Unfuddle #1704.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestDisableHTMLLabels extends JFrame {

    public TestDisableHTMLLabels() {
        super();
        setResizable( false );

        // panel with controls on a canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 300, 400 ) );
        final JPanel pswingPanel = new MyPanel( "PSwing" );
        PSwing pswingWrapper = new PSwing( pswingPanel );
        canvas.getLayer().addChild( pswingWrapper );
        pswingWrapper.setOffset( 50, 50 );

        // panel with controls outside the canvas
        final JPanel swingPanel = new MyPanel( "Swing" );
        final JCheckBox enableCheckBox = new JCheckBox( "enable components" );
        enableCheckBox.setForeground( Color.RED );
        enableCheckBox.setSelected( true );
        enableCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pswingPanel.setEnabled( enableCheckBox.isSelected() );
                swingPanel.setEnabled( enableCheckBox.isSelected() );
            }
        } );
        JPanel controlPanel = new VerticalLayoutPanel();
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
     * A panel with various JComponents.
     * Disabling the panel disables all of its child components.
     */
    public static class MyPanel extends JPanel {

        public MyPanel( String title ) {
            super();
            setBorder( new TitledBorder( title ) );
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            add( new JLabel( "plain label" ) );
            add( new JLabel( "<html>HTML label</html>" ) );
            add( new JSeparator() );
            add( new JCheckBox( "plain check box" ) );
            add( new JCheckBox( "<html>HTML check box</html>" ) );
            add( new JSeparator() );
            add( new JButton( "plain button" ) );
            add( new JButton( "<html>HTML button</html>" ) );
            add( new JSeparator() );
            add( new JRadioButton( "plain radio button" ) );
            add( new JRadioButton( "<html>HTML radio button</html>" ) );
        }

        public void setEnabled( boolean enabled ) {
            super.setEnabled( enabled );
            Component[] children = getComponents();
            for ( int i = 0; i < children.length; i++ ) {
                children[i].setEnabled( enabled );
            }
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new TestDisableHTMLLabels();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

}
