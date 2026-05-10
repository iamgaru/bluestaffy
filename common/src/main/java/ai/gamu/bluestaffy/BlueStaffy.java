package ai.gamu.bluestaffy;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import ai.gamu.bluestaffy.registry.ModBlocks;
import ai.gamu.bluestaffy.registry.ModEntities;
import ai.gamu.bluestaffy.registry.ModItems;
import net.minecraft.resources.Identifier;

public final class BlueStaffy {
    public static final String MOD_ID = "bluestaffy";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        ModBlocks.BLOCKS.register();
        ModEntities.ENTITIES.register();
        ModItems.ITEMS.register();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
