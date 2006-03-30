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
import netx.jnlp.PropertyDesc;
import netx.jnlp.event.ApplicationEvent;
import netx.jnlp.event.ApplicationListener;
import netx.jnlp.util.WeakList;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.security.AccessController;
import java.security.PrivilegedAction;


/**
 * Represents a running instance of an application described in a
 * JNLPFile.  This class provides a way to track the application's
 * resources and destroy the application.<p>
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$
 */
public class ApplicationInstance {

    // todo: should attempt to unload the environment variables
    // installed by the application.


    /**
     * the file
     */
    private JNLPFile file;

    /**
     * the thread group
     */
    private ThreadGroup group;

    /**
     * the classloader
     */
    private ClassLoader loader;

    /**
     * whether the application has stopped running
     */
    private boolean stopped = false;

    /**
     * weak list of windows opened by the application
     */
    private WeakList weakWindows = new WeakList();

    /**
     * list of application listeners
     */
    private EventListenerList listeners = new EventListenerList();


    /**
     * Create an application instance for the file.
     */
    public ApplicationInstance( JNLPFile file, ThreadGroup group, ClassLoader loader ) {
        this.file = file;
        this.group = group;
        this.loader = loader;
    }

    /**
     * Add an Application listener
     */
    public void addApplicationListener( ApplicationListener listener ) {
        listeners.add( ApplicationListener.class, listener );
    }

    /**
     * Remove an Application Listener
     */
    public void removeApplicationListener( ApplicationListener listener ) {
        listeners.remove( ApplicationListener.class, listener );
    }

    /**
     * Notify listeners that the application has been terminated.
     */
    protected void fireDestroyed() {
        Object list[] = listeners.getListenerList();
        ApplicationEvent event = null;

        for( int i = list.length - 1; i > 0; i -= 2 ) { // last to first required
            if( event == null ) {
                event = new ApplicationEvent( this );
            }

            ( (ApplicationListener)list[i] ).applicationDestroyed( event );
        }
    }

    /**
     * Initialize the application's environment (installs
     * environment variables, etc).
     */
    public void initialize() {
        installEnvironment();
    }

    /**
     * Releases the application's resources before it is collected.
     * Only collectable if classloader and thread group are
     * also collectable so basically is almost never called (an
     * application would have to close its windows and exit its
     * threads but not call System.exit).
     */
    public void finalize() {
        destroy();
    }

    /**
     * Install the environment variables.
     */
    void installEnvironment() {
        final PropertyDesc props[] = file.getResources().getProperties();

        PrivilegedAction installProps = new PrivilegedAction() {
            public Object run() {
                for( int i = 0; i < props.length; i++ ) {
                    System.setProperty( props[i].getKey(), props[i].getValue() );
                }

                return null;
            }
        };
        AccessController.doPrivileged( installProps );
    }

    /**
     * Returns the JNLP file for this task.
     */
    public JNLPFile getJNLPFile() {
        return file;
    }

    /**
     * Returns the application title.
     */
    public String getTitle() {
        return file.getTitle();
    }

    /**
     * Returns whether the application is running.
     */
    public boolean isRunning() {
        return !stopped;
    }

    /**
     * Stop the application and destroy its resources.
     */
    public void destroy() {
        if( stopped ) {
            return;
        }

        try {
            // destroy resources
            for( int i = 0; i < weakWindows.size(); i++ ) {
                System.out.println( "i = " + i );
                Window w = (Window)weakWindows.get( i );
                //Iconify windows so PhET applications stop using resources
                if( w instanceof JFrame ) {
                    JFrame f = (JFrame)w;
                    f.setState( JFrame.ICONIFIED );
                }
                if( w != null ) {
                    w.dispose();
                }
            }

            weakWindows.clear();

            // interrupt threads
            Thread threads[] = new Thread[ group.activeCount() * 2 ];
            int nthreads = group.enumerate( threads );
            for( int i = 0; i < nthreads; i++ ) {
                if( JNLPRuntime.isDebug() ) {
                    System.out.println( "Interrupt thread: " + threads[i] );
                }

                threads[i].interrupt();
            }

            // then stop
            Thread.currentThread().yield();
            nthreads = group.enumerate( threads );
            for( int i = 0; i < nthreads; i++ ) {
                if( JNLPRuntime.isDebug() ) {
                    System.out.println( "Stop thread: " + threads[i] );
                }
                //Don't destroy all threads, one could be the swing thread.
//                threads[i].stop();
            }

            // then destroy - except Thread.destroy() not implemented in jdk

        }
        finally {
            stopped = true;
            fireDestroyed();
        }
    }

    /**
     * Returns the thread group.
     *
     * @throws IllegalStateException if the app is not running
     */
    public ThreadGroup getThreadGroup() throws IllegalStateException {
        if( stopped ) {
            throw new IllegalStateException();
        }

        return group;
    }

    /**
     * Returns the classloader.
     *
     * @throws IllegalStateException if the app is not running
     */
    public ClassLoader getClassLoader() throws IllegalStateException {
        if( stopped ) {
            throw new IllegalStateException();
        }

        return loader;
    }

    /**
     * Adds a window that this application opened.  When the
     * application is disposed, these windows will also be disposed.
     */
    protected void addWindow( Window window ) {
        weakWindows.add( window );
        weakWindows.trimToSize();
    }

}

