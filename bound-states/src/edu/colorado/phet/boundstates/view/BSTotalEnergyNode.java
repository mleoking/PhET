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

import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSTotalEnergy;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


/**
 * BSTotalEnergyNode displays the total energy eigenstates as a set of horizontal lines.
 * Clicking on a line selects it.  Moving the cursor over the Energy chart highlights
 * the line that is closest to the mouse cursor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSTotalEnergyNode extends PNode implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSTotalEnergy _totalEnergy;
    private BSCombinedChartNode _chartNode;
    private PSwingCanvas _canvas;
    
    private ArrayList _lines; // array of PPath
    
    private int _selectionIndex;
    private int _highlightIndex;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param totalEnergy
     * @param chartNode
     * @param canvas
     */
    public BSTotalEnergyNode( BSTotalEnergy totalEnergy, BSCombinedChartNode chartNode, PSwingCanvas canvas ) {
        super();
        
        _totalEnergy = totalEnergy;
        _totalEnergy.addObserver( this );
        _chartNode = chartNode;
        _canvas = canvas;
        
        _lines = new ArrayList();
        _selectionIndex = 0;
        _highlightIndex = -1;
        
        PBasicInputEventHandler mouseHandler = new PBasicInputEventHandler() {
            public void mouseMoved( PInputEvent event ) {
                handleHighlight( event );
            }
            public void mouseExited( PInputEvent event ) {
                clearHightlight();
            }
            public void mousePressed( PInputEvent event ) {
                handleSelection();
            }
        };
        addInputEventListener( mouseHandler );

        updateDisplay();
    }

    /**
     * Call this method before releasing all references to an object of this type.
     */
    public void cleanup() {
        if ( _totalEnergy != null ) {
            _totalEnergy.deleteObserver( this );
            _totalEnergy = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the display with the model.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _totalEnergy ) {
            _selectionIndex = 0;
            updateDisplay();
        }
    }

    //----------------------------------------------------------------------------
    // Updater
    //----------------------------------------------------------------------------
    
    /**
     * Updates the display to account for changes in the model or the canvas size.
     */
    public void updateDisplay() {

        setBounds( _chartNode.getEnergyPlotBounds() );
        
        removeAllChildren();
        _lines.clear();

        BSEigenstate[] eigenstates = _totalEnergy.getEigenstates();
        for ( int i = 0; i < eigenstates.length; i++ ) {
            // Node the draws the eigenstate line...
            double minPosition = BSConstants.POSITION_RANGE.getLowerBound();
            double maxPosition = BSConstants.POSITION_RANGE.getUpperBound();
            double energy = eigenstates[i].getEnergy();
            Point2D pLeft = _chartNode.energyToNode( new Point2D.Double( minPosition, energy ) );
            Point2D pRight = _chartNode.energyToNode( new Point2D.Double( maxPosition, energy ) );
            Point2D[] points = new Point2D[] { pLeft, pRight };
            PPath line = new PPath();
            line.setPathToPolyline( points );
            line.setStroke( BSConstants.EIGENSTATE_UNSELECTED_STROKE );
            line.setStrokePaint( BSConstants.EIGENSTATE_UNSELECTED_COLOR );
            addChild( line );
            _lines.add( line );
        }
        
        selectEigenstate( _selectionIndex );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /*
     * Selects the eigenstate that the mouse cursor is closest to,
     * which also happens to be the eigenstate that is currently highlighted.
     * 
     * @param event
     */
    private void handleSelection() {
        selectEigenstate( _highlightIndex );
    }
    
    /*
     * Selects an eigenstate by index.
     * Any previous selection is deselected.
     * 
     * @param index
     */
    private void selectEigenstate( final int index ) {
        if ( index != -1 ) {
            if ( _selectionIndex != -1 ) {
                PPath line = (PPath) _lines.get( _selectionIndex );
                line.setStroke( BSConstants.EIGENSTATE_UNSELECTED_STROKE );
                line.setStrokePaint( BSConstants.EIGENSTATE_UNSELECTED_COLOR );
            }

            if ( index == _highlightIndex ) {
                _highlightIndex = -1;
            }

            _selectionIndex = index;
            PPath line = (PPath) _lines.get( _selectionIndex );
            line.setStroke( BSConstants.EIGENSTATE_SELECTED_STROKE );
            line.setStrokePaint( BSConstants.EIGENSTATE_SELECTED_COLOR );
        }
    }
    
    /*
     * Highlights the eigenstate that the mouse cursor is closest to.
     * Any previous highlight is unhighlighted.
     * The selected eigenstate is not highlightable.
     */
    private void handleHighlight( PInputEvent event ) {
        
        // Map the mouse position to an energy value...
        Point2D localPosition = event.getPositionRelativeTo( this );
        Point2D globalPosition = localToGlobal( localPosition );
        Point2D chartPosition = _chartNode.globalToLocal( globalPosition );
        Point2D energyPosition = _chartNode.nodeToEnergy( chartPosition );
        final double energy = energyPosition.getY();

        // Find the closest eigenstate...
        int highlightIndex = getClosestEigenstateIndex( energy );

        // Clear the previous highlight...
        clearHightlight();

        // Set the new highlight...
        if ( highlightIndex != _selectionIndex ) {
            _highlightIndex = highlightIndex;
            PPath line = (PPath) _lines.get( _highlightIndex );
            line.setStroke( BSConstants.EIGENSTATE_HIGHLIGHT_STROKE );
            line.setStrokePaint( BSConstants.EIGENSTATE_HIGHLIGHT_COLOR );

        }
    }
    
    /*
     * Gets the index of the eigenstate that is closest to a specified energy value.
     * 
     * @param energy
     */
    private int getClosestEigenstateIndex( double energy ) {
        BSEigenstate[] eigenstates = _totalEnergy.getEigenstates();
        int index = 0;
        if ( energy < eigenstates[0].getEnergy() ) {
            index = 0;
        }
        else if ( energy > eigenstates[ eigenstates.length - 1 ].getEnergy() ) {
            index = eigenstates.length - 1;
        }
        else {
            for ( int i = 1; i < eigenstates.length - 1; i++ ) {
                if ( energy <= eigenstates[i].getEnergy() ) {
                    double upperEnergyDifference = eigenstates[i].getEnergy() - energy;
                    double lowerEnergyDifference = energy - eigenstates[i-1].getEnergy();
                    if ( upperEnergyDifference < lowerEnergyDifference ) {
                        index = i;
                    }
                    else {
                        index = i - 1;
                    }
                    break;
                }
            }
        }
        return index;
    }
    
    /*
     * Clears the current highlight.
     */
    private void clearHightlight() {
        if ( _highlightIndex != -1 ) {
            PPath line = (PPath) _lines.get( _highlightIndex );
            line.setStroke( BSConstants.EIGENSTATE_UNSELECTED_STROKE );
            line.setStrokePaint( BSConstants.EIGENSTATE_UNSELECTED_COLOR );
            _highlightIndex = -1;
        }
    }
}
