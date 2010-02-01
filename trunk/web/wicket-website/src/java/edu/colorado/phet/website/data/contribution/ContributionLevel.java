package edu.colorado.phet.website.data.contribution;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.data.contribution.Level;

public class ContributionLevel implements Serializable {

    private int id;
    private String level;
    private Contribution contribution;

    private static Logger logger = Logger.getLogger( ContributionLevel.class.getName() );

    
    public ContributionLevel() {

    }

    public static List<Level> getCurrentLevels() {
        return Arrays.asList( Level.K_5, Level.MIDDLE_SCHOOL, Level.HIGH_SCHOOL, Level.UNDERGRADUATE_INTRO, Level.UNDERGRADUATE_ADVANCED, Level.GRADUATE, Level.OTHER );
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel( String level ) {
        this.level = level;
    }

    public Contribution getContribution() {
        return contribution;
    }

    public void setContribution( Contribution contribution ) {
        this.contribution = contribution;
    }
}
