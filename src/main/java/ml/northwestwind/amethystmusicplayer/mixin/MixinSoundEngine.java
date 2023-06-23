package ml.northwestwind.amethystmusicplayer.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SoundEngine.class)
public abstract class MixinSoundEngine {
    @Shadow protected abstract float calculatePitch(SoundInstance soundInstance);

    // Remove pitch limit of amethyst sound
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundEngine;calculatePitch(Lnet/minecraft/client/resources/sounds/SoundInstance;)F"), method = "play")
    public float calculatePitch(SoundEngine instance, SoundInstance soundInstance) {
        if (!soundInstance.getLocation().getPath().startsWith("block.amethyst") || soundInstance.getLocation().getPath().endsWith("block.chime")) return calculatePitch(soundInstance);
        return soundInstance.getPitch();
    }
}
