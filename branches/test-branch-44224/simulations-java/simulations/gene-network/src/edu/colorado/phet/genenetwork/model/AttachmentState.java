package edu.colorado.phet.genenetwork.model;

/**
 * This enum defines the possible states of an attachment that exists between
 * two model elements.
 * 
 * @author John Blanco
 */
public enum AttachmentState {
	UNATTACHED_AND_AVAILABLE,
	MOVING_TOWARDS_ATTACHMENT,
	ATTACHED,
	UNATTACHED_BUT_UNAVALABLE
}
