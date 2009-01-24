package edu.colorado.phet.acidbasesolutions.model.acids;


public class HypochlorousAcid implements IWeakAcid {

    public String getAcidName() {
        return "<html>HClO</html>";
    }

    public String getConjugateBaseName() {
        return "<html>HClO<sup>-</sup></html>";
    }
    
    public double getStrength() {
        return 2.9E-8;
    }
}
