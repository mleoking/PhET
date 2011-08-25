// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

/**
 * Exception thrown to signify that a crystal growth reached a dead end--it was impossible to add all components of one full formula ratio.
 * In this case, the crystal should be re-grown.
 *
 * @author Sam Reid
 */
class BondingFailure extends Exception {
}