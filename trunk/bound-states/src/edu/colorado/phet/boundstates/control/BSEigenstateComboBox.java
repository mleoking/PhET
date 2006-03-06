/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.control;

import javax.swing.JComboBox;

import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSEigenstateComboBox
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSEigenstateComboBox extends JComboBox {

    private int _numberOfEigenstates;
    private int _startingIndex;
    
    public BSEigenstateComboBox( int numberOfEigenstates, int startingIndex ) {
        super();
        setNumberOfEigenstates( numberOfEigenstates, startingIndex );
    }
    
    public void setNumberOfEigenstates( int numberOfEigenstates, int startingIndex ) {
        removeAllItems();
        _numberOfEigenstates = numberOfEigenstates;
        _startingIndex = startingIndex;
        String prefix = SimStrings.get( "choice.eigenstate.prefix" );
        for ( int i = 0; i < numberOfEigenstates; i++ ) {
            addItem( "<html>" + prefix + "<sub>" + (i-startingIndex) + "</sub></html>" );
        }
        addItem( SimStrings.get( "choice.eigenstate.superposition" ) );
    }
    
    public int getSelectedEigenstateIndex() {
        int eignestateIndex = -1;
        if ( !isSuperpositionSelected() ) {
            eignestateIndex = getSelectedIndex() + _startingIndex;
        }
        return eignestateIndex;
    }
    
    public void setSelectedEigenstateIndex( int index ) {
        if ( index < _startingIndex || index > _startingIndex + _numberOfEigenstates - 1 ) {
            throw new IllegalArgumentException( "index out of range: " + index );
        }
        setSelectedIndex( index - _startingIndex );
    }
    
    public boolean isSuperpositionSelected() {
        return getSelectedIndex() == getItemCount() - 1;
    }
    
    public void setSuperPositionSelected() {
        setSelectedIndex( getItemCount() - 1 );
    }
}
