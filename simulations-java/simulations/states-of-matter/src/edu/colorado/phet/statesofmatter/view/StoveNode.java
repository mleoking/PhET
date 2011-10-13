// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.statesofmatter.StatesOfMatterStrings.STOVE_CONTROL_PANEL_TITLE;

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
    private static final double WIDTH = 200; // In screen coords, which are close to pixels.

    // Basic color used for the stove.
    private static final Color BASE_COLOR = new Color( 159, 182, 205 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private PImage m_fireImage;
    private PImage m_iceImage;
    private PNode m_stoveControlBackground;
    private MultipleParticleModel m_model;
    private StoveControlSliderNode m_stoveControlSlider;

    // Heat value, ranges from -1 to +1.
    private Property<Double> m_heat = new Property<Double>( 0.0 );
    private PNode m_burner;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public StoveNode( MultipleParticleModel model, Paint backgroundPaint ) {

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

        // Create the burner, which is somewhat bowl-shaped.
        DoubleGeneralPath burnerShape = new DoubleGeneralPath( 0, 0 ) {{
            double burnerHeight = WIDTH * 0.3;
            lineTo( WIDTH * 0.20, burnerHeight );
            lineTo( WIDTH * 0.80, burnerHeight );
            lineTo( WIDTH, 0 );
            curveTo( WIDTH * 0.75, burnerHeight * 0.07, WIDTH * 0.25, burnerHeight * 0.07, 0, 0 );
            closePath();
        }};
        Paint burnerPaint = new GradientPaint( 0, 0, ColorUtils.brighterColor( BASE_COLOR, 0.5 ), (float) WIDTH, 0, ColorUtils.darkerColor( BASE_COLOR, 0.5 ) );
        m_burner = new PhetPPath( burnerShape.getGeneralPath(), burnerPaint, new BasicStroke( 1 ), Color.BLACK );

        // Create the inside of the burner, which is an ellipse.
        Shape burnerInteriorShape = new Ellipse2D.Double( 0, 0, WIDTH, WIDTH * 0.05 );
        Paint burnerInteriorPaint = new GradientPaint( 0, 0, ColorUtils.darkerColor( BASE_COLOR, 0.25 ), (float) WIDTH, 0, ColorUtils.brighterColor( BASE_COLOR, 0.5 ) );
        PNode burnerInterior = new PhetPPath( burnerInteriorShape, burnerInteriorPaint, new BasicStroke( 1 ), Color.LIGHT_GRAY );

        // Create the slider.
        m_stoveControlSlider = new StoveControlSliderNode( m_heat ) {{
            setScale( ( WIDTH - 20 ) / getFullBoundsReference().width );
        }};

        // Create the title label that goes above the slider.
        PhetPText sliderTitle = new PhetPText( STOVE_CONTROL_PANEL_TITLE, new PhetFont( 18, true ), Color.white );
        addChild( sliderTitle );

        // Create the bottom area of the stove, which is where the control resides.
        Shape controlBackgroundShape = new RoundRectangle2D.Double( 0, 0, WIDTH,
                                                                    m_stoveControlSlider.getFullBoundsReference().height + sliderTitle.getFullBoundsReference().height + 10,
                                                                    10, 10 );
        m_stoveControlBackground = new PhetPPath( controlBackgroundShape, BASE_COLOR );

        // Create the images that comprise the stove.
        m_fireImage = StatesOfMatterResources.getImageNode( "flame.png" );
        m_fireImage.setScale( ( WIDTH * 0.6 ) / m_fireImage.getFullBoundsReference().getWidth() );

        m_iceImage = StatesOfMatterResources.getImageNode( "ice-cube-stack.png" );
        m_iceImage.setScale( ( WIDTH * 0.6 ) / m_iceImage.getFullBoundsReference().getWidth() );

        // Add the various components in the order needed to achieve the
        // desired layering.
        addChild( burnerInterior );
        addChild( m_fireImage );
        addChild( m_iceImage );
        addChild( m_stoveControlBackground );
        addChild( m_burner );
        addChild( sliderTitle );
        addChild( m_stoveControlSlider );

        // Do the layout.
        double centerX = Math.max( m_burner.getFullBoundsReference().width, m_stoveControlBackground.getFullBoundsReference().width ) / 2;
        burnerInterior.setOffset( 0, -burnerInterior.getFullBoundsReference().height / 2 );
        m_burner.setOffset( centerX - m_burner.getFullBoundsReference().width / 2, 0 );
        m_stoveControlBackground.setOffset( centerX - m_stoveControlBackground.getFullBoundsReference().width / 2,
                                            m_burner.getFullBoundsReference().getMaxY() );
        m_stoveControlSlider.setOffset( centerX - m_stoveControlSlider.getFullBoundsReference().width / 2,
                                        m_stoveControlBackground.getFullBoundsReference().getMaxY() - m_stoveControlSlider.getFullBoundsReference().height - 10 );
        sliderTitle.setOffset( centerX - sliderTitle.getFullBoundsReference().width / 2,
                               m_stoveControlSlider.getFullBoundsReference().getMinY() - sliderTitle.getFullBoundsReference().height );

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
                    addNode( new StoveNode( null, Color.WHITE ) {{
                        setOffset( 100, 200 );
                    }} );
                    setBackground( Color.black );
                }}.setVisible( true );
            }
        } );
    }
}
