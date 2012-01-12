// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;


/**
 * Base class for all problems where the user is adjusting an interactive
 * symbol representation.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public abstract class ToSymbolProblem extends Problem {

    private final boolean configurableProtonCount;

    public boolean isConfigurableProtonCount() {
        return configurableProtonCount;
    }

    public boolean isConfigurableMass() {
        return configurableMass;
    }

    public boolean isConfigurableCharge() {
        return configurableCharge;
    }

    private final boolean configurableMass;
    private final boolean configurableCharge;

    public ToSymbolProblem( BuildAnAtomGameModel model, ImmutableAtom atomValue, boolean configurableProtonCount,
                            boolean configurableMass, boolean configurableCharge ) {
        super( model, atomValue );
        this.configurableProtonCount = configurableProtonCount;
        this.configurableMass = configurableMass;
        this.configurableCharge = configurableCharge;
    }
}