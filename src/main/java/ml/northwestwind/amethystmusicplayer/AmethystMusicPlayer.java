package ml.northwestwind.amethystmusicplayer;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import javax.sound.midi.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AmethystMusicPlayer implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final int NOTE_ON = 0x90;
    public static final int C5 = 72; // Middle C should be C4, but amethyst is already very high-pitched, so I put it 1 octave above.
    public static final int TEMPO_CHANGE = 0x51;
    private static final List<List<Float>> MUSIC = Lists.newArrayList();
    private static final List<List<Long>> DURATION = Lists.newArrayList();
    private static final Random RNG = new Random();
    private static List<Float> playing = null;
    private static List<Long> clicking = null;
    private static int index = -1, clickIndex = -1;
    private static final KeyMapping keyAutoplay, keyReload;
    private static Thread thread;

    static {
        System.setProperty("java.awt.headless", "false");
        keyAutoplay = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.amethystmusicplayer.autoplay",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_BRACKET,
                "key.categories.amethystmusicplayer"));
        keyReload = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.amethystmusicplayer.reload",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_BRACKET,
                "key.categories.amethystmusicplayer"));
    }

    @Override
    public void onInitializeClient() {
        reload();

        // Register keybind
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyAutoplay.consumeClick()) toggleAutoplay();
            while (keyReload.consumeClick()) reload();
        });
    }

    private static void reload() {
        playing = null;
        clicking = null;
        index = clickIndex = -1;
        MUSIC.clear();
        DURATION.clear();

        // Read config
        try {
            File configDir = new File("config/amethystmusicplayer");
            configDir.mkdirs();

            for (File file : Objects.requireNonNull(configDir.listFiles())) {
                String name = file.getName();
                if (!name.endsWith(".mid")) continue;
                LOGGER.info("Loading music file {}", name);
                Sequence sequence = MidiSystem.getSequence(file);
                int ppq = sequence.getResolution();
                Track track = sequence.getTracks()[0];
                Map<Long, Float> map = Maps.newTreeMap();
                Map<Long, Long> upts = Maps.newTreeMap();
                double upt = 0;
                for (int ii = 0; ii < track.size(); ii++) {
                    MidiEvent event = track.get(ii);
                    long tick = event.getTick();
                    MidiMessage message = event.getMessage();
                    if (message instanceof MetaMessage mm && mm.getType() == TEMPO_CHANGE) {
                        int upq = 0;
                        for (byte b : mm.getData()) {
                            //LOGGER.info(String.format("0x%02X", b));
                            upq = (upq << 8) + (b & 0xFF);
                        }
                        //LOGGER.info("type={} upq={}", String.format("0x%02X", mm.getType()), upq);
                        if (upt == 0) upt = (double) upq / ppq;
                    }
                    if (message instanceof ShortMessage sm && sm.getCommand() == NOTE_ON && sm.getData2() > 0) {
                        map.put(tick, (float) Math.pow(2, (sm.getData1() - C5) / 12.0));
                        List<Long> keys = map.keySet().stream().sorted().toList();
                        long secondToLastTick = 0;
                        if (keys.size() > 1) secondToLastTick = keys.get(keys.size() - 2);
                        //LOGGER.info("@{} key={} upt={} duration={}", tick, sm.getData1(), upt, upt * (tick - secondToLastTick));
                        upts.put(tick, 0L);
                        if (upts.size() > 1) upts.put(secondToLastTick, (long) (upt * (tick - secondToLastTick)));
                    }
                }
                if (map.size() > 0) {
                    MUSIC.add(new ArrayList<>(map.values()));
                    DURATION.add(new ArrayList<>(upts.values()));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (MUSIC.size() > 0) {
            int ii = RNG.nextInt(MUSIC.size());
            playing = MUSIC.get(ii);
            clicking = DURATION.get(ii);
            index = 0;
            clickIndex = 0;
        }
    }

    public static float nextPitch() {
        if (playing == null || index >= playing.size()) {
            if (MUSIC.size() == 0) return -1;
            int ii = RNG.nextInt(MUSIC.size());
            playing = MUSIC.get(ii);
            clicking = DURATION.get(ii);
            index = 0;
        }
        float result = playing.get(index++);
        if (index >= playing.size()) {
            int ii = RNG.nextInt(MUSIC.size());
            playing = MUSIC.get(ii);
            clicking = DURATION.get(ii);
            index = 0;
        }
        return result;
    }

    private static void makeThread() {
        thread = new Thread(() -> {
            LOGGER.info("Clicker thread started");
            try {
                Robot robot = new Robot();
                robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    TimeUnit.MICROSECONDS.sleep(clicking.get(clickIndex++));
                    Robot robot = new Robot();
                    robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
                    if (clickIndex >= clicking.size()) Thread.currentThread().interrupt();
                } catch (Exception e) {
                    LOGGER.error(e);
                    Thread.currentThread().interrupt();
                }
            }
            clickIndex = 0;
            LOGGER.info("Clicker thread ended");
            thread = null;
        });
    }

    private void toggleAutoplay() {
        if (thread == null) {
            makeThread();
            thread.start();
        } else {
            thread.interrupt();
            thread = null;
            index = 0;
            clickIndex = 0;
        }
    }
}
