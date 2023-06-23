package ml.northwestwind.amethystmusicplayer;

import net.fabricmc.api.ModInitializer;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class AmethystMusicPlayer implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    List<List<Float>> music = Lists.newArrayList();

    @Override
    public void onInitialize() {
        // Read config
        try {
            File configDir = new File("config/amethystmusicplayer");
            configDir.mkdirs();

            for (File file : Objects.requireNonNull(configDir.listFiles())) {
                String name = file.getName();
                if (name.endsWith(".txt")) {
                   String content = FileUtils.readFileToString(new File(configDir.getCanonicalPath() + File.separator + name), StandardCharsets.UTF_8);
                   String[] notes = content.split(" |\r?\n");
                   List<Float> pitches = Lists.newArrayList();
                   for (String note : notes) {
                       if (note.isBlank()) continue;
                       char n = note.charAt(0);
                       if (n < 'A' || n > 'G') continue;
                       if (note.length() > 1) {
                           char a = note.charAt(1);
                       }
                   }
                }
                if (!file.getName().endsWith(".txt") && !file.getName().endsWith(".mid")) continue;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
