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
}