package edu.colorado.phet.wickettest.translation;

import java.io.Serializable;

import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public interface PhetPanelFactory extends Serializable {
    public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle );
}
