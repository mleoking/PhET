package edu.colorado.phet.buildanatom.modules.game.model;


/**
 * Base class for all problems where the user is adjusting an interactive
 * symbol representation.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public abstract class ToSymbolProblem extends Problem {

    private final boolean configurableProtonCount;
    protected boolean isConfigurableProtonCount() {
        return configurableProtonCount;
    }

    protected boolean isConfigurableMass() {
        return configurableMass;
    }

    protected boolean isConfigurableCharge() {
        return configurableCharge;
    }

    private final boolean configurableMass;
    private final boolean configurableCharge;

    public ToSymbolProblem( BuildAnAtomGameModel model, AtomValue atomValue, boolean configurableProtonCount,
            boolean configurableMass, boolean configurableCharge ){
        super( model, atomValue );
        this.configurableProtonCount = configurableProtonCount;
        this.configurableMass = configurableMass;
        this.configurableCharge = configurableCharge;
    }
}