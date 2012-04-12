// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.modules.PlateMotionTab;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.umd.cs.piccolo.PNode;

/**
 * Panel with a Reset button, and for the 2nd tab, a Rewind button
 */
public class ResetPanel extends PNode {
    public ResetPanel( final PlateTectonicsTab tab, final Runnable resetAll ) {
        final Property<Double> maxWidth = new Property<Double>( 0.0 );
        final Property<Double> y = new Property<Double>( 0.0 );

        final boolean showRewindButton = tab instanceof PlateMotionTab;

        PNode rewindNode = null;
        if ( showRewindButton ) {
            rewindNode = new TextButtonNode( Strings.REWIND, new PhetFont( 14 ), Color.ORANGE ) {{
                setUserComponent( UserComponents.rewindButton );
                setOffset( 0, y.get() + 15 );
                y.set( getFullBounds().getMaxY() );
                maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
                ( (PlateMotionTab) tab ).getPlateMotionModel().animationStarted.addObserver( new SimpleObserver() {
                    public void update() {
                        setEnabled( ( (PlateMotionTab) tab ).getPlateMotionModel().animationStarted.get() );
                        repaint();
                    }
                } );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        LWJGLUtils.invoke( new Runnable() {
                            public void run() {
                                ( (PlateMotionTab) tab ).rewind();
                            }
                        } );
                    }
                } );
            }};
            addChild( rewindNode );
        }

        PNode resetAllNode = new TextButtonNode( Strings.RESET_ALL, new PhetFont( 14 ), Color.ORANGE ) {{
            setUserComponent( UserComponents.resetAllButton );
            setOffset( 0, y.get() + 15 );
            y.set( getFullBounds().getMaxY() );
            maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent actionEvent ) {
                    LWJGLUtils.invoke( resetAll );
                }
            } );
        }};
        addChild( resetAllNode );

        // horizontally center the buttons, if applicable
        resetAllNode.setOffset( ( maxWidth.get() - resetAllNode.getFullBounds().getWidth() ) / 2, resetAllNode.getYOffset() );
        if ( showRewindButton ) {
            rewindNode.setOffset( ( maxWidth.get() - rewindNode.getFullBounds().getWidth() ) / 2, rewindNode.getYOffset() );
        }

        // this prevents panel resizing when the button bounds change (like when they are pressed)
        addChild( new Spacer( 0, y.get(), 1, 1 ) );
    }
}
