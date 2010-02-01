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
}