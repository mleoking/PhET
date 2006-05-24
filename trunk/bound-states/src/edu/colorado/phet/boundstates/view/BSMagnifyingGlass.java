/* Copyright 2005, University of Colorado */

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.model.BSAbstractPotential;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSSuperpositionCoefficients;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * BSMagnifyingGlass is the magnifying glass that provides a magnified view
 * of eigenstates and potential energy, as shown on the Energy chart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSMagnifyingGlass extends PComposite implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int MAGNIFICATION = 10; // magnification ration, N:1
    
    private static final double LENS_DIAMETER = 100; // pixels
    private static final double BEZEL_WIDTH = 12; // pixels
    private static final double HANDLE_LENGTH = 65; // pixels
    private static final double HANDLE_WIDTH = HANDLE_LENGTH/4; // pixels
    private static final double HANDLE_ARC_SIZE = 10; // pixels
    private static final double HANDLE_ROTATION = -20; // degrees;    

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSModel _model;
    private BSCombinedChartNode _chartNode;
    private BSEigenstatesNode _eigenstatesNode;
    
    private PPath _lensNode;
    private PPath _bezelNode;
    private PPath _handleNode;
    private ClippedPath _potentialNode;
    
    private ArrayList _eigenstateLines; // array of PPath
    
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
        _chartNode = chartNode;
        _eigenstatesNode = eigenstatesNode;
        _eigenstateLines = new ArrayList();
        initNodes();
        initEventHandling();
        setColorScheme( colorScheme );
    }
    
    /*
     * Initializes the Piccolo nodes that make up the magnifying glass.
     */
    private void initNodes() {
        
        // Lens
        final double glassRadius = LENS_DIAMETER / 2;
        {
            Shape glassShape = new Ellipse2D.Double( -glassRadius, -glassRadius, LENS_DIAMETER, LENS_DIAMETER ); // x,y,w,h
            _lensNode = new PPath();
            _lensNode.setPathTo( glassShape );
            _lensNode.setPaint( Color.BLACK );
        }
        
        // Bezel 
        {
            final double bezelDiameter = ( LENS_DIAMETER + BEZEL_WIDTH );
            Shape glassShape = new Ellipse2D.Double( -bezelDiameter/2, -bezelDiameter/2, bezelDiameter, bezelDiameter ); // x,y,w,h
            _bezelNode = new PPath();
            _bezelNode.setPathTo( glassShape ); // same shape as glass, but we'll stroke it instead of filling it
        }
        
        // Handle
        {
            Shape handleShape = new RoundRectangle2D.Double( -HANDLE_WIDTH / 2, glassRadius, HANDLE_WIDTH, HANDLE_LENGTH, HANDLE_ARC_SIZE, HANDLE_ARC_SIZE );
            _handleNode = new PPath();
            _handleNode.setPathTo( handleShape );
            _handleNode.rotate( Math.toRadians( HANDLE_ROTATION ) );
        }
        
        // Potential plot
        {
            _potentialNode = new ClippedPath();
            _potentialNode.setStroke( BSConstants.POTENTIAL_ENERGY_STROKE );
        }
        
        addChild( _handleNode );
        addChild( _bezelNode );
        addChild( _lensNode );
    }
    
    /*
     * Initializes event handling.
     */
    private void initEventHandling() {
        
        addInputEventListener( new CursorHandler() );
        
        _eventHandler = new MagnifyingGlassEventHandler();
//XXX uncomment when MagnifyingGlassEventHandler extends ConstrainedDragHandler
//        _eventHandler.setTreatAsPointEnabled( true );
//        final double bezelRadius = ( LENS_DIAMETER + BEZEL_WIDTH ) / 2;
//        _eventHandler.setNodeCenter( bezelRadius, bezelRadius );
        addInputEventListener( _eventHandler );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
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
        _lensNode.setPaint( _colorScheme.getChartColor() );
        _bezelNode.setPaint( _colorScheme.getMagnifyingGlassBezelColor() );
        _handleNode.setPaint( _colorScheme.getMagnifyingGlassHandleColor() );
        _potentialNode.setStrokePaint( _colorScheme.getPotentialEnergyColor() );
        updateDisplay();
    }

    /**
     * Sets the drag bounds.
     * 
     * @param dragBounds
     */
    public void setDragBounds( Rectangle2D dragBounds ) {
//XXX uncomment when MagnifyingGlassEventHandler extends ConstrainedDragHandler
//        _eventHandler.setDragBounds( dragBounds );
        updateDisplay();
    }
    
    /*
     * Is the specified mouse point inside the lens?
     * 
     * @param point a mouse point, in global coordinates
     */
    private boolean isInLens( Point2D point ) {
        Rectangle2D lensBounds = localToGlobal( _lensNode.getFullBounds() );
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
        
        clearEigenstateLines();
        
        if ( !isInitialized() || !getVisible() ) {
            return;
        }
        
        // range of position & energy visible through the lens
        double centerPosition = 0;
        double minPosition = 0;
        double maxPosition = 0;
        double centerEnergy = 0;
        double minEnergy = 0;
        double maxEnergy = 0;
        {
            Point2D lensCenter = getLensCenter();
            centerPosition = lensCenter.getX();
            centerEnergy = lensCenter.getY();

            Point2D lensMin = _lensNode.localToGlobal( new Point2D.Double( -LENS_DIAMETER / 2, LENS_DIAMETER / 2 ) ); // +y is down
            Point2D chartMin = _chartNode.globalToLocal( lensMin );
            Point2D p2 = _chartNode.nodeToEnergy( chartMin );
            minPosition = p2.getX();
            minEnergy = p2.getY();
            
            maxPosition = centerPosition + Math.abs( centerPosition - minPosition );
            maxEnergy = centerEnergy + Math.abs( centerEnergy - minEnergy );
        }
//        System.out.println( "lens position: center=" + centerPosition + " min=" + minPosition + " max=" + maxPosition );//XXX
//        System.out.println( "lens energy: center=" + centerEnergy + " min=" + minEnergy + " max=" + maxEnergy );//XXX
        
        // adjust lens range by magnification factor
        minPosition = centerPosition - ( Math.abs( centerPosition - minPosition ) / MAGNIFICATION );
        maxPosition = centerPosition + ( Math.abs( centerPosition - maxPosition ) / MAGNIFICATION );
        minEnergy = centerEnergy - ( Math.abs( centerEnergy - minEnergy ) / MAGNIFICATION );
        maxEnergy = centerEnergy + ( Math.abs( centerEnergy - maxEnergy ) / MAGNIFICATION );
        
        // change in per pixel in the lens
        final double deltaPosition = ( maxPosition - minPosition ) / LENS_DIAMETER;
        final double deltaEnergy = ( maxEnergy - minEnergy ) / LENS_DIAMETER;
       
        // Draw the eigenstates that are visible through the lens
        BSEigenstate[] eigenstate = _model.getEigenstates();
        BSSuperpositionCoefficients superpositionCoefficients = _model.getSuperpositionCoefficients();
        for ( int i = 0; i < eigenstate.length; i++ ) {
            
            double eigenEnergy = eigenstate[i].getEnergy();
            if ( eigenEnergy >= minEnergy && eigenEnergy <= maxEnergy ) {
                
                final double y = ( centerEnergy - eigenEnergy ) / deltaEnergy; // Java's +y is down!
                
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
                
                _eigenstateLines.add( line );
                _lensNode.addChild( line );
            }
        }
        
        // Draw the potential energy visible through the lens
        {
            BSAbstractPotential potential = _model.getPotential();
            GeneralPath path = new GeneralPath();
            double position = minPosition;
            while ( position <= maxPosition ) {
                double energy = potential.getEnergyAt( position );
                
                final double x = ( position - centerPosition ) / deltaPosition;
                final double y = ( centerEnergy - energy ) / deltaEnergy; // Java's +y is down!
                
                if ( position == minPosition ) {
                    path.moveTo( (float) x, (float) y );
                }
                else {
                    path.lineTo( (float) x, (float) y );
                }
                
                position += deltaPosition;
            }
            _potentialNode.setPathTo( path );
            _lensNode.addChild( _potentialNode );
        }
    }
    
    /*
     * Removes all of the eigenstate lines.
     */
    private void clearEigenstateLines() {
        Iterator i = _eigenstateLines.iterator();
        while( i.hasNext() ) {
            PPath node = (PPath) i.next();
            _lensNode.removeChild( node );
        }
        _eigenstateLines.clear();
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
        double magnifiedEnergy = centerEnergy + ( ( mouseEnergy - centerEnergy ) / MAGNIFICATION );
        
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
    
//XXX MagnifyingGlassEventHandler should extend ConstrainedDragHandler
    /**
     * Handles events for the magnifying glass.
     */
    private class MagnifyingGlassEventHandler extends PDragEventHandler {
        
        private boolean _dragging; // true while a drag is in progress
        
        public MagnifyingGlassEventHandler() {
            _dragging = false;
        }
        
        /**
         * If the mouse moves inside the lens, hilite an eigenstate.
         */
        public void mouseMoved( PInputEvent e ) {
            if ( isInLens( e.getPosition() ) ) {
                hiliteEigenstate( e.getPosition() );
            }
        }
        
        /**
         * If the mouse is released without dragging in the lens, select an eigenstate.
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
         * If the magnifying glass is dragged, update what appears in the lens.
         */
        public void mouseDragged( PInputEvent e ) {
            _dragging = true;
            super.mouseDragged( e );
            updateDisplay();
        }
    }
    
    //----------------------------------------------------------------------------
    // 
    //----------------------------------------------------------------------------
    
    /**
     * Piccolo node for drawing paths inside the magnifying glass' lens.
     * Clips the line to the lens' boundary.
     */
    private class ClippedPath extends PPath {
        
        public ClippedPath() {}
        
        /*
         * Clips the PPath to the lens.
         */
        protected void paint( PPaintContext paintContext ) {
            GeneralPath path = _lensNode.getPathReference();
            Graphics2D g2 = paintContext.getGraphics();
            Shape oldClip = g2.getClip();
            g2.setClip(  path );
            super.paint( paintContext );
            g2.setClip( oldClip );
        }
    }
}
