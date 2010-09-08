package edu.colorado.phet.flashcommon {
public class MathUtil {
    public function MathUtil() {
    }

    public static function clamp(minimum:Number, value:Number, maximum:Number):Number {
        if (value < minimum) return minimum;
        else if (value > maximum) return maximum;
        else return value;
    }
}
}