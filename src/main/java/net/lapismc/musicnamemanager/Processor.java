package net.lapismc.musicnamemanager;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Random;
import java.util.logging.LogManager;

public class Processor {

    public Processor() {
        LogManager.getLogManager().reset();
    }

    void run() {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int response = JOptionPane.showConfirmDialog(null, "Is this for files from youtube?", "Ready to go!", dialogButton);
        boolean youtube = response == JOptionPane.YES_OPTION;
        Path root = new File("./").toPath();
        File input = new File(root.toFile(), "Input");
        File output = new File(root.toFile(), "Output");
        if (!input.exists() || !output.exists()) {
            input.mkdirs();
            output.mkdirs();
        }
        if (youtube) {
            //This code is for renaming songs from youtube
            for (File f : input.listFiles()) {
                if (!f.getName().endsWith(".jar")) {
                    String name = cleanupYoutubeName(f.getName());
                    f.renameTo(new File(output, name));
                }
            }
        } else {
            //this code is for files from google play music
            for (File f : input.listFiles()) {
                if (f.getName().endsWith(".mp3")) {
                    String name = "";
                    try {
                        AudioFile audioFile = AudioFileIO.read(f);
                        name = generateAudioFileName(audioFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (name.equals("")) {
                        log("Unable to get title for " + f.getName());
                        continue;
                    }
                    if (new File(output, name + ".mp3").exists()) {
                        f.renameTo(new File(output, "_" + new Random().nextInt(100) + name + ".mp3"));
                    } else {
                        f.renameTo(new File(output, name + ".mp3"));
                    }
                }
            }
        }
    }

    public String generateAudioFileName(AudioFile audioFile) throws FieldDataInvalidException, CannotWriteException {
        Tag tag = audioFile.getTag();
        String title, artist, album;
        title = tag.getFirst(FieldKey.TITLE);
        artist = tag.getFirst(FieldKey.ARTIST);
        album = tag.getFirst(FieldKey.ALBUM);
        Artwork artwork = tag.getFirstArtwork();
        tag = audioFile.createDefaultTag();
        tag.setField(FieldKey.TITLE, title);
        tag.setField(FieldKey.ARTIST, artist);
        tag.setField(FieldKey.ALBUM, album);
        if (artwork != null)
            tag.setField(artwork);
        audioFile.setTag(tag);
        audioFile.commit();
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

    private void log(String msg) {
        System.out.println(msg);
    }

}
