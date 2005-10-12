/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.persistence;

import java.awt.Color;
import java.io.Serializable;


/**
 * FourierConfig describes a configuration of the Fourier simulation.
 * It encapsulates all of the settings that the user can change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierConfig implements Serializable {

    private GlobalConfig globalConfig;
    
    private DiscreteConfig discreteConfig;
    private D2CConfig d2cConfig;
    private GameConfig gameConfig;
    
    //----------------------------------------------------------------------------
    // Application-level configuration
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public FourierConfig() {
        globalConfig = new GlobalConfig();
        discreteConfig = new DiscreteConfig();
        d2cConfig = new D2CConfig();
        gameConfig = new GameConfig();
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }
    
    public void setGlobalConfig( GlobalConfig globalConfig ) {
        this.globalConfig = globalConfig;
    }
    
    public DiscreteConfig getDiscreteConfig() {
        return discreteConfig;
    }
    
    public void setDiscreteConfig( DiscreteConfig discreteConfig ) {
        this.discreteConfig = discreteConfig;
    }
    
    public D2CConfig getD2CConfig() {
        return d2cConfig;
    }
    
    public void setD2CConfig( D2CConfig d2cConfig ) {
        this.d2cConfig = d2cConfig;
    }
    
    public GameConfig getGameConfig() {
        return gameConfig;
    }
    
    public void setGameConfig( GameConfig gameConfig ) {
        this.gameConfig = gameConfig;
    }    

    //----------------------------------------------------------------------------
    // Global-level configuration, applies to all modules
    //----------------------------------------------------------------------------
    
    public class GlobalConfig implements Serializable {
        
        private String versionNumber;
        private String buildNumber;
        private Color[] harmonicColors;
        
        /**
         * Zero-argument constructor for Java Bean compliance.
         */
        public GlobalConfig() {}
        
        public String getBuildNumber() {
            return buildNumber;
        }
        
        public void setBuildNumber( String buildNumber ) {
            this.buildNumber = buildNumber;
        }
        
        public String getVersionNumber() {
            return versionNumber;
        }
        
        public void setVersionNumber( String versionNumber ) {
            this.versionNumber = versionNumber;
        }
        
        public Color[] getHarmonicColors() {
            return harmonicColors;
        }
        
        public void setHarmonicColors( Color[] harmonicColors ) {
            this.harmonicColors = harmonicColors;
        }
    }
    
    //----------------------------------------------------------------------------
    // "Discrete" module configuration
    //----------------------------------------------------------------------------
    
    public class DiscreteConfig implements Serializable {
        
        // Configuration parameters
        private String presetName;
        private int numberOfHarmonics;
        private boolean showInfiniteEnabled;
        private String domainName;
        private String waveTypeName;
        private boolean wavelengthToolEnabled;
        private boolean periodToolEnabled;
        private boolean showMathEnabled;
        private String mathFormName;
        private boolean expandSumEnabled;
        private boolean soundEnabled;
        private float soundVolume;
        private double[] _amplitudes;
        
        /**
         * Zero-argument constructor for Java Bean compliance.
         */
        public DiscreteConfig() {}
        
        public String getDomainName() {
            return domainName;
        }
        
        public void setDomainName( String domainName ) {
            this.domainName = domainName;
        }
        
        public boolean isExpandSumEnabled() {
            return expandSumEnabled;
        }
        
        public void setExpandSumEnabled( boolean expandSum ) {
            this.expandSumEnabled = expandSum;
        }
        
        public String getMathFormName() {
            return mathFormName;
        }
        
        public void setMathFormName( String mathFormName ) {
            this.mathFormName = mathFormName;
        }
        
        public int getNumberOfHarmonics() {
            return numberOfHarmonics;
        }
        
        public void setNumberOfHarmonics( int numberOfHarmonics ) {
            this.numberOfHarmonics = numberOfHarmonics;
        }
        
        public String getPresetName() {
            return presetName;
        }
        
        public void setPresetName( String presetName ) {
            this.presetName = presetName;
        }
        
        public boolean isShowInfiniteEnabled() {
            return showInfiniteEnabled;
        }
        
        public void setShowInfiniteEnabled( boolean showInfiniteNumberOfHarmonics ) {
            this.showInfiniteEnabled = showInfiniteNumberOfHarmonics;
        }
        
        public boolean isShowMathEnabled() {
            return showMathEnabled;
        }
        
        public void setShowMathEnabled( boolean showMath ) {
            this.showMathEnabled = showMath;
        }
        
        public boolean isPeriodToolEnabled() {
            return periodToolEnabled;
        }
        
        public void setPeriodToolEnabled( boolean showPeriodTool ) {
            this.periodToolEnabled = showPeriodTool;
        }
        
        public boolean isWavelengthToolEnabled() {
            return wavelengthToolEnabled;
        }
        
        public void setWavelengthToolEnabled( boolean showWavelengthTool ) {
            this.wavelengthToolEnabled = showWavelengthTool;
        }
        
        public boolean isSoundEnabled() {
            return soundEnabled;
        }
        
        public void setSoundEnabled( boolean soundEnabled ) {
            this.soundEnabled = soundEnabled;
        }
        
        public float getSoundVolume() {
            return soundVolume;
        }
        
        public void setSoundVolume( float volume ) {
            this.soundVolume = volume;
        }
        
        public String getWaveTypeName() {
            return waveTypeName;
        }
        
        public void setWaveTypeName( String waveTypeName ) {
            this.waveTypeName = waveTypeName;
        }
        
        public void setAmplitudes( double[] amplitudes ) {
            _amplitudes = amplitudes;
        }
        
        public double[] getAmplitudes() {
            return _amplitudes;
        }
    }
    
    //----------------------------------------------------------------------------
    // "Discrete to Continous" (D2C) module configuration
    //----------------------------------------------------------------------------
    
    public class D2CConfig implements Serializable { 
        
        // Configuration parameters
        private double spacing;
        private boolean amplitudesEnvelopeEnabled;
        private double center;
        private double kWidth;
        private String domainName;
        private String waveTypeName;
        private boolean sumEnvelopeEnabled;
        private boolean showWidthsEnabled;
        
        /**
         * Zero-argument constructor for Java Bean compliance.
         */
        public D2CConfig() {}
        
        public double getCenter() {
            return center;
        }
        
        public void setCenter( double center ) {
            this.center = center;
        }
        
        public String getDomainName() {
            return domainName;
        }
        
        public void setDomainName( String domainName ) {
            this.domainName = domainName;
        }
        
        public double getKWidth() {
            return kWidth;
        }
        
        public void setKWidth( double width ) {
            kWidth = width;
        }
        
        public boolean isAmplitudesEnvelopeEnabled() {
            return amplitudesEnvelopeEnabled;
        }
        
        public void setAmplitudesEnvelopeEnabled( boolean showContinuousWaveform ) {
            this.amplitudesEnvelopeEnabled = showContinuousWaveform;
        }
        
        public boolean isShowWidthsEnabled() {
            return showWidthsEnabled;
        }
        
        public void setShowWidthsEnabled( boolean showWidthIndicators ) {
            this.showWidthsEnabled = showWidthIndicators;
        }
        
        public boolean isSumEnvelopeEnabled() {
            return sumEnvelopeEnabled;
        }
        
        public void setSumEnvelopeEnabled( boolean showXEnvelope ) {
            this.sumEnvelopeEnabled = showXEnvelope;
        }
        
        public double getSpacing() {
            return spacing;
        }
        
        public void setSpacing( double spacing ) {
            this.spacing = spacing;
        }
        
        public String getWaveTypeName() {
            return waveTypeName;
        }
        
        public void setWaveTypeName( String waveTypeName ) {
            this.waveTypeName = waveTypeName;
        }
    }
    
    //----------------------------------------------------------------------------
    // "Game" module configuration
    //----------------------------------------------------------------------------
    
    public class GameConfig implements Serializable {
        
        // Configuration parameters
        private String gameLevelName;
        private String presetName;

        /**
         * Zero-argument constructor for Java Bean compliance.
         */
        public GameConfig() {}

        public String getGameLevelName() {
            return gameLevelName;
        }

        public void setGameLevelName( String gameLevelName ) {
            this.gameLevelName = gameLevelName;
        }

        public String getPresetName() {
            return presetName;
        }

        public void setPresetName( String presetName ) {
            this.presetName = presetName;
        }
    }
}
