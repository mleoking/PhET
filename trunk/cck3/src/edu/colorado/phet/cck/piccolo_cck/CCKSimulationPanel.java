package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.common.SimpleKeyEvent;
import edu.colorado.phet.cck.grabbag.GrabBagButton;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Wire;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwing;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 11:15:22 AM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class CCKSimulationPanel extends PhetPCanvas {
    private CCKModel model;
    private ICCKModule module;
    private CircuitNode circuitNode;
    private MessageNode messageNode;
    private MeasurementToolSetNode measurementToolSetNode;
    private CCKHelpSuite cckHelpSuite;
    private BranchNodeFactory branchNodeFactory;
    private ToolboxNodeSuite toolboxSuite;

    public CCKSimulationPanel( CCKModel model, ICCKModule module ) {
        super( new Dimension( 10, 10 ) );
        this.model = model;
        this.module = module;
        setBackground( ICCKModule.BACKGROUND_COLOR );

        branchNodeFactory = new BranchNodeFactory( model, this, module, true );
        toolboxSuite = new ToolboxNodeSuite( model, module, this, branchNodeFactory );

        addWorldChild( toolboxSuite );

        circuitNode = new CircuitNode( model, model.getCircuit(), this, module, branchNodeFactory );
        addWorldChild( circuitNode );

        measurementToolSetNode = new MeasurementToolSetNode( model, this, module, module.getVoltmeterModel() );
        addWorldChild( measurementToolSetNode );
        messageNode = new MessageNode();
        addScreenChild( messageNode );

        addKeyListener( new SimpleKeyEvent( KeyEvent.VK_SPACE ) {
            public void invoke() {
                super.invoke();
                addTestElement();
            }
        } );
        setWorldScale( 30 );
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        relayout();
        setZoomEventHandler( new PZoomEventHandler() );
        setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );

        cckHelpSuite = new CCKHelpSuite( this, module );
        addScreenChild( cckHelpSuite );
    }

    private void relayout() {
        Rectangle2D screenRect = new Rectangle2D.Double( 0, 0, getWidth(), getHeight() );
        toolboxSuite.getParent().globalToLocal( screenRect );
        double toolboxInsetX = 15 / 80.0;
        double toolboxInsetY = 10 / 80.0;
        toolboxSuite.setOffset( screenRect.getWidth() - toolboxSuite.getFullBounds().getWidth() - toolboxInsetX, screenRect.getHeight() - toolboxSuite.getFullBounds().getHeight() - toolboxInsetY );
    }

    private void addTestElement() {
        model.getCircuit().addBranch( new Wire( model.getCircuitChangeListener(), new Junction( 5, 5 ), new Junction( 8, 5 ) ) );
    }

    public void setToolboxBackgroundColor( Color color ) {
        toolboxSuite.setBackground( color );
    }

    public Color getToolboxBackgroundColor() {
        return toolboxSuite.getBackgroundColor();
    }

    public void setVoltmeterVisible( boolean visible ) {
        measurementToolSetNode.setVoltmeterVisible( visible );
    }

    public CircuitNode getCircuitNode() {
        return circuitNode;
    }

    public void setVirtualAmmeterVisible( boolean selected ) {
        measurementToolSetNode.setVirtualAmmeterVisible( selected );
    }

    public void setStopwatchVisible( boolean selected ) {
        measurementToolSetNode.setStopwatchVisible( selected );
    }

    public void setSeriesAmmeterVisible( boolean selected ) {
        toolboxSuite.setSeriesAmmeterVisible( selected );
    }

    public void addGrabBag() {
        GrabBagButton grabBagButton = new GrabBagButton( module );
        final PSwing grabBagPSwing = new PSwing( this, grabBagButton );
        addScreenChild( grabBagPSwing );
        addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                updateButton();
            }

            public void componentShown( ComponentEvent e ) {
                updateButton();
            }

            public void updateButton() {
                int buttonInset = 4;
                grabBagPSwing.setOffset( getWidth() - grabBagPSwing.getFullBounds().getWidth() - buttonInset, buttonInset );
            }
        } );
    }

    public ToolboxNodeSuite getToolboxNodeSuite() {
        return toolboxSuite;
    }

    public void applicationStarted() {
        cckHelpSuite.applicationStarted();
    }

    public void setHelpEnabled( boolean enabled ) {
        cckHelpSuite.setHelpEnabled( enabled );
    }

    public boolean isLifelike() {
        return circuitNode.isLifelike();
    }

    public void setLifelike( boolean lifelike ) {
        circuitNode.setLifelike( lifelike );
    }

    public PNode getWireMaker() {
        return toolboxSuite.getWireMaker();
    }

    public void setZoom( double zoom ) {
        circuitNode.setZoom( zoom );
    }
}
