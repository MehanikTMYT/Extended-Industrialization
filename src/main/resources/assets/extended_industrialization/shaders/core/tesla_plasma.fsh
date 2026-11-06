#version 330

#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1; // the block atlas, so we can scale things appropriately. For some reason Sampler1 and Sampler2 are reserved for some textures so I must use it as the atlas

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main()
{
	float PlasmaScale = TextureMat[0][0];
	float PlasmaSpeed = TextureMat[1][1];
	
	vec2 textureScale = textureSize(Sampler1, 0) / 64.0;
	vec2 transformedUV = texCoord0;
	transformedUV *= PlasmaScale;
	vec2 motion = vec2(PlasmaSpeed);
	motion /= textureScale;
	vec2 translation = vec2(GameTime) * motion;
	transformedUV += translation;
	transformedUV *= textureScale;
	transformedUV = fract(transformedUV);
	
	vec4 plasmaTexture = texture(Sampler0, transformedUV) * vertexColor;
	if (plasmaTexture.a > 0)
	{
		fragColor = plasmaTexture;
	}
	else
	{
		discard;
	}
}
