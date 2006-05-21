/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.AbstractPotential;
import edu.colorado.phet.quantumtunneling.model.EigenstateSolver;
import edu.colorado.phet.quantumtunneling.model.WavePacket;
import edu.colorado.phet.quantumtunneling.model.EigenstateSolver.EigenstateException;
import edu.colorado.phet.quantumtunneling.model.EigenstateSolver.PotentialEvaluator;


/**
 * TotalEnergyRenderer renders a representation of total energy for a wave packet.
 * There are 3 possible representations of total energy:
 * <p>
 * (1) When total energy is less than the potential energy at the wave packet's 
 * initial center, the total energy is represented as a single dashed line.
 * <p> 
 * (2) When the wave packet's center is in a well, total energy above the top of the
 * well is represented by a "band" of probabilities (see #3 below). Below the 
 * top of the well we show a set of discrete eigenstate energies. The color brightness
 * of the eigenstate energies is indicative of their probability.
 * <p>
 * (3) In all other case, total energy is represented as a "band" of probabilites.
 * Higher brightness of color in the band indicates higher probability.
 * The average total energy is indicated by a diferent-colored line at the 
 * center of the band.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TotalEnergyRenderer extends AbstractXYItemRenderer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double HBAR = QTConstants.HBAR;
    private static final double MASS = QTConstants.MASS;
    
    private static final Color MASK_COLOR = new Color( 255, 0, 0, 255 ); // opaque red
    
    // EigenstateSolver parameters
    private static final double EIGENSTATE_HB = ( HBAR * HBAR ) / ( 2 * MASS ); // magic constant
    private static final int EIGENSTATE_POINTS = 1000; // number of sample points to use
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private WavePacket _wavePacket;
    private AbstractPotential _potentialEnergy;
    
    private Color _centerColor;
    private Color _edgeColor;
    
    private GeneralPath _path; // reusable path
    private AffineTransform _xform; // reusable transform
    private Rectangle2D _rectangle; // reusable rectangle

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public TotalEnergyRenderer() {
        super();
        _path = new GeneralPath();
        _xform = new AffineTransform();
        _rectangle = new Rectangle2D.Double();
        setColor( QTConstants.COLOR_SCHEME.getTotalEnergyColor() );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the color used for total energy.
     * 
     * @param color
     */
    public void setColor( Color color ) {
        _centerColor = color;
        _edgeColor = new Color( _centerColor.getRed(), _centerColor.getGreen(), _centerColor.getBlue(), 5 );
    }
    
    /**
     * Sets the wave packet.
     * The wave packet's initial width and center affect the total energy
     * probability distribution.
     * 
     * @param wavePacket
     */
    public void setWavePacket( WavePacket wavePacket ) {
        _wavePacket = wavePacket;
    }
    
    /**
     * Sets the potential energy.  
     * <p>
     * The wave packet (set via setWavePacket) provides access to its potential
     * energy.  But there are situations (like in the "Configure Energy" dialog)
     * where we want to use a "hypothetical" potential that hasn't been applied
     * to the wave packet model yet.  So we specify the potential energy separately
     * via this method.
     * 
     * @param potentialEnergy
     */
    public void setPotentialEnergy( AbstractPotential potentialEnergy ) {
        _potentialEnergy = potentialEnergy;
    }
    
    //----------------------------------------------------------------------------
    // AbstractXYItemRenderer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Draws the proper representation of total energy,
     * as described in the javadoc for this class.
     */
    public void drawItem( 
            Graphics2D g2, 
            XYItemRendererState state, 
            Rectangle2D dataArea, 
            PlotRenderingInfo info, 
            XYPlot plot, 
            ValueAxis domainAxis, 
            ValueAxis rangeAxis, 
            XYDataset dataset, 
            int series, 
            int item, 
            CrosshairState crosshairState, 
            int pass ) {
    
        // Do all rendering based on the first data point.
        if ( item != 0 ) {
            return;
        }
        
        // Initialized?
        if ( _wavePacket == null || _potentialEnergy == null ) {
            return;
        }
       
        // Visible?
        if ( !getItemVisible( series, item ) ) {
            return;
        }
        
        // Enabled antialiasing
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR ); // Java 1.5 workaround
        
        // Axis (model) coordinates
        final double packetCenter = _wavePacket.getCenter();
        final double E0 = dataset.getYValue( series, item ); // average total energy
        final double V0 = _potentialEnergy.getEnergyAt( packetCenter ); // potential energy at wave packet's initial center
          
        // Determine which representation to use for total energy
        if ( E0 <= V0 ) {
            drawDashedLine( g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item );
        }
        else if ( _potentialEnergy.isInWell( packetCenter ) ) {
            drawBandAndEigenstates( g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item );
        }
        else {
            drawBand( g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item );
        }
    }
  
    /* 
     * Draws total energy as a dashed line.
     * 
     * This drawing method is called if the total energy is less than the potential energy 
     * at the wave packet's center. We use a GeneralPath so that we are pixel-accurate with 
     * the FastPathRenderer used for plane waves. The line is drawn at its screen coordinates; 
     * there is no translation involved.
     */
    private void drawDashedLine( 
            Graphics2D g2, 
            XYItemRendererState state, 
            Rectangle2D dataArea, 
            PlotRenderingInfo info, 
            XYPlot plot, 
            ValueAxis domainAxis, 
            ValueAxis rangeAxis, 
            XYDataset dataset, 
            int series, 
            int item ) 
    {  
        // Axis (model) coordinates
        final double minPosition = domainAxis.getLowerBound();
        final double maxPosition = domainAxis.getUpperBound();
        final double E0 = dataset.getYValue( series, item ); // the average total energy
        
        // Java2D (screen) coordinates
        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        final double minX = domainAxis.valueToJava2D( minPosition, dataArea, domainAxisLocation );
        final double maxX = domainAxis.valueToJava2D( maxPosition, dataArea, domainAxisLocation );
        final double averageY = rangeAxis.valueToJava2D( E0, dataArea, rangeAxisLocation );
        
        g2.setPaint( getSeriesPaint( series ) );
        g2.setStroke( QTConstants.TOTAL_ENERGY_DASHED_STROKE );
        _path.reset();
        _path.moveTo( (float)minX, (float)averageY );
        _path.lineTo( (float)maxX, (float)averageY );
        g2.draw( _path );
    }
    
    /*
     * Draws the total energy as a set of discrete eigenstates.
     * This method is called when the wave packet's center is in a well.
     * <p>
     * NOTE: This method draws everything at screen coordinates.
     * The BufferedImages are slightly larger than would be necessary if
     * we were translating the image.  Things were done this way because
     * the other draw methods already existed, they already used absolute
     * screen coordinates, and changing them would have been destabilizing.
     */
    private void drawBandAndEigenstates( 
            Graphics2D g2, 
            XYItemRendererState state, 
            Rectangle2D dataArea, 
            PlotRenderingInfo info, 
            XYPlot plot, 
            ValueAxis domainAxis, 
            ValueAxis rangeAxis, 
            XYDataset dataset, 
            int series, 
            int item ) 
    {
        // Adapter for evaluating potential energy...
        PotentialEvaluator function = new PotentialEvaluator() {
            public double evaluate( double x ) {
                return _potentialEnergy.getEnergyAt( x );
            }
        };
        
        // Wave packet properties (model coordinates)
        final double packetCenter = _wavePacket.getCenter();
        
        // Axis (model) coordinates
        final double minPosition = domainAxis.getLowerBound();
        final double maxPosition = domainAxis.getUpperBound();
        final double E0 = dataset.getYValue( series, item ); // the average total energy
        final double minEnergy = rangeAxis.getLowerBound();
        final double topOfWellEnergy = _potentialEnergy.getTopOfWellAt( packetCenter );
        
        // Java2D (screen) coordinates
        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        final double minX = domainAxis.valueToJava2D( minPosition, dataArea, domainAxisLocation );
        final double maxX = domainAxis.valueToJava2D( maxPosition, dataArea, domainAxisLocation );
        final double maxY = rangeAxis.valueToJava2D( minEnergy, dataArea, rangeAxisLocation ); // +y is down!
        final double averageY = rangeAxis.valueToJava2D( E0, dataArea, rangeAxisLocation );
        final double topOfWellY = rangeAxis.valueToJava2D( topOfWellEnergy, dataArea, rangeAxisLocation );
        
        // Create the eigenstate solver...
        EigenstateSolver solver = new EigenstateSolver( EIGENSTATE_HB, minPosition, maxPosition, EIGENSTATE_POINTS, function );
        
        // Calculate the eigenstates...
        ArrayList energies = new ArrayList(); // array of Double
        int node = 0;
        boolean done = false;
        while ( !done ) {
            try {
                double energy = solver.getEnergy( node );
                if ( energy > topOfWellEnergy ) {
                    done = true;
                }
                else {
                    energies.add( new Double( energy ) );
                }
            }
            catch ( EigenstateException e ) {
                done =  true;
                System.err.println( e.getMessage() );
            }
            node++;
        }
        
        // Size for images...
        // Note that all of the images are sized as if their upper left corner is at screen coordinate (0,0).
        final int iwidth = (int) maxX + 1;
        final int iheight = (int) maxY + 1;
            
        // Draw the total energy "band" in a buffered image...
        BufferedImage band = new BufferedImage( iwidth, iheight, BufferedImage.TYPE_INT_ARGB );
        {
            Graphics2D gfx = band.createGraphics();
            drawBand( gfx, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item );
        }
        
        // Create a mask that shows the gradient above the top of the well, and 
        // horizontal eigenstate lines below the top of the well.
        // The opaque parts of this mask determine what parts of the gradient 
        // will be visible, ensuring that the eigenstate lines will pick up the
        // colors from the gradient.
        BufferedImage mask = new BufferedImage( iwidth, iheight, BufferedImage.TYPE_INT_ARGB );
        {  
            Graphics2D gfx = mask.createGraphics();
            
            gfx.setComposite( AlphaComposite.Src );
            
            // Area above the top of the well...
            gfx.setPaint( MASK_COLOR );
            _rectangle.setRect( 0, 0, iwidth, topOfWellY );
            gfx.fill( _rectangle );
            
            // Draw the eigenstate lines...
            gfx.setPaint( getSeriesPaint( series ) );
            gfx.setStroke( QTConstants.EIGENSTATE_STROKE );
            Iterator i = energies.iterator();
            while ( i.hasNext() ) {
                Double energy = (Double) i.next();
                double y = rangeAxis.valueToJava2D( energy.doubleValue(), dataArea, rangeAxisLocation );
                _path.reset();
                _path.moveTo( (float)minX, (float)y );
                _path.lineTo( (float)maxX, (float)y );
                gfx.draw( _path );
            }
        }
        
        // Apply the mask to the gradient band.
        // The gradient shows through the opaque part of the mask.
        BufferedImage compositeImage = new BufferedImage( iwidth, iheight, BufferedImage.TYPE_INT_ARGB );
        {
            Graphics2D gfx = compositeImage.createGraphics();
            gfx.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
            _xform.setToIdentity();
            gfx.drawRenderedImage( band, _xform );
            gfx.setComposite( AlphaComposite.DstIn );
            gfx.drawRenderedImage( mask, _xform );
        }

        // Draw the composite image...
        {
            _xform.setToIdentity();
            g2.drawRenderedImage( compositeImage, _xform );
            
            // If the average total energy is below the top of the well, represent it with a dashed line.
            if ( E0 < topOfWellEnergy ) {
                drawDashedLine( g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item );
            }
            else {
                // Otherwise draw a line for the average total energy.
                Paint paint = darkenColor( getSeriesPaint( series ) );
                g2.setPaint( paint );
                g2.setStroke( QTConstants.AVERAGE_TOTAL_ENERGY_STROKE );
                _path.reset();
                _path.moveTo( (float)minX, (float)averageY );
                _path.lineTo( (float)maxX, (float)averageY );
                g2.draw( _path );
            }
        }
    }
    
    /*
     * Draws total energy as a gradient band.
     * 
     * The band is implemented as 2 rectangles, each with its own GradientPaint.
     * The rectangles and gradients are arranged such that the "brightest" color is at 
     * the average total energy point, and the color fades out above and below.
     * The gradient band is drawn at its screen coordinates; there is 
     * no translation involved.
     * 
     * For total energy E, the band will be brightest at E=E0, and brightness decreases 
     * linearly to E=minE below and E=maxE above E0.  minE and maxE are given by:
     * 
     *   minE = E0 - ((2*hbar/w) * sqrt(2*(E0-V0 )/m )) + ((2*hbar*hbar)/(m*w*w))
     *   maxE = E0 + ((2*hbar/w) * sqrt(2*(E0-V0 )/m )) + ((2*hbar*hbar)/(m*w*w))
     * 
     * where:
     * 
     *   E0 = average total energy
     *   V0 = potential energy at the wave packet's initial center position
     *   w = wave packet's initial width
     *   m = mass
     *   hbar = Planck's constant
     * 
     * Exceptions to the above:
     * 
     *   If k0*w <= 2, then minE = V0, where k0=sqrt(2*m*(E0-V0)/(hbar*hbar))
     */
    private void drawBand( 
            Graphics2D g2, 
            XYItemRendererState state, 
            Rectangle2D dataArea, 
            PlotRenderingInfo info, 
            XYPlot plot, 
            ValueAxis domainAxis, 
            ValueAxis rangeAxis, 
            XYDataset dataset, 
            int series, 
            int item ) 
    {
        // Wave packet properties (model coordinates)
        final double packetCenter = _wavePacket.getCenter();
        final double packetWidth = _wavePacket.getWidth();
        
        // Axis (model) coordinates
        final double minPosition = domainAxis.getLowerBound();
        final double maxPosition = domainAxis.getUpperBound();
        final double E0 = dataset.getYValue( series, item ); // the average total energy
        final double V0 = _potentialEnergy.getEnergyAt( packetCenter );
        final double k0 = Math.sqrt( ( 2 * MASS * ( E0 - V0 ) ) / ( HBAR * HBAR ) );
        final double term1 = ( 2 * HBAR / packetWidth ) * Math.sqrt( 2 * ( E0 - V0 ) / MASS );
        final double term2 = ( 2 * HBAR * HBAR ) / ( MASS * packetWidth * packetWidth );
        final double maxE = E0 + term1 + term2; // min total energy
        double minE = E0 - term1 + term2; // max total energy
        if ( k0 * packetWidth <= 2 ) {
            minE = V0;
        }

        // Java2D (screen) coordinates
        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        final double minX = domainAxis.valueToJava2D( minPosition, dataArea, domainAxisLocation );
        final double maxX = domainAxis.valueToJava2D( maxPosition, dataArea, domainAxisLocation );
        final double minY = rangeAxis.valueToJava2D( maxE, dataArea, rangeAxisLocation ); // +y is down!
        final double maxY = rangeAxis.valueToJava2D( minE, dataArea, rangeAxisLocation ); // +y is down!
        final double averageY = rangeAxis.valueToJava2D( E0, dataArea, rangeAxisLocation );
        final double width = Math.max( maxX - minX, 1 );
        final double topHeight = Math.max( averageY - minY, 1 );
        final double bottomHeight = Math.max( maxY - averageY, 1 );
        
        // Draw a band...
        {
            Shape topShape = new Rectangle2D.Double( minX, minY, width, topHeight + 1 );
            Shape bottomShape = new Rectangle2D.Double( minX, averageY, width, bottomHeight );

            // Take care that the gradients aren't zero pixels high! That will crash the JVM.
            Paint topPaint = new GradientPaint( (float) minX, (float) minY, _edgeColor, (float) minX, (float) ( minY + topHeight ), _centerColor );
            Paint bottomPaint = new GradientPaint( (float) minX, (float) averageY, _centerColor, (float) minX, (float) ( averageY + bottomHeight ), _edgeColor );

            g2.setPaint( topPaint );
            g2.fill( topShape );
            g2.setPaint( bottomPaint );
            g2.fill( bottomShape );
        }
        
        // Draw a line for the average total energy...
        {
            Paint paint = darkenColor( getSeriesPaint( series ) );
            g2.setPaint( paint );
            g2.setStroke( QTConstants.AVERAGE_TOTAL_ENERGY_STROKE );
            _path.reset();
            _path.moveTo( (float)minX, (float)averageY );
            _path.lineTo( (float)maxX, (float)averageY );
            g2.draw( _path );
        }
    }
    
    /*
     * Darkens a color.
     * This is used to create a color for the "average total energy" line
     * using the color for total energy.
     * 
     * @param paint
     * @return
     */
    private static Paint darkenColor( Paint paint ) {
        Paint newPaint = paint;
        if ( paint instanceof Color ) {
            Color color = (Color)paint;
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            int a = color.getAlpha();
            final int darkenFactor = 50;
            r = Math.max( 0, r - darkenFactor );
            g = Math.max( 0, g - darkenFactor );
            b = Math.max( 0, b - darkenFactor );
            newPaint = new Color( r, g, b, a );
        }
        return newPaint;
    }
}
