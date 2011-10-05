// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

import static edu.colorado.phet.statesofmatter.StatesOfMatterStrings.STOVE_CONTROL_PANEL_TITLE;

/**
 * This class is the graphical representation of a stove that can be used to
 * heat or cool things.
 */
public class StoveNode extends PNode {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Width of the burner output - much of the reset of the size of the stove
    // derives from this value.
    private static final double BURNER_OUTPUT_WIDTH = 200;

    // Scaling used to set relative size of burner to control panel.  Tweak
    // as needed.
    private static final double INITIAL_STOVE_SCALING = 1.5;

    // Basic color used for the stove.
    private static final Color BASE_COLOR = Color.LIGHT_GRAY;

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

                public void containerExploded() {
                    m_heat.set( 0.0 );
                }
            } );
        }

        // Create the burner, which is somewhat bowl-shaped.
        DoubleGeneralPath burnerShape = new DoubleGeneralPath( 0, 0 ) {{
            double burnerHeight = BURNER_OUTPUT_WIDTH * 0.3;
            lineTo( BURNER_OUTPUT_WIDTH * 0.25, burnerHeight );
            lineTo( BURNER_OUTPUT_WIDTH * 0.75, burnerHeight );
            lineTo( BURNER_OUTPUT_WIDTH, 0 );
            closePath();
        }};
        PNode burner = new PhetPPath( burnerShape.getGeneralPath(), BASE_COLOR, new BasicStroke( 1 ), Color.BLACK );

        // Create the slider.
        m_stoveControlSlider = new StoveControlSliderNode( m_heat );

        // Create the title label that goes above the slider.
        PhetPText sliderTitle = new PhetPText( STOVE_CONTROL_PANEL_TITLE, new PhetFont( 16, true ), Color.white );
        addChild( sliderTitle );

        // Create the bottom area of the stove, which is where the control
        // resides.
        Shape controlBackgroundShape = new RoundRectangle2D.Double( 0, 0,
                                                                    m_stoveControlSlider.getFullBoundsReference().width + 20,
                                                                    m_stoveControlSlider.getFullBoundsReference().height + sliderTitle.getFullBoundsReference().height + 30,
                                                                    10, 10 );
        m_stoveControlBackground = new PhetPPath( controlBackgroundShape, BASE_COLOR );

        // Create the images that comprise the stove.
        m_fireImage = StatesOfMatterResources.getImageNode( "flames.gif" );
        m_fireImage.setScale( INITIAL_STOVE_SCALING );

        m_iceImage = StatesOfMatterResources.getImageNode( "ice.gif" );
        m_iceImage.setScale( INITIAL_STOVE_SCALING );

        /*
         * Put a background between the ice/fire and the stove, so we don't 
         * see them poking out below the top of the stove.  The background
         * color should match the color of whatever this node is placed on.
         */
        PPath backgroundNode = new PPath( m_stoveControlBackground.getFullBoundsReference() );
        backgroundNode.setPaint( backgroundPaint );
        backgroundNode.setStroke( null );

        // Add the various components in the order needed to achieve the
        // desired layering.
        addChild( m_fireImage );
        addChild( m_iceImage );
//        addChild( backgroundNode );
        addChild( m_stoveControlBackground );
        addChild( burner );
        addChild( sliderTitle );
        ZeroOffsetNode zeroedSliderNode = new ZeroOffsetNode( m_stoveControlSlider );
        addChild( zeroedSliderNode ); // TODO: Remove the ZeroOffsetNode with issues with HSliderNode are resolved.

        // Do the layout.
        double centerX = Math.max( burner.getFullBoundsReference().width, m_stoveControlBackground.getFullBoundsReference().width ) / 2;
        burner.setOffset( centerX - burner.getFullBoundsReference().width / 2, 0 );
        m_stoveControlBackground.setOffset( centerX - m_stoveControlBackground.getFullBoundsReference().width / 2,
                                            burner.getFullBoundsReference().getMaxY() );
        zeroedSliderNode.setOffset( centerX - m_stoveControlSlider.getFullBoundsReference().width / 2,
                                    m_stoveControlBackground.getFullBoundsReference().getMaxY() - m_stoveControlSlider.getFullBoundsReference().height - 10 );
        sliderTitle.setOffset( centerX - sliderTitle.getFullBoundsReference().width / 2,
                               zeroedSliderNode.getFullBoundsReference().getMinY() - sliderTitle.getFullBoundsReference().height );

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
            m_fireImage.setOffset( m_stoveControlBackground.getFullBoundsReference().width / 2 - m_fireImage.getFullBoundsReference().width / 2,
                                   -m_heat.get() * m_stoveControlBackground.getFullBoundsReference().height );
            m_iceImage.setOffset( m_stoveControlBackground.getFullBoundsReference().width / 2 - m_iceImage.getFullBoundsReference().width / 2, 0 );
        }
        else if ( m_heat.get() <= 0 ) {
            m_iceImage.setOffset( m_stoveControlBackground.getFullBoundsReference().width / 2 - m_iceImage.getFullBoundsReference().width / 2,
                                  m_heat.get() * m_stoveControlBackground.getFullBoundsReference().height );
            m_fireImage.setOffset( m_stoveControlBackground.getFullBoundsReference().width / 2 - m_fireImage.getFullBoundsReference().width / 2, 0 );
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
