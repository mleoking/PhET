// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.semiconductor.macro.energyprobe;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;


/**
 * User: Sam Reid
 * Date: Feb 2, 2004
 * Time: 8:25:04 PM
 */
public class Cable {
    Lead lead;
    private MutableVector2D attachmentPoint;


    public Cable( Lead lead, MutableVector2D attachmentPoint ) {
        this.lead = lead;
        this.attachmentPoint = attachmentPoint;
    }

    public Lead getLead() {
        return lead;
    }

    public MutableVector2D getAttachmentPoint() {
        return attachmentPoint;
    }
}
