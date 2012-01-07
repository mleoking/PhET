// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

import edu.colorado.phet.common.phetcommon.simsharing.IMessageSource;

/**
 * Reusable sim-sharing strings, grouped by their role in sim-sharing events.  Uses marker interfaces to ensure that all strings are "sancitioned" i.e. appearing
 * in a specified list and not as scattered and error-prone string literals.  Dynamic strings can still be constructed but must be put implementing interface.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 */
public class SimSharingConstants {

    public static enum PhetCommonMessageSource implements IMessageSource {
        phetcommon
    }

    //Parameters and values are top level so they can be shared between different namespaces, i.e. so you do not have to declare different parameters for model vs user, and so that
    //sims can use these values
    public static interface ParameterKey {
    }

    public static enum ParameterKeys implements ParameterKey {
        canvasPositionX, canvasPositionY, componentType, description, height, interactive, item, isSelected, key, part, text, title, value, width, window, x, y,
        isPlaying,

        //For system:
        time, name, version, project, flavor, locale, distributionTag, javaVersion, osName, osVersion, parserVersion, study, id, machineCookie, messageCount, messageIndex, shouldReset,

        errorMessage
    }

    public static interface ComponentType {
    }

    public static enum ComponentTypes implements ComponentType {
        button, checkBox, menuItem, radioButton, spinner, checkBoxMenuItem, icon, menu
    }

}