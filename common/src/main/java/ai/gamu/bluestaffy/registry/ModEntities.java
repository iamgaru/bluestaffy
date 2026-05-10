package ai.gamu.bluestaffy.registry;

import ai.gamu.bluestaffy.BlueStaffy;
import ai.gamu.bluestaffy.entity.BlueStaffyEntity;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BlueStaffy.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<BlueStaffyEntity>> BLUE_STAFFY =
            ENTITIES.register("blue_staffy", () ->
                    EntityType.Builder.<BlueStaffyEntity>of(BlueStaffyEntity::new, MobCategory.CREATURE)
                            .sized(0.6F, 0.85F)
                            .clientTrackingRange(10)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(BlueStaffy.MOD_ID, "blue_staffy"))));
}
