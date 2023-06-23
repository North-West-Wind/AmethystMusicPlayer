package ml.northwestwind.amethystmusicplayer.mixin;

import ml.northwestwind.amethystmusicplayer.ZeroRandomSource;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSoundInstance.class)
public abstract class MixinAbstractSoundInstance {
    @Shadow public abstract ResourceLocation getLocation();

    @Shadow protected RandomSource random;

    @Shadow protected float pitch;

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/util/RandomSource;)V")
    public void init(ResourceLocation resourceLocation, SoundSource soundSource, RandomSource randomSource, CallbackInfo ci) {
        if (!resourceLocation.getPath().startsWith("block.amethyst") || resourceLocation.getPath().endsWith("block.chime")) return;
        this.random = new ZeroRandomSource();
    }
}
