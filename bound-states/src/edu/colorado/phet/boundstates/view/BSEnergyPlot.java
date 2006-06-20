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

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.model.BSAbstractPotential;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSSuperpositionCoefficients;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSEnergyPlot is the plot that displays potential energy.
 * (Total energy is drawn as an overlay by BSEigenstatesNode.)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSEnergyPlot extends XYPlot implements Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model references
    private BSModel _model;
    
    // View
    private XYSeries _potentialSeries;
    private XYSeries _normalEigenstateSeries;
    private XYSeries _selectedEigenstateSeries;
    private XYSeries _hilitedEigenstateSeries;
    private int _potentialIndex;
    private int _normalEigenstateIndex;
    private int _selectedEigenstateIndex;
    private int _hilitedEigenstateIndex;
    private double _dx;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSEnergyPlot() {
        super();
        
        // Labels (localized)
        String energyLabel = SimStrings.get( "axis.energy" ) + " (" + SimStrings.get( "units.energy" ) + ")";
        String potentialEnergyLabel = SimStrings.get( "legend.potentialEnergy" );
        
        int dataSetIndex = 0;
        
        // "Hilited" eigenstates series
        _hilitedEigenstateSeries = new XYSeries( "hilited eigenstates", false /* autoSort */ );
        {
            _hilitedEigenstateIndex = dataSetIndex++;
            // Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _hilitedEigenstateSeries );
            setDataset( _hilitedEigenstateIndex, dataset );
            // Renderer
            XYItemRenderer renderer = BSRendererFactory.createEigenstatesRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getEigenstateHiliteColor() );
            renderer.setStroke( BSConstants.EIGENSTATE_HILITE_STROKE );
            setRenderer( _hilitedEigenstateIndex, renderer );
        }
        
        // "Selected" eigenstates series
        _selectedEigenstateSeries = new XYSeries( "selected eigenstates", false /* autoSort */ );
        {
            _selectedEigenstateIndex = dataSetIndex++;
            // Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _selectedEigenstateSeries );
            setDataset( _selectedEigenstateIndex, dataset );
            // Renderer
            XYItemRenderer renderer = BSRendererFactory.createEigenstatesRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getEigenstateSelectionColor() );
            renderer.setStroke( BSConstants.EIGENSTATE_SELECTION_STROKE );
            setRenderer( _selectedEigenstateIndex, renderer );
        }
        
        // "Normal" eigenstates series
        _normalEigenstateSeries = new XYSeries( "normal eigenstates", false /* autoSort */ );
        {
            _normalEigenstateIndex = dataSetIndex++;
            // Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _normalEigenstateSeries );
            setDataset( _normalEigenstateIndex, dataset );
            // Renderer
            XYItemRenderer renderer = BSRendererFactory.createEigenstatesRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getEigenstateNormalColor() );
            renderer.setStroke( BSConstants.EIGENSTATE_NORMAL_STROKE );
            setRenderer( _normalEigenstateIndex, renderer );
        }
        
        // Potential series
        _potentialSeries = new XYSeries( potentialEnergyLabel, false /* autoSort */ );
        {
            _potentialIndex = dataSetIndex++;
            // Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _potentialSeries );
            setDataset( _potentialIndex, dataset );
            // Renderer
            XYItemRenderer renderer = BSRendererFactory.createCurveRenderer();
            renderer.setPaint( BSConstants.COLOR_SCHEME.getPotentialEnergyColor() );
            renderer.setStroke( BSConstants.POTENTIAL_ENERGY_STROKE );
            setRenderer( _potentialIndex, renderer );
        }

        // X axis 
        BSPositionAxis xAxis = new BSPositionAxis();
        
        // Y axis
        NumberAxis yAxis = new NumberAxis( energyLabel );
        {
            yAxis.setRange( 1, 10 ); // this will be adjusted based on the potential type
            yAxis.setLabelFont( BSConstants.AXIS_LABEL_FONT );
            yAxis.setTickLabelFont( BSConstants.AXIS_TICK_LABEL_FONT );
            yAxis.setTickLabelPaint( BSConstants.COLOR_SCHEME.getTickColor() );
            yAxis.setTickMarkPaint( BSConstants.COLOR_SCHEME.getTickColor() );
            yAxis.setAutoTickUnitSelection( true );
            
            // WORKAROUND: y-axis label gets clipped when range is changed
            yAxis.setLabelInsets( new RectangleInsets( 0,0,10,0 ) );
        }

        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( BSConstants.COLOR_SCHEME.getChartColor() );
        setDomainGridlinesVisible( BSConstants.SHOW_VERTICAL_GRIDLINES );
        setRangeGridlinesVisible( BSConstants.SHOW_HORIZONTAL_GRIDLINES );
        setDomainGridlinePaint( BSConstants.COLOR_SCHEME.getGridlineColor() );
        setRangeGridlinePaint( BSConstants.COLOR_SCHEME.getGridlineColor() );
        setDomainAxis( xAxis );
        setRangeAxis( yAxis );
        
        _dx = 0.1;
    }
    
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
     * Sets the model that this plot observes.
     * Changes in the model cause the plot to be updated.
     * 
     * @param model
     */
    public void setModel( BSModel model ) {
        if ( _model != model ) {
            if ( _model != null ) {
                _model.deleteObserver( this );
            }
            _model = model;
            _model.addObserver( this );
            updateDatasets();
        }
    }
    
    /**
     * Sets the color scheme for the plot.
     * 
     * @param scheme
     */
    public void setColorScheme( BSColorScheme scheme ) {
        // Background
        setBackgroundPaint( scheme.getChartColor() );
        // Ticks
        getDomainAxis().setTickLabelPaint( scheme.getTickColor() );
        getDomainAxis().setTickMarkPaint( scheme.getTickColor() );
        getRangeAxis().setTickLabelPaint( scheme.getTickColor() );
        getRangeAxis().setTickMarkPaint( scheme.getTickColor() );
        // Gridlines
        setDomainGridlinePaint( scheme.getGridlineColor() );
        setRangeGridlinePaint( scheme.getGridlineColor() );
        // Series
        getRenderer( _potentialIndex ).setPaint( scheme.getPotentialEnergyColor() );
        getRenderer( _hilitedEigenstateIndex ).setPaint( scheme.getEigenstateHiliteColor() );
        getRenderer( _selectedEigenstateIndex ).setPaint( scheme.getEigenstateSelectionColor() );
        getRenderer( _normalEigenstateIndex ).setPaint( scheme.getEigenstateNormalColor() );
    }
    
    /**
     * Sets the change in position between sample points used to draw the potential.
     * 
     * @param dx
     */
    public void setDx( double dx ) {
        if ( dx != _dx ) {
            _dx = dx;
            updateDatasets();
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     * 
     * @param observable
     * @param arg
     */
    public void update( Observable observable, Object arg ) {
        if ( observable == _model ) {
            if ( arg == BSModel.PROPERTY_POTENTIAL ) {
                updateDatasets();
            }
            else if ( arg == BSModel.PROPERTY_SUPERPOSITION_COEFFICIENTS ) {
                updateSelectionEigenstatesDataset();
            }
            else if ( arg == BSModel.PROPERTY_HILITED_EIGENSTATE_INDEX ) {
                updateHiliteEigenstatesDataset();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates all datasets.
     */
    private void updateDatasets() {
        updatePotentialDataset();
        updateNormalEigenstatesDataset();
        updateSelectionEigenstatesDataset();
        updateHiliteEigenstatesDataset();
    }
    
    /*
     * Updates the potential dataset to match the model.
     */
    private void updatePotentialDataset() {
        _potentialSeries.setNotify( false );
        _potentialSeries.clear();
        final double minX = getDomainAxis().getLowerBound();
        final double maxX = getDomainAxis().getUpperBound();
        final double minY = getRangeAxis().getLowerBound();
        final double maxY = getRangeAxis().getUpperBound();
        BSAbstractPotential potential = _model.getPotential();
        Point2D[] points = potential.getPotentialPoints( minX, maxX, _dx );
        for ( int i = 0; i < points.length; i++ ) {
            /* 
             * WORKAROUND:
             * JFreeChart handles Double.NaN ok, but infinite or very large values
             * in the dataset will cause a sun.dc.pr.PRException with the message 
             * "endPath: bad path" on Windows.  So we constrain the y coordinates
             * to the visible range of the chart.
             */
            final double x = points[i].getX();
            double y = points[i].getY();
            if ( y > maxY ) { 
                y = maxY + 1;
            }
            else if ( y < minY ) {
                y = minY - 1;
            }
            _potentialSeries.add( x, y );
        }
        _potentialSeries.setNotify( true );
    }
    
    /*
     * Updates the "normal" eigenstates dataset to match the model.
     * All eigenstate energies are added to the dataset.
     */
    private void updateNormalEigenstatesDataset() {
        _normalEigenstateSeries.setNotify( false );
        _normalEigenstateSeries.clear();
        BSEigenstate[] eigenstates = _model.getEigenstates();
        for ( int i = 0; i < eigenstates.length; i++ ) {
            _normalEigenstateSeries.add( 0 /* don't care */, eigenstates[i].getEnergy() );
        }
        _normalEigenstateSeries.setNotify( true );
    }
    
    /*
     * Updates the "selected" eigenstates dataset to match the model.
     * Those eigenstates energies with non-zero superposition coefficients
     * are added to the dataset.
     */
    private void updateSelectionEigenstatesDataset() {
        _selectedEigenstateSeries.setNotify( false );
        _selectedEigenstateSeries.clear();
        BSEigenstate[] eigenstates = _model.getEigenstates();
        BSSuperpositionCoefficients superpositionCoefficients = _model.getSuperpositionCoefficients();
        for ( int i = 0; i < eigenstates.length; i++ ) {
            if ( superpositionCoefficients.getCoefficient( i ) != 0 ) {
                _selectedEigenstateSeries.add( 0 /* don't care */, eigenstates[i].getEnergy() );
            }
        }
        _selectedEigenstateSeries.setNotify( true );
    }
    
    /*
     * Updates the "hilite" eigenstates dataset to match the model.
     * Our model supports only one hilited eigenstate.
     */
    private void updateHiliteEigenstatesDataset() {
        _hilitedEigenstateSeries.setNotify( false );
        _hilitedEigenstateSeries.clear();
        final int hiliteIndex = _model.getHilitedEigenstateIndex();
        if ( hiliteIndex != BSEigenstate.INDEX_UNDEFINED ) {
            BSEigenstate hilitedEigenstate = _model.getEigenstate( hiliteIndex );
            _hilitedEigenstateSeries.add( 0 /* don't care */, hilitedEigenstate.getEnergy() );
        }
        _hilitedEigenstateSeries.setNotify( true );
    }
}
