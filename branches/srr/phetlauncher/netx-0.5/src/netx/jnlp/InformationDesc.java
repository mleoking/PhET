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


package netx.jnlp;

import java.io.*;
import java.awt.Dimension;
import java.net.*;
import java.util.*;

/**
 * The information element.<p>
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision$
 */
public class InformationDesc {

    // There is an understanding between this class and the parser
    // that description and icon types are keyed by "icon-"+kind and
    // "description-"+kind, and that other types are keyed by their
    // specification name.

    /** one-line description */
    public static final Object ONE_LINE= "oneline";

    /** short description */
    public static final Object SHORT= "short";

    /** tooltip description */
    public static final Object TOOLTIP= "tooltip";

    /** default description */
    public static final Object DEFAULT = "default";

    /** the locales for the information */
    private Locale locales[];

    /** the data as list of key,value pairs */
    private List info;

    /** the JNLPFile this information is for */
    private JNLPFile jnlpFile;


    /**
     * Create an information element object.
     *
     * @param jnlpFile file that the information is for
     * @param locale the the information is for
     */
    public InformationDesc(JNLPFile jnlpFile, Locale locales[]) {
        this.jnlpFile = jnlpFile;
        this.locales = locales;
    }

    /**
     * Returns the application's title.
     */
    public String getTitle() {
        return (String) getItem("title");
    }

    /**
     * Returns the application's vendor.
     */
    public String getVendor() {
        return (String) getItem("vendor");
    }

    /**
     * Returns the application's homepage.
     */
    public URL getHomepage() {
        return (URL)getItem("homepage");
    }

    /**
     * Returns the default description for the application.
     */
    public String getDescription() {
        String result = getDescription(DEFAULT);

        // try to find any description if default is null
        if (result == null)
            result = getDescription(ONE_LINE);
        if (result == null)
            result = getDescription(SHORT);
        if (result == null)
            result = getDescription(TOOLTIP);

        return result;
    }

    /**
     * Returns the application's description of the specified type.
     *
     * @param kind one of Information.SHORT, Information.ONE_LINE,
     * Information.TOOLTIP, Information.DEFAULT
     */
    public String getDescription(Object kind) {
        String result = (String) getItem("description-"+kind);
        if (result == null)
            return (String) getItem("description-"+DEFAULT);
        else
            return result;
    }

    /**
     * Returns the icons specified by the JNLP file.
     *
     * @param kind one of IconDesc.SELECTED, IconDesc.DISABLED,
     * IconDesc.ROLLOVER, IconDesc.SPLASH, IconDesc.DEFAULT
     * @return an array of zero of more IconDescs of the specified icon type
     */
    public IconDesc[] getIcons(Object kind) {
        List icons = getItems("icon-"+kind);

        return (IconDesc[]) icons.toArray(new IconDesc[icons.size()]);
    };

    /**
     * Returns the URL of the icon closest to the specified size and
     * kind.  This method will not return an icon smaller than the
     * specified width and height unless there are no other icons
     * available.
     *
     * @param kind the kind of icon to get
     * @param width desired width of icon
     * @param height desired height of icon
     * @return the closest icon by size or null if no icons declared
     */
    public URL getIconLocation(Object kind, int width, int height) {
        IconDesc icons[] = getIcons(kind);
        if (icons.length == 0)
            return null;

        IconDesc best = null;
        for (int i=0; i < icons.length; i++) {
            if (icons[i].getWidth() >= width &&
                icons[i].getHeight() >= height) {
                if (best == null)
                    best = icons[i];

                if (icons[i].getWidth() <= best.getWidth() && // Use <= so last specified of
                    icons[i].getHeight() <= best.getHeight()) // equivalent icons is chosen.
                    best = icons[i];
            }
        }

        if (best == null)
            best = icons[0];

        return best.getLocation();
    }

    /**
     * Returns the locales for the information.
     */
    public Locale[] getLocales() {
        return locales;
    }

    /**
     * Returns the JNLPFile the information is for.
     */
    public JNLPFile getJNLPFile() {
        return jnlpFile;
    }

    /**
     * Returns whether offline execution allowed.
     */
    public boolean isOfflineAllowed() {
        return null != getItem("offline-allowed");
    }

    /**
     * Returns whether the resources specified in the JNLP file may
     * be shared by more than one instance in the same JVM
     * (JNLP extension).  This is an extension to the JNLP spec and
     * will always return false for standard JNLP files.
     */
    public boolean isSharingAllowed() {
        return null != getItem("sharing-allowed");
    }

    /**
     * Returns the last item matching the specified key.
     */
    protected Object getItem(Object key) {
        List items = getItems(key);
        if (items.size() == 0)
            return null;
        else
            return items.get( items.size()-1 );
    }

    /**
     * Returns all items matching the specified key.
     */
    protected List getItems(Object key) {
        if (info == null)
            return Collections.EMPTY_LIST;

        List result = new ArrayList();
        for (int i=0; i < info.size(); i+=2)
            if (info.get(i).equals(key))
                result.add( info.get(i+1) );

        return result;
    }

    /**
     * Add an information item (description, icon, etc) under a
     * specified key name.
     */
    protected void addItem(String key, Object value) {
        if (info == null)
            info = new ArrayList();

        info.add(key);
        info.add(value);
    }

}

