package edu.colorado.phet.website.data;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;

import org.hibernate.Session;

import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.data.util.IntId;

/**
 * User account for the PhET website.
 */
public class PhetUser implements Serializable, IntId {

    private int id;
    private String email;
    private String hashedPassword;
    private boolean teamMember = false;
    private boolean newsletterOnlyAccount;
    private boolean confirmed = false;
    private Set translations = new HashSet();

    private String name;
    private String organization;

    private String description;
    private String jobTitle;

    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String zipcode;

    private String phone1;
    private String phone2;
    private String fax;

    private String confirmationKey; // key that is used for subscribing or unsubscribing to email. will be reset on each subscribe/unsubscribe operation

    private boolean receiveEmail = true; // for receiving newsletters
    private boolean receiveWebsiteNotifications = false; // for receiving internal (team-member) only notifications

    private static Random random = new Random(); // for computing things like the confirmation keys

    /**
     * @return An array of possible options for the 'description' field. Older descriptions may exist from legacy data.
     */
    public static List<String> getDescriptionOptions() {
        return Arrays.asList(
                "I am a teacher who uses PhET in my classes",
                "I am a teacher interested in using PhET in the future",
                "I am a student who uses PhET",
                "I am a student interested in using PhET in the future",
                "I am just interested in physics",
                "Other"
        );
    }

    /**
     * Lookup user from key, otherwise return null
     * <p/>
     * Assumes that it is within a transaction
     *
     * @param session
     * @param subscribeKey
     * @return
     */
    public static PhetUser getUserFromConfirmationKey( Session session, String confirmationKey ) {
        List list = session.createQuery( "select u from PhetUser as u where u.confirmationKey = :key" ).setString( "key", confirmationKey ).list();
        if ( list.size() == 0 ) {
            return null;
        }
        else if ( list.size() == 1 ) {
            return (PhetUser) list.get( 0 );
        }
        else {
            throw new RuntimeException( "Multiple users with same newsletter key" );
        }
    }

    /**
     * @return A new confirmation key that is suitable for setting a user's confirmationKey
     */
    public static synchronized String generateConfirmationKey() {
        return Long.toHexString( random.nextLong() ) + "-" + Long.toHexString( System.currentTimeMillis() );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        return o != null && o instanceof PhetUser && ( (PhetUser) o ).getId() == getId();
    }

    @Override
    public int hashCode() {
        return ( id * 475165 ) % 2567;
    }

    public static boolean isValidEmail( String email ) {
        return Pattern.matches( "^.+@.+\\.[a-z]+$", email );
    }

    public static String validateEmail( String email ) {
        if ( !isValidEmail( email ) ) {
            return "validation.email";
        }
        return null;
    }

    public void setPassword( String password ) {
        setHashedPassword( PhetSession.compatibleHashPassword( password ) );
    }

    // TODO: don't allow users with the same email address!

    public PhetUser() {

    }

    public PhetUser( String email, boolean newsletterOnly ) {
        setEmail( email );
        setConfirmationKey( generateConfirmationKey() );
        setNewsletterOnlyAccount( newsletterOnly );
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    /**
     * This returns a hashed copy of the password.
     *
     * @return
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword( String hashedPassword ) {
        this.hashedPassword = hashedPassword;
    }

    public boolean isTeamMember() {
        return teamMember;
    }

    public void setTeamMember( boolean teamMember ) {
        this.teamMember = teamMember;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed( boolean confirmed ) {
        this.confirmed = confirmed;
    }

    public boolean isNewsletterOnlyAccount() {
        return newsletterOnlyAccount;
    }

    public void setNewsletterOnlyAccount( boolean newsletterOnlyAccount ) {
        this.newsletterOnlyAccount = newsletterOnlyAccount;
    }

    public Set getTranslations() {
        return translations;
    }

    public void setTranslations( Set translations ) {
        this.translations = translations;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization( String organization ) {
        this.organization = organization;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle( String jobTitle ) {
        this.jobTitle = jobTitle;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1( String address1 ) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2( String address2 ) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity( String city ) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState( String state ) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry( String country ) {
        this.country = country;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode( String zipcode ) {
        this.zipcode = zipcode;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1( String phone1 ) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2( String phone2 ) {
        this.phone2 = phone2;
    }

    public String getFax() {
        return fax;
    }

    public void setFax( String fax ) {
        this.fax = fax;
    }

    public String getConfirmationKey() {
        return confirmationKey;
    }

    public void setConfirmationKey( String confirmationKey ) {
        this.confirmationKey = confirmationKey;
    }

    public boolean isReceiveEmail() {
        return receiveEmail;
    }

    public void setReceiveEmail( boolean receiveEmail ) {
        this.receiveEmail = receiveEmail;
    }

    public boolean isReceiveWebsiteNotifications() {
        return receiveWebsiteNotifications;
    }

    public void setReceiveWebsiteNotifications( boolean receiveWebsiteNotifications ) {
        this.receiveWebsiteNotifications = receiveWebsiteNotifications;
    }
}
