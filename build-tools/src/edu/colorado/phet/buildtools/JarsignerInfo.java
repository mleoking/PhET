package edu.colorado.phet.buildtools;


/**
 * This class contains the information needed in order to sign JAR files.
 */
public class JarsignerInfo {
    
    private final String keystore;
    private final String password;
    private final String alias;
    private final String tsaUrl;
    
    public JarsignerInfo( String keystore, String password, String alias, String tsaUrl ) {
        this.keystore = keystore;
        this.password = password;
        this.alias = alias;
        this.tsaUrl = tsaUrl;
    }
    
    public String getKeystore() {
        return keystore;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getAlias() {
        return alias;
    }

    public String getTsaUrl() {
        return tsaUrl;
    }
}
