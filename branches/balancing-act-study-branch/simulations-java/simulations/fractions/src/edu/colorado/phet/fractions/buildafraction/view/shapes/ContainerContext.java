// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

/**
 * Context in which a ContainerNode can be dragged/dropped.
 *
 * @author Sam Reid
 */
public interface ContainerContext {
    void startDrag( ContainerNode parent );

    void endDrag( ContainerNode containerNode );

    void syncModelFractions();

    void containerAdded( ContainerNode containerNode );

    boolean isFractionLab();

    void containerNodeAnimationToToolboxFinished( ContainerNode containerNode );
}