/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.model;


/**
 * ModelElement
 *
 * @author ?
 * @version $Revision$
 */
public interface ModelElement {
    public void stepInTime( double dt );
}
