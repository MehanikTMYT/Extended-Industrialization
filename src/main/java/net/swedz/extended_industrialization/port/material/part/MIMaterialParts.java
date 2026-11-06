package net.swedz.extended_industrialization.port.material.part;
import aztech.modern_industrialization.MI;
import net.swedz.tesseract.neoforge.material.part.MaterialPart;
import net.swedz.tesseract.neoforge.registry.common.CommonModelBuilders;
public interface MIMaterialParts {
 MaterialPart CURVED_PLATE = part("curved_plate", "Curved Plate");
 MaterialPart ROD = net.swedz.tesseract.neoforge.material.builtin.part.MaterialParts.ROD;
 MaterialPart PLATE = net.swedz.tesseract.neoforge.material.builtin.part.MaterialParts.PLATE;
 MaterialPart RING = part("ring", "Ring");
 MaterialPart WIRE = part("wire", "Wire");
 MaterialPart CABLE = part("cable", "Cable");
 static MaterialPart part(String id, String english) { return new MaterialPart(MI.id(id), english).itemModelBuilder(CommonModelBuilders::generated); }
}
