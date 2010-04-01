package edu.colorado.phet.website.data;

import java.io.Serializable;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.data.util.IntId;

public class LocalizedSimulation implements Serializable, IntId {
    private int id;
    private Locale locale;
    private String title;
    private Simulation simulation;

    public LocalizedSimulation() {
    }

    public PhetLink getRunLink( String id ) {
        return new PhetLink( id, getRunUrl() );
    }

    public PhetLink getDownloadLink( String id ) {
        return new PhetLink( id, getDownloadUrl() );
    }

    public String getRunUrl() {
        Simulation sim = getSimulation();
        Project project = sim.getProject();
        String str = "/sims/" + project.getName() + "/" + sim.getName() + "_" + getLocaleString();
        if ( sim.isJava() ) {
            str += ".jnlp";
        }
        else if ( sim.isFlash() ) {
            str += ".html";
        }
        else {
            throw new RuntimeException( "Handle more than java and flash" );
        }
        return str;
    }

    public String getDownloadUrl() {
        Simulation sim = getSimulation();
        Project project = sim.getProject();
        String str = "/sims/" + project.getName() + "/" + sim.getName() + "_" + getLocaleString();
        if ( sim.isJava() || sim.isFlash() ) {
            str += ".jar";
        }
        else {
            throw new RuntimeException( "Handle more than java and flash" );
        }
        return str;
    }

    public String getLocaleString() {
        return LocaleUtils.localeToString( getLocale() );
    }

    public String getSortableTitle() {
        String ret = getTitle();
        for ( String ignoreWord : HibernateUtils.SIM_TITLE_IGNORE_WORDS ) {
            if ( ret.startsWith( ignoreWord + " " ) ) {
                ret = ret.substring( ignoreWord.length() + 1 );
            }
        }
        return ret;
    }

    // getters and setters

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale( Locale locale ) {
        this.locale = locale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation( Simulation simulation ) {
        this.simulation = simulation;
    }
}
