package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.common.SimpleKeyEvent;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Wire;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 11:15:22 AM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class CCKSimulationPanel extends PhetPCanvas {
    private CCKModel model;
    private ICCKModule module;
    private ToolboxNode toolboxNode;
    private CircuitNode circuitNode;
    private MessageNode messageNode;

    public CCKSimulationPanel( CCKModel model, ICCKModule module ) {
        super( new Dimension( 10, 10 ) );
        this.model = model;
        this.module = module;
        setBackground( ICCKModule.BACKGROUND_COLOR );

        toolboxNode = new ToolboxNode( this, model, module );
        addScreenChild( toolboxNode );

        circuitNode = new CircuitNode( model, model.getCircuit(), this, module );
        addWorldChild( circuitNode );

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
    }

    private void relayout() {
        double toolboxInsetX = 15;
        toolboxNode.setOffset( getWidth() - toolboxNode.getFullBounds().getWidth() - toolboxInsetX, getHeight() / 2 - toolboxNode.getFullBounds().getHeight() / 2 );
    }

    private void addTestElement() {
        model.getCircuit().addBranch( new Wire( model.getCircuitChangeListener(), new Junction( 5, 5 ), new Junction( 8, 5 ) ) );
    }

    public void setToolboxBackgroundColor( Color color ) {
        toolboxNode.setBackground( color );
    }

    public Color getToolboxBackgroundColor() {
        return toolboxNode.getBackgroundColor();
    }
}
