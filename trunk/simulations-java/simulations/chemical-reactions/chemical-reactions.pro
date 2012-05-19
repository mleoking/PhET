#Prevents a nullpointer exception in jbox2d, maybe could be pared down even further
-keep public class org.jbox2d.**{
    <fields>;
    <methods>;
}
