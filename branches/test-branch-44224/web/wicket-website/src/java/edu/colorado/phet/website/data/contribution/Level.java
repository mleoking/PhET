package edu.colorado.phet.website.data.contribution;

public enum Level {
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

    public static Level getLevelFromOldAbbrev( String abbrev ) {
        if ( abbrev.equals( "Grad" ) ) {
            return GRADUATE;
        }
        else if ( abbrev.equals( "Other" ) ) {
            return OTHER;
        }
        else if ( abbrev.equals( "MS" ) ) {
            return MIDDLE_SCHOOL;
        }
        else if ( abbrev.equals( "HS" ) ) {
            return HIGH_SCHOOL;
        }
        else if ( abbrev.equals( "UG-Intro" ) ) {
            return UNDERGRADUATE_INTRO;
        }
        else if ( abbrev.equals( "UG-Adv" ) ) {
            return UNDERGRADUATE_ADVANCED;
        }
        else if ( abbrev.equals( "K-5" ) ) {
            return K_5;
        }
        else {
            throw new RuntimeException( "Unknown abbreviation: " + abbrev );
        }
    }
}