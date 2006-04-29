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

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.util.PhetUtilities;

import java.awt.*;

/**
 * TabbedModulePaneFactory
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TabbedModulePaneFactory {

    public static Container createTabbedPane( PhetApplication application, Module[] modules ) {
        ITabbedModulePane tabbedPane  = null;
        try {
            if( PhetUtilities.getGraphicsType() == PhetApplication.PHET_GRAPHICS ) {
                tabbedPane = (ITabbedModulePane)Class.forName( "edu.colorado.phet.common.view.TabbedModulePanePhetGraphics").newInstance();
                tabbedPane.init( application, modules );
            }
            else if(PhetUtilities.getGraphicsType() == PhetApplication.PICCOLO ) {
                tabbedPane = (ITabbedModulePane)Class.forName( "edu.colorado.phet.piccolo.TabbedModulePanePiccolo").newInstance();
                tabbedPane.init(application, modules );
            }
        }
        catch( InstantiationException e ) {
            e.printStackTrace();
        }
        catch( IllegalAccessException e ) {
            e.printStackTrace();
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        return (Container)tabbedPane;
    }
}
