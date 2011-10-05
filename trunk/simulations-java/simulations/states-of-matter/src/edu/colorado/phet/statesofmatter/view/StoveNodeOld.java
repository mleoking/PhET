// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

import static edu.colorado.phet.statesofmatter.StatesOfMatterStrings.STOVE_CONTROL_PANEL_TITLE;


public class StoveNodeOld extends PNode {

    // Offset in Y direction for stove, tweak as needed.
    private static final double BURNER_Y_OFFSET = 20;

    // Scaling used to set relative size of burner to control panel.  Tweak
    // as needed.
    private static final double INITIAL_STOVE_SCALING = 1.5;

    private PImage m_fireImage;
    private PImage m_iceImage;
    private PImage m_stoveImage;
    private MultipleParticleModel m_model;
    private StoveControlSliderNode m_stoveControlSlider;

    // Heat value, ranges from -1 to +1.
    private Property<Double> m_heat = new Property<Double>( 0.0 );

    public StoveNodeOld( MultipleParticleModel model, Paint backgroundPaint ) {

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

        // Create the images that comprise the stove.
        m_fireImage = StatesOfMatterResources.getImageNode( "flames.gif" );
        m_fireImage.setOffset( 0, BURNER_Y_OFFSET );
        m_fireImage.setScale( INITIAL_STOVE_SCALING );

        m_iceImage = StatesOfMatterResources.getImageNode( "ice.gif" );
        m_iceImage.setOffset( 0, BURNER_Y_OFFSET );
        m_iceImage.setScale( INITIAL_STOVE_SCALING );

        m_stoveImage = StatesOfMatterResources.getImageNode( "stove.png" );
        m_stoveImage.setOffset( 0, BURNER_Y_OFFSET );
        m_stoveImage.setScale( INITIAL_STOVE_SCALING );

        /*
         * Put a background between the ice/fire and the stove, so we don't 
         * see them poking out below the top of the stove.  The background
         * color should match the color of whatever this node is placed on.
         */
        PPath backgroundNode = new PPath( m_stoveImage.getFullBoundsReference() );
        backgroundNode.setPaint( backgroundPaint );
        backgroundNode.setStroke( null );

        addChild( m_fireImage );
        addChild( m_iceImage );
        addChild( backgroundNode );
        addChild( m_stoveImage );

        m_heat.addObserver( new VoidFunction1<Double>() {
            public void apply( Double heat ) {
                update();
                if ( m_model != null ) {
                    m_model.setHeatingCoolingAmount( heat );
                }
            }
        } );

        //Create the slider, which has its own labels
        m_stoveControlSlider = new StoveControlSliderNode( m_heat ) {{
            setOffset( m_stoveImage.getFullBoundsReference().getWidth() + 15, 15 );
        }};
        addChild( m_stoveControlSlider );

        //Create the title label above the slider
        addChild( new PhetPText( STOVE_CONTROL_PANEL_TITLE, new PhetFont( 16, true ), Color.white ) {{
            setOffset( m_stoveControlSlider.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, m_stoveControlSlider.getFullBounds().getY() - getFullBounds().getHeight() + 3 );
        }} );

        update();
    }

    private void update() {
        if ( m_heat.get() > 0 ) {
            m_fireImage.setOffset( m_stoveImage.getFullBoundsReference().width / 2 - m_fireImage.getFullBoundsReference().width / 2,
                                   -m_heat.get() * m_stoveImage.getFullBoundsReference().height + BURNER_Y_OFFSET );
            m_iceImage.setOffset( m_stoveImage.getFullBoundsReference().width / 2 - m_iceImage.getFullBoundsReference().width / 2, 0 );
        }
        else if ( m_heat.get() <= 0 ) {
            m_iceImage.setOffset( m_stoveImage.getFullBoundsReference().width / 2 - m_iceImage.getFullBoundsReference().width / 2,
                                  m_heat.get() * m_stoveImage.getFullBoundsReference().height + BURNER_Y_OFFSET );
            m_fireImage.setOffset( m_stoveImage.getFullBoundsReference().width / 2 - m_fireImage.getFullBoundsReference().width / 2, 0 );
        }
        m_iceImage.setVisible( m_heat.get() < 0 );
        m_fireImage.setVisible( m_heat.get() > 0 );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PiccoloTestFrame( "Stove Node Test" ) {{
                    addNode( new PhetPPath( new Rectangle2D.Double( 0, 0, 1000, 1000 ), Color.black ) );
                    addNode( new StoveNodeOld( null, Color.WHITE ) {{
                        setOffset( 100, 200 );
                    }} );
                    setBackground( Color.black );
                }}.setVisible( true );
            }
        } );
    }
}
