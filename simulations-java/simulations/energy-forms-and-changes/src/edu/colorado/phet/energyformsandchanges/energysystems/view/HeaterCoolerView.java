// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloPhetResources;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * A view element that represents a device that can be used to heat or cool
 * things.  This is based on the HeaterCoolerNode that was in common code as
 * of early October 2012, but is different in that it doesn't extend PNode.
 * Instead, it provides an API that enables the user to get the front and back
 * representations separately so that they can be layered on a canvas and other
 * view elements can be placed between them.
 *
 * @author John Blanco
 */
public class HeaterCoolerView {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Basic color used for the stove.
    private static final Color BASE_COLOR = new Color( 159, 182, 205 );

    // Valid range of heat values.
    private static final DoubleRange heatRange = new DoubleRange( -1, 1 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final PImage fireImage;
    private final PImage iceImage;
    private final Property<Double> heatCoolLevel;
    private final PNode burner;

    // This class maintains two layers and makes those layers available via its
    // API.  This is done so that its parts can be added to different layers,
    // thus making more easy to make things look like they are emerging from
    // the burner.
    private final PNode holeLayer = new PNode();
    private final PNode frontLayer = new PNode();
    private final PNode energyChunkLayer = new PNode();

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
     * @param snapToZero    Controls whether the slider will snap to the off
     */
    public HeaterCoolerView( Property<Double> heatCoolLevel, boolean heatEnabled, boolean coolEnabled, String heatLabel,
                             String coolLabel, final double width, final double height, final double openingHeight,
                             boolean snapToZero, final ObservableList<EnergyChunk> energyChunkList, final ModelViewTransform mvt ) {

        this.heatCoolLevel = heatCoolLevel;

        // Create the body of the stove.
        DoubleGeneralPath stoveBodyShape = new DoubleGeneralPath() {{

            // Draw the body of the burner.
            moveTo( 0, 0 ); // Start in upper left corner.
            curveTo( 0, openingHeight / 2, width, openingHeight / 2, width, 0 ); // Curve to upper right corner.
            double bottomWidth = width * 0.8;
            lineTo( ( width + bottomWidth ) / 2, height ); // Line to lower right corner.
            curveTo( ( width + bottomWidth ) / 2,
                     height + openingHeight / 2,
                     ( width - bottomWidth ) / 2,
                     height + openingHeight / 2,
                     ( width - bottomWidth ) / 2, height ); // Curve to lower left corner.
            lineTo( 0, 0 ); // Line back to the upper left corner.
            closePath(); // Just to be sure.
        }};
        Paint stoveBodyPaint = new GradientPaint( 0, 0, ColorUtils.brighterColor( BASE_COLOR, 0.5 ), (float) width, 0, ColorUtils.darkerColor( BASE_COLOR, 0.5 ) );
        burner = new PhetPPath( stoveBodyShape.getGeneralPath(), stoveBodyPaint, new BasicStroke( 1 ), Color.BLACK );

        // Create the inside bowl of the burner, which is an ellipse.
        Shape burnerInteriorShape = new Ellipse2D.Double( 0, 0, width, openingHeight );
        Paint burnerInteriorPaint = new GradientPaint( 0, 0, ColorUtils.darkerColor( BASE_COLOR, 0.25 ), (float) width, 0, ColorUtils.brighterColor( BASE_COLOR, 0.5 ) );
        PNode burnerInterior = new PhetPPath( burnerInteriorShape, burnerInteriorPaint, new BasicStroke( 1 ), Color.LIGHT_GRAY );

        // Create the slider.
        HeaterCoolerSliderNode stoveControlSlider = new HeaterCoolerSliderNode( HeaterCoolerView.this.heatCoolLevel, heatEnabled, heatLabel, coolEnabled, coolLabel, snapToZero ) {{
            // Scale the slider to look reasonable on the body of the stove. It
            // may be scaled differently for different translations.
            double maxWidth = width * 0.8;
            double maxHeight = height * 0.8;
            double sliderScale = Math.min( maxWidth / getFullBoundsReference().width,
                                           maxHeight / getFullBoundsReference().height );
            setScale( sliderScale );
        }};

        // Add the images for fire and ice that come out of the stove.
        fireImage = new PImage( PiccoloPhetResources.getImage( "flame.png" ) );
        fireImage.setScale( ( width * 0.8 ) / fireImage.getFullBoundsReference().getWidth() );

        iceImage = new PImage( PiccoloPhetResources.getImage( "ice-cube-stack.png" ) );
        iceImage.setScale( ( width * 0.8 ) / iceImage.getFullBoundsReference().getWidth() );

        // Monitor energy chunks and add/remove as needed.
        energyChunkList.addElementAddedObserver( new VoidFunction1<EnergyChunk>() {
            public void apply( final EnergyChunk addedEnergyChunk ) {
                final PNode energyChunkNode = new EnergyChunkNode( addedEnergyChunk, mvt );
                energyChunkLayer.addChild( energyChunkNode );
                energyChunkList.addElementRemovedObserver( new VoidFunction1<EnergyChunk>() {
                    public void apply( EnergyChunk removedEnergyChunk ) {
                        if ( removedEnergyChunk == addedEnergyChunk ) {
                            energyChunkLayer.removeChild( energyChunkNode );
                            energyChunkList.removeElementRemovedObserver( this );
                        }
                    }
                } );
            }
        } );

        // Add the various components in the order needed to achieve the
        // desired layering.
        holeLayer.addChild( burnerInterior );
        holeLayer.addChild( fireImage );
        holeLayer.addChild( iceImage );
        holeLayer.addChild( energyChunkLayer );
        frontLayer.addChild( burner );
        frontLayer.addChild( stoveControlSlider );

        // Do the layout.
        burnerInterior.setOffset( 0, -burnerInterior.getFullBoundsReference().height / 2 ); // Note - Goes a little negative in Y direction.
        stoveControlSlider.setOffset( width / 2 - stoveControlSlider.getFullBoundsReference().width / 2,
                                      height / 2 - stoveControlSlider.getFullBoundsReference().height / 2 + burnerInterior.getFullBoundsReference().height / 2 );

        // Add a handler that updates the appearance when the heat-cool amount
        // changes.
        heatCoolLevel.addObserver( new VoidFunction1<Double>() {
            public void apply( Double heatCoolAmount ) {
                update();
            }
        } );

        // Add a key handler that will allow the user to use the arrow keys to
        // add and remove heat.
        frontLayer.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                // Get the keyboard focus.
                event.getInputManager().setKeyboardFocus( event.getPath() );
            }

            @Override public void keyPressed( PInputEvent event ) {
                double numIncrements = 50;
                switch( event.getKeyCode() ) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_KP_UP:
                        HeaterCoolerView.this.heatCoolLevel.set( Math.min( HeaterCoolerView.this.heatCoolLevel.get() + heatRange.getLength() / numIncrements, 1 ) );
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_KP_DOWN:
                        HeaterCoolerView.this.heatCoolLevel.set( Math.max( HeaterCoolerView.this.heatCoolLevel.get() - heatRange.getLength() / numIncrements, -1 ) );
                        break;
                    case KeyEvent.VK_ESCAPE:
                    case KeyEvent.VK_0:
                    case KeyEvent.VK_NUMPAD0:
                    case KeyEvent.VK_ENTER:
                        HeaterCoolerView.this.heatCoolLevel.set( 0.0 );
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

    public PNode getHoleNode() {
        return holeLayer;
    }

    public PNode getFrontNode() {
        return frontLayer;
    }

    public void setOffset( double x, double y ) {
        holeLayer.setOffset( x, y );
        energyChunkLayer.setOffset( -x, -y ); // The energy chunk layer must remain in global space.
        frontLayer.setOffset( x, y );
    }

    public void setOffset( Point2D offset ) {
        setOffset( offset.getX(), offset.getY() );
    }

    /**
     * This class is the slider that is used to control the StoveNode, causing
     * it to add heat or cooling to the simulated system.
     *
     * @author Sam Reid
     * @author John Blanco
     */
    public static class HeaterCoolerSliderNode extends PNode {

        private static final Color TOP_SIDE_TRACK_COLOR = new Color( 255, 69, 0 );    // Meant to look warm.
        private static final Color BOTTOM_SIDE_TRACK_COLOR = new Color( 0, 0, 240 );  // Meant to look cold.
        private static final double ALWAYS_SNAP_THRESHOLD = 1.0 / 8.0;

        private static final Font LABEL_FONT = new PhetFont( 20, true );

        public HeaterCoolerSliderNode( final SettableProperty<Double> value, boolean heatingEnabled, String heatLabel, boolean coolingEnabled,
                                       String coolLabel, final boolean snapToZero ) {
            if ( !( heatingEnabled || coolingEnabled ) ) {
                throw new IllegalArgumentException( "Either heating or cooling must be enabled." );
            }

            VSliderNode slider = new VSliderNode( UserComponents.heaterCoolerSlider,
                                                  coolingEnabled ? -1 : 0,
                                                  heatingEnabled ? 1 : 0,
                                                  6,
                                                  75,
                                                  value,
                                                  new BooleanProperty( true ) );

            // Show labels for add, zero, and remove.
            slider.addLabel( 0.0, new TickMark() );
            slider.addLabel( +1, new PhetPText( heatLabel, LABEL_FONT ) );
            slider.addLabel( -1, new PhetPText( coolLabel, LABEL_FONT ) );

            // Return to 0 when the user releases the slider.
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    if ( snapToZero || Math.abs( value.get() ) < ALWAYS_SNAP_THRESHOLD ){
                        value.set( 0.0 );
                    }
                }
            } );

            // Show a gradient in the track that goes from orange to light blue to
            // indicate the heat/coolness setting.
            slider.setTrackFillPaint( new GradientPaint( 0, 0, TOP_SIDE_TRACK_COLOR, 0, (float) slider.trackLength, BOTTOM_SIDE_TRACK_COLOR, false ) );

            addChild( slider );
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
}
