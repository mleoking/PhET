// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.lwjglphet.utils.GLActionListener;
import edu.colorado.phet.platetectonics.PlateTectonicsConstants;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing;
import edu.colorado.phet.platetectonics.tabs.PlateMotionTab;
import edu.colorado.phet.platetectonics.tabs.PlateTectonicsTab;
import edu.umd.cs.piccolo.PNode;

/**
 * Panel with a Reset button, and for the 2nd tab, a Rewind button
 */
public class ResetPanel extends PNode {
    public ResetPanel( final PlateTectonicsTab tab, final Runnable resetAll ) {

        // maxWidth updated every time something is added, so we can position things nicely at the end
        final Property<Double> maxWidth = new Property<Double>( 0.0 );

        // current y to add things at
        final Property<Double> y = new Property<Double>( 0.0 );

        final boolean isMotionTab = tab instanceof PlateMotionTab;

        PNode rewindNode = null;
        PNode newCrustNode = null;
        if ( isMotionTab ) {
            final PlateMotionTab motionTab = (PlateMotionTab) tab;
            rewindNode = new TextButtonNode( Strings.REWIND, PlateTectonicsConstants.BUTTON_FONT, PlateTectonicsConstants.BUTTON_COLOR ) {{
                setUserComponent( UserComponents.rewindButton );
                setOffset( 0, y.get() + 15 );
                y.set( getFullBounds().getMaxY() );
                maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
                motionTab.getPlateMotionModel().animationStarted.addObserver( new SimpleObserver() {
                    public void update() {
                        setEnabled( motionTab.getPlateMotionModel().animationStarted.get() );
                        repaint();
                    }
                } );

                // run the rewind in the LWJGL thread
                addActionListener( new GLActionListener( new Runnable() {
                    public void run() {
                        motionTab.getPlateMotionModel().rewind();
                        motionTab.getClock().pause();
                        motionTab.getClock().resetSimulationTime();
                    }
                } ) );
            }};
            addChild( rewindNode );

            newCrustNode = new TextButtonNode( Strings.NEW_CRUST, PlateTectonicsConstants.BUTTON_FONT, PlateTectonicsConstants.BUTTON_COLOR ) {{
                setUserComponent( PlateTectonicsSimSharing.UserComponents.newCrustButton );
                setOffset( 0, y.get() + 15 );
                y.set( getFullBounds().getMaxY() );
                maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
                motionTab.getPlateMotionModel().hasAnyPlates.addObserver( new SimpleObserver() {
                    public void update() {
                        setEnabled( motionTab.getPlateMotionModel().hasAnyPlates.get() );
                        repaint();
                    }
                } );

                // run the rewind in the LWJGL thread
                addActionListener( new GLActionListener( new Runnable() {
                    public void run() {
                        motionTab.newCrust();
                    }
                } ) );
            }};
            addChild( newCrustNode );
        }

        PNode resetAllNode = new TextButtonNode( Strings.RESET_ALL, PlateTectonicsConstants.BUTTON_FONT, PlateTectonicsConstants.BUTTON_COLOR ) {{
            setUserComponent( UserComponents.resetAllButton );
            setOffset( 0, y.get() + 15 );
            y.set( getFullBounds().getMaxY() );
            maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );

            // run the reset in the LWJGL thread
            addActionListener( new GLActionListener( resetAll ) );
        }};
        addChild( resetAllNode );

        // horizontally center the buttons, if applicable
        resetAllNode.setOffset( ( maxWidth.get() - resetAllNode.getFullBounds().getWidth() ) / 2, resetAllNode.getYOffset() );
        if ( isMotionTab ) {
            rewindNode.setOffset( ( maxWidth.get() - rewindNode.getFullBounds().getWidth() ) / 2, rewindNode.getYOffset() );
            newCrustNode.setOffset( ( maxWidth.get() - newCrustNode.getFullBounds().getWidth() ) / 2, newCrustNode.getYOffset() );
        }

        // this prevents panel resizing when the button bounds change (like when they are pressed)
        addChild( new Spacer( 0, y.get(), 1, 1 ) );
    }
}
