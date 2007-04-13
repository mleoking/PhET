/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.*;
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.enums.BSBottomPlotMode;
import edu.colorado.phet.boundstates.model.BSAbstractPotential;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSSuperpositionCoefficients;
import edu.colorado.phet.piccolo.event.ConstrainedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * BSMagnifyingGlass provides a magnified view of eigenstates and 
 * potential energy, as shown on the Energy chart. The user interface
 * looks like a standard magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSMagnifyingGlass extends PNode implements Observer, PlotChangeListener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Shows the center point of the lens, useful for debugging
    private static final boolean SHOW_CENTER_POINT = false;
    
    // Default values for things that can be changed with mutators
    private static final double DEFAULT_MAGNIFICATION = 10; // magnification power
    
    private Color LENS_COLOR = new Color( 0, 0, 0, 0 ); // transparent
    
    // Constants that determine the size of the magnifying glass parts
    private static final double LENS_DIAMETER = 100; // pixels
    private static final double BEZEL_WIDTH = 8; // pixels
    private static final double HANDLE_LENGTH = 65; // pixels
    private static final double HANDLE_WIDTH = 16; // pixels
    private static final double HANDLE_ARC_SIZE = 10; // pixels
    private static final double HANDLE_ROTATION = -20; // degrees; 

    // Convenience constants, DO NOT CHANGE!
    private static final double LENS_RADIUS = LENS_DIAMETER / 2;
    private static final double BEZEL_DIAMETER = LENS_DIAMETER + ( 2 * BEZEL_WIDTH );
    private static final double BEZEL_RADIUS = BEZEL_DIAMETER / 2;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _magnification;
    private BSModel _model;
    private BSCombinedChartNode _chartNode;
    
    // All of the "parts" of the magnifying glass
    private PComposite _partsNode;
    private PPath _lensNode;
    private PPath _bezelNode;
    private PPath _handleNode;

    // All of the things that are viewed through the lens
    private PComposite _viewNode;
    private ClippedPath _chartBackgroundNode;
    private ClippedPath _chartEdgeNode;
    private ClippedPath _potentialNode;
    private PComposite _eigenstatesParentNode;
    
    private BSColorScheme _colorScheme;
    
    private MagnifyingGlassEventHandler _eventHandler;
    
    private boolean _bandSelectionEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param chartNode
     * @param colorScheme
     */
    public BSMagnifyingGlass( BSCombinedChartNode chartNode, BSColorScheme colorScheme ) {
        _magnification = DEFAULT_MAGNIFICATION;
        _chartNode = chartNode;
        _bandSelectionEnabled = false;
        initNodes();
        initEventHandling();
        setColorScheme( colorScheme );
    }
    
    /*
     * Initializes the Piccolo nodes that make up the magnifying glass.
     * In order for constrained dragging to work properly, we need to 
     * make sure that the drag handler is operation on bounds that 
     * contain only the parts of the magnifying glass.
     * All of the "parts" of the magnifying glass are in their own subtree
     * (under partsNode) and this is what is dragged.
     * All of the things visible through the lens are in their own subtree
     * (under viewNode) and their drawing is clipped to the lens shape.
     */
    private void initNodes() {
        
        // Lens
        Shape lensShape = new Ellipse2D.Double( -LENS_RADIUS, -LENS_RADIUS, LENS_DIAMETER, LENS_DIAMETER ); // x,y,w,h
        {
            _lensNode = new PPath();
            _lensNode.setPathTo( lensShape );
            _lensNode.setPaint( LENS_COLOR );
            _lensNode.setStrokePaint( LENS_COLOR );
        }
        
        // Bezel 
        {
            Shape bezelShape = new Ellipse2D.Double( -BEZEL_RADIUS, -BEZEL_RADIUS, BEZEL_DIAMETER, BEZEL_DIAMETER ); // x,y,w,h
            Area bezelArea = new Area( bezelShape );
            Area lensArea = new Area( lensShape );
            bezelArea.exclusiveOr( lensArea );
            _bezelNode = new PPath();
            _bezelNode.setPathTo( bezelArea );
            // paint is set in setColorScheme
        }
        
        // Handle
        {
            Shape handleShape = new RoundRectangle2D.Double( -HANDLE_WIDTH / 2, LENS_RADIUS, HANDLE_WIDTH, HANDLE_LENGTH, HANDLE_ARC_SIZE, HANDLE_ARC_SIZE );
            _handleNode = new PPath();
            _handleNode.setPathTo( handleShape );
            _handleNode.rotate( Math.toRadians( HANDLE_ROTATION ) );
            // paint is set in setColorScheme
        }
        
        // Center point of the lens, for debugging
        PPath centerNode = new PPath();
        if ( SHOW_CENTER_POINT ) {
            final double diameter = 5;
            final double radius = diameter / 2;
            Shape centerShape = new Ellipse2D.Double( -radius, -radius, diameter, diameter );
            centerNode.setPathTo( centerShape );
            centerNode.setPaint( Color.ORANGE );
        }
        
        // Parts of the magnifying glass
        {
            _partsNode = new PComposite();
            _partsNode.addChild( _handleNode ); // bottom
            _partsNode.addChild( _bezelNode );
            _partsNode.addChild( centerNode );
            _partsNode.addChild( _lensNode ); // top
        }
        
        // Chart background node
        {
            _chartBackgroundNode = new ClippedPath();
            Shape chartBackgroundShape = new Rectangle2D.Double( -LENS_RADIUS, -LENS_RADIUS, LENS_DIAMETER, LENS_DIAMETER );
            _chartBackgroundNode.setPathTo( chartBackgroundShape );
            // paint is set in setColorScheme
        }
        
        // Chart edge node
        {
            _chartEdgeNode = new ClippedPath();
            _chartEdgeNode.setPaint( BSConstants.CANVAS_BACKGROUND );
            // path is set in updateDisplay
        }
          
        // Potential plot
        {
            _potentialNode = new ClippedPath();
            _potentialNode.setStroke( BSConstants.POTENTIAL_ENERGY_STROKE );
            // paint is set in setColorScheme
        }
        
        // Eigenstates
        {
            _eigenstatesParentNode = new PComposite();
        }
        
        // Everything that's visible through the lens
        {
            _viewNode = new PComposite();
            _viewNode.addChild( _chartBackgroundNode ); // bottom
            _viewNode.addChild( _potentialNode );
            _viewNode.addChild( _eigenstatesParentNode );
            _viewNode.addChild( _chartEdgeNode ); // on top, to cover plots that fall off edge of chart
        }

        // Picking -- only the physical "parts" are pickable
        {
            this.setPickable( false );
            _viewNode.setPickable( false );
            _viewNode.setChildrenPickable( false );
            _partsNode.setPickable( true );
            _partsNode.setChildrenPickable( true );
        }
        
        addChild( _viewNode );
        addChild( _partsNode ); // on top, so we get mouse events
    }
    
    /*
     * Initializes event handling.
     */
    private void initEventHandling() {
        
        // Changes the cursor to a "hand"
        _partsNode.addInputEventListener( new CursorHandler() );

        // Handles mouse events
        _eventHandler = new MagnifyingGlassEventHandler();
        _partsNode.addInputEventListener( _eventHandler );
        
         // For constrained dragging, treat as a point at the center of the lens.
        _eventHandler.setTreatAsPointEnabled( true );
        _eventHandler.setNodeCenter( BEZEL_RADIUS, BEZEL_RADIUS );
        
        // Watches for changes to the Energy plot
        XYPlot energyPlot = _chartNode.getCombinedChart().getEnergyPlot();
        energyPlot.addChangeListener( this );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    /**
     * Sets the magnification.
     * 
     * @param magnification
     * @throws IllegalArgumentException if magnification is < 1
     */
    public void setMagnification( double magnification ) {
        if ( magnification < 1 ) {
            throw new IllegalArgumentException( "magnification must be >= 1" );
        }
        _magnification = magnification;
        updateDisplay();
    }
    
    /**
     * Gets the magnification.
     * 
     * @return magnification power
     */
    public double getMagnification() {
        return _magnification;
    }
    
    /*
     * Is the magnifying glass initialized?
     */
    private boolean isInitialized() {
        return ( _model != null );
    }
    
    /**
     * Sets the model.
     * 
     * @param model
     */
    public void setModel( BSModel model ) {
        if ( _model != null ) {
            _model.deleteObserver( this );
        }
        _model = model;
        _model.addObserver( this );
        updateDisplay();
    }
    
    /**
     * Sets the color scheme.
     * 
     * @param colorScheme
     */
    public void setColorScheme( BSColorScheme colorScheme ) {
        _colorScheme = colorScheme;
        _chartBackgroundNode.setPaint( _colorScheme.getChartColor() );
        _bezelNode.setPaint( _colorScheme.getMagnifyingGlassBezelColor() );
        _handleNode.setPaint( _colorScheme.getMagnifyingGlassHandleColor() );
        _potentialNode.setStrokePaint( _colorScheme.getPotentialEnergyColor() );
        updateDisplay();
    }

    /**
     * Sets the drag bounds.
     * The x coordinate and width are modified so that the magnifying
     * glass stays completely within the drag bounds when dragging along
     * the horizontal axis.
     * 
     * @param dragBounds the bounds of the energy plot
     */
    public void setDragBounds( Rectangle2D dragBounds ) {
        final double x = dragBounds.getX() + BEZEL_RADIUS;
        final double y = dragBounds.getY();
        final double w = dragBounds.getWidth() - BEZEL_DIAMETER;
        final double h = dragBounds.getHeight();
        _eventHandler.setDragBounds( x, y, w, h );
        updateDisplay();
    }
    
    /**
     * Gets a reference to the "parts" node, for attaching a help item.
     * Clients should NOT change this node.
     * 
     * @return PNode
     */
    public PNode getPartsNode() {
        return _partsNode;
    }
    
    /**
     * Sets the selection mode.
     * @param mode
     */
    public void setMode( BSBottomPlotMode mode ) {
        _bandSelectionEnabled = ( mode == BSBottomPlotMode.AVERAGE_PROBABILITY_DENSITY );
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    /**
     * Update the display when the magnifying glass is made visible.
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        _partsNode.setPickable( visible );
        _partsNode.setChildrenPickable( visible );
        if ( visible ) {
            updateDisplay();
        }
    }
    
    /**
     * Sets the transform for the child nodes.
     * Since _partsNode is what's being dragged around, we need to 
     * pass the transform to the child nodes (namely _partsNode and _viewNode).
     * Rather than implement all of the transform-related methods
     * (eg, setOffset), we'll require clients to use this implementation
     * of setTransform.
     * 
     * @param transform
     */
    public void setTransform( AffineTransform transform ) {
        _partsNode.setTransform( transform );
        _viewNode.setTransform( transform );
    }
    
    /**
     * Unsupported, see note in setTransform.
     */
    public void setOffset( double x, double y ) {
        throw new UnsupportedOperationException( "use setTransform" );
    }
    
    /**
     * Unsupported, see note in setTransform.
     */
    public void setOffset( Point2D point ) {
        throw new UnsupportedOperationException( "use setTransform" );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the display when the model changes.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( arg == BSModel.PROPERTY_PARTICLE ) {
            // ignore, we'll be notified that the potential changed
        }
        else {
            updateDisplay();
        }
    }
    
    //----------------------------------------------------------------------------
    // PlotChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the display when the Energy plot changes.
     */
    public void plotChanged( PlotChangeEvent event ) {
        updateDisplay();
    }
    
    //----------------------------------------------------------------------------
    // 
    //----------------------------------------------------------------------------
    
    /**
     * Updates the display.
     */
    public void updateDisplay() {
         
        if ( !getVisible() || !isInitialized() ) {
            return;
        }
        
        // Range of values that are physically obscured by the lens.
        Point2D lensCenter = getLensCenter();
        final double centerPosition = lensCenter.getX();
        final double centerEnergy = lensCenter.getY();
        if ( Double.isNaN( centerPosition ) || Double.isInfinite( centerPosition ) ) {
            return;
        }
        Point2D lensMin = _lensNode.localToGlobal( new Point2D.Double( -LENS_RADIUS, LENS_RADIUS ) ); // +y is down
        Point2D chartMin = _chartNode.globalToLocal( lensMin );
        Point2D p2 = _chartNode.nodeToEnergy( chartMin );
        final double minPosition = p2.getX();
        final double minEnergy = p2.getY();
        final double maxPosition = centerPosition + ( centerPosition - minPosition );
        final double maxEnergy = centerEnergy + ( centerEnergy - minEnergy );
                
        // Range of values that are visible in the lens (magnified).
        final double magMinPosition = centerPosition - ( ( centerPosition - minPosition ) / _magnification );
        final double magMaxPosition = centerPosition + ( ( maxPosition - centerPosition ) / _magnification );
        final double magMinEnergy = centerEnergy - ( ( centerEnergy - minEnergy ) / _magnification );
        final double magMaxEnergy = centerEnergy + ( ( maxEnergy - centerEnergy ) / _magnification );
        final double magDeltaPosition = ( magMaxPosition - magMinPosition ) / LENS_DIAMETER;
        final double magDeltaEnergy = ( magMaxEnergy - magMinEnergy ) / LENS_DIAMETER;
       
        // Draw the eigenstates that are visible through the lens
        BSEigenstate[] eigenstate = _model.getEigenstates();
        BSSuperpositionCoefficients superpositionCoefficients = _model.getSuperpositionCoefficients();
        _eigenstatesParentNode.removeAllChildren();
        for ( int i = 0; i < eigenstate.length; i++ ) {
            
            double eigenEnergy = eigenstate[i].getEnergy();
            if ( eigenEnergy >= magMinEnergy && eigenEnergy <= magMaxEnergy ) {
                
                final double y = ( centerEnergy - eigenEnergy ) / magDeltaEnergy; // Java's +y is down!
                
                ClippedPath line = new ClippedPath();
                GeneralPath path = new GeneralPath();
                path.moveTo( (float)-LENS_RADIUS, (float)y );
                path.lineTo( (float)+LENS_RADIUS, (float)y );
                line.setPathTo( path );
                _eigenstatesParentNode.addChild( line );
                
                Stroke lineStroke = null;
                Color lineColor = null;
                if ( i == _model.getHilitedEigenstateIndex() ) {
                    // eigenstate is hilited
                    lineStroke = BSConstants.EIGENSTATE_HILITE_STROKE;
                    lineColor = _colorScheme.getEigenstateHiliteColor();
                    line.moveToFront();
                }
                else if ( superpositionCoefficients.getCoefficient( i ) != 0 ) {
                    // eigenstate is selected
                    lineStroke = BSConstants.EIGENSTATE_SELECTION_STROKE;
                    lineColor = _colorScheme.getEigenstateSelectionColor();
                    line.moveToBack();
                }
                else {
                    lineStroke = BSConstants.EIGENSTATE_NORMAL_STROKE;
                    lineColor = _colorScheme.getEigenstateNormalColor();
                    line.moveToBack();
                }
                line.setStroke( lineStroke );
                line.setStrokePaint( lineColor );
            }
        }
        
        // Draw the potential energy visible through the lens
        {
            BSAbstractPotential potential = _model.getPotential();
            GeneralPath path = new GeneralPath();
            double position = magMinPosition;
            while ( position <= magMaxPosition ) {
                double energy = potential.getEnergyAt( position );
                
                final double x = ( position - centerPosition ) / magDeltaPosition;
                double y = ( centerEnergy - energy ) / magDeltaEnergy; // Java's +y is down!
                
                /* 
                 * WORKAROUND:
                 * JFreeChart handles Double.NaN ok, but infinite or very large values
                 * in the dataset will cause a sun.dc.pr.PRException with the message 
                 * "endPath: bad path" on Windows.  So we constrain the y coordinates
                 * to some sufficiently large value.
                 */
                {
                    if ( y > 2000 ) {
                        y = 2000;
                    }
                    else if ( y < -2000 ) {
                        y = -2000;
                    }
                }
                
                if ( position == magMinPosition ) {
                    path.moveTo( (float) x, (float) y );
                }
                else {
                    path.lineTo( (float) x, (float) y );
                }
                
                position += magDeltaPosition;
            }
            _potentialNode.setPathTo( path );
        }
        
        // Draw the top or bottom edge of the chart, if it's visible.
        // Left and right edges will never be visible because of how dragging is constrained.
        {
            // Find the plot's energy range
            BSEnergyPlot energyPlot = _chartNode.getCombinedChart().getEnergyPlot();
            ValueAxis yAxis = energyPlot.getRangeAxis();
            Range yRange = yAxis.getRange();
            final double plotMinEnergy = yRange.getLowerBound();
            final double plotMaxEnergy = yRange.getUpperBound();

            // Create a rectangle that represents the piece of PCanvas that's visible
            double x = LENS_DIAMETER;
            double y = LENS_DIAMETER;
            double w = 1;
            double h = 1;
            if ( magMaxEnergy > plotMaxEnergy ) {
                // Top edge is visible
                x = -LENS_RADIUS;
                w = LENS_DIAMETER;
                h = Math.abs( LENS_DIAMETER * ( magMaxEnergy - plotMaxEnergy ) / ( magMaxEnergy - magMinEnergy ) );
                y = -LENS_RADIUS; 
            }
            else if ( magMinEnergy < plotMinEnergy ) {
                // Bottom edge is visible
                x = -LENS_RADIUS;
                w = LENS_DIAMETER;
                h = Math.abs( LENS_DIAMETER * ( magMinEnergy - plotMinEnergy ) / ( magMaxEnergy - magMinEnergy ) );
                y = LENS_RADIUS - h;
            }
            
            Shape chartEdgeShape = new Rectangle2D.Double( x, y, w, h );
            _chartEdgeNode.setPathTo( chartEdgeShape );
        }
    }
    
    /*
     * Selects the eigenstate (or band) that is currently hilited.
     * Clear the hilite so that the selection is displayed.
     */
    private void selectEigenstate( Point2D mousePoint ) {

        int hiliteIndex = _model.getHilitedEigenstateIndex();
        if ( hiliteIndex != BSEigenstate.INDEX_UNDEFINED ) {

            if ( !_bandSelectionEnabled ) {
                _model.getSuperpositionCoefficients().setOneCoefficient( hiliteIndex );
            }
            else {
                final int numberOfWells = _model.getPotential().getNumberOfWells();
                final int bandSize = numberOfWells;
                final int bandIndex = (int)( hiliteIndex / bandSize ); // bands numbered starting from zero
                _model.getSuperpositionCoefficients().setBandCoefficients( bandIndex, bandSize, 1  );
            }
            
            _model.setHilitedEigenstateIndex( BSEigenstate.INDEX_UNDEFINED );
        }
    }
    
    /*
     * Hilites the eigenstate that is closest to the magnified energy at the mouse position.
     */
    private void hiliteEigenstate( Point2D mousePoint ) {
        
        // Energy at the center of the lens.
        final double centerEnergy = getLensCenter().getY();
        
        // Unmagnified energy at the mouse position.
        Point2D chartPoint = _chartNode.globalToLocal( mousePoint );
        Point2D energyPoint = _chartNode.nodeToEnergy( chartPoint );
        final double mouseEnergy = energyPoint.getY();
        
        // Adjust for magnification
        double magnifiedEnergy = centerEnergy + ( ( mouseEnergy - centerEnergy ) / _magnification );
        
        // Hilite the closest eigenstate visible through the lens
        double hiliteThreshold = BSConstants.HILITE_ENERGY_THRESHOLD / _magnification;
        int hiliteIndex = _model.getClosestEigenstateIndex( magnifiedEnergy, hiliteThreshold );
        if ( hiliteIndex != BSEigenstate.INDEX_UNDEFINED ) {
            final double hiliteEnergy = _model.getEigenstate( hiliteIndex ).getEnergy();
            if ( !isInLens( hiliteEnergy ) ) {
                hiliteIndex = BSEigenstate.INDEX_UNDEFINED;
            }
        }
        _model.setHilitedEigenstateIndex( hiliteIndex );
    }
    
    /*
     * Gets the (energy,position) coordinates at the center of the lens.
     */
    private Point2D getLensCenter() {
        Point2D lensCenter = _lensNode.localToGlobal( new Point2D.Double( 0, 0 ) );
        Point2D chartCenter = _chartNode.globalToLocal( lensCenter );
        Point2D modelCenter = _chartNode.nodeToEnergy( chartCenter );
        return modelCenter;
    }
    
    /*
     * Is the specified mouse point inside the lens?
     * 
     * @param point a mouse point, in global coordinates
     */
    private boolean isInLens( Point2D point ) {
        Rectangle2D lensBounds = _partsNode.localToGlobal( _lensNode.getFullBounds() );
        return lensBounds.contains( point );
    }
    
    private boolean isInLens( final double energy ) {
        Point2D lensCenter = getLensCenter();
        final double centerEnergy = lensCenter.getY();
        Point2D lensMin = _lensNode.localToGlobal( new Point2D.Double( -LENS_RADIUS, LENS_RADIUS ) ); // +y is down
        Point2D chartMin = _chartNode.globalToLocal( lensMin );
        Point2D p2 = _chartNode.nodeToEnergy( chartMin );
        final double minEnergy = p2.getY();
        final double maxEnergy = centerEnergy + ( centerEnergy - minEnergy );
        final double magMinEnergy = centerEnergy - ( ( centerEnergy - minEnergy ) / _magnification );
        final double magMaxEnergy = centerEnergy + ( ( maxEnergy - centerEnergy ) / _magnification );
        return ( energy <= magMaxEnergy && energy >= magMinEnergy );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * Handles events for the magnifying glass.
     */
    private class MagnifyingGlassEventHandler extends ConstrainedDragHandler {
        
        private boolean _dragging; // true while a drag is in progress
        
        public MagnifyingGlassEventHandler() {
            _dragging = false;
        }
        
        /**
         * If the mouse moves inside the lens, 
         * hilite an eigenstate.
         */
        public void mouseMoved( PInputEvent e ) {
            super.mouseMoved( e );
            if ( !_dragging ) {
                if ( isInLens( e.getPosition() ) ) {
                    hiliteEigenstate( e.getPosition() );
                }
                else {
                    _model.setHilitedEigenstateIndex( BSEigenstate.INDEX_UNDEFINED );
                }
            }
        }
        
        /**
         * If the mouse is pressed and released without dragging in the lens,
         * select an eigenstate.
         */
        public void mouseReleased( PInputEvent e ) {
            super.mouseReleased( e );
            if ( !_dragging ) {
                if ( isInLens( e.getPosition() ) ) {
                    selectEigenstate( e.getPosition() );
                }
            }
            _dragging = false;
        }
        
        /**
         * If the magnifying glass is dragged, 
         * update what appears in the lens.
         */
        public void mouseDragged( PInputEvent e ) {
            _dragging = true;
            super.mouseDragged( e );
            updateDisplay();
            _viewNode.setOffset( _partsNode.getOffset() );
            setFullBoundsInvalid( true );
        }
        
        /**
         * When the mouse leaves the magnifying glass, clear the hilited eigenstate.
         */
        public void mouseExited( PInputEvent e ) {
            _model.setHilitedEigenstateIndex( BSEigenstate.INDEX_UNDEFINED );
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * Piccolo node for drawing paths inside the magnifying glass' lens.
     * The node is clipped to the lens' boundary.
     * Note that children are *not* clipped.
     */
    private class ClippedPath extends PPath {
        
        public ClippedPath() {}
        
        /*
         * Clips the node to the lens.
         * Important to use pushClip and popClip to set/restore the clip!
         * Don't try to do this directly to the underlying Graphics2D.
         */
        protected void paint( PPaintContext paintContext ) {
            GeneralPath lensPath = _lensNode.getPathReference();
            paintContext.pushClip( lensPath );
            super.paint( paintContext );
            paintContext.popClip( lensPath );
        }
    }
}
