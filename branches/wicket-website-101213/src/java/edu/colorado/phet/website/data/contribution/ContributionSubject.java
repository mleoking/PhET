package edu.colorado.phet.website.data.contribution;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.data.util.IntId;

public class ContributionSubject implements Serializable, IntId {

    private int id;
    private Subject subject;
    private Contribution contribution;

    private static final Logger logger = Logger.getLogger( ContributionSubject.class.getName() );


    public ContributionSubject() {

    }

    public static List<Subject> getCurrentSubjects() {
        return Arrays.asList( Subject.ASTRONOMY, Subject.BIOLOGY, Subject.CHEMISTRY, Subject.EARTH_SCIENCE, Subject.MATHEMATICS, Subject.PHYSICS, Subject.OTHER );
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject( Subject subject ) {
        this.subject = subject;
    }

    public Contribution getContribution() {
        return contribution;
    }

    public void setContribution( Contribution contribution ) {
        this.contribution = contribution;
    }
}