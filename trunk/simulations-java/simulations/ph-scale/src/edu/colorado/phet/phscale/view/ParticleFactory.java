/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;


public class ParticleFactory {
    
    private static final double ACID_PH_THRESHOLD = 6;
    private static final double BASE_PH_THRESHOLD = 8;

    private static final int MAX_PARTICLES = 5000;
    
    public static PNode createParticles( double pH, PDimension containerSize ) {
       
        return new PNode();//XXX
        
//        final double ratio = ratio_H30_to_OH( pH );
//
//        // calculate the number of H30 and OH particles
//        final double multiplier = (int) MAX_PARTICLES / ratio_H30_to_OH( 0 );
//        int numH30, numOH;
//        if ( ratio == 1 ) {
//            numH30 = numOH = (int) Math.max( 1, multiplier );
//        }
//        else if ( ratio > 1 ) {
//            numH30 = (int) ( multiplier * ratio );
//            numOH = (int) Math.max( 1, multiplier );
//        }
//        else {
//            numH30 = (int) Math.max( 1, multiplier );
//            numOH = (int) ( multiplier / ratio );
//        }
//
//        // create particles
//        if ( numH30 > numOH ) {
//            createH3ONodes( numH30 );
//            createOHNodes( numOH );
//        }
//        else {
//            createOHNodes( numOH );
//            createH3ONodes( numH30 );
//        }
        
    }
    
//    /* 
//     * Computes the ratio of H30 to OH.
//     * Between pH of 6 and 8, we use the actual log scale.
//     * Below 6 and above 8, use a linear scale for "Hollywood" visualization.
//     */
//    private static double ratio_H30_to_OH( double pH ) {
//        double ratio;
//        if ( pH >= ACID_PH_THRESHOLD && pH <= BASE_PH_THRESHOLD ) {
//            ratio = concentrationH30( pH ) / concentrationOH( pH );
//        }
//        else if ( pH < ACID_PH_THRESHOLD ) {
//            double multiplier = ACID_PH_THRESHOLD - pH + 1;
//            ratio = multiplier * concentrationH30( ACID_PH_THRESHOLD ) / concentrationOH( ACID_PH_THRESHOLD );
//        }
//        else {
//            double multiplier = 1 / ( pH - BASE_PH_THRESHOLD + 1 );
//            ratio = multiplier * concentrationH30( BASE_PH_THRESHOLD ) / concentrationOH( BASE_PH_THRESHOLD );
//        }
//        return ratio;
//    }
}
