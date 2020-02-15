package net.lapismc.musicnamemanager;

import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.LogManager;

public class Processor {

    private File output;

    public Processor() {
        LogManager.getLogManager().reset();
    }

    void run() throws IOException {
        Path root = new File("./").toPath();
        File input = new File(root.toFile(), "Input");
        output = new File(root.toFile(), "Output");
        if (!input.exists() || !output.exists()) {
            input.mkdirs();
            output.mkdirs();
        }
        renameFromYouTube(input);
    }

    private void renameFromYouTube(File dir) throws IOException {
        //This code is for renaming songs from youtube
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                renameFromYouTube(f);
                FileUtils.deleteDirectory(dir);
                continue;
            }
            if (!f.getName().endsWith(".jar")) {
                String name = cleanupYoutubeName(f.getName());
                f.renameTo(new File(output, name));
            }
        }
    }

    public String generateAudioFileName(AudioFile audioFile) {
        Tag tag = audioFile.getTag();
        String title, artist;
        title = tag.getFirst(FieldKey.TITLE);
        artist = tag.getFirst(FieldKey.ARTIST);
        if (!title.equals("")) {
            if (!artist.equals("")) {
                title = title + " - " + artist;
            }
            title = title.replaceAll("[\\\\/:*?\"<>|]", "");
            return cleanupWhitespace(title);
        } else {
            return "";
        }
    }

    public String cleanupYoutubeName(String name) {
        name = name.replaceAll("\\(([^()]|)*\\)", "");
        name = name.replaceAll("\\[([^()]|)*]", "");
        if (name.contains("-") && name.charAt(name.lastIndexOf(".") - 12) == '-') {
            String id = name.substring(name.lastIndexOf('.') - 12, name.lastIndexOf('.'));
            if (id.length() == 12 && !id.contains(" "))
                name = name.replace(id, "");
        }
        while (name.charAt(name.lastIndexOf('.') - 1) == ' ') {
            name = name.substring(0, name.lastIndexOf('.') - 1) + name.substring(name.lastIndexOf('.'));
        }
        return cleanupWhitespace(name);
    }

    private String cleanupWhitespace(String s) {
        s = s.replace("_", " ");
        while (s.startsWith(" ")) {
            s = s.substring(1);
        }
        while (s.endsWith(" ")) {
            s = s.substring(0, s.length() - 1);
        }
        while (s.contains("  ")) {
            s = s.replace("  ", " ");
        }
        return s;
    }

}
