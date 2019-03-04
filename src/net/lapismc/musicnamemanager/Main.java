package net.lapismc.musicnamemanager;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
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
                    String name = f.getName();
                    name = name.replaceAll("\\(.*\\)", "");
                    name = name.replaceAll("\\[.*]", "");
                    if (name.contains("-") && name.charAt(name.lastIndexOf(".") - 12) == '-') {
                        String id = name.substring(name.lastIndexOf('.') - 12, name.lastIndexOf('.'));
                        if (id.length() == 12 && !id.contains(" "))
                            name = name.replace(id, "");
                    }
                    while (name.charAt(name.lastIndexOf('.') - 1) == ' ') {
                        name = name.substring(0, name.lastIndexOf('.') - 1) + name.substring(name.lastIndexOf('.'));
                    }
                    f.renameTo(new File(output, name));
                }
            }
        } else {
            //this code is for files from google play music
            for (File f : input.listFiles()) {
                if (f.getName().endsWith(".mp3")) {
                    String title;
                    String artist = "";
                    try {
                        AudioFile audioFile = AudioFileIO.read(f);
                        Tag tag = audioFile.getTag();
                        title = tag.getFirst(FieldKey.TITLE);
                        artist = tag.getFirst(FieldKey.ARTIST);
                        Artwork artwork = tag.getFirstArtwork();
                        tag = audioFile.createDefaultTag();
                        tag.setField(FieldKey.TITLE, title);
                        tag.setField(FieldKey.ARTIST, artist);
                        if (artwork != null)
                            tag.setField(artwork);
                        audioFile.setTag(tag);
                        audioFile.commit();
                    } catch (IOException | CannotReadException | ReadOnlyFileException | TagException | InvalidAudioFrameException | CannotWriteException e) {
                        e.printStackTrace();
                        title = "";
                    }
                    if (!title.equals("")) {
                        if (!artist.equals("")) {
                            title = title + " - " + artist;
                        }
                        String invalidCharRemoved = title.replaceAll("[\\\\/:*?\"<>|]", "");
                        f.renameTo(new File(output, invalidCharRemoved + ".mp3"));
                    } else {
                        System.out.println("Unable to get title for " + f.getName());
                    }
                }
            }
        }
    }

}
