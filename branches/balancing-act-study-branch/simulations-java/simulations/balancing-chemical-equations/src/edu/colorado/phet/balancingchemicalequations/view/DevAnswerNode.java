// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;

/**
 * Debug node that shows the balanced equation.
 * No i18n is required, since this is only visible to developers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DevAnswerNode extends HTMLNode {

    public DevAnswerNode( final Property<Equation> equationProperty ) {
        setFont( new PhetFont( 12 ) );

        equationProperty.addObserver( new SimpleObserver() {
            public void update() {
                StringBuffer b = new StringBuffer( "Answer: ");

                // reactants
                EquationTerm[] reactantTerms = equationProperty.get().getReactants();
                for ( int i = 0; i < reactantTerms.length; i++ ) {
                    b.append( reactantTerms[i].getBalancedCoefficient() );
                    b.append( reactantTerms[i].getMolecule().getSymbol() );
                    b.append( " " );
                    if ( i < reactantTerms.length - 1 ) {
                        b.append( "+ " );
                    }
                }

                // right arrow
                b.append( "\u2192 " );

                // products
                EquationTerm[] productTerms = equationProperty.get().getProducts();
                for ( int i = 0; i < productTerms.length; i++ ) {
                    b.append( productTerms[i].getBalancedCoefficient() );
                    b.append( productTerms[i].getMolecule().getSymbol() );
                    b.append( " " );
                    if ( i < productTerms.length - 1 ) {
                        b.append( "+ " );
                    }
                }

                setHTML( b.toString() );
            }
        } );
    }
}
