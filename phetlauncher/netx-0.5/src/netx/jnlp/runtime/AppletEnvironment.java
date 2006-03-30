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

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.net.*;
import java.io.*;
import javax.swing.*;
import netx.jnlp.*;
import netx.jnlp.util.*;

/**
 * The applet environment including stub, context, and frame.  The
 * default environment puts the applet in a non-resiable frame;
 * this can be changed by obtaining the frame and setting it
 * resizable.
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$ 
 */
public class AppletEnvironment implements AppletContext, AppletStub {

    /** the JNLP file */
    private JNLPFile file;

    /** the applet instance */
    private AppletInstance appletInstance;

    /** the applet */
    private Applet applet;

    /** the parameters */
    private Map parameters;

    /** the applet frame */
    private Frame frame;

    /** weak references to the audio clips */
    private WeakList weakClips = new WeakList();

    /** whether the applet has been started / displayed */
    private boolean appletStarted = false;

    /** whether the applet has been destroyed */
    private boolean destroyed = false;


    /**
     * Create a new applet environment for the applet specified by
     * the JNLP file.
     */
    public AppletEnvironment(JNLPFile file, final AppletInstance appletInstance) {
        this.file = file;
        this.appletInstance = appletInstance;
        this.applet = appletInstance.getApplet();

        parameters = file.getApplet().getParameters();
        frame = new Frame(file.getApplet().getName() + " - Applet");
        frame.setResizable(false);

        appletInstance.addWindow(frame);

        // may not need this once security manager can close windows
        // that do not have app code on the stack
        WindowListener closer = new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                appletInstance.destroy();
                System.exit(0);
            }
        };
        frame.addWindowListener(closer);
    }

    /**
     * Checks whether the applet has been destroyed, and throws an
     * IllegalStateException if the applet has been destroyed of.
     *
     * @throws IllegalStateException
     */
    private void checkDestroyed() {
        if (destroyed)
            throw new IllegalStateException("Illegal applet stub/context access: applet destroyed.");
    }

    /**
     * Disposes the applet's resources and disables the applet
     * environment from further use; after calling this method the
     * applet stub and context methods throw IllegalStateExceptions.
     */
    public void destroy() {
        destroyed = true;

        List clips = weakClips.hardList();
        for (int i = 0; i < clips.size(); i++) {
            ((AppletAudioClip)clips.get(i)).dispose();
        }
    }

    /**
     * Returns the frame that contains the applet.  Disposing this
     * frame will destroy the applet.
     */
    public Frame getAppletFrame() {
        return frame;
    }

    /**
     * Initialize, start, and show the applet.
     */
    public void startApplet() {
        checkDestroyed();

        if (appletStarted)
            return;

        appletStarted = true;

        try {
            AppletDesc appletDesc = file.getApplet();

            applet.setStub(this);

            frame.setLayout(new BorderLayout());
            frame.add(applet);
            frame.validate();
            frame.pack(); // cause insets to be calculated

            Insets insets = frame.getInsets();
            frame.setSize(appletDesc.getWidth() + insets.left + insets.right,
                          appletDesc.getHeight() + insets.top + insets.bottom);

            // do first because some applets need to be displayed before
            // starting (they use Component.getImage or something)
            frame.show();

            applet.init();
            applet.start();

            frame.invalidate(); // this should force the applet to
            frame.validate();   // the correct size and to repaint
            frame.repaint();
        }
        catch (Exception ex) {
            if (JNLPRuntime.isDebug())
                ex.printStackTrace();

            // should also kill the applet?
        }
    }

    // applet context methods 

    /**
     * Returns the applet if the applet's name is specified,
     * otherwise return null.
     */
    public Applet getApplet(String name) {
        checkDestroyed();

        if (name != null && name.equals(file.getApplet().getName()))
            return applet;
        else
            return null;
    }

    /**
     * Returns an enumeration that contains only the applet
     * from the JNLP file.
     */
    public Enumeration getApplets() {
        checkDestroyed();

        return Collections.enumeration( Arrays.asList(new Applet[] { applet }) );
    }

    /**
     * Returns an audio clip.
     */
    public AudioClip getAudioClip(URL location) {
        checkDestroyed();

        AppletAudioClip clip = new AppletAudioClip(location);

        weakClips.add(clip);
        weakClips.trimToSize();

        return clip;
    }

    /**
     * Return an image loaded from the specified location.
     */
    public Image getImage(URL location) {
        checkDestroyed();

        //return Toolkit.getDefaultToolkit().createImage(location);
        Image image = (new ImageIcon(location)).getImage();

        return image;
    }

    /**
     * Not implemented yet.
     */
    public void showDocument(java.net.URL uRL) {
        checkDestroyed();

    }

    /**
     * Not implemented yet.
     */
    public void showDocument(java.net.URL uRL, java.lang.String str) {
        checkDestroyed();

    }

    /**
     * Not implemented yet.
     */
    public void showStatus(java.lang.String str) {
        checkDestroyed();

    }

    /**
     * Required for JRE1.4, but not implemented yet.
     */
    public void setStream(String key, InputStream stream) {
        checkDestroyed();

    }

    /**
     * Required for JRE1.4, but not implemented yet.
     */
    public InputStream getStream(String key) {
        checkDestroyed();

        return null;
    }

    /**
     * Required for JRE1.4, but not implemented yet.
     */
    public Iterator getStreamKeys()  {
        checkDestroyed();

        return null;
    }

    // stub methods

    public void appletResize(int width, int height) {
        checkDestroyed();

        Insets insets = frame.getInsets();

        frame.setSize(width + insets.left + insets.right,
                      height + insets.top + insets.bottom);
    }

    public AppletContext getAppletContext() {
        checkDestroyed();

        return this;
    }

    public URL getCodeBase() {
        checkDestroyed();

        return file.getCodeBase();
    }

    public URL getDocumentBase() {
        checkDestroyed();

        return file.getApplet().getDocumentBase();
    }

    public String getParameter(String name) {
        checkDestroyed();

        return (String) parameters.get(name);
    }

    public boolean isActive() {
        checkDestroyed();

        // it won't be started or stopped, so if it can call it's running
        return true;
    }


}
