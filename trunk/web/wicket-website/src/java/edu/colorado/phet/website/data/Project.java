package edu.colorado.phet.website.data;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.ProjectPropertiesFile;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.flashlauncher.util.XMLUtils;

public class Project implements Serializable {

    private int id;
    private String name;
    private int versionMajor;
    private int versionMinor;
    private int versionDev;
    private int versionRevision;
    private long versionTimestamp;
    private boolean visible;

    private Set simulations = new HashSet();

    public Project() {
    }

    public String getVersionString() {
        String ret = "";
        ret += getVersionMajor();
        ret += ".";
        if ( getVersionMinor() < 10 && getVersionMinor() >= 0 ) {
            ret += "0";
        }
        ret += getVersionMinor();
        if ( getVersionDev() != 0 ) {
            ret += ".";
            if ( getVersionDev() < 10 && getVersionDev() >= 0 ) {
                ret += "0";
            }
            ret += getVersionDev();
        }
        return ret;
    }

    public ProjectPropertiesFile getProjectPropertiesFile( File docRoot ) {
        return new ProjectPropertiesFile( new File( docRoot, "sims/" + name + "/" + name + ".properties" ) );
    }

    public File getProjectRoot( File docRoot ) {
        return new File( docRoot, "sims/" + name );
    }

    private File getSimulationJARFile( File docRoot, String simulationName, Locale locale ) {
        return new File( getProjectRoot( docRoot ), simulationName + "_" + LocaleUtils.localeToString( locale ) + ".jar" );
    }

    private void appendWarning( StringBuilder builder, String message ) {
        builder.append( "<br/><font color='#FF0000'>WARNING: " + message + "</font>" );
    }

    /**
     * Returns a string with information about project consistency between the database representation and the files in
     * the deployment directory
     * <p/>
     * NOTE: should be persistent when this is called.
     *
     * @param docRoot Document root file
     * @return Information string
     */
    public String consistencyCheck( File docRoot ) {
        StringBuilder builder = new StringBuilder();
        builder.append( "project: <font color='#0000FF'>" + name + "</font>" );

        File projectRoot = new File( docRoot, "sims/" + name );
        ProjectPropertiesFile projectProperties = getProjectPropertiesFile( docRoot );

        boolean warning = false;

        if ( projectProperties.exists() ) {
            if ( projectProperties.getMajorVersion() != versionMajor ) {
                appendWarning( builder, "Major version mismatch! properties: " + projectProperties.getMajorVersion() + " db: " + versionMajor );
                warning = true;
            }
            if ( projectProperties.getMinorVersion() != versionMinor ) {
                appendWarning( builder, "Minor version mismatch! properties: " + projectProperties.getMinorVersion() + " db: " + versionMinor );
                warning = true;
            }
            if ( projectProperties.getDevVersion() != versionDev ) {
                appendWarning( builder, "Dev version mismatch! properties: " + projectProperties.getDevVersion() + " db: " + versionDev );
                warning = true;
            }
            if ( projectProperties.getSVNVersion() != versionRevision ) {
                appendWarning( builder, "revision mismatch! properties: " + projectProperties.getSVNVersion() + " db: " + versionRevision );
                warning = true;
            }
            if ( projectProperties.getVersionTimestamp() != versionTimestamp ) {
                appendWarning( builder, "Timestamp version mismatch! properties: " + projectProperties.getVersionTimestamp() + " db: " + versionTimestamp );
                warning = true;
            }

            try {
                Document document = XMLUtils.toDocument( FileUtils.loadFileAsString( new File( projectRoot, name + ".xml" ) ) );

                NodeList simulations = document.getElementsByTagName( "simulation" );

                Set<Simulation> usedSims = new HashSet<Simulation>();
                Set<LocalizedSimulation> usedLSims = new HashSet<LocalizedSimulation>();

                for ( int i = 0; i < simulations.getLength(); i++ ) {
                    Element element = (Element) simulations.item( i );

                    String simName = element.getAttribute( "name" );
                    String simLocaleString = element.getAttribute( "locale" );
                    String simTitle = ( (Element) ( element.getElementsByTagName( "title" ).item( 0 ) ) ).getChildNodes().item( 0 ).getNodeValue();

                    Locale simLocale = LocaleUtils.stringToLocale( simLocaleString );

                    File simulationJAR = getSimulationJARFile( docRoot, simName, simLocale );
                    if ( !simulationJAR.exists() ) {
                        builder.append( "<br/>" + simulationJAR.getName() + " does not exist" );
                        continue;
                    }

                    Simulation sim = null;
                    LocalizedSimulation lsim = null;

                    for ( Object o : getSimulations() ) {
                        Simulation so = (Simulation) o;
                        if ( so.getName().equals( simName ) ) {
                            sim = so;
                            usedSims.add( so );
                            break;
                        }
                    }

                    if ( sim != null ) {
                        for ( Object o : sim.getLocalizedSimulations() ) {
                            LocalizedSimulation lo = (LocalizedSimulation) o;
                            if ( lo.getLocale().equals( simLocale ) ) {
                                lsim = lo;
                                usedLSims.add( lo );
                                break;
                            }
                        }

                        if ( lsim != null ) {
                            if ( !lsim.getTitle().equals( simTitle ) ) {
                                appendWarning( builder, "Sim title changed? (" + simName + ", " + simLocaleString + ") xml: " + simTitle + " db: " + lsim.getTitle() );
                                warning = true;
                            }
                        }
                        else {
                            appendWarning( builder, "Could not find translation " + simLocaleString + " for simulation " + simName );
                            warning = true;
                        }
                    }
                    else {
                        appendWarning( builder, "Could not find simulation " + simName + " in the DB" );
                        warning = true;
                    }
                }

                for ( Object o : getSimulations() ) {
                    Simulation sim = (Simulation) o;
                    if ( !usedSims.contains( sim ) ) {
                        appendWarning( builder, "Found sim " + sim.getName() + " in database, but did not find in project directory" );
                        warning = true;
                    }
                    else {
                        for ( Object o1 : sim.getLocalizedSimulations() ) {
                            LocalizedSimulation lsim = (LocalizedSimulation) o1;
                            if ( !usedLSims.contains( lsim ) ) {
                                appendWarning( builder, "Found translation " + lsim.getLocaleString() + " for sim " + sim.getName() + " in database, but did not find in project directory" );
                                warning = true;
                            }
                        }
                    }
                }
            }
            catch( Exception e ) {
                e.printStackTrace();
                appendWarning( builder, "Error matching XML and simulations" );
                warning = true;
            }
        }
        else {
            appendWarning( builder, "Could not find project properties file" );
            warning = true;
        }

        for ( Object o : getSimulations() ) {
            Simulation sim = (Simulation) o;
            int detected = sim.detectSimKilobytes( docRoot );
            if ( sim.getKilobytes() != detected ) {
                appendWarning( builder, "Sim " + sim.getName() + " kilobytes inaccurate. file: " + detected + " db: " + sim.getKilobytes() );
                warning = true;
            }
        }

        if ( !warning ) {
            builder.append( " <font color='#00FF00'>OK</font><br/>" );
        }
        else {
            builder.append( "<br/>" );
        }

        return builder.toString();
    }

