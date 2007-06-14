/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/tests-src/edu/colorado/phet/common/tests/TestMedianFilter.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: samreid $
 * Revision : $Revision: 1.6 $
 * Date modified : $Date: 2005/12/09 21:17:35 $
 */
package edu.colorado.phet.common.tests;

import edu.colorado.phet.common.math.MedianFilter;

/**
 * User: Sam Reid
 * Date: Dec 12, 2004
 * Time: 9:17:24 PM
 * Copyright (c) Dec 12, 2004 by Sam Reid
 */

public class TestMedianFilter {
    public static void main( String[] args ) {
        double[] d = new double[]{9, 8, 7, 6, 5, 6, 7, 4, 5, 8, 9};
        MedianFilter mf = new MedianFilter( d );
        d = mf.filter( 3 );
        for( int i = 0; i < d.length; i++ ) {
            System.out.println( d[i] );
        }
    }
}