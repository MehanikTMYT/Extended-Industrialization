#version 330

#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0; // texture mask
uniform sampler2D Sampler1; // glint overlay
uniform sampler2D Sampler2; // quantum star 1
uniform sampler2D Sampler3; // quantum star 2
uniform sampler2D Sampler4; // quantum star 3
uniform sampler2D Sampler5; // quantum star 4

#ifdef RENDER_STARS
const vec2 STAR_SCALE = vec2(STAR_SCALE_X, STAR_SCALE_Y);

const vec2 starMotion[4] = vec2[](
vec2(0, 40),
vec2(0, 30),
vec2(0, 20),
vec2(0, 30)
);
#endif

in vec2 texCoord0;
in vec4 shaderColor;

out vec4 fragColor;

#ifdef RENDER_STARS
vec4 sampleStar(int layer, vec2 uv)
{
	if (layer == 0) return texture(Sampler2, uv);
	if (layer == 1) return texture(Sampler3, uv);
	if (layer == 2) return texture(Sampler4, uv);
	return texture(Sampler5, uv);
}

vec2 transformStarUV(vec2 uv, int layer)
{
	vec2 transformedUV = uv * STAR_SCALE;
	transformedUV += vec2(GameTime) * starMotion[layer];
	return fract(transformedUV);
}
#endif

vec2 transformGlintUV(vec2 uv)
{
	vec2 transformedUV = uv;
	vec2 translation = vec2(GameTime) * 15;
	transformedUV += translation;
	return fract(transformedUV);
}

void main()
{
	vec4 textureColor = texture(Sampler0, texCoord0);
	float mask = textureColor.a;
	if (mask > 0)
	{
		vec3 color = textureColor.rgb;
		vec4 glintTexture = texture(Sampler1, transformGlintUV(texCoord0));
		if (glintTexture.a > 0)
		{
			color += glintTexture.rgb * shaderColor.rgb;
		}
#ifdef RENDER_STARS
		for (int layer = 0; layer < 4; layer++)
		{
			vec4 starTexture = sampleStar(layer, transformStarUV(texCoord0, layer));
			if (starTexture.a > 0)
			{
				color = starTexture.rgb * shaderColor.rgb;
			}
		}
#endif
		fragColor = vec4(color, 1.0);
	}
	else
	{
		discard;
	}
}
