// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetRootPNode;
import edu.colorado.phet.common.piccolophet.nodes.MeasuringTape;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.basics.ESPSpeedometerNode;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.Floor;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * User: Sam Reid
 * Date: Oct 21, 2005
 * Time: 2:16:21 PM
 */

public class EnergySkateParkRootNode extends PhetRootPNode {
    private final AbstractEnergySkateParkModule module;
    private final EnergySkateParkSimulationPanel simulationPanel;

    private final PNode skaterNodeLayer = new PNode();
    private final PNode splineLayer = new PNode();
    private final PNode historyLayer = new PNode();
    private final PNode historyReadoutLayer = new PNode();
    private final PNode pieChartLayer = new PNode();

    private final MeasuringTape measuringTape;
    private final ReturnSkaterButtonNode returnSkaterButtonNode;
    private boolean ignoreThermal = EnergySkateParkApplication.IGNORE_THERMAL_DEFAULT;
    private final EnergySkateParkLegend legend;
    private final BackgroundScreenNode backgroundScreenNode;
    private final SplineToolboxNode splineToolbox;
    private final FloorNode floorNode;
    private final ZeroPointPotentialNode zeroPointPotentialNode;
    public final GridNode gridNode;
    private final PanZoomOnscreenControlNode panZoomControls;
    private final PNode energyErrorIndicatorContainer = new PNode();
    private final EnergyErrorIndicatorNode energyErrorIndicatorNode;
    private final SurfaceObjectNode houseNode;
    private final SurfaceObjectNode mountainNode;

    public static final Color SKY_COLOR = new Color( 170, 200, 220 );
    private static final boolean DEFAULT_TAPE_VISIBLE = false;
    private boolean splinesMovable;
    public final PNode controlLayer = new PNode();

    public EnergySkateParkRootNode( final AbstractEnergySkateParkModule module, final EnergySkateParkSimulationPanel simulationPanel, boolean hasZoomControls, double gridHighlightX ) {
        this.module = module;
        this.simulationPanel = simulationPanel;
        this.splinesMovable = module.splinesMovable;
        EnergySkateParkModel energySkateParkModel = getModel();
        Floor floor = energySkateParkModel.getFloor();

        simulationPanel.setBackground( SKY_COLOR );
        splineToolbox = new SplineToolboxNode( simulationPanel, splinesMovable, module.limitNumberOfTracks ) {{
            setOffset( 10, 10 );
        }};

        measuringTape = new MeasuringTape( new ModelViewTransform2D(
                new Rectangle( 50, 50 ), new Rectangle2D.Double( 0, 0, 50, 50 ) ),
                                           new Point2D.Double( 25, 25 ), EnergySkateParkResources.getString( "units.meters.abbreviation" ) );
        updateMapping();
        resetMeasuringTapeLocation();

        legend = new EnergySkateParkLegend( module );
        floorNode = new FloorNode( module, getModel(), floor );
        backgroundScreenNode = new BackgroundScreenNode( simulationPanel, null, floorNode );
        zeroPointPotentialNode = new ZeroPointPotentialNode( simulationPanel, energySkateParkModel );

        //Move the grid a little to the left so the grid numbers will be to the left of the track
        double epsilon = -0.5;
        gridNode = new GridNode( -50 + epsilon, 0, 100, 150, 1, 1, gridHighlightX + epsilon );

        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void gravityChanged() {
                HashMap map = new HashMap();
                map.put( new Double( EnergySkateParkModel.G_SPACE ), Color.lightGray );
                map.put( new Double( EnergySkateParkModel.G_JUPITER ), Color.white );
                map.put( new Double( EnergySkateParkModel.G_MOON ), Color.lightGray );
                Double key = new Double( module.getEnergySkateParkModel().getGravity() );
                Paint paint = (Paint) ( map.containsKey( key ) ? map.get( key ) : Color.black );
                gridNode.setGridPaint( paint );
            }
        } );

        houseNode = new SurfaceObjectNode( SurfaceObjectNode.HOUSE_RESOURCE_NAME, 1.25, 12 );
        mountainNode = new SurfaceObjectNode( SurfaceObjectNode.MOUNTAIN_RESOURCE_NAME, 1.6 * 0.75, -1 );

        returnSkaterButtonNode = new ReturnSkaterButtonNode( simulationPanel, module, null );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void gravityChanged() {
                updateHouseAndMountainVisible();
            }
        } );

        addScreenChild( backgroundScreenNode );
        addScreenChild( splineToolbox );

        addWorldChild( houseNode );
        addWorldChild( mountainNode );
        addWorldChild( floorNode );
        addWorldChild( gridNode );
        addChild( controlLayer );
        addWorldChild( splineLayer );
        addWorldChild( skaterNodeLayer );

        addScreenChild( historyLayer );
        addScreenChild( historyReadoutLayer );
        addScreenChild( measuringTape );
        addScreenChild( pieChartLayer );
        addScreenChild( legend );
        addScreenChild( zeroPointPotentialNode );
        addScreenChild( returnSkaterButtonNode );

        resetDefaults();
        simulationPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                backgroundScreenNode.update();
            }
        } );
        setZeroPointVisible( false );
        panZoomControls = new PanZoomOnscreenControlNode( simulationPanel );
        if ( hasZoomControls ) {
            addScreenChild( panZoomControls );
        }

        energyErrorIndicatorNode = new EnergyErrorIndicatorNode( module.getEnergySkateParkModel() );
        energyErrorIndicatorContainer.setVisible( false );
        energyErrorIndicatorContainer.addChild( energyErrorIndicatorNode );
        addScreenChild( energyErrorIndicatorContainer );
        simulationPanel.addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                updateEnergyIndicator();
            }

            public void componentResized( ComponentEvent e ) {
                updateEnergyIndicator();
            }
        } );
        updateMapping();
        addWorldTransformListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateMapping();
            }
        } );

        //todo: this could be moved to PDebugKeyHandlers or a subclass
        simulationPanel.addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent event ) {
                if ( event.getKeyCode() == KeyEvent.VK_V && event.isControlDown() && event.isShiftDown() ) {
                    showAll( EnergySkateParkRootNode.this );
                }
            }
        } );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void bodyCountChanged() {
                updateBodies();
            }

            public void splineCountChanged() {
                updateSplines();
            }

            public void historyChanged() {
                updateHistory();
            }

            public void splinesSynced() {
                updateSplines();
            }

            public void bodiesSynced() {
                updateBodies();
            }
        } );

        // Set up the listeners to properties that control visibility of
        // various aspects of the view.
        module.pieChartVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean pieChartVisible ) {
                pieChartLayer.setVisible( pieChartVisible );
                legend.setVisible( pieChartVisible );
            }
        } );
        module.gridVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean gridVisible ) {
                gridNode.setVisible( gridVisible );
            }
        } );

        //add the floating clock control panel
