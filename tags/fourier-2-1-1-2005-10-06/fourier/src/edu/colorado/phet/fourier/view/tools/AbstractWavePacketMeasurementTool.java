/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view.tools;

import java.awt.*;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.model.GaussianWavePacket;


/**
 * AbstractWavePacketMeasurementTool is the abstract base class for all tools
 * used to measure Gaussian wave packets.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractWavePacketMeasurementTool extends MeasurementTool implements SimpleObserver, Chart.Listener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Default "look" for all wave packet measurement tools
    private static final Font LABEL_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 16 );
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final Color LABEL_BACKGROUND_COLOR = new Color(255,255,255,150); // translucent white
    private static final Color BAR_FILL_COLOR = Color.YELLOW;
    private static final Color BAR_BORDER_COLOR = Color.BLACK;
    private static final Stroke BAR_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GaussianWavePacket _wavePacket;
    private Chart _chart;
    private int _domain;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param wavePacket
     * @param chart
     */
    public AbstractWavePacketMeasurementTool( Component component, GaussianWavePacket wavePacket, Chart chart ) {
        super( component );
        
        assert( wavePacket != null );
        _wavePacket = wavePacket;
        _wavePacket.addObserver( this );
        
        assert( chart != null );
        _chart = chart;
        _chart.addListener( this );
        
        setLabelFont( LABEL_FONT );
        setLabelColor( LABEL_COLOR );
        setFillColor( BAR_FILL_COLOR );
        setBorderColor( BAR_BORDER_COLOR );
        setStroke( BAR_STROKE );
        setLabelBackground( LABEL_BACKGROUND_COLOR );
        
        _domain = FourierConstants.DOMAIN_SPACE;
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _wavePacket.removeObserver( this );
        _wavePacket = null;
        _chart.removeListener( this );
        _chart = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the Gaussian wave packet that this tool is observing.
     * 
     * @return GaussianWavePacket
     */
    protected GaussianWavePacket getWavePacket() {
        return _wavePacket;
    }
    
    /**
     * Gets the Chart that this tool is related to.
     * 
     * @return Chart
     */
    protected Chart getChart() {
        return _chart;
    }
    
    /**
     * Sets the domain.
     * The tool is updated.
     * 
     * @param domain see FourierConstants.DOMAIN_*
     */
    public void setDomain( int domain ) {
        if ( !FourierConstants.isValidDomain( domain ) ) {
            throw new IllegalArgumentException( "invalid domain: " + domain );
        }
        _domain = domain;
        updateTool();
    }
    
    /**
     * Gets the domain.
     * 
     * @return see FourierConstants.DOMAIN_*
     */
    protected int getDomain() {
        return _domain;
    }
    
    //----------------------------------------------------------------------------
    // Abstract interface
    //----------------------------------------------------------------------------
    
    /**
     * Updates the tool to match the current state of the wave packet,
     * chart and domain.
     */
    protected abstract void updateTool();
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the tool when the wave packet changes.
     */
    public void update() {
        if ( isVisible() ) {
            updateTool();
        }
    }
    
    //----------------------------------------------------------------------------
    // Chart.Listener implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the tool when the chart changes.
     * 
     * @param chart the chart that changed
     */
    public void transformChanged( Chart chart ) {
        updateTool();
    }
}
