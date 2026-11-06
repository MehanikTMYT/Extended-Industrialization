package net.swedz.extended_industrialization.port.material;
import aztech.modern_industrialization.MI;
import net.swedz.tesseract.neoforge.material.Material;
import static net.swedz.extended_industrialization.port.material.part.MIMaterialParts.*;
import static net.swedz.tesseract.neoforge.material.builtin.part.MaterialParts.*;
import static net.swedz.extended_industrialization.port.material.property.MIMaterialProperties.*;
public interface MIMaterials {
 Material SILVER = create("silver", "Silver", net.swedz.extended_industrialization.port.material.property.TimeFactor.SOFT);
 Material COPPER = create("copper", "Copper", net.swedz.extended_industrialization.port.material.property.TimeFactor.SOFT);
 Material ELECTRUM = create("electrum", "Electrum", net.swedz.extended_industrialization.port.material.property.TimeFactor.SOFT);
 Material ALUMINUM = create("aluminum", "Aluminum", net.swedz.extended_industrialization.port.material.property.TimeFactor.AVERAGE);
 Material ANNEALED_COPPER = create("annealed_copper", "Annealed Copper", net.swedz.extended_industrialization.port.material.property.TimeFactor.SOFT);
 Material SUPERCONDUCTOR = create("superconductor", "Superconductor", net.swedz.extended_industrialization.port.material.property.TimeFactor.AVERAGE);
 Material STEEL = create("steel", "Steel", net.swedz.extended_industrialization.port.material.property.TimeFactor.AVERAGE);
 static Material create(String name, String english, double time) {
  return new Material(MI.id(name), english).set(TIME_FACTOR,time)
   .addNative(INGOT, NUGGET, DUST, TINY_DUST, PLATE, ROD, RING, WIRE, CABLE, CURVED_PLATE);
 }
}
