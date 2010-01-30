package edu.colorado.phet.website.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class ContributionLevel implements Serializable {

    private int id;
    private Level level;
    private Contribution contribution;

    private static Logger logger = Logger.getLogger( ContributionLevel.class.getName() );

    public ContributionLevel() {

    }

    public static List<Level> getCurrentLevels() {
        return Arrays.asList( Level.K_5, Level.MIDDLE_SCHOOL, Level.HIGH_SCHOOL, Level.UNDERGRADUATE_INTRO, Level.UNDERGRADUATE_ADVANCED, Level.GRADUATE, Level.OTHER );
    }

    public static enum Level {
        K_5( "k5" ),
        MIDDLE_SCHOOL( "middleSchool" ),
        HIGH_SCHOOL( "highSchool" ),
        UNDERGRADUATE_INTRO( "undergraduateIntro" ),
        UNDERGRADUATE_ADVANCED( "undergraduateAdvanced" ),
        GRADUATE( "graduate" ),
        OTHER( "other" );

        private String translationKey;

        Level( String translationKey ) {
            this.translationKey = translationKey;
        }

        public String getTranslationKey() {
            return "contribution.level." + translationKey;
        }

        public String getAbbreviatedTranslationKey() {
            return getTranslationKey() + ".abbrev";
        }
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel( Level level ) {
        this.level = level;
    }

    public Contribution getContribution() {
        return contribution;
    }

    public void setContribution( Contribution contribution ) {
        this.contribution = contribution;
    }
}
