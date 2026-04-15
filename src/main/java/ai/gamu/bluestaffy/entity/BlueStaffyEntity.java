package ai.gamu.bluestaffy.entity;

import ai.gamu.bluestaffy.BlueStaffy;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;

public class BlueStaffyEntity extends Wolf {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(BlueStaffy.MODID, "textures/entity/bluestaffy.png");

    public BlueStaffyEntity(EntityType<? extends Wolf> type, Level level) {
        super(type, level);
    }

    @Override
    public Identifier getTexture() {
        return TEXTURE;
    }
}
