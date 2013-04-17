// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.module.nuclearreactor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.dialog.ReactorPictureDialog;
import edu.colorado.phet.nuclearphysics.view.NuclearReactorNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the nuclear reactor tab of this simulation.
 *
 * @author John Blanco
 */
public class NuclearReactorCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Canvas dimensions.
    private final double CANVAS_WIDTH = 700;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * 0.87;

    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 2.0;
    private final double HEIGHT_TRANSLATION_FACTOR = 2.5;

    // Timer for delaying the appearance of the reset button.
    private static final int BUTTON_DELAY_TIME = 1000; // In milliseconds.
    private static final Timer BUTTON_DELAY_TIMER = new Timer( BUTTON_DELAY_TIME, null );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private NuclearReactorModel _nuclearReactorModel;
    private NuclearReactorNode _nuclearReactorNode;
    private HTMLImageButtonNode _resetNucleiButtonNode;
    private HTMLImageButtonNode _showReactorImageButtonNode;
    private Frame _parentFrame;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public NuclearReactorCanvas( NuclearReactorModel nuclearReactorModel, Frame parentFrame ) {

        _nuclearReactorModel = nuclearReactorModel;
        _parentFrame = parentFrame;

        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setWorldTransformStrategy( new RenderingSizeStrategy( this,
                                                              new PDimension( CANVAS_WIDTH, CANVAS_HEIGHT ) ) {
            protected AffineTransform getPreprocessedTransform() {
                return AffineTransform.getTranslateInstance( getWidth() / WIDTH_TRANSLATION_FACTOR,
                                                             getHeight() / HEIGHT_TRANSLATION_FACTOR );
            }
        } );

        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );

        // Register with the model so that we can receive notifications of the
        // events that we care about.
        _nuclearReactorModel.addListener( new NuclearReactorModel.Adapter() {
            public void resetOccurred() {
                _resetNucleiButtonNode.setVisible( false );
            }

            public void reactionStarted() {
                BUTTON_DELAY_TIMER.restart();
            }
        } );

        // Set up the button delay timer that will make the reset button
        // appear some time after the decay has occurred.
        BUTTON_DELAY_TIMER.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                // Show the button.
                _resetNucleiButtonNode.setVisible( true );
                BUTTON_DELAY_TIMER.stop();
            }
        } );

        // Add the reactor node to the canvas.
        _nuclearReactorNode = new NuclearReactorNode( _nuclearReactorModel, this );
        addWorldChild( _nuclearReactorNode );

        // Add the button for resetting the reactor.  This won't be visible
        // until the reaction has been started.
        _resetNucleiButtonNode = new HTMLImageButtonNode( NuclearPhysicsStrings.RESET_NUCLEI, new PhetFont( Font.BOLD, 16 ),
                                                 NuclearPhysicsConstants.CANVAS_RESET_BUTTON_COLOR );
        addWorldChild( _resetNucleiButtonNode );
        _resetNucleiButtonNode.setOffset( _nuclearReactorNode.getFullBounds().getMinX(),
                                          _nuclearReactorNode.getFullBounds().getMinY() - _resetNucleiButtonNode.getFullBounds().height );
        _resetNucleiButtonNode.setVisible( false );

        _resetNucleiButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                _nuclearReactorModel.reset();
            }
        } );

        // Add the button for showing the reactor photo.
        _showReactorImageButtonNode = new HTMLImageButtonNode( NuclearPhysicsStrings.SHOW_REACTOR_IMAGE, new PhetFont( Font.BOLD, 16 ),
                                                      NuclearPhysicsConstants.CANVAS_RESET_BUTTON_COLOR );
        addWorldChild( _showReactorImageButtonNode );
        _showReactorImageButtonNode.setOffset( -( CANVAS_WIDTH / 2 ), CANVAS_HEIGHT / 2 );

        _showReactorImageButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                // Show the image dialog.
                ReactorPictureDialog reactorCorePictureDlg = new ReactorPictureDialog( _parentFrame );
                if ( reactorCorePictureDlg != null ) {
                    SwingUtils.centerDialogInParent( reactorCorePictureDlg );
                }
                reactorCorePictureDlg.setVisible( true );
            }
        } );
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------

}
