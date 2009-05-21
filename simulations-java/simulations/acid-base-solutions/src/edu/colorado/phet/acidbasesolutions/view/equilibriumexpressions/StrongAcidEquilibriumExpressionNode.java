package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;


/**
 * Strong acid equilibrium expression: Ka = [H3O+][A-] / [HA] = Large
 */
class StrongAcidEquilibriumExpressionNode extends AbstractAcidEquilibriumExpressionNode {
    
    public StrongAcidEquilibriumExpressionNode( String acidSymbol, String baseSymbol ) {
        super( acidSymbol, baseSymbol );
        setLargeValueVisible( true );
    }
}