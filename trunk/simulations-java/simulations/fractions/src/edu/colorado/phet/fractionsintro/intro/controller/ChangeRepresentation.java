// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.controller;

import fj.F2;
import lombok.Data;

import edu.colorado.phet.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.FactorySet;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

/**
 * @author Sam Reid
 */
public @Data class ChangeRepresentation extends F2<IntroState, Representation, IntroState> {
    public final FactorySet factorySet;

    public IntroState f( IntroState s, Representation r ) {
        //Workaround for a bug: when dragging number line quickly, pie set gets out of sync.  So update it when representations change
        return s.representation( r ).fromContainerSet( s.containerSet, factorySet );
    }
}