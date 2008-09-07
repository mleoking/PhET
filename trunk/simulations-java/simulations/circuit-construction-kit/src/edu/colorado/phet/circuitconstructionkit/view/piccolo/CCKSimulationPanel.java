package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.controls.GrabBagButton;
import edu.colorado.phet.circuitconstructionkit.controls.SimpleKeyEvent;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 11:15:22 AM
 */

public class CCKSimulationPanel extends PhetPCanvas {
    private CCKModel model;
    private CCKModule module;
    private CircuitNode circuitNode;
    private MessageNode messageNode;
    private MeasurementToolSetNode measurementToolSetNode;
    private CCKHelpSuite cckHelpSuite;
    private BranchNodeFactory branchNodeFactory;
    private ToolboxNodeSuite toolboxSuite;
    private ChartSetNode chartSetNode;
    private TimeScaleNode timeScaleNode;
    private PSwing grabBagPSwing;

    public CCKSimulationPanel( CCKModel model, final CCKModule module, IClock clock ) {
        super( new Dimension( 10, 10 ) );
        this.model = model;
        this.module = module;

        addScreenChild( new CCKBackground( model, this ) );
        setBackground( CCKModule.BACKGROUND_COLOR );

        branchNodeFactory = new BranchNodeFactory( model, this, module, true );
        toolboxSuite = new ToolboxNodeSuite( model, module, this, branchNodeFactory );
        addScreenChild( toolboxSuite );

        circuitNode = new CircuitNode( model, model.getCircuit(), this, module, branchNodeFactory );
        addWorldChild( circuitNode );

        measurementToolSetNode = new MeasurementToolSetNode( model, this, module, module.getVoltmeterModel() );
        addWorldChild( measurementToolSetNode );
        messageNode = new MessageNode();
        addScreenChild( messageNode );

        chartSetNode = new ChartSetNode( this, model.getCircuit(), clock );
        addScreenChild( chartSetNode );

        cckHelpSuite = new CCKHelpSuite( this, module );
        addScreenChild( cckHelpSuite );

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

//        setZoomEventHandler( new PZoomEventHandler() );
        setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );

        requestFocus();
        addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
            }

            public void keyReleased( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_A && e.isControlDown() ) {
                    module.selectAll();
                }
                if ( e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE ) {
                    module.desolderSelection();
                    module.deleteSelectedBranches();
                }
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        addTimeScaleListener();

        relayout();
    }

    private void addTimeScaleListener() {
        timeScaleNode = new TimeScaleNode( this, model );
        addScreenChild( timeScaleNode );
        timeScaleNode.addPropertyChangeListener( PNode.PROPERTY_VISIBLE, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                relayout();
            }
        } );
    }

    private void relayout() {
        if ( getWidth() > 0 ) {
            if ( grabBagPSwing != null ) {
                updateButtonLayout();
            }
            updateToolboxLayout();
            updateTimeScaleNodeLayout();
        }
    }

    private double getToolboxTopY() {
        return grabBagPSwing == null ? 0 : grabBagPSwing.getFullBounds().getMaxY();
    }

    private void updateToolboxLayout() {
        toolboxSuite.setTransform( new AffineTransform() );
        Rectangle2D rect = toolboxSuite.getGlobalFullBounds();
        getPhetRootNode().globalToScreen( rect );
        int bottomInsetY = 5;
        double distBetweenGrabBagAndToolbarTop = getHeight() > 600 ? 50 : 10;
        double availableHeightForToolbar = getHeight() - bottomInsetY - distBetweenGrabBagAndToolbarTop - getToolboxTopY();
        double sy = availableHeightForToolbar / rect.getHeight();
        toolboxSuite.scale( sy );
        double insetX = 8;
        toolboxSuite.setOffset( getWidth() - toolboxSuite.getFullBounds().getWidth() - insetX, getToolboxTopY() + distBetweenGrabBagAndToolbarTop );
    }

    private void updateTimeScaleNodeLayout() {
        timeScaleNode.setScale( 1.0 );
        double timeScaleGraphicRatio = timeScaleNode.getFullBounds().getWidth() / getWidth();
        if ( timeScaleGraphicRatio > 0.8 ) {
            timeScaleNode.setScale( 0.8 / timeScaleGraphicRatio );
        }
        else {
            timeScaleNode.setScale( 1.0 );
        }
        double timeScaleNodeY = getHeight() - 10 - timeScaleNode.getFullBounds().getHeight();
        timeScaleNode.setOffset( 10, timeScaleNodeY );
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
        grabBagPSwing = new PSwing( grabBagButton );
        addScreenChild( grabBagPSwing );
    }

    public void updateButtonLayout() {
        int buttonInset = 4;
        grabBagPSwing.setOffset( getWidth() - grabBagPSwing.getFullBounds().getWidth() - buttonInset, buttonInset );
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
        AffineTransform desiredTx = circuitNode.getTransformForZoom( zoom, this );
        int animateTime = 2000;
        circuitNode.animateToTransform( desiredTx, animateTime );
        measurementToolSetNode.animateToTransform( desiredTx, animateTime );
    }

    public void addCurrentChart() {
        chartSetNode.addCurrentChart();
    }

    public void addVoltageChart() {
        chartSetNode.addVoltageChart();
    }

    public boolean getElectronsVisible() {
        return circuitNode.isElectronsVisible();
    }

}
