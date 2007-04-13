/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

/**
 * Selectable
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface Selectable {
    static class Selection {
        private Selection(){};
    }
    static Selection NOT_SELECTED = new Selection();
    static Selection SELECTED = new Selection();
    static Selection NEAREST_TO_SELECTED = new Selection();

    void setSelectionStatus( Selection selection );

    Selection getSelectionStatus();
}
