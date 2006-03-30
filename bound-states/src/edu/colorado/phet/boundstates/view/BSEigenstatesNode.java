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
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.debug.DebugOutput;
import edu.colorado.phet.boundstates.model.BSAbstractPotential;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


/**
 * BSEigenstatesNode displays a potential's eigenstates as a set of horizontal lines.
 * Clicking on a line selects it.  Moving the cursor over the Energy chart highlights
 * the line that is closest to the mouse cursor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSEigenstatesNode extends PNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int INDEX_UNDEFINED = -1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSCombinedChartNode _chartNode;
    private BSAbstractPotential _potential;
    
    private ArrayList _eigenstates; // array of BSEigenstate, one for each displayed eigenstate
    private ArrayList _lines; // array of PPath, one for each entry in _eigenstates array
    
    private int _selectionIndex;
    private int _hiliteIndex;
    
    private Color _normalColor, _hiliteColor, _selectionColor;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param chartNode
     * @param canvas
     */
    public BSEigenstatesNode( BSCombinedChartNode chartNode, PSwingCanvas canvas ) {
        super();
        
        _chartNode = chartNode;
        _lines = new ArrayList();
        _eigenstates = new ArrayList();
        _selectionIndex = INDEX_UNDEFINED;
        _hiliteIndex = INDEX_UNDEFINED;
        
        _normalColor = BSConstants.COLOR_SCHEME.getEigenstateNormalColor();
        _hiliteColor = BSConstants.COLOR_SCHEME.getEigenstateHiliteColor();
        _selectionColor = BSConstants.COLOR_SCHEME.getEigenstateSelectionColor();
        
        PBasicInputEventHandler mouseHandler = new PBasicInputEventHandler() {
            public void mouseMoved( PInputEvent event ) {
                handleHighlight( event );
            }
            public void mouseExited( PInputEvent event ) {
                clearHilite();
            }
            public void mousePressed( PInputEvent event ) {
                handleSelection();
            }
        };
        addInputEventListener( mouseHandler );
    }

    /**
     * Call this method before releasing all references to an object of this type.
     */
    public void cleanup() {
        if ( _potential != null ) {
            _potential.deleteObserver( this );
            _potential = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSAbstractPotential potential ) {
        if ( _potential != null ) {
            _potential.deleteObserver( this );
        }
        _potential = potential;
        _potential.addObserver( this );
        updateDisplay();
    }
    
    public void setColorScheme( BSColorScheme colorScheme ) {
        _normalColor = colorScheme.getEigenstateNormalColor();
        _hiliteColor = colorScheme.getEigenstateHiliteColor();
        _selectionColor = colorScheme.getEigenstateSelectionColor();
        updateDisplay();
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
        if ( o == _potential ) {
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
        
        clearHilite();
        
        removeAllChildren();
        _eigenstates.clear();
        _lines.clear();
        
        // Determine the Energy chart's bounds...
        BSCombinedChart chart = _chartNode.getCombinedChart();
        final double minEnergy = chart.getEnergyPlot().getRangeAxis().getLowerBound();
        final double maxEnergy = chart.getEnergyPlot().getRangeAxis().getUpperBound();
        final double minPosition = chart.getEnergyPlot().getDomainAxis().getLowerBound();
        final double maxPosition = chart.getEnergyPlot().getDomainAxis().getUpperBound();
        
        // Create a line for each eigenstate...
        BSEigenstate[] eigenstates = _potential.getEigenstates();
//        DebugOutput.printEigenstates( eigenstates );//XXX
        for ( int i = 0; i < eigenstates.length; i++ ) {
            final double energy = eigenstates[i].getEnergy();
            Point2D pLeft = _chartNode.energyToNode( new Point2D.Double( minPosition, energy ) );
            Point2D pRight = _chartNode.energyToNode( new Point2D.Double( maxPosition, energy ) );
            Point2D[] points = new Point2D[] { pLeft, pRight };
            PPath line = new PPath();
            line.setPathToPolyline( points );
            line.setStroke( BSConstants.EIGENSTATE_NORMAL_STROKE );
            line.setStrokePaint( _normalColor );
            line.setVisible( energy >= minEnergy && energy <= maxEnergy );
            addChild( line );
            _lines.add( line );
            _eigenstates.add( eigenstates[i] );
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
        if ( _hiliteIndex != INDEX_UNDEFINED ) {
            selectEigenstate( _hiliteIndex );
        }
    }
    
    /*
     * Selects an eigenstate by index.
     * Any previous selection is deselected.
     * 
     * @param index
     */
    private void selectEigenstate( final int index ) {
//        System.out.println( "selectEigenstate " + index );//XXX
        
        clearSelection();
        if ( index == _hiliteIndex ) {
            clearHilite();
        }
        
        if ( index >= 0 && index < _lines.size() ) {
            _selectionIndex = index;
        }
        else {
            _selectionIndex = INDEX_UNDEFINED;
        }
        
        if ( _selectionIndex != INDEX_UNDEFINED ) {
            PPath line = (PPath) _lines.get( _selectionIndex );
            line.setStroke( BSConstants.EIGENSTATE_SELECTION_STROKE );
            line.setStrokePaint( _selectionColor );
        }
    }
    
    /*
     * Clears the current selection.
     */
    private void clearSelection() {
        if ( _selectionIndex != INDEX_UNDEFINED ) {
            if ( _lines.size() > _selectionIndex ) {
                PPath line = (PPath) _lines.get( _selectionIndex );
                line.setStroke( BSConstants.EIGENSTATE_NORMAL_STROKE );
                line.setStrokePaint( _normalColor );
            }
            _selectionIndex = INDEX_UNDEFINED;
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
        int hiliteIndex = getClosestEigenstateIndex( energy );
        hiliteEigenstate( hiliteIndex );
    }
    
    /**
     * Hilites a specified eigenstate.
     * The eigenstate is hilited only if it's not selected.
     * 
     * @param hiliteIndex
     */
    private void hiliteEigenstate( int hiliteIndex ) {

        // Clear the previous highlight...
        clearHilite();

        // Set the new highlight...
        if ( hiliteIndex != _selectionIndex ) {
            _hiliteIndex = hiliteIndex;
            PPath line = (PPath) _lines.get( _hiliteIndex );
            line.setStroke( BSConstants.EIGENSTATE_HILITE_STROKE );
            line.setStrokePaint( _hiliteColor );
        }
    }
    
    /*
     * Clears the current highlight.
     */
    private void clearHilite() {
        if ( _hiliteIndex != INDEX_UNDEFINED ) {
            PPath line = (PPath) _lines.get( _hiliteIndex );
            line.setStroke( BSConstants.EIGENSTATE_NORMAL_STROKE );
            line.setStrokePaint( _normalColor );
            _hiliteIndex = INDEX_UNDEFINED;
        }
    }
    
    /*
     * Gets the index of the eigenstate that is closest to a specified energy value.
     * 
     * @param energy
     */
    private int getClosestEigenstateIndex( final double energy ) {
        int index = 0;
        if ( energy < getDisplayedEnergy( 0 ) ) {
            index = 0;
        }
        else if ( energy > getDisplayedEnergy(  _eigenstates.size() - 1 ) ) {
            index = _eigenstates.size() - 1;
        }
        else {
            for ( int i = 1; i < _eigenstates.size(); i++ ) {
                final double currentEnergy = getDisplayedEnergy( i );
                if ( energy <= currentEnergy ) {
                    final double lowerEnergy = getDisplayedEnergy( i - 1 );
                    final double upperEnergyDifference = currentEnergy - energy;
                    final double lowerEnergyDifference = energy - lowerEnergy;
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
     * Gets the energy of one of the displayed eigenstate lines.
     * 
     * @param index
     */
    private double getDisplayedEnergy( int index ) {
        return ( (BSEigenstate) _eigenstates.get( index ) ).getEnergy();
    }
}
