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

import java.util.Arrays;

@Mixin(SimpleSoundInstance.class)
public abstract class MixinSimpleSoundInstance extends MixinAbstractSoundInstance {
    private static final String[] HIGH_PITCHES = {"block.break", "block.place", "cluster.place"};
    private static final String[] MID_PITCHES = {"block.fall", "block.hit", "block.step", "cluster.step", "cluster.hit", "cluster.fall"};
    private static final double ONE_PITCH = Math.pow(2, 1.0 / 12);
    private static final float HIGH_C_PITCH = 1 / (float) Math.pow(ONE_PITCH, 8), MID_C_PITCH = 1 / (float) Math.pow(ONE_PITCH, 6), LOW_C_PITCH = 1 / (float) Math.pow(ONE_PITCH, 4);


    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/util/RandomSource;ZILnet/minecraft/client/resources/sounds/SoundInstance$Attenuation;DDDZ)V")
    public void init(ResourceLocation resourceLocation, SoundSource soundSource, float f, float g, RandomSource randomSource, boolean bl, int i, SoundInstance.Attenuation attenuation, double d, double e, double h, boolean bl2, CallbackInfo ci) {
        if (!resourceLocation.getPath().startsWith("block.amethyst") || resourceLocation.getPath().endsWith("block.chime")) return;
        if (Arrays.stream(HIGH_PITCHES).anyMatch(str -> resourceLocation.getPath().endsWith(str))) this.pitch = HIGH_C_PITCH * AmethystMusicPlayer.nextPitch();
        else if (Arrays.stream(MID_PITCHES).anyMatch(str -> resourceLocation.getPath().endsWith(str))) this.pitch = MID_C_PITCH * AmethystMusicPlayer.nextPitch();
        else this.pitch = LOW_C_PITCH * AmethystMusicPlayer.nextPitch();
    }
}
