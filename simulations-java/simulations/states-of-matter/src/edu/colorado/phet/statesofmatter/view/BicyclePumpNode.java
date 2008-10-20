/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.statesofmatter.model.AbstractMultipleParticleModel;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Rectangle2D;

/**
 * This class represents creates a graphical display that is meant to look
 * like a bicycle pump.  It allows the user to interact with it to move the
 * handle up and down.
 *
 * @author John Blanco
 */
public class BicyclePumpNode extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // The follow constants define the size and positions of the various
    // components of the pump as proportions of the overall width and height
    // of the node.
    private static final double PUMP_HORIZ_POSITION_PROPORTION = 0.85;
    private static final double PUMP_BASE_WIDTH_PROPORTION = 0.4;
    private static final double PUMP_BASE_HEIGHT_PROPORTION = 0.02;
    private static final Color PUMP_BASE_COLOR = new Color( 0xbb8855 );
    private static final double PUMP_BODY_HEIGHT_PROPORTION = 0.75;
    private static final double PUMP_BODY_WIDTH_PROPORTION = 0.1;
    private static final Color PUMP_BODY_COLOR = Color.red;
    private static final double PUMP_SHAFT_WIDTH_PROPORTION = PUMP_BODY_WIDTH_PROPORTION * 0.25;
    private static final double PUMP_SHAFT_HEIGHT_PROPORTION = PUMP_BODY_HEIGHT_PROPORTION;
    private static final Color PUMP_SHAFT_COLOR = Color.LIGHT_GRAY;
    private static final double PUMP_HANDLE_WIDTH_PROPORTION = 0.35;
    private static final double PUMP_HANDLE_HEIGHT_PROPORTION = 0.02;
    private static final double PUMP_HANDLE_INIT_VERT_POS_PROPORTION = PUMP_BODY_HEIGHT_PROPORTION * 1.1;
    private static final Color PUMP_HANDLE_COLOR = new Color( 0xddaa77 );
    private static final double HOSE_CONNECTOR_HEIGHT_PROPORTION = 0.04;
    private static final double HOSE_CONNECTOR_WIDTH_PROPORTION = 0.05;
    private static final double HOSE_CONNECTOR_VERT_POS_PROPORTION = 0.5;
    private static final Color HOSE_CONNECTOR_COLOR = new Color( 0xffff99 );
    private static final double HOSE_ATTACH_VERT_POS_PROPORTION = 0.075;
    private static final Color HOSE_COLOR = Color.WHITE;
    private static final double HOSE_WIDTH_PROPORTION = 0.02;
    private static final double PUMPING_REQUIRED_TO_INJECT_PROPORTION = PUMP_SHAFT_HEIGHT_PROPORTION / 10;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    AbstractMultipleParticleModel m_model;
    PPath m_pumpHandle;
    double m_currentHandleOffset;
    double m_maxHandleOffset;
    double m_pumpingRequiredToInject;
    double m_currentPumpingAmount;
    PPath m_pumpShaft;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    public BicyclePumpNode( double width, double height, AbstractMultipleParticleModel model ) {

        // Initialize local variables.
        m_model = model;
        m_pumpingRequiredToInject = height * PUMPING_REQUIRED_TO_INJECT_PROPORTION;
        m_currentPumpingAmount = 0;
        
        // Add the base of the pump.
        double pumpBaseWidth = width * PUMP_BASE_WIDTH_PROPORTION;
        double pumpBaseHeight = height * PUMP_BASE_HEIGHT_PROPORTION;
        PPath pumpBase = new PPath( new Rectangle2D.Double( 0, 0, pumpBaseWidth, pumpBaseHeight ) );
        pumpBase.setPaint( PUMP_BASE_COLOR );
        pumpBase.setOffset( width * PUMP_HORIZ_POSITION_PROPORTION - ( pumpBaseWidth / 2 ), height - pumpBaseHeight );
        pumpBase.setPickable( false );
        addChild( pumpBase );

        // Add the handle of the pump.  This is the node that the user will
        // interact with in order to use the pump.
        double pumpHandleWidth = width * PUMP_HANDLE_WIDTH_PROPORTION;
        double pumpHandleHeight = height * PUMP_HANDLE_HEIGHT_PROPORTION;
        m_pumpHandle = new PPath( new Rectangle2D.Double( 0, 0, pumpHandleWidth, pumpHandleHeight ) );
        m_pumpHandle.setPaint( PUMP_HANDLE_COLOR );
        m_pumpHandle.setOffset( width * PUMP_HORIZ_POSITION_PROPORTION - ( pumpHandleWidth / 2 ), height - ( height * PUMP_HANDLE_INIT_VERT_POS_PROPORTION ) - pumpHandleHeight );
        m_pumpHandle.setPickable( true );
        m_pumpHandle.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        m_currentHandleOffset = 0;
        m_maxHandleOffset = -PUMP_SHAFT_HEIGHT_PROPORTION * height / 2;
        addChild( m_pumpHandle );

        // Set ourself up to listen for and handle mouse dragging events on
        // the handle.
        m_pumpHandle.addInputEventListener( new PDragEventHandler() {

            public void startDrag( PInputEvent event ) {
                super.startDrag( event );
            }

            public void drag( PInputEvent event ) {
                PDimension d = event.getDeltaRelativeTo( m_pumpHandle );
                m_pumpHandle.localToParent( d );
                if ( ( m_currentHandleOffset + d.getHeight() >= m_maxHandleOffset ) && 
                     ( m_currentHandleOffset + d.getHeight() <= 0 ) ) {
                    m_pumpHandle.offset( 0, d.getHeight() );
                    m_pumpShaft.offset( 0, d.getHeight() );
                    m_currentHandleOffset += d.getHeight();
                    if (d.getHeight() > 0){
                        // This motion is in the pumping direction, so accumulate it.
                        m_currentPumpingAmount += d.getHeight();
                        if (m_currentPumpingAmount >= m_pumpingRequiredToInject){
                            // Enough pumping has been done to inject a new particle.
                            m_model.injectMolecule();
                            m_currentPumpingAmount = 0;
                        }
                    }
                }
            }
        } );

        // Add the shaft for the pump.
        double pumpShaftWidth = width * PUMP_SHAFT_WIDTH_PROPORTION;
        double pumpShaftHeight = height * PUMP_SHAFT_HEIGHT_PROPORTION;
        m_pumpShaft = new PPath( new Rectangle2D.Double( 0, 0, pumpShaftWidth, pumpShaftHeight ) );
        m_pumpShaft.setPaint( PUMP_SHAFT_COLOR );
        m_pumpShaft.setOffset( width * PUMP_HORIZ_POSITION_PROPORTION - ( pumpShaftWidth / 2 ), height - ( height * PUMP_HANDLE_INIT_VERT_POS_PROPORTION ) );
        m_pumpShaft.setPickable( false );
        addChild( m_pumpShaft );

        // Add the hose.
        double hoseExternalAttachPtX = 0;
        double hoseExternalAttachPtY = height - ( height * HOSE_CONNECTOR_VERT_POS_PROPORTION ) + ( height * HOSE_CONNECTOR_HEIGHT_PROPORTION / 2 );
        double hoseToPumpAttachPtX = width * PUMP_HORIZ_POSITION_PROPORTION;
        double hoseToPumpAttachPtY = height - ( height * HOSE_ATTACH_VERT_POS_PROPORTION );
        CubicCurve2D hoseShape = new CubicCurve2D.Double( hoseExternalAttachPtX, hoseExternalAttachPtY, width, height - ( height * HOSE_CONNECTOR_VERT_POS_PROPORTION ), 0, height - ( height * HOSE_ATTACH_VERT_POS_PROPORTION ), hoseToPumpAttachPtX, hoseToPumpAttachPtY );
        PPath hose = new PPath( hoseShape );
        hose.setStroke( new BasicStroke( (float) ( HOSE_WIDTH_PROPORTION * width ) ) );
        hose.setStrokePaint( HOSE_COLOR );
        hose.setPickable( false );
        addChild( hose );

        // Add the hose connector.
        double hoseConnectorWidth = width * HOSE_CONNECTOR_WIDTH_PROPORTION;
        double hoseConnectorHeight = height * HOSE_CONNECTOR_HEIGHT_PROPORTION;
        PPath hoseConnector = new PPath( new Rectangle2D.Double( 0, 0, hoseConnectorWidth, hoseConnectorHeight ) );
        hoseConnector.setPaint( HOSE_CONNECTOR_COLOR );
        hoseConnector.setOffset( 0, height - ( height * HOSE_CONNECTOR_VERT_POS_PROPORTION ) );
        hoseConnector.setPickable( false );
        addChild( hoseConnector );

        // Add the body of the pump
        double pumpBodyWidth = width * PUMP_BODY_WIDTH_PROPORTION;
        double pumpBodyHeight = height * PUMP_BODY_HEIGHT_PROPORTION;
        PPath pumpBody = new PhetPPath( new Rectangle2D.Double( 0, 0, pumpBodyWidth, pumpBodyHeight ) );
        GradientPaint pumpBodyPaint = new GradientPaint(0, (float)pumpBodyHeight/2, PUMP_BODY_COLOR, 
        		(float)pumpBodyWidth, (float)pumpBodyHeight/2, Color.LIGHT_GRAY);
        
        pumpBody.setPaint( pumpBodyPaint );
        pumpBody.setOffset( width * PUMP_HORIZ_POSITION_PROPORTION - ( pumpBodyWidth / 2 ), height - pumpBodyHeight - pumpBaseHeight );
        pumpBody.setPickable( false );
        addChild( pumpBody );
    }
}
