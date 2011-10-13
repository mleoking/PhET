uniform sampler2D m_Tex1;
uniform sampler2D m_Tex2;
uniform sampler2D m_Tex3;
uniform float m_TextureScale;

varying vec2 texCoord;
varying vec2 texCoord2;

void main(void) {

    float elevation = texCoord2.x;
    float stonyness = clamp( ( elevation - 3200.0 ) * ( 255.0 / 400.0 ), 0.0,255.0 ); // tree line at 3400 km
    float beachyness = clamp( -( elevation - 1500.0 ) / 3.0, 0.0,255.0 );
    vec4 alpha = vec4( ( 255.0 - stonyness - beachyness ), stonyness, beachyness, 1.0 ) / 255.0;

	vec4 tex1    = texture2D( m_Tex1, texCoord.xy * m_TextureScale ); // grass
	vec4 tex2    = texture2D( m_Tex2, texCoord.xy * m_TextureScale ); // stone
	vec4 tex3    = texture2D( m_Tex3, texCoord.xy * m_TextureScale ); // beach

    vec4 outColor = tex1 * alpha.r; // Red channel
	outColor = mix( outColor, tex2, alpha.g ); // Green channel
	outColor = mix( outColor, tex3, alpha.b ); // Blue channel
	gl_FragColor = outColor;
}

