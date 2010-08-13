package edu.colorado.phet.densityflex {
public class DensityConstants {
    public static const MIN_MASS:Number = 1;
    public static const MAX_MASS:Number = 20;
    public static const MIN_VOLUME:Number = litersToMetersCubed(1);
    public static const MAX_VOLUME:Number = litersToMetersCubed(10);
    public static const MIN_DENSITY:Number = kgLtoSI(0.1);
    public static const MAX_DENSITY:Number = kgLtoSI(20);

    //Pool volume =60 L
    public static const POOL_WIDTH_X:Number = litersToMetersCubed(6);
    public static const POOL_DEPTH_Z:Number = litersToMetersCubed(2);
    public static const POOL_HEIGHT_Y:Number = litersToMetersCubed(5);
    public static const DEFAULT_BLOCK_MASS:Number = 2;

    private static function kgLtoSI(number:Number):Number {
        return number / 0.001;
    }

    public static function litersToMetersCubed(x:Number):Number {
        return 0.001 * x;
    }

    public static function metersToLitersCubed(v:Number):Number {
        return v / 0.001;
    }
}
}