// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.view.tools;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.IToolProducer;

/**
 * TrashCanIconNode shows a picture of a trash can.
 */
public class TrashCanIconNode extends AbstractToolIconNode {
    
    private final TrashCanDelegate _trashCanDelegate;
    
    /**
     * Constructor.
     * 
     * @param toolProducer
     */
    public TrashCanIconNode( final IToolProducer toolProducer ) {
        super( GlaciersImages.TRASH_CAN );
        _trashCanDelegate = new TrashCanDelegate( this, toolProducer );
    }
    
    /**
     * Gets the delegate that handles the specifics of trashing tools.
     * 
     * @return TrashCanDelegate
     */
    public TrashCanDelegate getTrashCanDelegate() {
        return _trashCanDelegate;
    }
}