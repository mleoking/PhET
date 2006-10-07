/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleEvent;
import edu.colorado.phet.common.application.ModuleObserver;
import edu.colorado.phet.common.application.PhetApplication;

import javax.swing.*;

/**
 * ITabbedModulePane
 * <p>
 * The interface for tabbed panes that contain and switch between module views
 *
 * @author Ron LeMaster
 * @version $Revision$
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
    public JComponent getComponent();
    public void setLogoVisible(boolean logoVisible);
    public boolean getLogoVisible();
}
