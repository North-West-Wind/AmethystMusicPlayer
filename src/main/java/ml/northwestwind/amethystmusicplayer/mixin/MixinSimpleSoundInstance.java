package ml.northwestwind.amethystmusicplayer.mixin;

import ml.northwestwind.amethystmusicplayer.AmethystMusicPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleSoundInstance.class)
public class MixinSimpleSoundInstance {
    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/util/RandomSource;ZILnet/minecraft/client/resources/sounds/SoundInstance$Attenuation;DDDZ)V")
    public void init(ResourceLocation resourceLocation, SoundSource soundSource, float f, float g, RandomSource randomSource, boolean bl, int i, SoundInstance.Attenuation attenuation, double d, double e, double h, boolean bl2, CallbackInfo ci) {
        if (!resourceLocation.getPath().startsWith("block.amethyst")) return;
        AmethystMusicPlayer.LOGGER.info("playing amethysy sound");
    }
}
