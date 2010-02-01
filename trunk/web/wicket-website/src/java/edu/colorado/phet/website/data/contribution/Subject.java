package edu.colorado.phet.website.data.contribution;

public enum Subject {
    ASTRONOMY( "astronomy" ),
    BIOLOGY( "biology" ),
    CHEMISTRY( "chemistry" ),
    EARTH_SCIENCE( "earthScience" ),
    MATHEMATICS( "mathematics" ),
    PHYSICS( "physics" ),
    OTHER( "other" );

    private String translationKey;

    Subject( String translationKey ) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return "contribution.subject." + translationKey;
    }

    public String getAbbreviatedTranslationKey() {
        return getTranslationKey() + ".abbrev";
    }
}