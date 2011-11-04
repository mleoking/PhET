// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This class is the graphical representation of a stove that can be used to
 * heat or cool things.
 */
public class StoveNode extends PNode {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Width of the burner output - much of the rest of the size of the stove
    // derives from this value.
    private static final double WIDTH = 250; // In screen coords, which are close to pixels.
    private static final double HEIGHT = WIDTH * 0.75; // In screen coords, which are close to pixels.
    private static final double BURNER_OPENING_HEIGHT = WIDTH * 0.1; // In screen coords, which are close to pixels.

    // Basic color used for the stove.
    private static final Color BASE_COLOR = new Color( 159, 182, 205 );

    // Valid range of heat values.
    private static DoubleRange heatRange = new DoubleRange( -1, 1 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final PImage m_fireImage;
    private final PImage m_iceImage;
    private final MultipleParticleModel m_model;

    // Heat value, ranges from -1 to +1.
    private final Property<Double> m_heat = new Property<Double>( 0.0 );
    private final PNode m_burner;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public StoveNode( MultipleParticleModel model ) {

        m_model = model;

        // Register for events from the model that will affect the stove.
        if ( m_model != null ) {
            m_model.addListener( new MultipleParticleModel.Adapter() {
                public void resetOccurred() {
                    m_heat.set( 0.0 );
                }

                public void moleculeTypeChanged() {
                    m_heat.set( 0.0 );
                }

                public void containerExplodedStateChanged( boolean containerExploded ) {
                    m_heat.set( 0.0 );
                }
            } );
        }

        // Create the body of the stove.
        DoubleGeneralPath stoveBodyShape = new DoubleGeneralPath() {{
            double bottomWidth = WIDTH * 0.8;
            moveTo( 0, 0 ); // Start in upper left corner.
            curveTo( 0, BURNER_OPENING_HEIGHT / 2, WIDTH, BURNER_OPENING_HEIGHT / 2, WIDTH, 0 ); // Curve to upper right corner.
            lineTo( ( WIDTH + bottomWidth ) / 2, HEIGHT ); // Line to lower right corner.
            curveTo( ( WIDTH + bottomWidth ) / 2,
                     HEIGHT + BURNER_OPENING_HEIGHT / 2,
                     ( WIDTH - bottomWidth ) / 2,
                     HEIGHT + BURNER_OPENING_HEIGHT / 2,
                     ( WIDTH - bottomWidth ) / 2, HEIGHT ); // Curve to lower left corner.
            lineTo( 0, 0 ); // Line back to the upper left corner.
            closePath(); // Just to be sure.
        }};
        Paint stoveBodyPaint = new GradientPaint( 0, 0, ColorUtils.brighterColor( BASE_COLOR, 0.5 ), (float) WIDTH, 0, ColorUtils.darkerColor( BASE_COLOR, 0.5 ) );
        m_burner = new PhetPPath( stoveBodyShape.getGeneralPath(), stoveBodyPaint, new BasicStroke( 1 ), Color.BLACK );

        // Create the inside bowl of the burner, which is an ellipse.
        Shape burnerInteriorShape = new Ellipse2D.Double( 0, 0, WIDTH, BURNER_OPENING_HEIGHT );
        Paint burnerInteriorPaint = new GradientPaint( 0, 0, ColorUtils.darkerColor( BASE_COLOR, 0.25 ), (float) WIDTH, 0, ColorUtils.brighterColor( BASE_COLOR, 0.5 ) );
        PNode burnerInterior = new PhetPPath( burnerInteriorShape, burnerInteriorPaint, new BasicStroke( 1 ), Color.LIGHT_GRAY );

        // Create the slider.
        StoveControlSliderNode stoveControlSlider = new StoveControlSliderNode( m_heat ) {{
            // Scale the slider to look reasonable on the body of the stove.  It
            // may be scaled differently for different translations.
            double maxWidth = WIDTH * 0.8;
            double maxHeight = HEIGHT * 0.8;
            double scale = Math.min( maxWidth / getFullBoundsReference().width,
                                     maxHeight / getFullBoundsReference().height );
            setScale( scale );
        }};

        // Add the images for fire and ice that come out of the stove.
        m_fireImage = StatesOfMatterResources.getImageNode( "flame.png" );
        m_fireImage.setScale( ( WIDTH * 0.8 ) / m_fireImage.getFullBoundsReference().getWidth() );

        m_iceImage = StatesOfMatterResources.getImageNode( "ice-cube-stack.png" );
        m_iceImage.setScale( ( WIDTH * 0.8 ) / m_iceImage.getFullBoundsReference().getWidth() );

        // Add the various components in the order needed to achieve the
        // desired layering.
        addChild( burnerInterior );
        addChild( m_fireImage );
        addChild( m_iceImage );
        addChild( m_burner );
        addChild( stoveControlSlider );

        // Do the layout.
        burnerInterior.setOffset( 0, -burnerInterior.getFullBoundsReference().height / 2 ); // Note - Goes a little negative in Y direction.
        stoveControlSlider.setOffset( WIDTH / 2 - stoveControlSlider.getFullBoundsReference().width / 2,
                                      HEIGHT / 2 - stoveControlSlider.getFullBoundsReference().height / 2 + burnerInterior.getFullBoundsReference().height / 2 );

        // Observe the heat value and set the model heating/cooling amount
        // accordingly.
        m_heat.addObserver( new VoidFunction1<Double>() {
            public void apply( Double heat ) {
                update();
                if ( m_model != null ) {
                    m_model.setHeatingCoolingAmount( heat );
                }
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
                        m_heat.set( Math.min( m_heat.get() + heatRange.getLength() / numIncrements, 1 ) );
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_KP_DOWN:
                        m_heat.set( Math.max( m_heat.get() - heatRange.getLength() / numIncrements, -1 ) );
                        break;
                    case KeyEvent.VK_ESCAPE:
                    case KeyEvent.VK_0:
                    case KeyEvent.VK_NUMPAD0:
                    case KeyEvent.VK_ENTER:
                        m_heat.set( 0.0 );
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
        if ( m_heat.get() > 0 ) {
            m_fireImage.setOffset( m_burner.getFullBoundsReference().width / 2 - m_fireImage.getFullBoundsReference().width / 2,
                                   -m_heat.get() * m_fireImage.getFullBoundsReference().height * 0.9 );
            m_iceImage.setOffset( m_burner.getFullBoundsReference().width / 2 - m_iceImage.getFullBoundsReference().width / 2, 0 );
        }
        else if ( m_heat.get() <= 0 ) {
            m_iceImage.setOffset( m_burner.getFullBoundsReference().width / 2 - m_iceImage.getFullBoundsReference().width / 2,
                                  m_heat.get() * m_iceImage.getFullBoundsReference().height * 0.9 );
            m_fireImage.setOffset( m_burner.getFullBoundsReference().width / 2 - m_fireImage.getFullBoundsReference().width / 2, 0 );
        }
        m_iceImage.setVisible( m_heat.get() < 0 );
        m_fireImage.setVisible( m_heat.get() > 0 );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PiccoloTestFrame( "Stove Node Test" ) {{
                    addNode( new PhetPPath( new Rectangle2D.Double( 0, 0, 1000, 1000 ), Color.black ) );
                    addNode( new StoveNode( null ) {{
                        setOffset( 100, 200 );
                    }} );
                    setBackground( Color.black );
                }}.setVisible( true );
            }
        } );
    }
}
