package edu.colorado.phet.website.data;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class ContributionFile implements Serializable {

    private int id;

    // TODO: decide on implementation options, particularly how to avoid XSS vulnerabilities!

    private static Logger logger = Logger.getLogger( ContributionFile.class.getName() );

    public ContributionFile() {
    }
}