When updating the JME3 version, please update the native directory using the instructions
at http://jmonkeyengine.org/wiki/doku.php/jme3:webstart

The referenced script is reproduced below:

mkdir tmp
cd tmp
jar xfv ../jME3-lwjgl-natives.jar
cd native
for i in *; do
  cd $i
  jar cfv ../../native_$i.jar .
  cd ..
done

The JARs are left unsigned.