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

    public static Subject getSubjectFromOldAbbrev( String abbrev ) {
        if ( abbrev.equals( "ES" ) ) {
            return EARTH_SCIENCE;
        }
        else if ( abbrev.equals( "Bio" ) ) {
            return BIOLOGY;
        }
        else if ( abbrev.equals( "Chem" ) ) {
            return CHEMISTRY;
        }
        else if ( abbrev.equals( "Physics" ) ) {
            return PHYSICS;
        }
        else if ( abbrev.equals( "Astro" ) ) {
            return ASTRONOMY;
        }
        else if ( abbrev.equals( "Math" ) ) {
            return MATHEMATICS;
        }
        else if ( abbrev.equals( "Other" ) ) {
            return OTHER;
        }
        else {
            throw new RuntimeException( "Unknown abbreviation: " + abbrev );
        }
    }
}