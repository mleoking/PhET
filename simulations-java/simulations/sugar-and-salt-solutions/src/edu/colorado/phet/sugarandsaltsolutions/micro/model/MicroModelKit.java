// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

/**
 * Model kit, for making sure that particle draining can happen in formula units so there isn't an unbalanced number of solutes for crystallization
 *
 * @author Sam Reid
 */
public class MicroModelKit {
    private final ItemList<Formula> formulae;

    public MicroModelKit( Formula... formulae ) {
        this.formulae = new ItemList<Formula>( formulae );
    }

    public ItemList<Formula> getFormulae() {
        return formulae;
    }
}