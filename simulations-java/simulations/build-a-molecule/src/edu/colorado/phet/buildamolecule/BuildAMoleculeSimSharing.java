// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildamolecule;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

public class BuildAMoleculeSimSharing {
    public enum UserComponent implements IUserComponent {
        atom
    }

    public enum ParameterKey implements IParameterKey {
        atomSymbol, atomReference
    }
}
