// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

/**
 * System component types.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public enum SystemComponentTypes implements ISystemComponentType {
    unknown, // TODO look for these occurrences and replace with something sensible
    window, dialog,
    application,
    simsharingManager
}
