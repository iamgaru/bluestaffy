package ai.gamu.bluestaffy.client.renderer;

import net.minecraft.client.renderer.entity.state.WolfRenderState;

/**
 * Extends WolfRenderState with Blue Staffy–specific render fields
 * so the model can read custom entity state without hacking existing fields.
 */
public class BlueStaffyRenderState extends WolfRenderState {
    /** True while the entity is in a nap session (server-synced via EntityDataAccessor). */
    public boolean isNapping = false;
    /** True while the entity is in a side-flop within a nap (server-synced). */
    public boolean isSideFlopping = false;
}
