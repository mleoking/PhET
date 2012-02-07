// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.text.MessageFormat;
import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.model.Solute;
import edu.colorado.phet.beerslawlab.common.view.SoluteChoiceNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * The "Solution" combo box is identical to the Solute combo box (it choose a solute),
 * but the designers wanted it to be labeled "Solution".
 * It also has a different format for the solute labels, showing both the formula and name.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionChoiceNode extends SoluteChoiceNode {

    public SolutionChoiceNode( ArrayList<Solute> solutes, final Property<Solute> currentSolute ) {
        super( Strings.SOLUTION, solutes, currentSolute,
               new Function1<Solute, String>() {
                   public String apply( Solute solute ) {
                       return ( solute.formula.equals( solute.name ) ? solute.formula : MessageFormat.format( "{0}: {1}", solute.formula, solute.name ) );
                   }
               } );
    }
}
