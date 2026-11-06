package net.swedz.extended_industrialization.client.ber.tesla.behavior;

import net.minecraft.resources.Identifier;
import org.joml.Vector3f;

public interface TeslaBehavior
{
	boolean shouldTeslaRender();
	
	Identifier getTeslaModelLocation();
	
	default Vector3f getTeslaColor()
	{
		return new Vector3f(1, 1, 1);
	}
}
