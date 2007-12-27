package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * AbstractTool is the base class for all tools in the toolbox.
 * A tool is a movable model element.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractTool extends Movable implements ModelElement {
    public AbstractTool( Point2D position ) {
        super( position );
    }
}
