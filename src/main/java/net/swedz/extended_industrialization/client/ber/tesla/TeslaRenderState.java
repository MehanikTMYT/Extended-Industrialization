package net.swedz.extended_industrialization.client.ber.tesla;

import aztech.modern_industrialization.client.machines.MachineRenderState;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.swedz.extended_industrialization.client.ber.tesla.TeslaPartRenderer.ExtractedArc;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TeslaRenderState extends MachineRenderState
{
	public @Nullable MachineBlockEntity machine;
	public final List<ExtractedArc> arcs = new ArrayList<>();
	public final Vector3f teslaColor = new Vector3f(1, 1, 1);
	public boolean shouldTeslaRender;
	public boolean hasPlasma;
}
