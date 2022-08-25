package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderStateShard.class)
public interface RenderStateShardAccessor {
    @Accessor
    static RenderStateShard.TransparencyStateShard getTRANSLUCENT_TRANSPARENCY() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static RenderStateShard.OverlayStateShard getOVERLAY() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static RenderStateShard.CullStateShard getNO_CULL() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static RenderStateShard.WriteMaskStateShard getCOLOR_WRITE() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static RenderStateShard.ShaderStateShard getRENDERTYPE_EYES_SHADER() {
        throw new UnsupportedOperationException();
    }
}