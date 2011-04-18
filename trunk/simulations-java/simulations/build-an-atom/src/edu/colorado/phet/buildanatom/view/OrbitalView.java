package edu.colorado.phet.buildanatom.view;

/**
 * Enumeration that defines the different views that can be used for the
 * electrons, i.e. whether they are individual particles (Bohr model) or
 * some type of cloud.
 */
public enum OrbitalView {
    PARTICLES,         // Electrons are shown as individual particles.
    RESIZING_CLOUD,    // Electrons are depicted as a cloud that grows as more are added.
}