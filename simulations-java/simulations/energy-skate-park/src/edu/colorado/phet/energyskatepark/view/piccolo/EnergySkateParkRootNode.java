/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetRootPNode;
import edu.colorado.phet.common.piccolophet.nodes.MeasuringTape;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.Floor;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

/**
 * User: Sam Reid
 * Date: Oct 21, 2005
 * Time: 2:16:21 PM
 */

public class EnergySkateParkRootNode extends PhetRootPNode {
    private PNode bodyGraphics = new PNode();
    private PNode jetPackGraphics = new PNode();
    private PNode splineLayer = new PNode();
    private EnergySkateParkModule module;
    private EnergySkateParkSimulationPanel simulationPanel;
    private PNode historyGraphics = new PNode();
    private PNode historyReadouts = new PNode();
    private MeasuringTape measuringTape;
    private static final boolean DEFAULT_TAPE_VISIBLE = false;
    private static final boolean DEFAULT_PIE_CHART_VISIBLE = false;
    private PNode pieCharts = new PNode();
    private OffscreenManIndicatorNode offscreenManIndicatorNode;
    private boolean ignoreThermal = true;
    private PauseIndicatorNode pauseIndicator;
    private EnergySkateParkLegend legend;
    private BackgroundScreenNode screenBackground;
    private SplineToolboxNode splineToolbox;
    private FloorNode floorNode;
    private ZeroPointPotentialNode zeroPointPotentialNode;
    public static final Color SKY_COLOR = new Color( 170, 200, 220 );
    private GridNode gridNode;
    private PanZoomOnscreenControlNode panZoomControls;
    private PNode energyErrorIndicatorContainer = new PNode();
    private EnergyErrorIndicatorNode energyErrorIndicatorNode;
    private SurfaceObjectNode houseNode;
    private SurfaceObjectNode mountainNode;

