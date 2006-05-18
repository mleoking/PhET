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

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
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
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;


public class BSMagnifyingGlass extends PNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int MAGNIFICATION = 10; // magnification ration, N:1
    
    private static final double LENS_DIAMETER = 100; // pixels
    private static final double BEZEL_WIDTH = 8; // pixels
    private static final double HANDLE_LENGTH = LENS_DIAMETER/2; // pixels
    private static final double HANDLE_WIDTH = HANDLE_LENGTH/4; // pixels
    private static final double HANDLE_ARC_SIZE = 10; // pixels
    private static final double HANDLE_ROTATION = -20; // degrees;
    
    private static final Color BEZEL_COLOR = Color.GRAY;
    private static final Color HANDLE_COLOR = Color.ORANGE;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSModel _model;
    private BSCombinedChartNode _chartNode;
    
    private PPath _lensPath;
    private PPath _bezelPath;
    private PPath _handlePath;
    private ArrayList _eigenstateLines; // array of PPath
    private ClippedPath _potentialPath;
    
    private BSColorScheme _colorScheme;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSMagnifyingGlass( BSCombinedChartNode chartNode, BSColorScheme colorScheme ) {
        
        _chartNode = chartNode;
        
        this.setPickable( false ); // only the handle is pickable
   
        final double glassRadius = LENS_DIAMETER/2;
        Shape glassShape = new Ellipse2D.Double( -glassRadius, -glassRadius, LENS_DIAMETER, LENS_DIAMETER ); // x,y,w,h
        
        _lensPath = new PPath();
        _lensPath.setPaint( Color.BLACK );
        _lensPath.setPathTo( glassShape );
        _lensPath.setPickable( true );
        //XXX for now, swallow all events that occur in the lens
        _lensPath.addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent e ) {
                    e.setHandled( true );
                }
            });
        
        _bezelPath = new PPath();
        _bezelPath.setPathTo( glassShape ); // same shape as glass, but we'll stroke it instead of filling it
        _bezelPath.setStroke( new BasicStroke( (float)BEZEL_WIDTH ) );
        _bezelPath.setStrokePaint( BEZEL_COLOR );
        _bezelPath.setPickable( false );
        
        _handlePath = new PPath();
        _handlePath.setPaint( HANDLE_COLOR );
        Shape handleShape = new RoundRectangle2D.Double( 
                -HANDLE_WIDTH/2, glassRadius, 
                HANDLE_WIDTH, HANDLE_LENGTH,
                HANDLE_ARC_SIZE, HANDLE_ARC_SIZE );
        _handlePath.setPathTo( handleShape );
//        _handlePath.rotate( Math.toRadians( HANDLE_ROTATION ) ); //XXX this rotates everything!
     
        _eigenstateLines = new ArrayList();
        
        _potentialPath = new ClippedPath();
        _potentialPath.setStroke( BSConstants.POTENTIAL_ENERGY_STROKE );
        _potentialPath.setPickable( false );
        
        setColorScheme( colorScheme );
        
        /*
         * The magnifying glass should only be dragged by the handle.
         * We make the glass & bezel children  of the handle so that
         * they move with the handle.
         */
        addChild( _handlePath );
        _handlePath.addChild( _bezelPath );
        _handlePath.addChild( _lensPath );
        
        // Interaction
        _handlePath.addInputEventListener( new CursorHandler() );
        _handlePath.addInputEventListener( new BSMagnifyingGlassDragHandler() );
        
        // Crosshairs...
        {
            BasicStroke stroke = new BasicStroke( 0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {5,5}, 0 );
            Color color = new Color( 255, 255, 255, 100 );  // transparent white
            
            PPath horizontalLine = new PPath();
            GeneralPath horizontalPath = new GeneralPath();
            horizontalPath.moveTo( (float)-LENS_DIAMETER/2, 0 );
            horizontalPath.lineTo( (float)+LENS_DIAMETER/2, 0 );
            horizontalLine.setPathTo( horizontalPath );
            horizontalLine.setStroke( stroke );
            horizontalLine.setStrokePaint( color );
            horizontalLine.setPickable( false );
            
            PPath verticalLine = new PPath();
            GeneralPath verticalPath = new GeneralPath();
            verticalPath.moveTo( 0, (float)-LENS_DIAMETER/2 );
            verticalPath.lineTo( 0, (float)+LENS_DIAMETER/2 );
            verticalLine.setPathTo( verticalPath );
            verticalLine.setStroke( stroke );
            verticalLine.setStrokePaint( color );
            verticalLine.setPickable( false );
            
            _handlePath.addChild( horizontalLine );
            _handlePath.addChild( verticalLine );
        }
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
        _lensPath.setPaint( _colorScheme.getChartColor() );
        _potentialPath.setStrokePaint( _colorScheme.getPotentialEnergyColor() );
        updateDisplay();
    }

    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    /**
     * Update the display when the magnifying glass is made visible.
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
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
            Point2D lensCenter = _lensPath.localToGlobal( new Point2D.Double( 0, 0 ) );
            Point2D chartCenter = _chartNode.globalToLocal( lensCenter );
            Point2D p1 = _chartNode.nodeToEnergy( chartCenter );
            centerPosition = p1.getX();
            centerEnergy = p1.getY();

            Point2D lensMin = _lensPath.localToGlobal( new Point2D.Double( -LENS_DIAMETER / 2, LENS_DIAMETER / 2 ) ); // +y is down
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
                _lensPath.addChild( line );
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
            _potentialPath.setPathTo( path );
            _lensPath.addChild( _potentialPath );
        }
    }
    
    /*
     * Removes all of the eigenstate lines.
     */
    private void clearEigenstateLines() {
        Iterator i = _eigenstateLines.iterator();
        while( i.hasNext() ) {
            PPath node = (PPath) i.next();
            _lensPath.removeChild( node );
        }
        _eigenstateLines.clear();
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /*
     * Handles dragging of the magnifying glass.
     */
    private class BSMagnifyingGlassDragHandler extends PBasicInputEventHandler {
        
        public BSMagnifyingGlassDragHandler() {}
        
        public void mousePressed( PInputEvent event ) {
            //XXX anything to do here?
        }
        
        public void mouseReleased( PInputEvent event ) {
            //XXX anything to do here?
        }
        
        /**
         * Updates the display as the mouse is dragged.
         */
        public void mouseDragged( PInputEvent event ) {
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
            GeneralPath path = _lensPath.getPathReference();
            Graphics2D g2 = paintContext.getGraphics();
            Shape oldClip = g2.getClip();
            g2.setClip(  path );
            super.paint( paintContext );
            g2.setClip( oldClip );
        }
    }
}
