uniform mat4 g_WorldViewProjectionMatrix;
attribute vec3 inPosition;
attribute vec2 inTexCoord;

varying float density;

void main(){
    density = inTexCoord[0];

    gl_Position = g_WorldViewProjectionMatrix * vec4 (inPosition, 1.0 );
}