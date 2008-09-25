package edu.colorado.phet.semiconductor.macro.energyprobe;

import edu.colorado.phet.common.phetcommon.math.Vector2D;


/**
 * User: Sam Reid
 * Date: Feb 2, 2004
 * Time: 8:25:04 PM
 */
public class Cable {
    Lead lead;
    private Vector2D.Double attachmentPoint;


    public Cable( Lead lead, Vector2D.Double attachmentPoint ) {
        this.lead = lead;
        this.attachmentPoint = attachmentPoint;
    }

    public Lead getLead() {
        return lead;
    }

    public Vector2D.Double getAttachmentPoint() {
        return attachmentPoint;
    }
}
