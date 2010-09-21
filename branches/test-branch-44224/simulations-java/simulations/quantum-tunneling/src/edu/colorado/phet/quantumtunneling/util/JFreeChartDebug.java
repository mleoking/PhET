/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.util;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.plot.PlotRenderingInfo;


/**
 * JFreeChartDebug is a collection of static debugging methods for use with JFreeChart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class JFreeChartDebug {

    /* Not intended for instantiation */
    private JFreeChartDebug() {}
    
    //----------------------------------------------------------------------------
    // ChartRenderingInfo
    //----------------------------------------------------------------------------
    
    /**
     * Prints a ChartRenderingInfo to System.out.
     * 
     * @param info
     */
    public static void print( ChartRenderingInfo info ) {
        if ( info == null ) {
            System.out.println( "ChartRenderingInfo: null" );
        }
        else {
            System.out.println( "ChartRenderingInfo:" );
            System.out.println( "  chartArea = " + info.getChartArea() );
            print( info.getPlotInfo() ); 
        }
    }
    
    /**
     * Prints a PlotRenderingInfo to System.out.
     * 
     * @param info
     */
    public static void print( PlotRenderingInfo info ) {
        print( info, 0, 0 );
    }
    
    /*
     * Recursively prints the PlotRenderingInfo for nested subplots.
     * 
     * @param plotInfo
     * @param depth
     * @param index
     */
    private static void print( PlotRenderingInfo info, int depth, int index ) {
        if ( info == null ) {
            System.out.println( "PlotRenderingInfo[" + depth + "," + index + "]: null" );
        }
        else {
            System.out.println( "PlotRenderingInfo[" + depth + "," + index + "]:" );
            System.out.println( "  dataArea = " + info.getDataArea() );
            System.out.println( "  plotArea = " + info.getPlotArea() );
            System.out.println( "  subplotCount = " + info.getSubplotCount() );
            // Recursively print info for each subplot
            for ( int i = 0; i < info.getSubplotCount(); i++ ) {
                print( info.getSubplotInfo( i ), depth + 1, i );
            }
        }
    }
}
