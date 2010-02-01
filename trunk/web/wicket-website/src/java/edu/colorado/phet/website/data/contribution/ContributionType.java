package edu.colorado.phet.website.data.contribution;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class ContributionType implements Serializable {

    private int id;
    private String type;
    private Contribution contribution;

    private static Logger logger = Logger.getLogger( ContributionType.class.getName() );


    public ContributionType() {

    }

    public static List<Type> getCurrentLevels() {
        return Arrays.asList( Type.LAB, Type.HOMEWORK, Type.CONCEPT_QUESTIONS, Type.DEMONSTRATION, Type.OTHER );
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public Contribution getContribution() {
        return contribution;
    }

    public void setContribution( Contribution contribution ) {
        this.contribution = contribution;
    }
}