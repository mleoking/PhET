// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Demonstrates a bounds problem with HTMLNode.
 * When setting the font some sizes, the bounds (as shown by the red PPath) are computed incorrectly.
 * <p/>
 * Use the "font size" spinner to change the font size.
 * Start with size=100, then decrease by 1.
 * You'll see garbage left on the canvas for any sizes that have incorrect bounds.
 * The problem font sizes differs between platforms (probably because it differs between fonts).
 * <p/>
 * See Unfuddle #2178.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestHTMLNodeBounds extends JFrame {

    private static final int MIN_FONT_SIZE = 10;
    private static final int MAX_FONT_SIZE = 100;
    private static final int DEFAULT_FONT_SIZE = 100;

    private final PPath boundsNode;
    private final HTMLNode htmlNode;

    public TestHTMLNodeBounds() {
        super( TestHTMLNodeBounds.class.getName() );
        setSize( new Dimension( 800, 450 ) );

        // Piccolo canvas
        PSwingCanvas canvas = new PSwingCanvas();
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );

        // the HTMLNode
        String html = "<html><center>How many<br>reactants<br>in this reaction?</center></html>";
        htmlNode = new HTMLNode( html );
        htmlNode.setFont( new PhetFont( DEFAULT_FONT_SIZE ) );
        htmlNode.addPropertyChangeListener( new PropertyChangeListener() {
            // when the bounds change, update boundsNode
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                    updateBoundsNode();
                }
            }
        } );
        canvas.getLayer().addChild( htmlNode );

        // displays the bounds of htmlNode
        boundsNode = new PPath( htmlNode.getBounds() );
        boundsNode.setStroke( new BasicStroke( 1f ) );
        boundsNode.setStrokePaint( Color.RED );
        canvas.getLayer().addChild( boundsNode );

        // label to show font name
        JLabel fontNameLabel = new JLabel( "font face name: " + new PhetFont().getFontName() );

        // spinner to change the font size of htmlNode
        final JSpinner fontSizeSpinner = new JSpinner();
        fontSizeSpinner.setModel( new SpinnerNumberModel( DEFAULT_FONT_SIZE, MIN_FONT_SIZE, MAX_FONT_SIZE, 1 ) );
        NumberEditor editor = new NumberEditor( fontSizeSpinner );
        // don't allow editing of the textfield, so we don't have to handle bogus input
        editor.getTextField().setEditable( false );
        fontSizeSpinner.setEditor( editor );
        fontSizeSpinner.addChangeListener( new ChangeListener() {
            // when the spinner changes, update htmlNode font
            public void stateChanged( ChangeEvent e ) {
                int fontSize = ( (Integer) fontSizeSpinner.getValue() ).intValue();
                htmlNode.setFont( new PhetFont( fontSize ) );
            }
        } );

        JPanel controlPanel = new JPanel();
        controlPanel.add( fontNameLabel );
        controlPanel.add( Box.createHorizontalStrut( 40 ) );
        controlPanel.add( new JLabel( "font size:" ) );
        controlPanel.add( fontSizeSpinner );

        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( controlPanel, BorderLayout.SOUTH );

        setContentPane( mainPanel );
    }

    private void updateBoundsNode() {
        boundsNode.setPathTo( htmlNode.getBounds() );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JFrame frame = new TestHTMLNodeBounds();
                frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
                SwingUtils.centerWindowOnScreen( frame );
                frame.setVisible( true );
            }
        } );
    }
}

//Sample application that mimics the above behavior, but using Swing only (no Piccolo).
class Test2 {
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                final JLabel label = new JLabel( "<html><center>How many<br>reactants<br>in this reaction?</center></html>" );
                frame.setContentPane( new JComponent() {
                    protected void paintComponent( Graphics g ) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setPaint( Color.blue );
                        g2.setStroke( new BasicStroke( 1 ) );
                        g2.drawRect( 0, 0, label.getPreferredSize().width, label.getPreferredSize().height );
                    }
                } );

                label.setForeground( Color.red );
                label.setFont( new PhetFont( 53 ) );
                System.out.println( "label.getPreferredSize() = " + label.getPreferredSize() );
                frame.getContentPane().setLayout( null );
                frame.getContentPane().add( label );
                label.setBounds( 0, 0, label.getPreferredSize().width, label.getPreferredSize().height );

                frame.setVisible( true );
                frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                frame.setSize( new Dimension( 800, 450 ) );
            }
        } );
    }
}