    public EnergySkateParkRootNode( final EnergySkateParkModule module, EnergySkateParkSimulationPanel simulationPanel ) {
        this.module = module;
        this.simulationPanel = simulationPanel;
        EnergySkateParkModel energySkateParkModel = getModel();
        Floor floor = energySkateParkModel.getFloor();

        simulationPanel.setBackground( SKY_COLOR );
        splineToolbox = new SplineToolboxNode( simulationPanel, this );

        measuringTape = new MeasuringTape( new ModelViewTransform2D(
                new Rectangle( 50, 50 ), new Rectangle2D.Double( 0, 0, 50, 50 ) ),
                                           new Point2D.Double( 25, 25 ) );
        updateMapping();
        measuringTape.setModelSrc( new Point2D.Double( 5, 5 ) );
        measuringTape.setModelDst( new Point2D.Double( 7, 5 ) );

        pauseIndicator = new PauseIndicatorNode( module, simulationPanel, this );
        legend = new EnergySkateParkLegend( module );
        floorNode = new FloorNode( module, getModel(), floor );
        screenBackground = new BackgroundScreenNode( simulationPanel, null, floorNode, this );
        zeroPointPotentialNode = new ZeroPointPotentialNode( simulationPanel, energySkateParkModel );

        gridNode = new GridNode( -50, -150, 100, 150, 1, 1 );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void gravityChanged() {
                HashMap map = new HashMap();
                map.put( new Double( EnergySkateParkModel.G_SPACE ), Color.lightGray );
                map.put( new Double( EnergySkateParkModel.G_JUPITER ), Color.white );
                map.put( new Double( EnergySkateParkModel.G_MOON ), Color.lightGray );
                Double key = new Double( module.getEnergySkateParkModel().getGravity() );
//                System.out.println( "module.getEnergySkateParkModel().getGravity() = " + module.getEnergySkateParkModel().getGravity() +", contains key="+map.containsKey( new Double(module.getEnergySkateParkModel().getGravity( ))));
                Paint paint = (Paint)( map.containsKey( key ) ? map.get( key ) : Color.black );
                gridNode.setGridPaint( paint );
            }
        } );

        houseNode = new SurfaceObjectNode( SurfaceObjectNode.HOUSE_URL, 1.5, 10 );
        mountainNode = new SurfaceObjectNode( SurfaceObjectNode.MOUNTAIN_URL, 1.5, 0.0 );

        addScreenChild( screenBackground );
        addScreenChild( splineToolbox );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void gravityChanged() {
                updateHouseAndMountainVisible();
            }
        } );
        addWorldChild( houseNode );
        addWorldChild( mountainNode );
        addWorldChild( floorNode );
        addWorldChild( splineLayer );

        addWorldChild( jetPackGraphics );
        addWorldChild( bodyGraphics );

        addScreenChild( historyGraphics );
        addScreenChild( historyReadouts );

        addScreenChild( measuringTape );
        addScreenChild( pieCharts );
        addScreenChild( pauseIndicator );
        addScreenChild( legend );
        addScreenChild( zeroPointPotentialNode );
        offscreenManIndicatorNode = new OffscreenManIndicatorNode( simulationPanel, module, null );
        addScreenChild( offscreenManIndicatorNode );

        addWorldChild( gridNode );
        setGridVisible( false );

        resetDefaults();
        simulationPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                screenBackground.update();
            }
        } );
        setZeroPointVisible( false );
        panZoomControls = new PanZoomOnscreenControlNode( simulationPanel );
        addScreenChild( panZoomControls );

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

        simulationPanel.addKeyListener( new KeyAdapter() {

            public void keyReleased( KeyEvent event ) {
                if( event.getKeyCode() == KeyEvent.VK_V && event.isControlDown() && event.isShiftDown() ) {
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
        } );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
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
    }

    private void updateHouseAndMountainVisible() {
        houseNode.setVisible( module.getEnergySkateParkModel().getGravity() == EnergySkateParkModel.G_EARTH && getBackground().getVisible() );
        mountainNode.setVisible( module.getEnergySkateParkModel().getGravity() == EnergySkateParkModel.G_EARTH && getBackground().getVisible() );
    }

    private void showAll( PNode node ) {
        node.setVisible( true );
        for( int i = 0; i < node.getChildrenCount(); i++ ) {
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

    public BackgroundScreenNode getBackground() {
        return screenBackground;
    }

    public void setBackground( BufferedImage image ) {
        screenBackground.setBackground( image );
    }

    private void resetDefaults() {
        setPieChartVisible( DEFAULT_PIE_CHART_VISIBLE );
        setMeasuringTapeVisible( DEFAULT_TAPE_VISIBLE );
    }

    private EnergySkateParkModel getModel() {
        return module.getEnergySkateParkModel();
    }

    public void addSplineNode( SplineNode splineNode ) {
        splineLayer.addChild( splineNode );
    }

    //todo: this should be model-based
    public void reset() {
        bodyGraphics.removeAllChildren();
        splineLayer.removeAllChildren();
        jetPackGraphics.removeAllChildren();
        pieCharts.removeAllChildren();
        setZeroPointVisible( false );
        setMeasuringTapeVisible( false );
        panZoomControls.reset();
    }

    public void addSkaterNode( SkaterNode skaterNode ) {
        skaterNode.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                module.setRecordOrLiveMode();
            }
        } );
        bodyGraphics.addChild( skaterNode );
    }

    public SplineNode splineGraphicAt( int i ) {
        return (SplineNode)splineLayer.getChildrenReference().get( i );
    }

    public int numSplineGraphics() {
        return splineLayer.getChildrenReference().size();
    }

    public void removeSplineNode( SplineNode splineNode ) {
        splineLayer.removeChild( splineNode );
        splineNode.detachListeners();
    }

    private void updateHistory() {
        while( numHistoryGraphics() < getModel().getNumHistoryPoints() ) {
            addHistoryGraphic( new HistoryPointNode( getModel().historyPointAt( 0 ), this ) );
        }
        while( numHistoryGraphics() > getModel().getNumHistoryPoints() ) {
            removeHistoryPointGraphic( historyGraphicAt( numHistoryGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().getNumHistoryPoints(); i++ ) {
            historyGraphicAt( i ).setHistoryPoint( getModel().historyPointAt( i ) );
        }
    }

    private HistoryPointNode historyGraphicAt( int i ) {
        return (HistoryPointNode)historyGraphics.getChild( i );
    }

    private void removeHistoryPointGraphic( HistoryPointNode node ) {
        historyGraphics.removeChild( node );
        historyReadouts.removeChild( node.getReadoutGraphic() );
    }

    private void addHistoryGraphic( HistoryPointNode historyPointNode ) {
        historyGraphics.addChild( historyPointNode );
        historyReadouts.addChild( historyPointNode.getReadoutGraphic() );
    }

    private int numHistoryGraphics() {
        return historyGraphics.getChildrenCount();
    }

    private void updateBodies() {
        while( numBodyGraphics() < getModel().getNumBodies() ) {
            addSkaterNode( new SkaterNode( getModel().getBody( 0 ) ) );
        }
        while( numBodyGraphics() > getModel().getNumBodies() ) {
            removeSkaterNode( getSkaterNode( numBodyGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().getNumBodies(); i++ ) {
            getSkaterNode( i ).setBody( getModel().getBody( i ) );
        }
        if( bodyGraphics.getChildrenCount() == 1 ) {
            initPieChart();
            offscreenManIndicatorNode.setSkaterNode( getSkaterNode( 0 ) );
        }
    }

    private void initPieChart() {
        pieCharts.removeAllChildren();

        EnergySkateParkPieChartNode energySkateParkPieChartNode = new EnergySkateParkPieChartNode( module, getSkaterNode( 0 ) );
        energySkateParkPieChartNode.setIgnoreThermal( ignoreThermal );
        pieCharts.addChild( energySkateParkPieChartNode );
    }

    private void updateSplines() {
        while( numSplineGraphics() < getModel().getNumSplines() ) {
            addSplineNode( new SplineNode( simulationPanel, getModel().getSpline( 0 ), simulationPanel ) );
        }
        while( numSplineGraphics() > getModel().getNumSplines() ) {
            removeSplineNode( splineGraphicAt( numSplineGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().getNumSplines(); i++ ) {
            splineGraphicAt( i ).setSpline( getModel().getSpline( i ) );
        }
    }

    private void removeSkaterNode( SkaterNode skaterNode ) {
        bodyGraphics.removeChild( skaterNode );
        skaterNode.delete();
    }

    public int numBodyGraphics() {
        return bodyGraphics.getChildrenCount();
    }

    public SkaterNode getSkaterNode( int i ) {
        return (SkaterNode)bodyGraphics.getChild( i );
    }

    public boolean isMeasuringTapeVisible() {
        return measuringTape.getVisible();
    }

    public void setMeasuringTapeVisible( boolean selected ) {
        measuringTape.setVisible( selected );
        measuringTape.setPickable( selected );
        measuringTape.setChildrenPickable( selected );
    }

    public boolean isPieChartVisible() {
        return pieCharts.getVisible();
    }

    public void setPieChartVisible( boolean selected ) {
        pieCharts.setVisible( selected );
        legend.setVisible( selected );
    }

    public boolean getIgnoreThermal() {
        return ignoreThermal;
    }

    public void setIgnoreThermal( boolean selected ) {
        this.ignoreThermal = selected;
        for( int i = 0; i < pieCharts.getChildrenCount(); i++ ) {
            EnergySkateParkPieChartNode energySkateParkPieChartNode = (EnergySkateParkPieChartNode)pieCharts.getChild( i );
            energySkateParkPieChartNode.setIgnoreThermal( ignoreThermal );
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
        pauseIndicator.relayout();
        double insetX = 10;
        double insetY = 10;
        legend.setOffset( getSimulationPanel().getWidth() - legend.getFullBounds().getWidth() - insetX, insetY );
        if( panZoomControls != null ) {
            panZoomControls.setOffset( getSimulationPanel().getWidth() - panZoomControls.getFullBounds().getWidth() - insetX, getSimulationPanel().getHeight() - panZoomControls.getFullBounds().getHeight() - insetY );
        }
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

    public boolean isGridVisible() {
        return gridNode.getVisible();
    }

    public void setGridVisible( boolean selected ) {
        gridNode.setVisible( selected );
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

    public static void main( String[] args ) {
        System.out.println( "new Double(0).equals( new Double(-0)) = " + new Double( 0 ).equals( new Double( -0 ) ) );
    }

    public void setBackgroundVisible( boolean selected ) {
        getBackground().setVisible( selected );
        updateHouseAndMountainVisible();
    }
}
