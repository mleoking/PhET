uniform mat4 g_WorldViewProjectionMatrix;

attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec2 inTexCoord;
attribute vec2 inTexCoord2;

varying vec2 texCoord;
varying vec2 texCoord2;

//#ifdef TRI_PLANAR_MAPPING
//  varying vec4 vVertex;
//  varying vec3 vNormal;
//#endif

void main(){
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
    texCoord = inTexCoord;
    texCoord2 = inTexCoord2;

//#ifdef TRI_PLANAR_MAPPING
//    vVertex = vec4(inPosition,0.0);
//    vNormal = inNormal;
//#endif

}