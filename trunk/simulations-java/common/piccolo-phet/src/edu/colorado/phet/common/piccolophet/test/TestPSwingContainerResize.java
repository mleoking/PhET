// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Demonstrates a PSwing bounds problem with Containers.
 * See Unfuddle #1670.
 * <p/>
 * Click the radio buttons in the control panel a few times to see the issue.
 * The panel outside the play area behaves correctly.
 * But the panel in the play area will become wider to accommodate the wider label,
 * but the label will be wrapped to the panel's previous width.
 * This problem was observed with JCheckBox, JButton, JRadioButton.
 * If the text is not HTML, there is no problem.
 * <p/>
 * This is definitely a PSwing problem, see Unfuddle #1670.
 * Workaround is to call PSwing.computeBounds whenever you change anything
 * in the PSwing's component or its children.
 * <p/>
 * Observed on:
 * Mac OS 10.5.6 + Java 1.5.0_16.
 * Windows XP + Java 1.5.0_17.
 * Untested on other platforms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPSwingContainerResize extends JFrame {

    private static final boolean WORKAROUND_ENABLED = true;

    public TestPSwingContainerResize() {

        /*
        * Swing control panel embedded in a Piccolo canvas using PSwing.
        * The red check box will have it's label changed dynamically.
        */
        final DynamicPanel playAreaPanel = new DynamicPanel();
        final PSwing wrapperNode = new PSwing( playAreaPanel );
        final PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 600, 400 ) );
        canvas.getLayer().addChild( wrapperNode );
        wrapperNode.setOffset( 100, 100 );

        /*
        * Swing control panel outside of Piccolo.
        * The red check box will have it's label changed dynamically.
        */
        final DynamicPanel dynamicSwingPanel = new DynamicPanel();

        /*
        * Swing control panel outside of Piccolo.
        * Selecting one of the radio buttons changes the label on the
        * red check box in the play area.
        */
        JPanel controlPanel = new JPanel();
        {
            controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.Y_AXIS ) );
            TitledBorder controlPanelBorder = new TitledBorder( new LineBorder( Color.BLACK, 1 ), "Set the red check box label to:" );
            controlPanelBorder.setTitleColor( Color.RED );
            controlPanel.setBorder( controlPanelBorder );

            ActionListener listener = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( e.getSource() instanceof JRadioButton ) {
                        playAreaPanel.setDynamicLabel( ( (JRadioButton) e.getSource() ).getText() );
                        dynamicSwingPanel.setDynamicLabel( ( (JRadioButton) e.getSource() ).getText() );
                        if ( WORKAROUND_ENABLED ) {
                            wrapperNode.updateBounds();
                        }
                    }
                }
            };

            JRadioButton rb1 = new JRadioButton( "<html>short label</html>" );
            rb1.addActionListener( listener );
            controlPanel.add( rb1 );

            JRadioButton rb2 = new JRadioButton( "<html>a much much much much longer label</html>" );
            rb2.addActionListener( listener );
            controlPanel.add( rb2 );

            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( rb1 );
            buttonGroup.add( rb2 );
            rb1.setSelected( true );
            playAreaPanel.setDynamicLabel( rb1.getText() );
            dynamicSwingPanel.setDynamicLabel( rb1.getText() );
        }

        /*
        * Frame layout.
        */
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( controlPanel, BorderLayout.EAST );
        mainPanel.add( dynamicSwingPanel, BorderLayout.WEST );
        mainPanel.add( canvas, BorderLayout.CENTER );

        setContentPane( mainPanel );
        pack();
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }

    /*
    * The top check box in this panel will have its label changed.
    */
    private static class DynamicPanel extends JPanel {

        private final AbstractButton dynamicButton;

        public DynamicPanel() {
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 1 ), "Dynamic panel" );
            setBorder( border );
            dynamicButton = new JCheckBox();
            dynamicButton.setForeground( Color.RED );
            add( dynamicButton );
            add( new JCheckBox( "red" ) );
            add( new JCheckBox( "blue" ) );
            add( new JCheckBox( "green" ) );


            // test ComponentListener
            this.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    System.out.println( "DynamicPanel.componentResized" );
                }
            } );
            dynamicButton.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    System.out.println( "JCheckBox.componentResized" );
                }
            } );
        }

        public void setDynamicLabel( String text ) {
            dynamicButton.setText( text );
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new TestPSwingContainerResize();
        frame.setVisible( true );
    }

}
