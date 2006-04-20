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
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSSuperpositionCoefficients;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
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
    
    private static final DecimalFormat HILITE_VALUE_FORMAT = new DecimalFormat( "0.000" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSModel _model;
    
    private BSCombinedChartNode _chartNode;
    private ArrayList _lines; // array of PPath, one for each entry in _eigenstates array
    private PText _hiliteValueNode;
    
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
      
        _normalColor = BSConstants.COLOR_SCHEME.getEigenstateNormalColor();
        _hiliteColor = BSConstants.COLOR_SCHEME.getEigenstateHiliteColor();
        _selectionColor = BSConstants.COLOR_SCHEME.getEigenstateSelectionColor();
        
        _model = null;
        
        _chartNode = chartNode;
        _lines = new ArrayList();
        _hiliteValueNode = new PText();
        _hiliteValueNode.setTextPaint( _hiliteColor );
        _hiliteValueNode.setFont( new Font( "Lucida Sans", Font.PLAIN, 12 ) );
        _hiliteValueNode.setVisible( false );
        _hiliteValueNode.setOffset( 100, 100 );
        
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
        if ( _model != null ) {
            _model.deleteObserver( this );
            _model = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setModel( BSModel model ) {
        if ( model != _model ) {
            if ( _model != null ) {
                _model.deleteObserver( this );
            }
            _model = model;
            _model.addObserver( this );
            updateDisplay();
        }
    }
    
    public void setColorScheme( BSColorScheme colorScheme ) {
        _normalColor = colorScheme.getEigenstateNormalColor();
        _hiliteColor = colorScheme.getEigenstateHiliteColor();
        _selectionColor = colorScheme.getEigenstateSelectionColor();
        _hiliteValueNode.setTextPaint( _hiliteColor );
        updateDisplay();
    }
    
    private boolean isInitalized() {
        return ( _model != null );
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
        if ( o == _model ) {
            if ( arg == BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS ) {
                selectEigenstates();
            }
            else if ( arg == BSModel.PROPERTY_POTENTIAL ) {
                updateDisplay();
            }
        }
    }

    //----------------------------------------------------------------------------
    // Updater
    //----------------------------------------------------------------------------
    
    /**
     * Updates the display to account for changes in the model or the canvas size.
     */
    public void updateDisplay() {

        if ( !isInitalized() ) {
            return;
        }
        
        setBounds( _chartNode.getEnergyPlotBounds() );
        
        clearHilite();
        
        removeAllChildren();
        addChild( _hiliteValueNode );
        
        _lines.clear();
        
        // Determine the Energy chart's bounds...
        BSCombinedChart chart = _chartNode.getCombinedChart();
        final double minEnergy = chart.getEnergyPlot().getRangeAxis().getLowerBound();
        final double maxEnergy = chart.getEnergyPlot().getRangeAxis().getUpperBound();
        final double minPosition = chart.getEnergyPlot().getDomainAxis().getLowerBound();
        final double maxPosition = chart.getEnergyPlot().getDomainAxis().getUpperBound();
        
        // Create a line for each eigenstate...
        BSEigenstate[] eigenstates = _model.getEigenstates();
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
        }

        selectEigenstates();
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
        final int hiliteIndex = _model.getHilitedEigenstateIndex();
        if ( hiliteIndex != BSEigenstate.INDEX_UNDEFINED ) {
            selectEigenstate( hiliteIndex );
        }
    }
    
    /*
     * Selects an eigenstate by index.
     * Any previous selection is deselected.
     * 
     * @param index
     */
    private void selectEigenstate( final int index ) {
        BSSuperpositionCoefficients superpositionCoefficients = _model.getSuperpositionCoefficients();
        final int numberOfCoefficients = superpositionCoefficients.getNumberOfCoefficients();
        superpositionCoefficients.setNotifyEnabled( false );  // we're going to change them all 
        for ( int i = 0; i < numberOfCoefficients; i++ ) {
            superpositionCoefficients.setCoefficient( i, 0 );
        }
        superpositionCoefficients.setCoefficient( index, 1 );
        superpositionCoefficients.setNotifyEnabled( true ); // we're done changing, OK to notify
    }
    
    /*
     * Selects eignestate lines based on superposition coefficient values.
     * Each line with a non-zero coefficient is selected.
     */
    private void selectEigenstates() {
        
        BSSuperpositionCoefficients superpositionCoefficients = _model.getSuperpositionCoefficients();
        final int numberOfCoefficients = superpositionCoefficients.getNumberOfCoefficients();
        for ( int i = 0; i < numberOfCoefficients; i++ ) {
            final double value = superpositionCoefficients.getCoefficient( i );
            PPath line = (PPath) _lines.get( i );
            if ( value == 0 ) {
                line.setStroke( BSConstants.EIGENSTATE_NORMAL_STROKE );
                line.setStrokePaint( _normalColor );
            }
            else {
                line.setStroke( BSConstants.EIGENSTATE_SELECTION_STROKE );
                line.setStrokePaint( _selectionColor );
            }
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

        if ( hiliteIndex != BSEigenstate.INDEX_UNDEFINED ) {
            
            // Set the new highlight...
            PPath line = (PPath) _lines.get( hiliteIndex );
            line.setStroke( BSConstants.EIGENSTATE_HILITE_STROKE );
            line.setStrokePaint( _hiliteColor );

            // Update the model...
            _model.setHilitedEigenstateIndex( hiliteIndex );

            // Show the eigenstate's value...
            BSEigenstate[] eigenstates = _model.getEigenstates();
            double energy = eigenstates[hiliteIndex].getEnergy();
            String text = HILITE_VALUE_FORMAT.format( energy );
            _hiliteValueNode.setText( text );
            _hiliteValueNode.setVisible( true );

            // Position the value just above the hilited line...
            Rectangle2D bounds = line.getFullBounds();
            _hiliteValueNode.setOffset( bounds.getX() + 5, bounds.getY() - _hiliteValueNode.getHeight() - 1 );
        }
    }
    
    /*
     * Clears the current highlight.
     */
    private void clearHilite() {
        final int hiliteIndex = _model.getHilitedEigenstateIndex();
        if ( hiliteIndex != BSEigenstate.INDEX_UNDEFINED ) {
            BSSuperpositionCoefficients superpositionCoefficients = _model.getSuperpositionCoefficients();
            final double value = superpositionCoefficients.getCoefficient( _model.getHilitedEigenstateIndex() );
            PPath line = (PPath) _lines.get( hiliteIndex );
            if ( value == 0 ) {
                line.setStroke( BSConstants.EIGENSTATE_NORMAL_STROKE );
                line.setStrokePaint( _normalColor );
            }
            else {
                line.setStroke( BSConstants.EIGENSTATE_SELECTION_STROKE );
                line.setStrokePaint( _selectionColor );
            }
            _hiliteValueNode.setVisible( false );
            _model.setHilitedEigenstateIndex( BSEigenstate.INDEX_UNDEFINED );
        }
    }
    
    /*
     * Gets the index of the eigenstate that is closest to a specified energy value.
     * 
     * @param energy
     * @return index, possibly INDEX_UNDEFINED
     */
    private int getClosestEigenstateIndex( final double energy ) {
        BSEigenstate[] eigenstates = _model.getEigenstates();
        int index = 0;
        if ( eigenstates == null || eigenstates.length == 0 ) {
            index = BSEigenstate.INDEX_UNDEFINED;
        }
        else if ( energy < eigenstates[0].getEnergy() ) {
            index = 0;
        }
        else if ( energy > eigenstates[eigenstates.length - 1 ].getEnergy() ) {
            index = eigenstates.length - 1;
        }
        else {
            for ( int i = 1; i < eigenstates.length; i++ ) {
                final double currentEnergy = eigenstates[ i ].getEnergy();
                if ( energy <= currentEnergy ) {
                    final double lowerEnergy = eigenstates[ i - 1 ].getEnergy();
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
}