//        EnergySkateParkTimePanel timePanel = new EnergySkateParkTimePanel( module, module.getTimeSeriesModel().getTimeModelClock() );
//        final PSwing timePanelNode = new PSwing( timePanel ) {{
//            final double scale = 0.02;
//            final AffineTransform transform = AffineTransform.getScaleInstance( scale, -scale );
//            transform.preConcatenate( AffineTransform.getTranslateInstance( EnergySkateParkSimulationPanel.VIEW_WIDTH / 2 - getFullBounds().getWidth() / 2 * scale, 0 ) );
//            setTransform( transform );
//        }};
//        addWorldChild( timePanelNode );

        addScreenChild( new SelectedTimeControlPanel( module, module.getTimeSeriesModel().getTimeModelClock() ) {{
            final ComponentAdapter listener = new ComponentAdapter() {
                @Override public void componentResized( ComponentEvent e ) {
                    setOffset( simulationPanel.getWidth() / 2 - getFullWidth() / 2, simulationPanel.getHeight() - getFullHeight() - 2 );
                }
            };
            simulationPanel.addComponentListener( listener );
            listener.componentResized( null );
        }} );

        //Add the speedometer
        addWorldChild( new ESPSpeedometerNode( module.getPrimarySkaterSpeed() ) {{
            final float scale = 0.02f;
            final AffineTransform transform = AffineTransform.getScaleInstance( scale, -scale );
            transform.preConcatenate( AffineTransform.getTranslateInstance( EnergySkateParkSimulationPanel.VIEW_WIDTH / 2 - getFullBounds().getWidth() / 2 * scale, 8.25 ) );
//            scale( scale );
//            translate( EnergySkateParkSimulationPanel.VIEW_WIDTH / 2 - getFullBounds().getWidth() / 2 * scale, 0 );
            setTransform( transform );
            module.speedVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    setVisible( visible );
                }
            } );
        }} );
    }

    public SplineToolboxNode getSplineToolbox() {
        return splineToolbox;
    }

    //Removes the spline toolbox node for basic modules
    public void removeSplineToolbox() {
        removeChild( splineToolbox );
    }

    private void resetMeasuringTapeLocation() {
        measuringTape.setModelSrc( new Point2D.Double( 5, 5 ) );
        measuringTape.setModelDst( new Point2D.Double( 7, 5 ) );
    }

    private void updateHouseAndMountainVisible() {
        houseNode.setVisible( module.getEnergySkateParkModel().getGravity() == EnergySkateParkModel.G_EARTH && getBackgroundScreenNode().getVisible() );
        mountainNode.setVisible( module.getEnergySkateParkModel().getGravity() == EnergySkateParkModel.G_EARTH && getBackgroundScreenNode().getVisible() );
    }

    private void showAll( PNode node ) {
        node.setVisible( true );
        for ( int i = 0; i < node.getChildrenCount(); i++ ) {
            showAll( node.getChild( i ) );
        }
    }

    private void updateMapping() {
        Rectangle2D rect = new Rectangle2D.Double( 0, 0, 1, 1 );
        worldToScreen( rect );
        measuringTape.setModelViewTransform2D( new ModelViewTransform2D( new Rectangle( 1, 1 ), rect ) );
    }

    private void updateEnergyIndicator() {
        double insetX = 20;
        double insetY = 20;
        energyErrorIndicatorNode.setOffset( insetX, simulationPanel.getHeight() - insetY - energyErrorIndicatorNode.getFullBounds().getHeight() );
    }

    private BackgroundScreenNode getBackgroundScreenNode() {
        return backgroundScreenNode;
    }

    public void setBackground( BufferedImage image ) {
        backgroundScreenNode.setBackground( image );
    }

    private void resetDefaults() {
        module.pieChartVisible.reset();
        setMeasuringTapeVisible( DEFAULT_TAPE_VISIBLE );
        module.gridVisible.reset();
    }

    private EnergySkateParkModel getModel() {
        return module.getEnergySkateParkModel();
    }

    public void addSplineNode( SplineNode splineNode ) {
        splineLayer.addChild( splineNode );
    }

    /**
     * Resets the state of the controls in the view.
     */
    public void reset() {
        setMeasuringTapeVisible( false );
        resetMeasuringTapeLocation();
        panZoomControls.reset();
        module.gridVisible.reset();
        module.pieChartVisible.reset();
        module.barChartVisible.reset();
    }

    public void addSkaterNode( SkaterNode skaterNode ) {
        skaterNode.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                module.setRecordOrLiveMode();
            }
        } );
        skaterNodeLayer.addChild( skaterNode );
    }

    public SplineNode splineGraphicAt( int i ) {
        return (SplineNode) splineLayer.getChildrenReference().get( i );
    }

    public int numSplineGraphics() {
        return splineLayer.getChildrenReference().size();
    }

    public void removeSplineNode( SplineNode splineNode ) {
        splineLayer.removeChild( splineNode );
        splineNode.detachListeners();
    }

    private void updateHistory() {
        while ( numHistoryGraphics() < getModel().getNumHistoryPoints() ) {
            addHistoryGraphic( new HistoryPointNode( getModel().historyPointAt( 0 ), this ) );
        }
        while ( numHistoryGraphics() > getModel().getNumHistoryPoints() ) {
            removeHistoryPointGraphic( historyGraphicAt( numHistoryGraphics() - 1 ) );
        }
        for ( int i = 0; i < getModel().getNumHistoryPoints(); i++ ) {
            historyGraphicAt( i ).setHistoryPoint( getModel().historyPointAt( i ) );
        }
    }

    private HistoryPointNode historyGraphicAt( int i ) {
        return (HistoryPointNode) historyLayer.getChild( i );
    }

    private void removeHistoryPointGraphic( HistoryPointNode node ) {
        historyLayer.removeChild( node );
        historyReadoutLayer.removeChild( node.getReadoutGraphic() );
    }

    private void addHistoryGraphic( HistoryPointNode historyPointNode ) {
        historyLayer.addChild( historyPointNode );
        historyReadoutLayer.addChild( historyPointNode.getReadoutGraphic() );
    }

    private int numHistoryGraphics() {
        return historyLayer.getChildrenCount();
    }

    private void updateBodies() {
        while ( numBodyGraphics() < getModel().getNumBodies() ) {
            addSkaterNode( new SkaterNode( getModel().getBody( 0 ) ) );
        }
        while ( numBodyGraphics() > getModel().getNumBodies() ) {
            removeSkaterNode( getSkaterNode( numBodyGraphics() - 1 ) );
        }
        for ( int i = 0; i < getModel().getNumBodies(); i++ ) {
            getSkaterNode( i ).setBody( getModel().getBody( i ) );
        }
        if ( skaterNodeLayer.getChildrenCount() == 1 ) {
            initPieChart();
            returnSkaterButtonNode.setSkaterNode( getSkaterNode( 0 ) );
        }
    }

    private void initPieChart() {
        pieChartLayer.removeAllChildren();

        EnergySkateParkPieChartNode energySkateParkPieChartNode = new EnergySkateParkPieChartNode( module, getSkaterNode( 0 ) );
        energySkateParkPieChartNode.setIgnoreThermal( ignoreThermal );
        pieChartLayer.addChild( energySkateParkPieChartNode );
    }

    private void updateSplines() {
        while ( numSplineGraphics() < getModel().getNumSplines() ) {
            addSplineNode( new SplineNode( simulationPanel, getModel().getSpline( 0 ), simulationPanel, splinesMovable ) );
        }
        while ( numSplineGraphics() > getModel().getNumSplines() ) {
            removeSplineNode( splineGraphicAt( numSplineGraphics() - 1 ) );
        }
        for ( int i = 0; i < getModel().getNumSplines(); i++ ) {
            splineGraphicAt( i ).setSpline( getModel().getSpline( i ) );
        }
    }

    private void removeSkaterNode( SkaterNode skaterNode ) {
        skaterNodeLayer.removeChild( skaterNode );
        skaterNode.delete();
    }

    public int numBodyGraphics() {
        return skaterNodeLayer.getChildrenCount();
    }

    public SkaterNode getSkaterNode( int i ) {
        return (SkaterNode) skaterNodeLayer.getChild( i );
    }

    public boolean isMeasuringTapeVisible() {
        return measuringTape.getVisible();
    }

    public void setMeasuringTapeVisible( boolean selected ) {
        measuringTape.setVisible( selected );
    }

    public boolean isPieChartVisible() {
        return pieChartLayer.getVisible();
    }

    public void setPieChartVisible( boolean selected ) {
        pieChartLayer.setVisible( selected );
        legend.setVisible( selected );
    }

    public boolean getIgnoreThermal() {
        return ignoreThermal;
    }

    public void setIgnoreThermal( boolean selected ) {
        if ( this.ignoreThermal != selected ) {
            this.ignoreThermal = selected;
            for ( int i = 0; i < pieChartLayer.getChildrenCount(); i++ ) {
                EnergySkateParkPieChartNode energySkateParkPieChartNode = (EnergySkateParkPieChartNode) pieChartLayer.getChild( i );
                energySkateParkPieChartNode.setIgnoreThermal( ignoreThermal );
            }
        }
    }

    public void clearBackground() {
        BufferedImage image = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = image.createGraphics();
        g2.setColor( new Color( 0, 0, 0, 255 ) );
        g2.fillRect( 0, 0, 1, 1 );
        setBackground( null );
    }

    protected void layoutChildren() {
        super.layoutChildren();
        double insetX = 10;
        double insetY = 10;
        legend.setOffset( getSimulationPanel().getWidth() - legend.getFullBounds().getWidth() - insetX, insetY );
        if ( panZoomControls != null ) {
            panZoomControls.setOffset( getSimulationPanel().getWidth() - panZoomControls.getFullBounds().getWidth() - insetX, getSimulationPanel().getHeight() - panZoomControls.getFullBounds().getHeight() - insetY );
        }
        for ( VoidFunction0 layoutListener : layoutListeners ) {
            layoutListener.apply();
        }
    }

    private final ArrayList<VoidFunction0> layoutListeners = new ArrayList<VoidFunction0>();

    public void addLayoutListener( VoidFunction0 layoutListener ) {
        layoutListeners.add( layoutListener );
    }

    private EnergySkateParkSimulationPanel getSimulationPanel() {
        return simulationPanel;
    }

    public void setZeroPointVisible( boolean selected ) {
        zeroPointPotentialNode.setVisible( selected );
    }

    public boolean isZeroPointVisible() {
        return zeroPointPotentialNode.getVisible();
    }

    public PNode getMeasuringTapeNode() {
        return measuringTape;
    }

    public void updateScale() {
        panZoomControls.updateScale();
    }

    public void setEnergyErrorVisible( boolean selected ) {
        energyErrorIndicatorContainer.setVisible( selected );
    }

    public boolean isEnergyErrorVisible() {
        return energyErrorIndicatorContainer.getVisible();
    }

    public void setBackgroundVisible( boolean selected ) {
        getBackgroundScreenNode().setVisible( selected );
        updateHouseAndMountainVisible();
    }

    public void updateSplineNodes() {
        updateSplines();
    }

    public SkaterNode getSkaterNode( Body body ) {
        for ( int i = 0; i < skaterNodeLayer.getChildrenCount(); i++ ) {
            SkaterNode node = (SkaterNode) skaterNodeLayer.getChild( i );
            if ( node.getBody() == body ) {
                return node;
            }
        }
        return null;
    }

    //Have to call update background when changing the canvas size without changing the image, when a component event won't be fired (such as before the window is visible)
    public void updateBackground() {
        backgroundScreenNode.update();
    }

    //Gets the legend that shows the different kinds of energy and their color
    public EnergySkateParkLegend getLegend() {
        return legend;
    }
}