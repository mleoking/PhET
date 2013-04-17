// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.genenetwork.model;

public interface IModelElementListener {
    void positionChanged();
    void shapeChanged();
    void existenceStrengthChanged();
    void removedFromModel();
}
