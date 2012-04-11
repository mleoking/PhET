// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.fractions.util.RichVoidFunction0;

/**
 * @author Sam Reid
 */
public class FractionsIntroSimSharing {
    public enum Components implements IUserComponent {
        matchingGameTab, equalityLabTab, introTab,
        maxSpinnerUpButton,
        maxSpinnerDownButton,
    }

    public enum ParameterKeys implements IParameterKey {
        max
    }

    public static RichVoidFunction0 sendMessage( final IUserComponent component, final IUserComponentType type, final IUserAction action, final Function0<ParameterSet> parameters ) {
        return new RichVoidFunction0() {
            @Override public void apply() {
                SimSharingManager.sendUserMessage( component, type, action, parameters.apply() );
            }
        };
    }
}