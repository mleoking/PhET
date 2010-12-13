package edu.colorado.phet.website.data.contribution;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.data.util.IntId;

public class ContributionType implements Serializable, IntId {

    private int id;
    private Type type;
    private Contribution contribution;

    private static final Logger logger = Logger.getLogger( ContributionType.class.getName() );


    public ContributionType() {

    }

    public static List<Type> getCurrentTypes() {
        return Arrays.asList( Type.LAB, Type.HOMEWORK, Type.CONCEPT_QUESTIONS, Type.DEMONSTRATION, Type.OTHER );
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public Contribution getContribution() {
        return contribution;
    }

    public void setContribution( Contribution contribution ) {
        this.contribution = contribution;
    }
}