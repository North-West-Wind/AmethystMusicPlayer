package ml.northwestwind.amethystmusicplayer;

import com.google.common.collect.Maps;
import net.fabricmc.api.ClientModInitializer;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.midi.*;
import java.io.File;
import java.util.*;

public class AmethystMusicPlayer implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final int NOTE_ON = 0x90;
    public static final int C5 = 72; // Middle C should be C4, but amethyst is already very high-pitched, so I put it 1 octave above.
    private static final List<List<Float>> MUSIC = Lists.newArrayList();
    private static final Random RNG = new Random();
    private static List<Float> playing = null;
    private static int index = -1;

    @Override
    public void onInitializeClient() {
        // Read config
        try {
            File configDir = new File("config/amethystmusicplayer");
            configDir.mkdirs();

            Sequencer sequencer = MidiSystem.getSequencer();

            for (File file : Objects.requireNonNull(configDir.listFiles())) {
                String name = file.getName();
                if (!name.endsWith(".mid")) continue;
                LOGGER.info("Loading music file {}", name);
                Sequence sequence = MidiSystem.getSequence(file);
                Track track = sequence.getTracks()[0];
                Map<Long, Float> map = Maps.newTreeMap();
                for (int ii = 0; ii < track.size(); ii++) {
                    MidiEvent event = track.get(ii);
                    long tick = event.getTick();
                    MidiMessage message = event.getMessage();
                    if (message instanceof ShortMessage sm && sm.getCommand() == NOTE_ON && sm.getData2() > 0) map.put(tick, (float) Math.pow(2, (sm.getData1() - C5) / 12.0));
                }
                if (map.size() > 0) MUSIC.add(new ArrayList<>(map.values()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static float nextPitch() {
        if (playing == null || index >= playing.size()) {
            if (MUSIC.size() == 0) return -1;
            playing = MUSIC.get(RNG.nextInt(MUSIC.size()));
            index = 0;
        }
        return playing.get(index++);
    }
}
