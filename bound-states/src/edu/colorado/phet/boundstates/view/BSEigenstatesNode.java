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
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSSuperpositionCoefficients;
import edu.colorado.phet.piccolo.nodes.HTMLNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.nodes.PComposite;


/**
 * BSEigenstatesNode is an "overlay" on top of BSEnergyPlot.
 * It supports selection and hiliting of eigenstates.
 * Moving the cursor over the Energy chart highlights
 * the line that is closest to the mouse cursor.
 * Clicking on an eigenstate selects it.
 * <p>
 * This node does not draw the eigenstates; drawing is handled by BSEnergyPlot.
 * This node does draws the value of the hilited eigenstate, which is positioned
 * at the far left edge of the chart, just above the hilited eigenstate line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSEigenstatesNode extends PComposite implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------    
    
    private static final int MIN_DECIMAL_PLACES = 2; // min decimal places for displaying energy value
    private static final int MAX_DECIMAL_PLACES = 12; // max decimal places for displaying energy value
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSModel _model;
    private int _hiliteIndex; // index of the eigenstate that is displayed as hilited
    
    private PCanvas _canvas;
    private BSCombinedChartNode _chartNode;
    private HTMLNode _hiliteValueNode;
    
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
        _hiliteIndex = BSEigenstate.INDEX_UNDEFINED;
        
        _chartNode = chartNode;
        _canvas = canvas;
        
        String format = "0.";
        for ( int i = 0; i < MAX_DECIMAL_PLACES + 1; i++ ) {
            format += "#";
        }
        _differenceFormat = new DecimalFormat( format );
        
        _hiliteValueNode = new HTMLNode();
        _hiliteValueNode.setFont( BSConstants.HILITE_ENERGY_FONT );
        _hiliteValueNode.setVisible( false );
        _hiliteValueNode.setOffset( 100, 100 );
        addChild( _hiliteValueNode );
        
        // Handle mouse events
        PBasicInputEventHandler mouseHandler = new PBasicInputEventHandler() {
            public void mouseMoved( PInputEvent event ) {
                handleHighlight( event );
            }
            public void mouseExited( PInputEvent event ) {
                _model.setHilitedEigenstateIndex( BSEigenstate.INDEX_UNDEFINED );
            }
            public void mousePressed( PInputEvent event ) {
                handleSelection();
            }
        };
        addInputEventListener( mouseHandler );
        
        // Watch for changes to the Energy (y) axis range
        ValueAxis yAxis = _chartNode.getCombinedChart().getEnergyPlot().getRangeAxis();
        yAxis.addChangeListener( new AxisChangeListener() {
            public void axisChanged( AxisChangeEvent event ) {
                // Clear the hilite index
                _model.setHilitedEigenstateIndex( BSEigenstate.INDEX_UNDEFINED );
            }
        } );
        
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
    
    /**
     * Sets the model.
     * 
     * @param model
     */
    public void setModel( BSModel model ) {
        if ( model != _model ) {
            if ( _model != null ) {
                _model.deleteObserver( this );
            }
            _model = model;
            _model.addObserver( this );
            updateHiliteValueDisplay();
        }
    }
    
    /**
     * Sets the color scheme.
     * 
     * @param colorScheme
     */
    public void setColorScheme( BSColorScheme colorScheme ) {
        Color c = colorScheme.getChartColor();
        Color hiliteValueBackground = new Color( c.getRed(), c.getGreen(), c.getBlue(), BSConstants.HILITE_VALUE_BACKGROUND_ALPHA );
        _hiliteValueNode.setPaint( hiliteValueBackground );
        _hiliteValueNode.setHTMLColor( colorScheme.getEigenstateHiliteColor() );
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
            _model.setHilitedEigenstateIndex( BSEigenstate.INDEX_UNDEFINED );
            BSSuperpositionCoefficients superpositionCoefficients = _model.getSuperpositionCoefficients();
            final int numberOfCoefficients = superpositionCoefficients.getNumberOfCoefficients();
            superpositionCoefficients.setNotifyEnabled( false );  // we're going to change them all
            for ( int i = 0; i < numberOfCoefficients; i++ ) {
                superpositionCoefficients.setCoefficient( i, 0 );
            }
            superpositionCoefficients.setCoefficient( hiliteIndex, 1 );
            superpositionCoefficients.setNotifyEnabled( true ); // we're done changing, OK to notify
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
        int hiliteIndex = _model.getClosestEigenstateIndex( energy, BSConstants.HILITE_ENERGY_THRESHOLD );

        // Update the model...
        _model.setHilitedEigenstateIndex( hiliteIndex );
        
        // Set the mouse cursor...
        setCursor( hiliteIndex );
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
        if ( o == _model && arg == BSModel.PROPERTY_HILITED_EIGENSTATE_INDEX ) {
            updateHiliteValueDisplay();
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the display so that the the hilited eigenstate 
     * appears to be hilited.
     */
    private void updateHiliteValueDisplay() {

        _hiliteIndex = _model.getHilitedEigenstateIndex();
        if ( _hiliteIndex == BSEigenstate.INDEX_UNDEFINED ) {
            _hiliteValueNode.setVisible( false );
        }
        else {
            _hiliteValueNode.setVisible( true );
            
            // Set the displayed value...
            String text = createValueString( _hiliteIndex );
            _hiliteValueNode.setHTML( text );
            
            // Position the value just above the hilited eigenstate, at left edge of chart...
            final double minPosition = _chartNode.getCombinedChart().getPositionRange().getLowerBound();
            final double hiliteEnergy = _model.getEigenstate( _hiliteIndex ).getEnergy();
            Point2D p = _chartNode.energyToNode( new Point2D.Double( minPosition, hiliteEnergy ) );
            final double x = p.getX() + 2;
            final double y = p.getY() - _hiliteValueNode.getHeight() - 1;
            _hiliteValueNode.setOffset( x, y );
        }
    }
    
    //----------------------------------------------------------------------------
    // Utility methods
    //----------------------------------------------------------------------------
    
    /*
     * Sets the cursor based on the index of the hilite eigenstate.
     */
    private void setCursor( final int hiliteIndex ) {
        if ( _hiliteIndex == BSEigenstate.INDEX_UNDEFINED ) {
          _canvas.setCursor( BSConstants.DEFAULT_CURSOR );
        }
        else {
          _canvas.setCursor( BSConstants.HAND_CURSOR );
        }
    }
    
    /*
     * Creates an HTML string used to display the hilited eigenstate's energy value.
     * 
     * @param hiliteIndex
     */
    private String createValueString( int hiliteIndex ) {
        
        BSEigenstate[] eigenstates = _model.getEigenstates();
        double energy = eigenstates[hiliteIndex].getEnergy();
        int subscript = eigenstates[hiliteIndex].getSubscript();
        NumberFormat format = createValueFormat( hiliteIndex );
        String energyString = format.format( energy );
        
        return "<html> E<sub>" + subscript + "</sub> = " + energyString + "</html>";
    }
    
    /*
     * Creates a NumberFormat that will show the proper number of decimal places
     * to differentiate between adjacent eigenstate energy values.
     * 
     * @param hiliteIndex
     */
    private NumberFormat createValueFormat( int hiliteIndex ) {
        
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
