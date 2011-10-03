// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

//REVIEW This seems like an abuse of exceptions. Exceptions are for abnormal conditions, they shouldn't be used to report
// conditions that can are expected as part of the everyday functioning of a method. Your various Crystal.grow* methods throw this
// exception to indicate that they ran into a dead end, and that the client should reattempt the grow. A better design would
// be to have the grow methods return a boolean indicating success or failure.

/**
 * Exception thrown to signify that a crystal growth reached a dead end--it was impossible to add all components of one full formula ratio.
 * In this case, the crystal should be re-grown.
 *
 * @author Sam Reid
 */
class BondingFailure extends Exception {
}