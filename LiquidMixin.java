package com.example.xray.mixin;

import com.example.xray.XrayConfig;
import com.example.xray.XrayState;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LiquidBlockRenderer.class)
public abstract class LiquidMixin {

    // require = 0: se algo não bater, só desativa o esconder água/lava em vez de crashar
    @Inject(method = "tesselate", at = @At("HEAD"), cancellable = true, require = 0)
    private void xray$tesselate(BlockAndTintGetter level, BlockPos pos, VertexConsumer buffer,
                                BlockState blockState, FluidState fluidState, CallbackInfo ci) {
        if (!XrayState.enabled) return;
        if (!XrayState.inRange(pos)) return;
        if (fluidState.is(FluidTags.WATER) && !XrayConfig.showWater) {
            ci.cancel();
        } else if (fluidState.is(FluidTags.LAVA) && !XrayConfig.showLava) {
            ci.cancel();
        }
    }
}
