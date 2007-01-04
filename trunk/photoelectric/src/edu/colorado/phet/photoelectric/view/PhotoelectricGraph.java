/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.Range2D;

import java.awt.*;

public abstract class PhotoelectricGraph extends Chart {

    public PhotoelectricGraph( Component component, Range2D range, Dimension chartSize, double horizMinorSpacing, double horizMajorSpacing, double vertMinorSpacing, double vertMajorSpacing ) {
        super( component, range, chartSize, horizMinorSpacing, horizMajorSpacing, vertMinorSpacing, vertMajorSpacing );
    }

    

    //--------------------------------------------------------------------------------------------------
    // Abstract methods
    //--------------------------------------------------------------------------------------------------

    abstract public void clearData();
}
