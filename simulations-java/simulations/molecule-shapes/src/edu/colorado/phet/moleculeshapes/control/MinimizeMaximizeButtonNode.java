// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lwjglphet.LWJGLCursorHandler;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Displays a toggle-button that shows either a green + when minimized or a red - when maximized,
 * depending on the property value.
 */
public class MinimizeMaximizeButtonNode extends PhetPPath {
    private static final double SIZE = 16; // vertical and horizontal

    private static final double SYMBOL_PADDING = 3; // padding from button edge to +/- extent
    private static final double SYMBOL_WIDTH = 2; // width of +/-

    private boolean mouseDown = false;

    public MinimizeMaximizeButtonNode( final Property<Boolean> minimized ) {
        super( new RoundRectangle2D.Double( 0, 0, SIZE, SIZE, SIZE / 2, SIZE / 2 ) ); // rounded button size

        final RoundRectangle2D.Double horizontalBar = new RoundRectangle2D.Double(
                SYMBOL_PADDING, SIZE / 2 - SYMBOL_WIDTH / 2, // x,y
                SIZE - 2 * SYMBOL_PADDING, SYMBOL_WIDTH, // width,height
                SYMBOL_WIDTH / 2, SYMBOL_WIDTH / 2 );

        final RoundRectangle2D.Double verticalBar = new RoundRectangle2D.Double(
                SIZE / 2 - SYMBOL_WIDTH / 2, SYMBOL_PADDING, // x,y
                SYMBOL_WIDTH, SIZE - 2 * SYMBOL_PADDING, // width,height
                SYMBOL_WIDTH / 2, SYMBOL_WIDTH / 2
        );

        final Shape minus = horizontalBar;
        final Shape plus = new Area() {{
            add( new Area( horizontalBar ) );
            add( new Area( verticalBar ) );
        }};

        PhetPPath icon = new PhetPPath() {{
            setPaint( Color.BLACK );
            setStroke( null );

            // change its shape based on whether we are signaling a minimized state
            minimized.addObserver( new SimpleObserver() {
                public void update() {
                    reset();
                    append( minimized.get() ? plus : minus, false );
                }
            } );
        }};
        addChild( icon );

        // keep the color updated
        final Runnable update = new Runnable() {
            public void run() {
                Color color = minimized.get() ? MoleculeShapesConstants.MAXIMIZE_GREEN : MoleculeShapesConstants.MINIMIZE_RED;
                Color baseColor = mouseDown ? color.darker() : color;
                Color highlightColor = mouseDown ? baseColor.brighter() : toHighlight( baseColor );
                setPaint( new GradientPaint( 0, 0, highlightColor, 0, (float) SIZE / 2, baseColor, false ) );
            }

            private Color toHighlight( Color color ) {
                int c = 100;
                return new Color(
                        Math.min( 255, color.getRed() + c ),
                        Math.min( 255, color.getGreen() + c ),
                        Math.min( 255, color.getBlue() + c ) );
            }
        };

        // when minimization changes, update our view
        minimized.addObserver( LWJGLUtils.swingObserver( update ), false );
        update.run();

        // mouse handling
        addInputEventListener( new LWJGLCursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                mouseDown = true;
                update.run();
            }

            @Override public void mouseReleased( PInputEvent event ) {
                mouseDown = false;
                update.run();
            }

            @Override public void mouseClicked( PInputEvent event ) {
                // toggle it in JME thread
                LWJGLUtils.invoke( new Runnable() {
                    public void run() {
                        minimized.set( !minimized.get() );
                    }
                } );
            }
        } );
    }
}
