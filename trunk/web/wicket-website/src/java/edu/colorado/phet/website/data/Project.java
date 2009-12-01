package edu.colorado.phet.website.data;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.ProjectPropertiesFile;
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

            String name = element.getAttribute( "name" );
            String locale = element.getAttribute( "locale" );
            String title = ( (Element) ( element.getElementsByTagName( "title" ).item( 0 ) ) ).getChildNodes().item( 0 ).getNodeValue();

            System.out.println( "  " + name + " " + locale + " " + title );
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
