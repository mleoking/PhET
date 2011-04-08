// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler.ButtonEventAdapter;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Button composed of 2 images: one for armed state, one for unarmed state.
 * Releasing the mouse while the button is in the armed state fires an ActionEvent.
 *
 * @author Chris Malley
 */
public class ImageButtonNode extends PhetPNode {

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    private Image _unarmedImage, _armedImage;
    private PImage _imageNode;
    private ArrayList<ActionListener> _actionListeners;

    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param unarmedImage
     * @param armedImage
     */
    public ImageButtonNode( Image unarmedImage, Image armedImage ) {

        _unarmedImage = unarmedImage;
        _armedImage = armedImage;

        _imageNode = new PImage( unarmedImage );
        addChild( _imageNode );

        // Initialize local data.
        _actionListeners = new ArrayList<ActionListener>();

        // Register a handler to watch for button state changes.
        ButtonEventHandler handler = new ButtonEventHandler();
        addInputEventListener( handler );
        handler.addButtonEventListener( new ButtonEventAdapter() {
            public void setArmed( boolean armed ) {
                _imageNode.setImage( armed ? _armedImage : _unarmedImage );
            }

            public void fire() {
                ActionEvent event = new ActionEvent( this, 0, "BUTTON_FIRED" );
                for ( int i = 0; i < _actionListeners.size(); i++ ) {
                    ( (ActionListener) _actionListeners.get( i ) ).actionPerformed( event );
                }
            }
        } );
    }

    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------

    public void addActionListener( ActionListener listener ) {
        if ( !_actionListeners.contains( listener ) ) {
            _actionListeners.add( listener );
        }
    }

    public void removeActionListener( ActionListener listener ) {
        _actionListeners.remove( listener );
    }

    //------------------------------------------------------------------------
    // Test Harness
    //------------------------------------------------------------------------

    public static void main( String[] args ) {

        // Use Piccolo to create some test images.
        Shape buttonShape = new Ellipse2D.Double( 0, 0, 100, 100 );
        Color buttonColor = Color.RED;
        PPath unarmedPathNode = new PPath( buttonShape );
        unarmedPathNode.setPaint( buttonColor.darker() );
        unarmedPathNode.setStroke( null );
        Image unarmedImage = unarmedPathNode.toImage();
        PPath armedPathNode = new PPath( buttonShape );
        armedPathNode.setPaint( buttonColor );
        armedPathNode.setStroke( null );
        Image armedImage = armedPathNode.toImage();

        // Create the button node
        ImageButtonNode buttonNode = new ImageButtonNode( unarmedImage, armedImage );
        buttonNode.addInputEventListener( new CursorHandler() );
        buttonNode.setOffset( 100, 100 );

        // Attach an event listener
        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                System.out.println( "actionPerformed event= " + event );
            }
        };
        buttonNode.addActionListener( listener );

        // Canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.addScreenChild( buttonNode );

        // Frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( 400, 300 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
