// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterMolecule;

/**
 * Provides physical locations (positions) of the atoms within a sucrose molecule.
 * Positions sampled from a 2d rasterized view of sucrose from JMol with ProjectorUtil
 * <p/>
 * C12H22O11
 *
 * @author Sam Reid
 */
public class SucrosePositions extends ProjectedPositions {
    public static final double scale = WaterMolecule.oxygenRadius * 10 / 800 * 0.7;

    public SucrosePositions() {
        super( "H 695, 300\n" +
               "H 470, 514\n" +
               "H 668, 516\n" +
               "H 264, 281\n" +
               "O 770, 155\n" +
               "H 848, 188\n" +
               "C 755, 352\n" +
               "C 667, 463\n" +
               "C 536, 432\n" +
               "O 490, 337\n" +
               "C 564, 231\n" +
               "C 692, 257\n" +
               "O 866, 379\n" +
               "H 877, 474\n" +
               "O 722, 555\n" +
               "C 415, 529\n" +
               "C 403, 404\n" +
               "C 372, 283\n" +
               "C 218, 321\n" +
               "O 294, 429\n" +
               "C 291, 206\n" +
               "C 177, 284\n" +
               "H 129, 205\n" +
               "H 127, 339\n" +
               "O 113, 390\n" +
               "H 43, 406\n" +
               "O 486, 222\n" +
               "H 465, 293\n" +
               "C 503, 149\n" +
               "H 554, 75\n" +
               "H 416, 119\n" +
               "O 201, 114\n" +
               "H 232, 573\n" +
               "H 458, 619\n" +
               "H 496, 512\n" +
               "O 526, 385\n" +
               "H 261, 62\n" +
               "H 308, 302\n" +
               "H 563, 174\n" +
               "H 544, 211\n" +
               "H 761, 303\n" +
               "O 296, 570\n" +
               "H 700, 514\n" +
               "H 348, 160\n" +
               "O 480, 198",
               scale );
    }
}