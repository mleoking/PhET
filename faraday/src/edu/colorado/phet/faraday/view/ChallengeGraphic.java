/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.Color;
import java.awt.Component;

import edu.colorado.phet.common.model.BaseModel;


/**
 * ChallengeGraphic displays a "challenge", a task that the user must try to complete.
 * <p>
 * NOTE: We don't currently have a specification for challenges, so for now their
 * implementation is identical to wiggle mes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ChallengeGraphic extends WiggleMeGraphic {

    /**
     * Sole constructor.
     * 
     * @param component
     * @param model
     */
    public ChallengeGraphic( Component component, BaseModel model ) {
        super( component, model );
    }
}
