/* Copyright 2007, University of Colorado */
package edu.umd.cs.piccolo;

public interface IgnorableEventSource {
    boolean isIgnoringEvents();

    void setIgnoringEvents(boolean ignore);
}
