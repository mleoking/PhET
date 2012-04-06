// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloPhetResources;
import edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Node that represents a device that can be used to heat or cool things.  It
 * looks something like a Bunsen burner, or a stove.  When heating, flames
 * appear to come out of it and when cooling, ice cubes come out of it.  This
 * originated in the States of Matter simulation, and was moved into common
 * code from there.
 *
 * @author John Blanco
 */
public class HeaterCoolerNode extends PNode {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Size, in screen coordinates, of this node.  The size is fixed, but can
    // be scaled if needed (within reasonable limits).
    private static final double WIDTH = 250;                          // In screen coords, which are close to pixels.
    private static final double HEIGHT = WIDTH * 0.75;                // In screen coords, which are close to pixels.
    private static final double BURNER_OPENING_HEIGHT = WIDTH * 0.1;  // In screen coords, which are close to pixels.
    private static final double BOTTOM_WIDTH = WIDTH * 0.8;           // In screen coords, which are close to pixels.

    // Basic color used for the stove.
    private static final Color BASE_COLOR = new Color( 159, 182, 205 );

    // Valid range of heat values.
    private static DoubleRange heatRange = new DoubleRange( -1, 1 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final PImage fireImage;
    private final PImage iceImage;
    private final Property<Double> heatCoolLevel;
    private final PNode burner;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param heatCoolLevel Property that is set based on what the user does
     *                      when interacting with the slider.  Max value is 1
     *                      (max heat), min value is -1 (max cooling).
     * @param heatLabel     Textual label for the slider knob position that
     *                      corresponds to max heat.
     * @param coolLabel     Textual label for the slider knob position that
     *                      corresponds to max cooling.
     */
    public HeaterCoolerNode( Property<Double> heatCoolLevel, String heatLabel, String coolLabel ) {

        this.heatCoolLevel = heatCoolLevel;

        // Create the body of the stove.
        DoubleGeneralPath stoveBodyShape = new DoubleGeneralPath() {{

            // Draw the body of the burner.
            moveTo( 0, 0 ); // Start in upper left corner.
            curveTo( 0, BURNER_OPENING_HEIGHT / 2, WIDTH, BURNER_OPENING_HEIGHT / 2, WIDTH, 0 ); // Curve to upper right corner.
            lineTo( ( WIDTH + BOTTOM_WIDTH ) / 2, HEIGHT ); // Line to lower right corner.
            curveTo( ( WIDTH + BOTTOM_WIDTH ) / 2,
                     HEIGHT + BURNER_OPENING_HEIGHT / 2,
                     ( WIDTH - BOTTOM_WIDTH ) / 2,
                     HEIGHT + BURNER_OPENING_HEIGHT / 2,
                     ( WIDTH - BOTTOM_WIDTH ) / 2, HEIGHT ); // Curve to lower left corner.
            lineTo( 0, 0 ); // Line back to the upper left corner.
            closePath(); // Just to be sure.
        }};
        Paint stoveBodyPaint = new GradientPaint( 0, 0, ColorUtils.brighterColor( BASE_COLOR, 0.5 ), (float) WIDTH, 0, ColorUtils.darkerColor( BASE_COLOR, 0.5 ) );
        burner = new PhetPPath( stoveBodyShape.getGeneralPath(), stoveBodyPaint, new BasicStroke( 1 ), Color.BLACK );

        // Create the inside bowl of the burner, which is an ellipse.
        Shape burnerInteriorShape = new Ellipse2D.Double( 0, 0, WIDTH, BURNER_OPENING_HEIGHT );
        Paint burnerInteriorPaint = new GradientPaint( 0, 0, ColorUtils.darkerColor( BASE_COLOR, 0.25 ), (float) WIDTH, 0, ColorUtils.brighterColor( BASE_COLOR, 0.5 ) );
        PNode burnerInterior = new PhetPPath( burnerInteriorShape, burnerInteriorPaint, new BasicStroke( 1 ), Color.LIGHT_GRAY );

        // Create the slider.
        HeaterCoolerSliderNode stoveControlSlider = new HeaterCoolerSliderNode( HeaterCoolerNode.this.heatCoolLevel, heatLabel, coolLabel ) {{
            // Scale the slider to look reasonable on the body of the stove.  It
            // may be scaled differently for different translations.
            double maxWidth = WIDTH * 0.8;
            double maxHeight = HEIGHT * 0.8;
            double scale = Math.min( maxWidth / getFullBoundsReference().width,
                                     maxHeight / getFullBoundsReference().height );
            setScale( scale );
        }};

        // Add the images for fire and ice that come out of the stove.
        fireImage = new PImage( PiccoloPhetResources.getImage( "flame.png" ) );
        fireImage.setScale( ( WIDTH * 0.8 ) / fireImage.getFullBoundsReference().getWidth() );

        iceImage = new PImage( PiccoloPhetResources.getImage( "ice-cube-stack.png" ) );
        iceImage.setScale( ( WIDTH * 0.8 ) / iceImage.getFullBoundsReference().getWidth() );

        // Add the various components in the order needed to achieve the
        // desired layering.
        addChild( burnerInterior );
        addChild( fireImage );
        addChild( iceImage );
        addChild( burner );
        addChild( stoveControlSlider );

        // Do the layout.
        burnerInterior.setOffset( 0, -burnerInterior.getFullBoundsReference().height / 2 ); // Note - Goes a little negative in Y direction.
        stoveControlSlider.setOffset( WIDTH / 2 - stoveControlSlider.getFullBoundsReference().width / 2,
                                      HEIGHT / 2 - stoveControlSlider.getFullBoundsReference().height / 2 + burnerInterior.getFullBoundsReference().height / 2 );

        // Add a handler that updates the appearance when the heat-cool amount
        // changes.
        heatCoolLevel.addObserver( new VoidFunction1<Double>() {
            public void apply( Double heatCoolAmount ) {
                update();
            }
        } );

        // Add a key handler that will allow the user to use the arrow keys to
        // add and remove heat.
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                // Get the keyboard focus.
                event.getInputManager().setKeyboardFocus( event.getPath() );
            }

            @Override public void keyPressed( PInputEvent event ) {
                double numIncrements = 50;
                switch( event.getKeyCode() ) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_KP_UP:
                        HeaterCoolerNode.this.heatCoolLevel.set( Math.min( HeaterCoolerNode.this.heatCoolLevel.get() + heatRange.getLength() / numIncrements, 1 ) );
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_KP_DOWN:
                        HeaterCoolerNode.this.heatCoolLevel.set( Math.max( HeaterCoolerNode.this.heatCoolLevel.get() - heatRange.getLength() / numIncrements, -1 ) );
                        break;
                    case KeyEvent.VK_ESCAPE:
                    case KeyEvent.VK_0:
                    case KeyEvent.VK_NUMPAD0:
                    case KeyEvent.VK_ENTER:
                        HeaterCoolerNode.this.heatCoolLevel.set( 0.0 );
                        break;
                    default:
                        // Ignore the key.
                        break;
                }
            }
        } );

        update();
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    private void update() {
        if ( heatCoolLevel.get() > 0 ) {
            fireImage.setOffset( burner.getFullBoundsReference().width / 2 - fireImage.getFullBoundsReference().width / 2,
                                 -heatCoolLevel.get() * fireImage.getFullBoundsReference().height * 0.9 );
            iceImage.setOffset( burner.getFullBoundsReference().width / 2 - iceImage.getFullBoundsReference().width / 2, 0 );
        }
        else if ( heatCoolLevel.get() <= 0 ) {
            iceImage.setOffset( burner.getFullBoundsReference().width / 2 - iceImage.getFullBoundsReference().width / 2,
                                heatCoolLevel.get() * iceImage.getFullBoundsReference().height * 0.9 );
            fireImage.setOffset( burner.getFullBoundsReference().width / 2 - fireImage.getFullBoundsReference().width / 2, 0 );
        }
        iceImage.setVisible( heatCoolLevel.get() < 0 );
        fireImage.setVisible( heatCoolLevel.get() > 0 );
    }

    /**
     * This class is the slider that is used to control the StoveNode, causing it
     * to add heat or cooling to the simulated system.
     *
     * @author Sam Reid
     * @author John Blanco
     */
    public static class HeaterCoolerSliderNode extends VSliderNode {

        private static final Color TOP_SIDE_TRACK_COLOR = new Color( 255, 69, 0 );    // Meant to look warm.
        private static final Color BOTTOM_SIDE_TRACK_COLOR = new Color( 0, 0, 240 );  // Meant to look cold.

        private static final Font LABEL_FONT = new PhetFont( 20, true );

        public HeaterCoolerSliderNode( final SettableProperty<Double> value, String heatLabel, String coolLabel ) {
            super( UserComponents.heaterCoolerSlider, -1, 1, 6, 75, value, new BooleanProperty( true ) );

            // Show labels for add, zero, and remove.
            addLabel( +1, new PhetPText( heatLabel, LABEL_FONT ) );
            addLabel( 0.0, new TickMark() );
            addLabel( -1, new PhetPText( coolLabel, LABEL_FONT ) );

            // Return to 0 when the user releases the slider.
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    value.set( 0.0 );
                }
            } );


            // Show a gradient in the track that goes from orange to light blue to
            // indicate the heat/coolness setting.
            setTrackFillPaint( new GradientPaint( 0, 0, TOP_SIDE_TRACK_COLOR, 0, (float) trackLength, BOTTOM_SIDE_TRACK_COLOR, false ) );
        }

        // Convenience class for creating a tick mark that works for this slider.
        private static class TickMark extends PNode {
            private static final double INDENT = 4;
            private static final double LENGTH = 10;
            private static final float STROKE_WIDTH = 2;
            private static final Stroke STROKE = new BasicStroke( STROKE_WIDTH );

            private TickMark() {
                DoubleGeneralPath path = new DoubleGeneralPath( INDENT, STROKE_WIDTH / 2 ) {{
                    lineTo( INDENT + LENGTH, STROKE_WIDTH / 2 );
                }};
                addChild( new PhetPPath( path.getGeneralPath(), STROKE, Color.BLACK ) );
            }
        }
    }

    // Test harness.
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PiccoloTestFrame( "Heater-Cooler Node Test" ) {{
                    addNode( new PhetPPath( new Rectangle2D.Double( 0, 0, 1000, 1000 ), Color.black ) );
                    addNode( new HeaterCoolerNode( new Property<Double>( 0.0 ), "Heat", "Cool" ) {{
                        setOffset( 100, 200 );
                    }} );
                    setBackground( Color.black );
                }}.setVisible( true );
            }
        } );
    }
}
