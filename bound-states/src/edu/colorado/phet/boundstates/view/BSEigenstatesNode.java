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
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSSuperpositionCoefficients;
import edu.colorado.phet.piccolo.nodes.HTMLNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;


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
    
    // Energy must be at least this close to eigenstate to be hilited.
    private static final double HILITE_ENERGY_THRESHOLD = BSConstants.ENERGY_RANGE.getLength() / 20;
    
    private static final int MIN_DECIMAL_PLACES = 2; // min decimal places shown for energy value
    private static final int MAX_DECIMAL_PLACES = 12; // max decimal places shown for energy value
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSModel _model;
    
    private PCanvas _canvas;
    private BSCombinedChartNode _chartNode;
    private ArrayList _lines; // array of PPath, one for each entry in _eigenstates array
    private HTMLNode _hiliteValueNode;
    
    private Color _normalColor, _hiliteColor, _selectionColor;
    
    // Used to determine the number of decimal places in display of the hilited energy value.
    private NumberFormat _differenceFormat;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param chartNode
     * @param canvas
     */
    public BSEigenstatesNode( BSCombinedChartNode chartNode, PCanvas canvas ) {
        super();
      
        _model = null;
        
        _chartNode = chartNode;
        _canvas = canvas;
        
        String format = "0.";
        for ( int i = 0; i < MAX_DECIMAL_PLACES + 1; i++ ) {
            format += "#";
        }
        _differenceFormat = new DecimalFormat( format );
        
        _lines = new ArrayList();
        _hiliteValueNode = new HTMLNode();
        _hiliteValueNode.setFont( BSConstants.HILITE_ENERGY_FONT );
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
        
        setColorScheme( BSConstants.COLOR_SCHEME );
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
        
        _hiliteValueNode.setPaint( colorScheme.getChartColor() );
        _hiliteValueNode.setHTMLColor( _hiliteColor );
        
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

        addChild( _hiliteValueNode ); // add last, so it's on top
        
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
            line.moveToFront();
        }
        _hiliteValueNode.moveToFront();
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
        if ( hiliteIndex != _model.getHilitedEigenstateIndex() ) {
            hiliteEigenstate( hiliteIndex );
        }
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
            line.moveToFront();

            // Update the model...
            _model.setHilitedEigenstateIndex( hiliteIndex );

            // Show the eigenstate's value...
            String text = createValueString( hiliteIndex );
            _hiliteValueNode.setHTML( text );
            _hiliteValueNode.setVisible( true );

            // Position the value just above the hilited line...
            Rectangle2D bounds = line.getFullBounds();
            _hiliteValueNode.setOffset( bounds.getX() + 2, bounds.getY() - _hiliteValueNode.getHeight() - 1 );
            _hiliteValueNode.moveToFront();
            
            // Set the cursor to a hand...
            _canvas.setCursor( BSConstants.HAND_CURSOR );
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
                line.moveToBack();
            }
            else {
                line.setStroke( BSConstants.EIGENSTATE_SELECTION_STROKE );
                line.setStrokePaint( _selectionColor );
                line.moveToFront();
            }
            _hiliteValueNode.setVisible( false );
            _model.setHilitedEigenstateIndex( BSEigenstate.INDEX_UNDEFINED );
            _canvas.setCursor( BSConstants.DEFAULT_CURSOR );
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
        int index = BSEigenstate.INDEX_UNDEFINED;
        if ( eigenstates != null && eigenstates.length > 0 ) {
            if ( energy < eigenstates[0].getEnergy() ) {
                if ( energy + HILITE_ENERGY_THRESHOLD >= eigenstates[0].getEnergy() ) {
                    index = 0;
                }
            }
            else if ( energy > eigenstates[eigenstates.length - 1].getEnergy() ) {
                if ( energy - HILITE_ENERGY_THRESHOLD <= eigenstates[eigenstates.length - 1].getEnergy() ) {
                    index = eigenstates.length - 1;
                }
            }
            else {
                for ( int i = 1; i < eigenstates.length; i++ ) {
                    final double currentEnergy = eigenstates[i].getEnergy();
                    if ( energy <= currentEnergy ) {
                        final double lowerEnergy = eigenstates[i - 1].getEnergy();
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
        }
        return index;
    }
    
    /*
     * Creates an HTML string used to display the hilited eigenstate's energy value.
     * 
     * @param hiliteIndex
     */
    private String createValueString( int hiliteIndex ) {
        
        int startIndex = _model.getPotential().getStartingIndex();
        int indexE = startIndex + hiliteIndex;
        
        BSEigenstate[] eigenstates = _model.getEigenstates();
        double energy = eigenstates[hiliteIndex].getEnergy();
        NumberFormat format = createFormat( hiliteIndex );
        String energyString = format.format( energy );
        
        return "<html> E<sub>" + indexE + "</sub> = " + energyString + "</html>";
    }
    
    /*
     * Creates a NumberFormat that will show the proper number of decimal places
     * to differentiate between adjacent eigenstate energy values.
     * 
     * @param hiliteIndex
     */
    private NumberFormat createFormat( int hiliteIndex ) {
        
        // Determine the smallest difference between adjacent eigenstate energies...
        BSEigenstate[] eigenstates = _model.getEigenstates();
        double energy = eigenstates[hiliteIndex].getEnergy();
        double difference = 1;
        if ( hiliteIndex > 0 ) {
            difference = Math.abs( energy - eigenstates[hiliteIndex-1].getEnergy() );
        }
        if ( hiliteIndex < eigenstates.length - 1 ) {
            double d2 = Math.abs( energy - eigenstates[hiliteIndex+1].getEnergy() );
            if ( d2 < difference ) {
                difference = d2;
            }
        }
        
        // Determine the number of significant decimal places needed to show the difference...
        String differenceString = _differenceFormat.format( difference );
        System.out.println( 
                "hiliteIndex=" + hiliteIndex + 
                " energy=" + energy + 
                " diff=" + difference +
                " diffString=" + differenceString );
        int significantDecimalPlaces = 0;
        if ( differenceString.charAt( 0 ) != '0' ) {
            significantDecimalPlaces = MIN_DECIMAL_PLACES;
        }
        else {
            int decimalPointIndex = differenceString.indexOf( '.' );
            for ( int i = decimalPointIndex + 1; i < differenceString.length(); i++ ) {
                significantDecimalPlaces++;
                char c = differenceString.charAt( i );
                if ( c != '0' ) {
                    break;
                }
            }
            if ( significantDecimalPlaces < MIN_DECIMAL_PLACES ) {
                significantDecimalPlaces = MIN_DECIMAL_PLACES;
            }
            else if ( significantDecimalPlaces > MAX_DECIMAL_PLACES ) {
                significantDecimalPlaces = MAX_DECIMAL_PLACES;
            }
        }
        
        // Construct a formatter...
        String formatString = "0.";
        for ( int i = 0; i < significantDecimalPlaces; i++ ) {
            formatString += "0";
        }
        return new DecimalFormat( formatString );
    }
}
