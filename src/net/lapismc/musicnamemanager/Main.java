package net.lapismc.musicnamemanager;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int response = JOptionPane.showConfirmDialog(null, "Is this for files from youtube?", "Ready to go!", dialogButton);
        boolean youtube = response == JOptionPane.YES_OPTION;
        if (youtube) {
            //This code if for renaming songs from youtube
            Path root = new File("./").toPath();
            for (File f : root.toFile().listFiles()) {
                if (!f.getName().endsWith(".jar")) {
                    String name = f.getName();
                    name = name.replaceAll("\\(.*\\)", "");
                    name = name.replaceAll("\\[.*]", "");
                    String id = name.substring(name.length() - 16, name.lastIndexOf('.'));
                    name = name.replace(id, "");
                    System.out.println(name.substring(name.length() - 5, name.length() - 4));
                    while (name.substring(name.length() - 5, name.length() - 4).equals(" ")) {
                        name = name.substring(0, name.length() - 5) + name.substring(name.length() - 4);
                    }
                    f.renameTo(new File(name));
                }
            }
        } else {
            //this code is for files from google play music
            Path root = new File("./").toPath();
            for (File f : root.toFile().listFiles()) {
                if (f.getName().endsWith(".mp3")) {
                    String title = "";
                    String artist = "";
                    try {
                        AudioFile audioFile = AudioFileIO.read(f);
                        Tag tag = audioFile.getTag();
                        title = tag.getFirst(FieldKey.TITLE);
                        artist = tag.getFirst(FieldKey.ARTIST);

                    } catch (IOException | CannotReadException | ReadOnlyFileException | TagException | InvalidAudioFrameException e) {
                        e.printStackTrace();
                    }
                    if (!title.equals("")) {
                        if (!artist.equals("")) {
                            title = title + " - " + artist;
                        }
                        String invalidCharRemoved = title.replaceAll("[\\\\/:*?\"<>|]", "");
                        f.renameTo(new File(root + File.separator + invalidCharRemoved + ".mp3"));
                    } else {
                        System.out.println("Unable to get title for " + f.getName());
                    }
                }
            }
        }
    }

}
