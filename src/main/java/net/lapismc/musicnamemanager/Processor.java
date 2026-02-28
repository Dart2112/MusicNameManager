package net.lapismc.musicnamemanager;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;

public class Processor {

    private File output;

    public Processor() {
        LogManager.getLogManager().reset();
    }

    void run() throws IOException {
        Path root = new File("./").toPath();
        File input = new File(root.toFile(), "Download");
        output = new File(root.toFile(), "Edit");
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
            if (f.getName().endsWith(".mp3")) {
                String name = cleanupYoutubeName(f.getName());
                f.renameTo(new File(output, name));
            }
        }
    }

    public String cleanupYoutubeName(String name) {
        name = removeBracketedTags(name);
        //Remove youtube video ID
        if (name.contains("[") && name.charAt(name.lastIndexOf(".") - 13) == '[') {
            String id = name.substring(name.lastIndexOf('.') - 13, name.lastIndexOf('.'));
            if (id.length() == 13 && !id.contains(" "))
                name = name.replace(id, "");
        }
        //Remove whitespace at the end of the string
        while (name.charAt(name.lastIndexOf('.') - 1) == ' ') {
            name = name.substring(0, name.lastIndexOf('.') - 1) + name.substring(name.lastIndexOf('.'));
        }
        if (name.contains("？")) {
            //Remove this stupid question mark
            name = name.replace("？", "");
        }
        //Remove "- Topic-" tags
        if (name.contains("- Topic-")) {
            name = name.replace("Topic-", "");
        }
        return cleanupWhitespace(name);
    }

    private String removeBracketedTags(String name) {
        //Process smooth brackets e.g. ( & )
        name = removeMatchedContents("(", ")", name);
        //Process hard brackets e.g. [ & ]
        name = removeMatchedContents("[", "]", name);
        return name;
    }

    private String removeMatchedContents(String start, String end, String text) {
        while (text.contains(start)) {
            int open = text.lastIndexOf(start);
            int close = text.lastIndexOf(end);
            String tag = text.substring(open + 1, close).toLowerCase();
            String wholeTag = text.substring(open, close + 1);
            System.out.println(tag + " " + wholeTag);
            if (matchFilter(tag)) {
                text = text.replace(wholeTag, "");
            } else {
                text = text.replace(wholeTag, wholeTag.replace(start, "@1").replace(end, "@2"));
            }
        }
        text = text.replace("@1", start).replace("@2", end);
        return text;
    }

    private boolean matchFilter(String tag) {
        List<String> wordsToFilter = Arrays.asList("lyric", "official", "video", "audio");
        for (String word : wordsToFilter) {
            if (tag.contains(word))
                return true;
        }
        return false;
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
