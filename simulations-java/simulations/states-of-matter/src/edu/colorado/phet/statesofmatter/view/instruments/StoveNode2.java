// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.view.instruments;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
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
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.view.StoveControlSliderNode2;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This class is the graphical representation of a stove that can be used to
 * heat or cool things.  This version has a control panel on the side, as
 * opposed to underneath.  It was prototyped in October 2011 to see if people
 * liked it better, but decided against.  This can be kept for a while in case
 * the team changes their collective mind, and deleted after 6 months or so if
 * still unused at that point in time.
 */
public class StoveNode2 extends PNode {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Width of the burner output - much of the rest of the size of the stove
    // derives from this value.
    private static final double BURNER_WIDTH = 200; // In screen coords, which are close to pixels.

    // Length of the connector between stove and control.
    private static final double CONNECTOR_LENGTH = BURNER_WIDTH * 0.2;
    private static final double CONNECTOR_HEIGHT = BURNER_WIDTH * 0.1;

    // Basic color used for the stove.
    private static final Color STOVE_BASIC_COLOR = new Color( 159, 182, 205 );

    // Basic color used for the connector.
    private static final Color CONNECTOR_BASIC_COLOR = new Color( 255, 185, 15 );

    // Stroke used for drawing shapes.
    private static final Stroke STROKE = new BasicStroke( 1 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private PImage m_fireImage;
    private PImage m_iceImage;
    private MultipleParticleModel m_model;

    // Heat value, ranges from -1 to +1.
    private Property<Double> m_heat = new Property<Double>( 0.0 );
    private PNode m_burner;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public StoveNode2( MultipleParticleModel model, Paint backgroundPaint ) {

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

        // Create the burner, which is somewhat bowl-shaped and has a bit of
        // faux 3D shape to it.
        DoubleGeneralPath burnerShape = new DoubleGeneralPath( 0, 0 ) {{
            double burnerHeight = BURNER_WIDTH * 0.3;
            lineTo( BURNER_WIDTH * 0.20, burnerHeight );
            lineTo( BURNER_WIDTH * 0.80, burnerHeight );
            lineTo( BURNER_WIDTH, 0 );
            curveTo( BURNER_WIDTH * 0.75, burnerHeight * 0.07, BURNER_WIDTH * 0.25, burnerHeight * 0.07, 0, 0 );
            closePath();
        }};
        Paint burnerPaint = new GradientPaint( 0, 0, ColorUtils.brighterColor( STOVE_BASIC_COLOR, 0.5 ), (float) BURNER_WIDTH, 0, ColorUtils.darkerColor( STOVE_BASIC_COLOR, 0.5 ) );
        m_burner = new PhetPPath( burnerShape.getGeneralPath(), burnerPaint, STROKE, Color.BLACK );

        // Create the inside of the burner, which is an ellipse.
        Shape burnerInteriorShape = new Ellipse2D.Double( 0, 0, BURNER_WIDTH, BURNER_WIDTH * 0.05 );
        Paint burnerInteriorPaint = new GradientPaint( 0, 0, ColorUtils.darkerColor( STOVE_BASIC_COLOR, 0.25 ), (float) BURNER_WIDTH, 0, ColorUtils.brighterColor( STOVE_BASIC_COLOR, 0.5 ) );
        PNode burnerInterior = new PhetPPath( burnerInteriorShape, burnerInteriorPaint, new BasicStroke( 1 ), Color.LIGHT_GRAY );

        // Create the bottom area of the stove, upon which the burner rests.
        // The height is chosen to look good, adjust as needed.
        RoundRectangle2D stoveBaseShape = new RoundRectangle2D.Double( 0, 0, BURNER_WIDTH, BURNER_WIDTH * 0.2, 10, 10 );
        PNode stoveBase = new PhetPPath( stoveBaseShape, STOVE_BASIC_COLOR, STROKE, Color.BLACK );

        // Need this var for sizing things below.
        final double totalHeight = stoveBaseShape.getHeight() + m_burner.getFullBoundsReference().getHeight();

        // Create the title for the control panel.
        final PhetPText controlPanelTitle = new PhetPText( StatesOfMatterStrings.STOVE_CONTROL_PANEL_TITLE, new PhetFont( 18, true ), Color.white );

        // Create the slider that controls heating and cooling.
        StoveControlSliderNode2 stoveControlSlider = new StoveControlSliderNode2( m_heat ) {{
            setScale( ( totalHeight - controlPanelTitle.getFullBoundsReference().height ) * 0.9 / getFullBoundsReference().height );
        }};

        // Create the control panel.
        double controlPanelWidth = Math.max( controlPanelTitle.getFullBoundsReference().width * 1.1, stoveControlSlider.getFullBoundsReference().width ) * 1.1;
        final RoundRectangle2D controlPanelShape = new RoundRectangle2D.Double( 0, 0, controlPanelWidth, totalHeight, 10, 10 );
        PNode m_controlPanel = new PhetPPath( controlPanelShape, STOVE_BASIC_COLOR, STROKE, Color.BLACK );

        // Layout the control panel.
        controlPanelTitle.setOffset( controlPanelWidth / 2 - controlPanelTitle.getFullBoundsReference().width / 2, 0 );
        m_controlPanel.addChild( controlPanelTitle );
        stoveControlSlider.setOffset( controlPanelWidth / 2 - stoveControlSlider.getFullBoundsReference().width / 2,
                                      controlPanelTitle.getFullBoundsReference().getMaxY() );
        m_controlPanel.addChild( stoveControlSlider );

        // Create the connector that goes from the control panel to the burner.
        Paint connectorPaint = new GradientPaint( 0, 0, ColorUtils.brighterColor( CONNECTOR_BASIC_COLOR, 0.5 ), 0, (float) CONNECTOR_HEIGHT, ColorUtils.darkerColor( CONNECTOR_BASIC_COLOR, 0.5 ) );
        PNode m_connector = new PhetPPath( new Rectangle2D.Double( 0, 0, CONNECTOR_LENGTH, CONNECTOR_HEIGHT ), connectorPaint );

        // Create the images that are shown when heating and cooling are in progress.
        m_fireImage = StatesOfMatterResources.getImageNode( "flame.png" );
        m_fireImage.setScale( ( BURNER_WIDTH * 0.6 ) / m_fireImage.getFullBoundsReference().getWidth() );

        m_iceImage = StatesOfMatterResources.getImageNode( "ice-cube-stack.png" );
        m_iceImage.setScale( ( BURNER_WIDTH * 0.6 ) / m_iceImage.getFullBoundsReference().getWidth() );

        // Add the various components in the order needed to achieve the
        // desired layering.
        addChild( burnerInterior );
        addChild( m_fireImage );
        addChild( m_iceImage );
        addChild( stoveBase );
        addChild( m_controlPanel );
        addChild( m_connector );
        addChild( m_burner );

        // Do the layout.
        double centerX = Math.max( m_burner.getFullBoundsReference().width, stoveBase.getFullBoundsReference().width ) / 2;
        burnerInterior.setOffset( 0, -burnerInterior.getFullBoundsReference().height / 2 );
        m_burner.setOffset( centerX - m_burner.getFullBoundsReference().width / 2, 0 );
        stoveBase.setOffset( centerX - stoveBase.getFullBoundsReference().width / 2,
                             m_burner.getFullBoundsReference().getMaxY() );
        m_connector.setOffset( stoveBase.getFullBoundsReference().getMaxX(),
                               stoveBase.getFullBoundsReference().getCenterY() - m_connector.getFullBoundsReference().height / 2 );
        m_controlPanel.setOffset( m_connector.getFullBoundsReference().getMaxX(),
                                  stoveBase.getFullBoundsReference().getMaxY() - m_controlPanel.getFullBoundsReference().height );

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
                    addNode( new StoveNode2( null, Color.WHITE ) {{
                        setOffset( 100, 200 );
                    }} );
                    setBackground( Color.black );
                }}.setVisible( true );
            }
        } );
    }
}
