/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/view/ITabbedModulePane.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.2 $
 * Date modified : $Date: 2006/05/31 18:00:42 $
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleEvent;
import edu.colorado.phet.common.application.ModuleObserver;
import edu.colorado.phet.common.application.PhetApplication;

/**
 * ITabbedModulePane
 * <p>
 * The interface for tabbed panes that contain and switch between module views
 *
 * @author Ron LeMaster
 * @version $Revision: 1.2 $
 */
public interface ITabbedModulePane extends ModuleObserver {
    public void init( final PhetApplication application, final Module[] modules );
    public void addTab( Module module );
    public void moduleAdded( ModuleEvent event );
    public void removeTab( Module module );
    public void activeModuleChanged( ModuleEvent event );
    public int getTabCount();
    public void moduleRemoved( ModuleEvent event );
    public ModulePanel getModulePanel( int i );
}