    public void debugProjectFiles( File docRoot ) throws IOException, TransformerException, ParserConfigurationException {
        File projectRoot = new File( docRoot, "sims/" + name );

        ProjectPropertiesFile projectProperties = new ProjectPropertiesFile( new File( projectRoot, name + ".properties" ) );

        System.out.println( "project: " + name );
        System.out.println( "major: " + projectProperties.getMajorVersion() );
        System.out.println( "minor: " + projectProperties.getMinorVersion() );
        System.out.println( "dev: " + projectProperties.getDevVersion() );
        System.out.println( "revision: " + projectProperties.getSVNVersion() );
        System.out.println( "timestamp: " + projectProperties.getVersionTimestamp() );

        Document document = XMLUtils.toDocument( FileUtils.loadFileAsString( new File( projectRoot, name + ".xml" ) ) );

        NodeList simulations = document.getElementsByTagName( "simulation" );

        for ( int i = 0; i < simulations.getLength(); i++ ) {
            Element element = (Element) simulations.item( i );

            String simName = element.getAttribute( "name" );
            String simLocaleString = element.getAttribute( "locale" );
            String simTitle = ( (Element) ( element.getElementsByTagName( "title" ).item( 0 ) ) ).getChildNodes().item( 0 ).getNodeValue();

            System.out.println( "  " + simName + " " + simLocaleString + " " + simTitle );
        }

    }

    // getters and setters

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public int getVersionMajor() {
        return versionMajor;
    }

    public void setVersionMajor( int versionMajor ) {
        this.versionMajor = versionMajor;
    }

    public int getVersionMinor() {
        return versionMinor;
    }

    public void setVersionMinor( int versionMinor ) {
        this.versionMinor = versionMinor;
    }

    public int getVersionDev() {
        return versionDev;
    }

    public void setVersionDev( int versionDev ) {
        this.versionDev = versionDev;
    }

    public int getVersionRevision() {
        return versionRevision;
    }

    public void setVersionRevision( int versionRevision ) {
        this.versionRevision = versionRevision;
    }

    public long getVersionTimestamp() {
        return versionTimestamp;
    }

    public void setVersionTimestamp( long versionTimestamp ) {
        this.versionTimestamp = versionTimestamp;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public Set getSimulations() {
        return simulations;
    }

    public void setSimulations( Set simulations ) {
        this.simulations = simulations;
    }
}
