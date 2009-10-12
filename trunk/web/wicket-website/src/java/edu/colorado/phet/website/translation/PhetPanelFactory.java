package edu.colorado.phet.website.translation;

import java.io.Serializable;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public interface PhetPanelFactory extends Serializable {
    public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle );
}
