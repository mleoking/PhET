// Copyright (C) 2001-2003 Jon A. Maxwell (JAM)
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.


package netx.jnlp.runtime;

import netx.jnlp.JNLPFile;

import java.applet.Applet;

/**
 * Represents a launched application instance created from a JNLP
 * file.  This class does not control the operation of the applet,
 * use the AppletEnvironment class to start and stop the applet.
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$
 */
public class AppletInstance extends ApplicationInstance {

    /**
     * whether the applet's stop and destroy methods have been called
     */
    private boolean appletStopped = false;

    /**
     * the applet
     */
    private Applet applet;

    /**
     * the applet environment
     */
    private AppletEnvironment environment;


    /**
     * Create a New Task based on the Specified URL
     */
    public AppletInstance( JNLPFile file, ThreadGroup group, ClassLoader loader, Applet applet ) {
        super( file, group, loader );

        this.applet = applet;

        this.environment = new AppletEnvironment( file, this );
    }

    /**
     * Sets whether the applet is resizable or not.  Applets default
     * to being not resizable.
     */
    public void setResizable( boolean resizable ) {
        environment.getAppletFrame().setResizable( resizable );
    }

    /**
     * Returns whether the applet is resizable.
     */
    public boolean isResizable() {
        return environment.getAppletFrame().isResizable();
    }

    /**
     * Returns the application title.
     */
    public String getTitle() {
        return getJNLPFile().getApplet().getName();
    }

    /**
     * Returns the applet environment.
     */
    public AppletEnvironment getAppletEnvironment() {
        return environment;
    }

    /**
     * Returns the applet.
     */
    public Applet getApplet() {
        return applet;
    }

    /**
     * Stop the application and destroy its resources.
     */
    public void destroy() {
        if( appletStopped ) {
            return;
        }

        appletStopped = true;

        try {
            applet.stop();
            applet.destroy();
        }
        catch( Exception ex ) {
            if( JNLPRuntime.isDebug() ) {
                ex.printStackTrace();
            }
        }

        environment.destroy();

        super.destroy();
    }

}

