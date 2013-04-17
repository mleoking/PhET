package away3d.core.utils
{

    /** Static helper class for color manipulations */
    public class Color 
    {
        public static const white:int = 0xFFFFFF;
        public static const black:int = 0x000000;
        public static const red:int = 0xFF0000;
        public static const green:int = 0x00FF00;
        public static const blue:int = 0x0000FF;
        public static const yellow:int = 0xFFFF00;
        public static const cyan:int = 0x00FFFF;
        public static const purple:int = 0xFF00FF;

        public static function multiply(color:int, k:Number):int
        {
            var r:int = color & 0xFF0000 >> 16;
            var g:int = color & 0xFF00 >> 8;
            var b:int = color & 0xFF;

            return fromIntsCheck(int(r*k), int(g*k), int(b*k));
        }

        public static function add(colora:int, colorb:int):int
        {
            var ra:int = colora & 0xFF0000 >> 16;
            var ga:int = colora & 0xFF00 >> 8;
            var ba:int = colora & 0xFF;

            var rb:int = colorb & 0xFF0000 >> 16;
            var gb:int = colorb & 0xFF00 >> 8;
            var bb:int = colorb & 0xFF;

            return fromIntsCheck(ra+rb, ga+gb, ba+bb);
        }

        public static function inverseAdd(colora:int, colorb:int):int
        {
            var ra:int = 255 - colora & 0xFF0000 >> 16;
            var ga:int = 255 - colora & 0xFF00 >> 8;
            var ba:int = 255 - colora & 0xFF;

            var rb:int = 255 - colorb & 0xFF0000 >> 16;
            var gb:int = 255 - colorb & 0xFF00 >> 8;
            var bb:int = 255 - colorb & 0xFF;

            return fromIntsCheck(255 - (ra+rb), 255 - (ga+gb), 255 - (ba+bb));
        }

        public static function fromHSV(hue:Number, saturation:Number, value:Number):int
        {
            var h:Number = ((hue % 360) + 360) % 360;
            var s:Number = saturation;
            var v:Number = value;
            var hi:int = int(h / 60) % 6;
            var f:Number = h / 60 - hi;
            var p:Number = v * (1 - s);
            var q:Number = v * (1 - f*s);
            var t:Number = v * (1 - (1 - f)*s);
            switch (hi)
            {
                case 0: return fromFloats(v, t, p); break;
                case 1: return fromFloats(q, v, p); break;
                case 2: return fromFloats(p, v, t); break;
                case 3: return fromFloats(p, q, v); break;
                case 4: return fromFloats(t, p, v); break;
                case 5: return fromFloats(v, p, q); break;
            }
            return 0;
        }

        public static function fromFloats(red:Number, green:Number, blue:Number):int
        {
            return 0x10000*int(red*0xFF) + 0x100*int(green*0xFF) + int(blue*0xFF);
        }

        public static function fromInts(red:int, green:int, blue:int):int
        {
            return 0x10000*red + 0x100*green + blue;
        }

        public static function fromIntsCheck(red:int, green:int, blue:int):int
        {
            red = Math.max(0, Math.min(255, red));
            green = Math.max(0, Math.min(255, green));
            blue = Math.max(0, Math.min(255, blue));
            return 0x10000*red + 0x100*green + blue;
        }
    }
}
