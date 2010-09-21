package edu.colorado.phet.website.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.data.util.IntId;

public class PhetUser implements Serializable, IntId {

    private int id;
    private String email;
    private String hashedPassword;
    private boolean teamMember;
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

    private boolean receiveEmail = true;
    private boolean receiveWebsiteNotifications = false;

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

    public static String validateEmail( String email ) {
        if ( !Pattern.matches( "^.+@.+\\.[a-z]+$", email ) ) {
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
