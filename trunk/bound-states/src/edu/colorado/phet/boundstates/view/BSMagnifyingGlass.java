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
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.*;
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.Range;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
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
public class BSMagnifyingGlass extends PNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG = true;
    
    private static final double DEFAULT_MAGNIFICATION = 10;
    
    private static final double LENS_DIAMETER = 100; // pixels
    private static final double BEZEL_WIDTH = 8; // pixels
    private static final double BEZEL_DIAMETER = LENS_DIAMETER + ( 2 * BEZEL_WIDTH );
    private static final double HANDLE_LENGTH = 65; // pixels
    private static final double HANDLE_WIDTH = HANDLE_LENGTH/4; // pixels
    private static final double HANDLE_ARC_SIZE = 10; // pixels
    private static final double HANDLE_ROTATION = -20; // degrees;    

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _magnification;
    private BSModel _model;
    private BSCombinedChartNode _chartNode;
    private BSEigenstatesNode _eigenstatesNode;
    
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
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param chartNode
     * @param colorScheme
     */
    public BSMagnifyingGlass( BSCombinedChartNode chartNode, BSEigenstatesNode eigenstatesNode, BSColorScheme colorScheme ) {
        _magnification = DEFAULT_MAGNIFICATION;
        _chartNode = chartNode;
        _eigenstatesNode = eigenstatesNode;
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
        Shape lensShape = new Ellipse2D.Double( -LENS_DIAMETER / 2, -LENS_DIAMETER / 2, LENS_DIAMETER, LENS_DIAMETER ); // x,y,w,h
        {
            _lensNode = new PPath();
            _lensNode.setPathTo( lensShape );
            Color lensColor = new Color( 0, 0, 0, 0 ); // transparent
            _lensNode.setPaint( lensColor );
            _lensNode.setStrokePaint( lensColor );
        }
        
        // Bezel 
        {
            Shape bezelShape = new Ellipse2D.Double( -BEZEL_DIAMETER / 2, -BEZEL_DIAMETER / 2, BEZEL_DIAMETER, BEZEL_DIAMETER ); // x,y,w,h
            Area bezelArea = new Area( bezelShape );
            Area lensArea = new Area( lensShape );
            bezelArea.exclusiveOr( lensArea );
            _bezelNode = new PPath();
            _bezelNode.setPathTo( bezelArea );
            // paint is set in setColorScheme
        }
        
        // Handle
        {
            Shape handleShape = new RoundRectangle2D.Double( -HANDLE_WIDTH / 2, LENS_DIAMETER / 2, HANDLE_WIDTH, HANDLE_LENGTH, HANDLE_ARC_SIZE, HANDLE_ARC_SIZE );
            _handleNode = new PPath();
            _handleNode.setPathTo( handleShape );
            _handleNode.rotate( Math.toRadians( HANDLE_ROTATION ) );
            // paint is set in setColorScheme
        }
        
        // Center point of the lens, for debugging
        PPath centerNode = new PPath();
        if ( DEBUG ) {
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
            Shape chartBackgroundShape = new Rectangle2D.Double( -LENS_DIAMETER / 2, -LENS_DIAMETER / 2, LENS_DIAMETER, LENS_DIAMETER );
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
        _eventHandler.setNodeCenter( BEZEL_DIAMETER / 2, BEZEL_DIAMETER / 2 );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the magnification.
     * 
     * @param magnification
     */
    public void setMagnification( double magnification ) {
        _magnification = magnification;
        updateDisplay();
    }
    
    /**
     * Gets the magnification.
     * 
     * @return
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
        final double x = dragBounds.getX() + ( BEZEL_DIAMETER / 2 );
        final double y = dragBounds.getY();
        final double w = dragBounds.getWidth() - BEZEL_DIAMETER;
        final double h = dragBounds.getHeight();
        _eventHandler.setDragBounds( x, y, w, h );
        updateDisplay();
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
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    /**
     * Update the display when the magnifying glass is made visible.
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        setPickable( visible );
        setChildrenPickable( visible );
        if ( visible ) {
            updateDisplay();
        }
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
        
        _eigenstatesParentNode.removeAllChildren();
        
        // Range of values that are physically obscured by the lens.
        Point2D lensCenter = getLensCenter();
        final double centerPosition = lensCenter.getX();
        final double centerEnergy = lensCenter.getY();
        if ( Double.isNaN( centerPosition ) || Double.isInfinite( centerPosition ) ) {
            return;
        }
        Point2D lensMin = _lensNode.localToGlobal( new Point2D.Double( -LENS_DIAMETER / 2, LENS_DIAMETER / 2 ) ); // +y is down
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
        for ( int i = 0; i < eigenstate.length; i++ ) {
            
            double eigenEnergy = eigenstate[i].getEnergy();
            if ( eigenEnergy >= magMinEnergy && eigenEnergy <= magMaxEnergy ) {
                
                final double y = ( centerEnergy - eigenEnergy ) / magDeltaEnergy; // Java's +y is down!
                
                ClippedPath line = new ClippedPath();
                GeneralPath path = new GeneralPath();
                path.moveTo( (float)-LENS_DIAMETER/2, (float)y );
                path.lineTo( (float)+LENS_DIAMETER/2, (float)y );
                line.setPathTo( path );
                Stroke lineStroke = BSConstants.EIGENSTATE_NORMAL_STROKE;
                Color lineColor = _colorScheme.getEigenstateNormalColor();
                if ( i == _model.getHilitedEigenstateIndex() ) {
                    lineStroke = BSConstants.EIGENSTATE_HILITE_STROKE;
                    lineColor = _colorScheme.getEigenstateHiliteColor();
                }
                else if ( superpositionCoefficients.getCoefficient( i ) != 0 ) {
                    lineStroke = BSConstants.EIGENSTATE_SELECTION_STROKE;
                    lineColor = _colorScheme.getEigenstateSelectionColor();
                }
                line.setStroke( lineStroke );
                line.setStrokePaint( lineColor );
                
                _eigenstatesParentNode.addChild( line );
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
                final double y = ( centerEnergy - energy ) / magDeltaEnergy; // Java's +y is down!
                
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

            double x = LENS_DIAMETER;
            double y = LENS_DIAMETER;
            double w = 1;
            double h = 1;
            if ( magMinEnergy < plotMinEnergy ) {
                // Bottom edge is visible
                x = -LENS_DIAMETER / 2;
                w = LENS_DIAMETER;
                h = ( Math.abs( magMinEnergy - plotMinEnergy ) / ( magMaxEnergy - magMinEnergy ) ) * LENS_DIAMETER;
                y = ( LENS_DIAMETER / 2 ) - h;
            }
            else if ( magMaxEnergy > plotMaxEnergy ) {
                // Top edge is visible
                x = -LENS_DIAMETER / 2;
                w = LENS_DIAMETER;
                h = ( Math.abs( magMaxEnergy - plotMaxEnergy ) / ( magMaxEnergy - magMinEnergy ) ) * LENS_DIAMETER;
                y = -LENS_DIAMETER / 2; 
            }
            
            Shape chartEdgeShape = new Rectangle2D.Double( x, y, w, h );
            _chartEdgeNode.setPathTo( chartEdgeShape );
        }
    }
    
    /*
     * Tells the EigenstateNode to select the eigenstate that is currently hilited.
     */
    private void selectEigenstate( Point2D mousePoint ) {
        _eigenstatesNode.selectEigenstate();
    }
    
    /*
     * Tells the EigenstateNode to hilite the eigenstate that is closest 
     * to the magnified energy at the mouse position.
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
        
        _eigenstatesNode.hiliteEigenstate( magnifiedEnergy );
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
            if ( !_dragging && isInLens( e.getPosition() ) ) {
                hiliteEigenstate( e.getPosition() );
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
        }
    }
    
    //----------------------------------------------------------------------------
    // 
    //----------------------------------------------------------------------------
    
    /**
     * Piccolo node for drawing paths inside the magnifying glass' lens.
     * The node is clipped to the lens' boundary.
     */
    private class ClippedPath extends PPath {
        
        public ClippedPath() {}
        
        /*
         * Clips the PPath to the lens.
         */
        protected void paint( PPaintContext paintContext ) {
            GeneralPath lensPath = _lensNode.getPathReference();
            Graphics2D g2 = paintContext.getGraphics();
            Shape oldClip = g2.getClip();
            g2.setClip( lensPath );
            super.paint( paintContext );
            g2.setClip( oldClip );
        }
    }
}
