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
import edu.colorado.phet.glaciers.control.ToolboxNode;
import edu.colorado.phet.glaciers.model.AbstractModel;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.Viewport;
import edu.colorado.phet.glaciers.model.IToolProducer.ToolProducerListener;
import edu.colorado.phet.glaciers.model.Viewport.ViewportListener;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;

/**
 * PlayArea is the area of the application that constains the birds-eye and zoomed views
 * of the world.  
 * <p>
 * The birds-eye view appears at the top of the play area, and shows a tiny
 * overview picture of the world. A viewport shown in the birds-eye view indicates the 
 * portion of the world shown in the zoomed view. A penguin image can be dragged 
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
public class PlayArea extends JPanel implements ToolProducerListener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // constant height of the birds-eye view, in pixels
    private static final double BIRDS_EYE_VIEW_HEIGHT = 75;
    
    // camera view scales
    private static final double BIRDS_EYE_CAMERA_VIEW_SCALE = 0.2;
    private static final double ZOOMED_CAMERA_VIEW_SCALE = 1;
    
    // offset of upper-left corner of birds-eye viewport from highest point on the glacier
    private static final Point2D BIRDS_EYE_VIEWPORT_OFFSET = new Point2D.Double( -500, +1000 ); // meters
    
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
    private AbstractModel _model;
    private Viewport _birdsEyeViewport, _zoomedViewport;
    
    // View
    private PhetPCanvas _birdsEyeCanvas, _zoomedCanvas;
    private PLayer _mountainsLayer, _valleyLayer, _glacierLayer, _coordinatesLayer, _toolboxLayer, _toolsLayer, _viewportLayer;
    private ToolboxNode _toolboxNode;
    private PNode _penguinNode;
    private EquilibriumLineNode _equilibriumLineNode;
    private ElevationAxisNode _leftElevationAxisNode, _rightElevationAxisNode;
    private HashMap _toolsMap; // key=AbstractTool, value=AbstractToolNode, used for removing tool nodes when their model elements are deleted
    private ModelViewTransform _mvt;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PlayArea( AbstractModel model, ModelViewTransform mvt, double valleyMinX, double valleyMaxX ) {
        super();
        
        assert( ZOOMED_CAMERA_VIEW_SCALE >= BIRDS_EYE_CAMERA_VIEW_SCALE );
        assert( BIRDS_EYE_CAMERA_VIEW_SCALE > 0 );
        assert( ZOOMED_CAMERA_VIEW_SCALE > 0 );
        
        _model = model;
        _model.addToolProducerListener( this ); // manage nodes when tools are added/removed
        
        _mvt = mvt;
        
        // elevation left edge of valley
        double elevationAtMinX = _model.getValley().getElevation( valleyMinX );
        
        // birds-eye viewport
        _birdsEyeViewport = new Viewport( "birds-eye" ); // bounds will be set when play area is resized
        _birdsEyeViewport.setPosition( valleyMinX + BIRDS_EYE_VIEWPORT_OFFSET.getX(), elevationAtMinX + BIRDS_EYE_VIEWPORT_OFFSET.getY() );
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
        _birdsEyeCanvas.getCamera().setViewScale( BIRDS_EYE_CAMERA_VIEW_SCALE );
        
        // zoomed view
        _zoomedCanvas = new PhetPCanvas();
        _zoomedCanvas.setBorder( null ); // no border on the canvas, because we use canvas bounds in calculations
        _zoomedCanvas.setBackground( GlaciersConstants.ZOOMED_CANVAS_COLOR );
        _zoomedCanvas.getCamera().setViewScale( ZOOMED_CAMERA_VIEW_SCALE );
        // zoomed camera offset will be set based on viewport position

        // Layout the panel
        {
            // put a border around the birds-eye canvas, and constrain its height
            JPanel birdsEyeWrapperPanel = new JPanel( new BorderLayout() );
            birdsEyeWrapperPanel.add( Box.createVerticalStrut( (int) BIRDS_EYE_VIEW_HEIGHT ), BorderLayout.WEST ); // fixed height
            birdsEyeWrapperPanel.add( _birdsEyeCanvas, BorderLayout.CENTER );
            birdsEyeWrapperPanel.setBorder( CANVAS_BORDER );

            // add a vertical spacer below the birds-eye cANVAS
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
        _mountainsLayer = new PLayer();
        _valleyLayer = new PLayer();
        _glacierLayer = new PLayer();
        _coordinatesLayer = new PLayer();
        _toolboxLayer = new PLayer();
        _toolsLayer = new PLayer();
        _viewportLayer = new PLayer();
        addToBothViews( _mountainsLayer );
        addToBothViews( _valleyLayer );
        addToBothViews( _glacierLayer );
        addToZoomedView( _coordinatesLayer );
        addToZoomedView( _toolboxLayer );
        addToBothViews( _toolsLayer );
        addToBirdsEyeView( _viewportLayer );
        
        // viewport in the birds-eye view indicates what is shown in zoomed view
        float strokeWidth = VIEWPORT_STROKE_WIDTH / (float)BIRDS_EYE_CAMERA_VIEW_SCALE;
        ViewportNode viewportNode = new ViewportNode( _zoomedViewport, strokeWidth, _mvt );
        _viewportLayer.addChild( viewportNode );
        
        // Mountains
        PNode mountainsNode = new MountainsNode( _model.getValley(), _mvt, valleyMinX, valleyMaxX );
        _mountainsLayer.addChild( mountainsNode );
        
        // Valley
        PNode valleyNode = new ValleyNode( _model.getValley(), _mvt, valleyMinX, valleyMaxX );
        _valleyLayer.addChild( valleyNode );
        
        // Glacier
        PNode glacierNode = new GlacierNode( _model.getGlacier(), _mvt );
        _glacierLayer.addChild( glacierNode );
        
        // Coordinates
        _leftElevationAxisNode = new ElevationAxisNode( _mvt, GlaciersConstants.ELEVATION_AXIS_RANGE, GlaciersConstants.ELEVATION_AXIS_TICK_SPACING, false );
        _rightElevationAxisNode = new ElevationAxisNode( _mvt, GlaciersConstants.ELEVATION_AXIS_RANGE, GlaciersConstants.ELEVATION_AXIS_TICK_SPACING, true );
        _coordinatesLayer.addChild( _leftElevationAxisNode );
        _coordinatesLayer.addChild( _rightElevationAxisNode );
        
        // Equilibrium line
        _equilibriumLineNode = new EquilibriumLineNode( _model.getClimate(), _mvt );
        _glacierLayer.addChild( _equilibriumLineNode );
        
        // Toolbox
        _toolsMap = new HashMap();
        _toolboxNode = new ToolboxNode( _model, _mvt );
        _toolboxLayer.addChild( _toolboxNode );
        
        // Penguin is the control for moving the zoomed viewport
        _penguinNode = new PenguinNode( _birdsEyeViewport, _zoomedViewport, _mvt );
        _viewportLayer.addChild( _penguinNode );
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
    }
    
    public void setIceFlowVisible( boolean visible ) {
        //XXX
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
        double newY = elevation + ( 0.55 * rModel.getHeight() );
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
        
        // move the vertical (elevation) axis
        updateElevationAxis();
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
     * Moves the elevation (vertical) coordinate axes to the left & right edges of the zoomed viewport.
     */
    private void updateElevationAxis() {
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        final double margin = 15; // pixels
        _leftElevationAxisNode.setOffset( rView.getMinX() + margin, _rightElevationAxisNode.getYOffset() );
        _rightElevationAxisNode.setOffset( rView.getMaxX() - margin, _rightElevationAxisNode.getYOffset() );
    }
    
    //----------------------------------------------------------------------------
    // ToolProducerListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * When a tool is added to the model, create a node for it.
     * 
     * @param tool
     */
    public void toolAdded( AbstractTool tool ) {
        PNode node = ToolNodeFactory.createNode( tool, _mvt );
        _toolsLayer.addChild( node );
        _toolsMap.put( tool, node );
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
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * PlayAreaResizeListener listens for size and visibility changes to the play area.
     */
    private static class PlayAreaResizeListener extends ComponentAdapter implements AncestorListener {

        private PlayArea _playArea;
        private boolean _layoutDirty;

        public PlayAreaResizeListener( PlayArea playArea ) {
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
