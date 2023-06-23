package ml.northwestwind.amethystmusicplayer;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public class ZeroRandomSource implements RandomSource {
    @Override
    public RandomSource fork() {
        return new ZeroRandomSource();
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return new ZeroPositionalRandomFactory();
    }

    @Override
    public void setSeed(long l) { }

    @Override
    public int nextInt() {
        return 0;
    }

    @Override
    public int nextInt(int i) {
        return 0;
    }

    @Override
    public long nextLong() {
        return 0;
    }

    @Override
    public boolean nextBoolean() {
        return false;
    }

    @Override
    public float nextFloat() {
        return 0;
    }

    @Override
    public double nextDouble() {
        return 0;
    }

    @Override
    public double nextGaussian() {
        return 0;
    }

    class ZeroPositionalRandomFactory implements PositionalRandomFactory {
        @Override
        public RandomSource fromHashOf(String string) {
            return new ZeroRandomSource();
        }

        @Override
        public RandomSource at(int i, int j, int k) {
            return new ZeroRandomSource();
        }

        @Override
        public void parityConfigString(StringBuilder stringBuilder) { }
    }
}
