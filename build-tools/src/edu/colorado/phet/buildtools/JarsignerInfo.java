package edu.colorado.phet.buildtools;


public class JarsignerInfo {
    
    private final String keystore;
    private final String password;
    private final String alias;
    
    public JarsignerInfo( String keystore, String password, String alias ) {
        this.keystore = keystore;
        this.password = password;
        this.alias = alias;
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
}
