package edu.colorado.phet.website.data.contribution;

public enum Type {
    LAB( "lab" ),
    HOMEWORK( "homework" ),
    CONCEPT_QUESTIONS( "conceptQuestions" ),
    DEMONSTRATION( "demonstration" ),
    OTHER( "other" );

    private String translationKey;

    Type( String translationKey ) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return "contribution.type." + translationKey;
    }

    public String getAbbreviatedTranslationKey() {
        return getTranslationKey() + ".abbrev";
    }

    public static Type getTypeFromOldAbbrev( String abbrev ) {
        if ( abbrev.equals( "HW" ) ) {
            return HOMEWORK;
        }
        else if ( abbrev.equals( "Demo" ) ) {
            return DEMONSTRATION;
        }
        else if ( abbrev.equals( "CQs" ) ) {
            return CONCEPT_QUESTIONS;
        }
        else if ( abbrev.equals( "Lab" ) ) {
            return LAB;
        }
        else if ( abbrev.equals( "Other" ) ) {
            return OTHER;
        }
        else if ( abbrev.equals( "A" ) ) {
            // there is no "activity" type, this appears to be bad data
            return OTHER;
        }
        else {
            throw new RuntimeException( "Unknown abbreviation: " + abbrev );
        }
    }
}