import os
import re

file_path = 'src/main/java/net/swedz/extended_industrialization/datagen/server/provider/advancements/AdvancementDatagenProvider.java'
with open(file_path, 'r') as f:
    content = f.read()

# Простая замена, которая может помочь
content = content.replace('InventoryChangeTrigger.TriggerInstance', 'InventoryChangeTrigger.CriterionTriggerInstance')

with open(file_path, 'w') as f:
    f.write(content)
