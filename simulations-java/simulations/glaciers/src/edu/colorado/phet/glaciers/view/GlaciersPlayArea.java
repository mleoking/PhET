/* Copyright 2007-2008, University of Colorado */ 

package edu.colorado.phet.glaciers.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.control.ScrollArrowNode;
import edu.colorado.phet.glaciers.control.ScrollArrowNode.LeftScrollArrowNode;
import edu.colorado.phet.glaciers.control.ScrollArrowNode.RightScrollArrowNode;
import edu.colorado.phet.glaciers.model.*;
import edu.colorado.phet.glaciers.model.IBoreholeProducer.IBoreholeProducerListener;
import edu.colorado.phet.glaciers.model.IDebrisProducer.IDebrisProducerListener;
import edu.colorado.phet.glaciers.model.IIceSurfaceRippleProducer.IIceSurfaceRippleProducerListener;
import edu.colorado.phet.glaciers.model.IToolProducer.IToolProducerListener;
import edu.colorado.phet.glaciers.model.Viewport.ViewportListener;
import edu.colorado.phet.glaciers.view.tools.AbstractToolNode;
import edu.colorado.phet.glaciers.view.tools.ToolNodeFactory;
import edu.colorado.phet.glaciers.view.tools.ToolboxNode;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;

/**
 * GlaciersPlayArea is "play area" for the glaciers sim.
 * It contains the birds-eye and zoomed views of the world.  
 * <p>
 * The birds-eye view appears at the top of the play area, and shows a tiny
 * overview picture of the world. A viewport shown in the birds-eye view indicates the 
 * portion of the world shown in the zoomed view. A pan control can be dragged 
 * horizontally to move the zoomed viewport.
 * <p>
 * The zoomed view appears below the birds-eye view, and displays a zoomed-in 
 * view of a portion of the world.
 * <p>
 * The two views are implemented as separate Piccolo canvases.  The canvases
 * share common layers, and their cameras are manipulated (scales and translated)
 * to display the appropriate portions of the world.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersPlayArea extends JPanel implements IToolProducerListener, IBoreholeProducerListener, IDebrisProducerListener, IIceSurfaceRippleProducerListener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // width of the stroke used to display the zoomed viewport, in pixels
    private static final float VIEWPORT_STROKE_WIDTH = 1;
    
    // properties used to layout the canvases in the play area
    private static final int VERTICAL_CANVAS_SPACING = 4;
    private static final int BORDER_WIDTH = 5;
    private static final Color BACKGROUND_COLOR = GlaciersConstants.CONTROL_PANEL_BACKGROUND_COLOR;
    private static final Border CANVAS_BORDER = BorderFactory.createLineBorder( Color.BLACK, 1 );
    private static final Border PLAY_AREA_BORDER = BorderFactory.createLineBorder( BACKGROUND_COLOR, BORDER_WIDTH );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model
    private final GlaciersModel _model;
    private final Viewport _birdsEyeViewport, _zoomedViewport;
    
    // View
    private final PhetPCanvas _birdsEyeCanvas, _zoomedCanvas;
    private final PLayer _backgroundLayer, _iceLayer, _ripplesLayer, _debrisLayer, _velocityLayer, _boreholeLayer;
    private final PLayer _snowfallLayer, _coordinatesLayer, _toolboxLayer, _toolsLayer, _viewportLayer, _debugLayer;
    private final IceFlowNode _iceFlowNode;
    private final SnowfallNode _snowfallNode;
    private final ToolboxNode _toolboxNode;
    private final PNode _panControlNode;
    private final EquilibriumLineNode _equilibriumLineNode;
    private final ELAValueNode _elaValueNode;
    private final ElevationAxisNode _leftElevationAxisNode, _rightElevationAxisNode;
    private final DistanceAxisNode _distanceAxisNode;
    private final HashMap _toolsMap; // key=AbstractTool, value=AbstractToolNode, used for removing tool nodes when their model elements are deleted
    private final ModelViewTransform _mvt;
    private final ScrollArrowNode _leftScrollArrowNode, _rightScrollArrowNode;
    private final HashMap _boreholesMap; // key=Borehole, value=BoreholeNode, used for removing borehole nodes when their model elements are deleted
    private final HashMap _debrisMap; // key=Debris, value=DebrisNode, used for removing debris nodes when their model elements are deleted
    private final HashMap _ripplesMap; // key=IceSurfaceRipple, value=IceSurfaceRippleNode, used for removing ripple nodes when their model elements are deleted
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlaciersPlayArea( GlaciersModel model ) {
        super();
        
        assert( GlaciersConstants.ZOOMED_CAMERA_VIEW_SCALE >= GlaciersConstants.BIRDS_EYE_CAMERA_VIEW_SCALE );
        assert( GlaciersConstants.BIRDS_EYE_CAMERA_VIEW_SCALE > 0 );
        assert( GlaciersConstants.ZOOMED_CAMERA_VIEW_SCALE > 0 );
        
        _model = model;
        _model.addToolProducerListener( this ); // manage nodes when tools are added/removed
        _model.addBoreholeProducerListener( this ); // manage nodes when boreholes are added/removed
        _model.addDebrisProducerListener( this ); // manage nodes when debris is added/removed
        _model.addIceSurfaceRippleProducerListener( this ); // manage nodes when ripples are added/removed

        _mvt = new GlaciersModelViewTransform();
        
        _toolsMap = new HashMap();
        _boreholesMap = new HashMap();
        _debrisMap = new HashMap();
        _ripplesMap = new HashMap();
        
        // headwall position
        Point2D headwallPosition = _model.getValley().getHeadwallPositionReference();
        
        // birds-eye viewport
        _birdsEyeViewport = new Viewport( "birds-eye" ); // bounds will be set when play area is resized
        _birdsEyeViewport.setPosition( headwallPosition.getX() + GlaciersConstants.BIRDS_EYE_VIEWPORT_OFFSET.getX(), headwallPosition.getY() + GlaciersConstants.BIRDS_EYE_VIEWPORT_OFFSET.getY() );
        _birdsEyeViewport.addViewportListener( new ViewportListener() {
            public void boundsChanged() {
                handleBirdsEyeViewportChanged();
            }
        });
        
        // zoomed viewport
        _zoomedViewport = new Viewport( "zoomed" ); // bounds will be set when play area is resized
        _zoomedViewport.setPosition( _birdsEyeViewport.getX(), _birdsEyeViewport.getY() ); // upper-left of birds-eye viewport
        _zoomedViewport.addViewportListener( new ViewportListener() {
            public void boundsChanged() {
                verticallyAlignZoomedViewport();
                handleZoomedViewportChanged();
            }
        });

        // birds-eye view
        _birdsEyeCanvas = new PhetPCanvas();
        _birdsEyeCanvas.setBorder( null ); // no border on the canvas, because we use canvas bounds in calculations
        _birdsEyeCanvas.setBackground( GlaciersConstants.BIRDS_EYE_CANVAS_COLOR );
        _birdsEyeCanvas.getCamera().setViewScale( GlaciersConstants.BIRDS_EYE_CAMERA_VIEW_SCALE );
        
        // zoomed view
        _zoomedCanvas = new PhetPCanvas();
        _zoomedCanvas.setBorder( null ); // no border on the canvas, because we use canvas bounds in calculations
        _zoomedCanvas.setBackground( GlaciersConstants.ZOOMED_CANVAS_COLOR );
        _zoomedCanvas.getCamera().setViewScale( GlaciersConstants.ZOOMED_CAMERA_VIEW_SCALE );
        // zoomed camera offset will be set based on viewport position

        // Layout the panel
        {
            // put a border around the birds-eye canvas, and constrain its height
            JPanel birdsEyeWrapperPanel = new JPanel( new BorderLayout() );
            birdsEyeWrapperPanel.add( Box.createVerticalStrut( (int) GlaciersConstants.BIRDS_EYE_VIEW_HEIGHT ), BorderLayout.WEST ); // fixed height
            birdsEyeWrapperPanel.add( _birdsEyeCanvas, BorderLayout.CENTER );
            birdsEyeWrapperPanel.setBorder( CANVAS_BORDER );

            // add a vertical spacer below the birds-eye canvas
            JPanel topPanel = new JPanel( new BorderLayout() );
            topPanel.setBackground( BACKGROUND_COLOR );
            topPanel.add( birdsEyeWrapperPanel, BorderLayout.CENTER );
            topPanel.add( Box.createVerticalStrut( VERTICAL_CANVAS_SPACING ), BorderLayout.SOUTH );

            // put a border around birds-eye canvas
            JPanel bottomPanel = new JPanel( new BorderLayout() );
            bottomPanel.add( _zoomedCanvas, BorderLayout.CENTER );
            bottomPanel.setBorder( CANVAS_BORDER );

            // birds-eye view above zoomed view, make the zoomed canvas fill all available space
            setBorder( PLAY_AREA_BORDER );
            setLayout( new BorderLayout() );
            add( topPanel, BorderLayout.NORTH );
            add( bottomPanel, BorderLayout.CENTER );
        }
        
        // update layout when the play area is resized
        PlayAreaResizeListener resizeListener = new PlayAreaResizeListener( this );
        this.addComponentListener( resizeListener );
        this.addAncestorListener( resizeListener );
        
        // Layers, back to front
        _backgroundLayer = new PLayer();
        _iceLayer = new PLayer();
        _ripplesLayer = new PLayer();
        _debrisLayer = new PLayer();
        _boreholeLayer = new PLayer();
        _snowfallLayer = new PLayer();
        _coordinatesLayer = new PLayer();
        _velocityLayer = new PLayer();
        _toolboxLayer = new PLayer();
        _toolsLayer = new PLayer();
        _viewportLayer = new PLayer();
        _debugLayer = new PLayer();
        addToBothViews( _backgroundLayer );
        addToBothViews( _iceLayer );
        addToZoomedView( _ripplesLayer );
        addToZoomedView( _debrisLayer );
        addToZoomedView( _boreholeLayer );
        addToBothViews( _snowfallLayer );
        addToZoomedView( _velocityLayer );
        addToZoomedView( _coordinatesLayer );
        addToZoomedView( _toolboxLayer );
        addToZoomedView( _toolsLayer );
        addToBirdsEyeView( _viewportLayer );
        addToBothViews( _debugLayer );
        
        // viewport in the birds-eye view indicates what is shown in zoomed view
        float strokeWidth = VIEWPORT_STROKE_WIDTH / (float) GlaciersConstants.BIRDS_EYE_CAMERA_VIEW_SCALE;
        ViewportNode viewportNode = new ViewportNode( _zoomedViewport, strokeWidth, _mvt );
        _viewportLayer.addChild( viewportNode );
        
        // Background image (mountains & valley)
        PNode mountainsAndValleyNode = new MountainsAndValleyNode( _model.getValley(), _mvt );
        _backgroundLayer.addChild( mountainsAndValleyNode );
        
        /*
         * The background image contains a alignment markers at a few (x,F(x)) locations.
         * The alignment markers created here should line up with the ones in the image file.
         * If they don't line up, then adjust scaling and translation in MountainsAndValleyNode.
         */
        if ( GlaciersConstants.DEBUG_BACKGROUND_IMAGE_ALIGNMENT ) {
            
            double x = 0; // meters
            PNode markerNode1 = new MarkerNode();
            markerNode1.setOffset( _mvt.modelToView( x, _model.getValley().getElevation( x ) ) );
            _debugLayer.addChild( markerNode1 );
            
            x = 10000; // meters
            PNode markerNode2 = new MarkerNode();
            markerNode2.setOffset( _mvt.modelToView( x, _model.getValley().getElevation( x ) ) );
            _debugLayer.addChild( markerNode2 );
            
            x = 70000; // meters
            PNode markerNode3 = new MarkerNode();
            markerNode3.setOffset( _mvt.modelToView( x, _model.getValley().getElevation( x ) ) );
            _debugLayer.addChild( markerNode3 );
        }
        
        // Glacier
        IceNode iceNode = new IceNode( _model.getGlacier(), _mvt );
        _iceLayer.addChild( iceNode );
        _iceFlowNode = new IceFlowNode( _model.getGlacier(), _mvt );
        _velocityLayer.addChild( _iceFlowNode );
        
        // Snowfall
        _snowfallNode = new SnowfallNode( _model.getGlacier(), _mvt );
        _snowfallLayer.addChild( _snowfallNode );
        
        // Axes
        _leftElevationAxisNode = new ElevationAxisNode( _mvt, GlaciersConstants.ELEVATION_AXIS_RANGE, GlaciersConstants.ELEVATION_AXIS_TICK_SPACING, false );
        _rightElevationAxisNode = new ElevationAxisNode( _mvt, GlaciersConstants.ELEVATION_AXIS_RANGE, GlaciersConstants.ELEVATION_AXIS_TICK_SPACING, true );
        _distanceAxisNode = new DistanceAxisNode( _model.getValley(), _mvt, GlaciersConstants.DISTANCE_AXIS_TICK_SPACING );
        _coordinatesLayer.addChild( _leftElevationAxisNode );
        _coordinatesLayer.addChild( _rightElevationAxisNode );
        _coordinatesLayer.addChild( _distanceAxisNode );
        
        // Equilibrium line
        _equilibriumLineNode = new EquilibriumLineNode( _model.getGlacier(), _mvt );
        _iceLayer.addChild( _equilibriumLineNode );
        
        // Toolbox
        _toolboxNode = new ToolboxNode( _model, _mvt );
        _toolboxLayer.addChild( _toolboxNode );
        
        // ELA value display
        _elaValueNode = new ELAValueNode( _model.getClimate() );
        _elaValueNode.setVisible( GlaciersConstants.DEBUG_ELA_VALUE_VISIBLE );
        _toolboxLayer.addChild( _elaValueNode );
        
        // Pan control, for moving the zoomed viewport
        _panControlNode = new PanControlNode( _birdsEyeViewport, _zoomedViewport, _mvt, GlaciersConstants.ZOOMED_VIEW_MAX_X );
        _viewportLayer.addChild( _panControlNode );
        
        // Arrows for moving zoomed viewport
        _leftScrollArrowNode = new LeftScrollArrowNode( _birdsEyeViewport, _zoomedViewport );
        _toolboxLayer.addChild( _leftScrollArrowNode );
        _rightScrollArrowNode = new RightScrollArrowNode( _birdsEyeViewport, _zoomedViewport, GlaciersConstants.ZOOMED_VIEW_MAX_X );
        _toolboxLayer.addChild( _rightScrollArrowNode );
    }
    
    public void cleanup() {
        _model.removeToolProducerListener( this );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setEquilibriumLineVisible( boolean visible ) {
        _equilibriumLineNode.setVisible( visible );
    }
    
    public void setAxesVisible( boolean visible ) {
        _leftElevationAxisNode.setVisible( visible );
        _rightElevationAxisNode.setVisible( visible );
        _distanceAxisNode.setVisible( visible );
    }
    
    public void setIceFlowVisible( boolean visible ) {
        _iceFlowNode.setVisible( visible );
    }
    
    public void setSnowfallVisible( boolean visible ) {
        _snowfallNode.setVisible( visible );
    }
    
    public static Point2D getBirdsEyeViewportOffset() {
        return new Point2D.Double( GlaciersConstants.BIRDS_EYE_VIEWPORT_OFFSET.getX(), GlaciersConstants.BIRDS_EYE_VIEWPORT_OFFSET.getY() );
    }
    
    //----------------------------------------------------------------------------
    // Layer management
    //----------------------------------------------------------------------------
    
    /*
     * Adds layer to the birds-eye view.
     */
    private void addToBirdsEyeView( PLayer layer ) {
        _birdsEyeCanvas.getCamera().addLayer( layer );
        _birdsEyeCanvas.getRoot().addChild( layer );
    }
    
    /*
     * Adds a layer to the zoomed view.
     */
    private void addToZoomedView( PLayer layer ) {
        _zoomedCanvas.getCamera().addLayer( layer );
        _zoomedCanvas.getRoot().addChild( layer );
    }
    
    /*
     * Adds a layer to both the birds-eye and zoomed views.
     */
    private void addToBothViews( PLayer layer ) {
        addToBirdsEyeView( layer );
        addToZoomedView( layer );
    }
    
    //----------------------------------------------------------------------------
    // Viewport and layout management
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of the play area when it is resized or made visible.
     */
    private void updateLayout() {

        // set the bounds of the birds-eye viewport, based on the birds-eye canvas size
        updateBirdsEyeViewportBounds();
        
        // set the bounds of the zoomed viewport, based on the zoomed canvas size
        updateZoomedViewportBounds();

        // keep the zoomed viewport inside the birds-eye viewport
        constrainZoomedViewport();
        
//        System.out.println( "PlayArea.updateLayout birdsEyeViewport.bounds   =" + _birdsEyeViewport.getBoundsReference() );//XXX
//        System.out.println( "PlayArea.updateLayout birdsEyeCamera.viewBounds =" + _birdsEyeCanvas.getCamera().getViewBounds() );//XXX
//        System.out.println( "PlayArea.updateLayout zoomedViewport.bounds     =" + _zoomedViewport.getBoundsReference() );//XXX
//        System.out.println( "PlayArea.updateLayout zoomedCamera.viewBounds   =" + _zoomedCanvas.getCamera().getViewBounds() );//XXX
    }
    
    /*
     * Updates the dimensions of the birds-eye viewport, based on the size of the birds-eye canvas.
     * The position of the birds-eye viewport never changes.
     */
    private void updateBirdsEyeViewportBounds() {
        
        Rectangle2D bb = _birdsEyeCanvas.getBounds();
        assert ( !bb.isEmpty() );
        double scale = _birdsEyeCanvas.getCamera().getViewScale();
        Rectangle2D rView = new Rectangle2D.Double( 0, 0, bb.getWidth() / scale, bb.getHeight() / scale );
        Rectangle2D rModel = _mvt.viewToModel( rView );
        _birdsEyeViewport.setBounds( _birdsEyeViewport.getX(), _birdsEyeViewport.getY(), rModel.getWidth(), rModel.getHeight() );
        
        // snowfall node has same bounds as the birds-eye view
        _snowfallNode.setWorldBounds( _birdsEyeViewport.getBoundsReference() );
    }
    
    /*
     * Updates the bounds of the zoomed viewport based on the size of the zoomed canvas.
     */
    private void updateZoomedViewportBounds() {
        Rectangle2D zb = _zoomedCanvas.getBounds();
        assert ( !zb.isEmpty() );
        double scale = _zoomedCanvas.getCamera().getViewScale();
        Rectangle2D rView = _mvt.modelToView( _zoomedViewport.getBoundsReference() );
        rView.setRect( rView.getX(), rView.getY(), zb.getWidth() / scale, zb.getHeight() / scale );
        Rectangle2D rModel = _mvt.viewToModel( rView );
        _zoomedViewport.setBounds( rModel );
    }
    
    /*
     * Vertically aligns the zoomed viewport.
     * Keeps the glacier vertically centered in the viewport.
     */
    private void verticallyAlignZoomedViewport() {
        Rectangle2D rModel = _zoomedViewport.getBounds();
        double centerX = rModel.getCenterX();
        double elevation = _model.getValley().getElevation( centerX );
        double newY = elevation + ( ( 1 - GlaciersConstants.ALIGNMENT_FACTOR_FOR_GLACIER_IN_ZOOMED_VIEWPORT ) * rModel.getHeight() );
        rModel.setRect( rModel.getX(), newY, rModel.getWidth(), rModel.getHeight() );
        _zoomedViewport.setBounds( rModel );
    }
    
    /* 
     * Keeps the left & right edges of the zoomed viewport inside the birds-eye viewport.
     */
    private void constrainZoomedViewport() {
        Rectangle2D bb = _birdsEyeViewport.getBoundsReference();
        Rectangle2D zb = _zoomedViewport.getBoundsReference();
        if ( zb.getX() < bb.getX() ) {
            double dx = bb.getX() - zb.getX();
            _zoomedViewport.translate( dx, 0 );
        }
        else if ( zb.getMaxX() > bb.getMaxX() ) {
            double dx = zb.getMaxX() - bb.getMaxX();
            _zoomedViewport.translate( -dx, 0 );
        }
    }
    
    /* 
     * When the birds-eye viewport changes...
     */
    private void handleBirdsEyeViewportChanged() {
        
        // translate the birds-eye view's camera
        Rectangle2D rModel = _birdsEyeViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        double scale = _birdsEyeCanvas.getCamera().getViewScale();
        _birdsEyeCanvas.getCamera().setViewOffset( -rView.getX() * scale, -rView.getY() * scale );
    }
    
    /*
     * When the zoomed viewport changes...
     */
    private void handleZoomedViewportChanged() {
        
        // translate the zoomed view's camera
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        double scale = _zoomedCanvas.getCamera().getViewScale();
        _zoomedCanvas.getCamera().setViewOffset( -rView.getX() * scale, -rView.getY() * scale );
        
        // move the toolbox
        updateToolboxPosition();
        
        // move the ELA value display
        updateELAValuePosition();
        
        // update the coordinate
        updateCoordinateAxes();
        
        // reposition the left/right scroll arrows
        updateScrollArrows();
    }
    
    /*
     * Moves the left/right scroll arrows to the upper left/right corners of the zoomed viewport.
     */
    private void updateScrollArrows() {
        
        final double margin = 5;
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        
        // left
        double xOffset = rView.getX() + margin;
        double yOffset = rView.getY() + _leftScrollArrowNode.getFullBoundsReference().getHeight()/2 + margin ;
        _leftScrollArrowNode.setOffset( xOffset, yOffset );
        
        // right
        xOffset = rView.getMaxX() - margin;
        yOffset = rView.getY() + _rightScrollArrowNode.getFullBoundsReference().getHeight()/2 + margin ;
        _rightScrollArrowNode.setOffset( xOffset, yOffset );
        
        // visibility of arrows
        _leftScrollArrowNode.setVisible( _zoomedViewport.getBoundsReference().getX() > _birdsEyeViewport.getBoundsReference().getX() );
        _rightScrollArrowNode.setVisible( _zoomedViewport.getBoundsReference().getMaxX() < GlaciersConstants.ZOOMED_VIEW_MAX_X );
    }
    
    /*
     * Moves the toolbox to the lower-left corner of the zoomed viewport
     */
    private void updateToolboxPosition() {
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        double xOffset = rView.getX() + 5;
        double yOffset = rView.getY() + rView.getHeight() - _toolboxNode.getFullBoundsReference().getHeight() - 5;
        _toolboxNode.setOffset( xOffset, yOffset );
    }
    
    /*
     * Moves the ELA value display to the lower-right of the toolbox
     */
    private void updateELAValuePosition() {
        double xOffset = _toolboxNode.getFullBoundsReference().getMaxX() + 5;
        double yOffset = _toolboxNode.getFullBoundsReference().getMaxY() - _elaValueNode.getFullBoundsReference().getHeight();
        _elaValueNode.setOffset( xOffset, yOffset );
    }
    
    /*
     * Moves the elevation (y) axes to the left & right edges of the zoomed viewport.
     * Rebuilds the distance (x) axis.
     */
    private void updateCoordinateAxes() {
        
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        
        // reposition the vertical (elevation) axes
        final double margin = 15; // pixels
        _leftElevationAxisNode.setOffset( rView.getMinX() + margin, _rightElevationAxisNode.getYOffset() );
        _rightElevationAxisNode.setOffset( rView.getMaxX() - margin, _rightElevationAxisNode.getYOffset() );
        
        // rebuild the horizontal (distance) axis, ticks in multiples of 1000 meters
        final int precision = 1000;
        final int minX = Math.max( 0, precision * (int)( ( rModel.getX() / precision ) - 1 ) ); // don't draw axis for x < 0
        final int maxX = precision * (int)( ( ( rModel.getX() + rModel.getWidth() ) / precision ) + 1 );
        _distanceAxisNode.setRange( minX, maxX );
    }
    
    //----------------------------------------------------------------------------
    // IToolProducerListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * When a tool is added to the model, create a node for it.
     * 
     * @param tool
     */
    public void toolAdded( AbstractTool tool ) {
        AbstractToolNode toolNode = ToolNodeFactory.createNode( tool, _model, _mvt, _toolboxNode.getTrashCanDelegate() );
        _toolsLayer.addChild( toolNode );
        _toolsMap.put( tool, toolNode );
    }

    /**
     * When a tool is removed from the model, remove its corresponding node.
     * 
     * @param tool
     */
    public void toolRemoved( AbstractTool tool ) {
        AbstractToolNode toolNode = (AbstractToolNode)_toolsMap.get( tool );
        _toolsLayer.removeChild( toolNode );
        _toolsMap.remove( tool );
        toolNode.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // IBoreholeProducerListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * When a borehole is added to the model, add a corresponding node.
     * 
     * @param borehole
     */
    public void boreholeAdded( Borehole borehole ) {
        BoreholeNode boreholeNode = new BoreholeNode( borehole, _mvt );
        _boreholeLayer.addChild( boreholeNode );
        _boreholesMap.put( borehole, boreholeNode );
    }
    
    /**
     * When a borehole is removed from the model, remove its corresponding node.
     * 
     * @param borehole
     */
    public void boreholeRemoved( Borehole borehole ) {
        BoreholeNode boreholeNode = (BoreholeNode)_boreholesMap.get( borehole );
        _boreholeLayer.removeChild( boreholeNode );
        _boreholesMap.remove( borehole );
        boreholeNode.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // IDebrisProducerListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * When debris is added to the model, add a corresponding node.
     * 
     * @param debris
     */
    public void debrisAdded( Debris debris ) {
        DebrisNode debrisNode = new DebrisNode( debris, _model.getGlacier(), _mvt );
        _debrisLayer.addChild( debrisNode );
        _debrisMap.put( debris, debrisNode );
    }
    
    /**
     * When debris is removed from the model, remove its corresponding node.
     * 
     * @param debris
     */
    public void debrisRemoved( Debris debris ) {
        DebrisNode debrisNode = (DebrisNode)_debrisMap.get( debris );
        assert( debrisNode != null );
        _debrisLayer.removeChild( debrisNode );
        _debrisMap.remove( debris );
        debrisNode.cleanup();
    }
    
    
    //----------------------------------------------------------------------------
    // IIceSurfaceRippleProducerListener implementation
    //----------------------------------------------------------------------------
    
    public void rippleAdded( IceSurfaceRipple ripple ) {
        IceSurfaceRippleNode rippleNode = new IceSurfaceRippleNode( ripple, _model.getGlacier(), _mvt );
        _ripplesLayer.addChild( rippleNode );
        _ripplesMap.put( ripple, rippleNode );
    }
    
    public void rippleRemoved( IceSurfaceRipple ripple ) {
        IceSurfaceRippleNode rippleNode = (IceSurfaceRippleNode) _ripplesMap.get( ripple );
        assert ( rippleNode != null );
        _ripplesLayer.removeChild( rippleNode );
        _ripplesMap.remove( ripple );
        rippleNode.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * PlayAreaResizeListener listens for size and visibility changes to the play area.
     */
    private static class PlayAreaResizeListener extends ComponentAdapter implements AncestorListener {

        private GlaciersPlayArea _playArea;
        private boolean _layoutDirty;

        public PlayAreaResizeListener( GlaciersPlayArea playArea ) {
            _playArea = playArea;
            _layoutDirty = true;
        }

        // If the play area is resized while visible, update the layout.
        // Otherwise, mark the layout as dirty and wait for it to become visible.
        public void componentResized( ComponentEvent e ) {
            if ( _playArea.isShowing() ) {
                _playArea.updateLayout();
                _layoutDirty = false;
            }
            else {
                _layoutDirty = true;
            }
        }

        // Called when the play area or one of its ancestors is make visible, either
        // by setVisible(true) being called or by its being added to the component hierarchy.
        // If the layout is dirty when this happens, update the layout.
        public void ancestorAdded( AncestorEvent e ) {
            if ( _layoutDirty && _playArea.isShowing() ) {
                _playArea.updateLayout();
                _layoutDirty = false;
            }
        }

        public void ancestorMoved( AncestorEvent event ) {}

        public void ancestorRemoved( AncestorEvent event ) {}
    }
}
