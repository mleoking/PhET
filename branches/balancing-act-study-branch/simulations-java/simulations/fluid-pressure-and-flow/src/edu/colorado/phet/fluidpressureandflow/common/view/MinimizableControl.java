// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.piccolophet.nodes.MinimizeMaximizeNode;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureCanvas;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.piccolophet.nodes.MinimizeMaximizeNode.BUTTON_LEFT;
import static java.awt.Color.black;

/**
 * Control in the play area that can be minimized, such as fluid density or gravity slider.
 *
 * @author Sam Reid
 */
public class MinimizableControl extends PNode {
    protected final Property<Boolean> maximized;

    public MinimizableControl( IUserComponent minimizeButtonComponent, IUserComponent maximizeButtonComponent, final Property<Boolean> maximized, final PNode content, final String text ) {
        this.maximized = maximized;

        //Button for showing/hiding the slider
        MinimizeMaximizeNode minimizeMaximizeNode = new MinimizeMaximizeNode( minimizeButtonComponent, maximizeButtonComponent, text, BUTTON_LEFT, FluidPressureCanvas.CONTROL_FONT, black, 10 ) {{
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    maximized.set( isMaximized() );
                }
            } );
            maximized.addObserver( new SimpleObserver() {
                public void update() {
                    setMaximized( maximized.get() );
                }
            } );
            translate( 0, -getFullBounds().getHeight() );
        }};

        //Make it invisible when not maximized so that the bounds will be right.
        maximized.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean aBoolean ) {
                content.setVisible( aBoolean );
            }
        } );

        //Add children
        addChild( new NoBoundsWhenInvisible( content ) );
        addChild( minimizeMaximizeNode );
    }

    //Find the size of this component when the slider is visible
    public Dimension2DDouble getMaximumSize() {
        //Store the original value
        boolean visible = maximized.get();

        //Enable the slider visibility
        maximized.set( true );

        //Find the value we were looking for
        Dimension2DDouble size = new Dimension2DDouble( getFullBounds().getWidth(), getFullBounds().getHeight() );

        //Restore the old value; single threaded so nobody will notice a flicker
        maximized.set( visible );

        //return the size of this component when slider is showing
        return size;
    }
}
