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
        File input = new File(root.toFile(), "Download" + File.separator + "Output");
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
            if (!f.getName().endsWith(".jar")) {
                String name = cleanupYoutubeName(f.getName());
                f.renameTo(new File(output, name));
            }
        }
    }

    public String cleanupYoutubeName(String name) {
        name = removeBracketedTags(name);
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

    private String removeBracketedTags(String name) {

        //Process smooth brackets e.g. ( & )
        while (name.contains("(")) {
            int open = name.lastIndexOf("(");
            int close = name.lastIndexOf(")");
            String tag = name.substring(open + 1, close - 1).toLowerCase();
            String wholeTag = name.substring(open, close);
            if (matchFilter(tag)) {
                name = name.replace(name.substring(open, close), "");
            }
            name = name.replace(wholeTag, wholeTag.replace("(", "@1").replace(")", "@2"));
        }
        name = name.replace("@1", "(").replace("@2", ")");
        //Process hard brackets e.g. [ & ]
        while (name.contains("[")) {
            int open = name.lastIndexOf("[");
            int close = name.lastIndexOf("]");
            String tag = name.substring(open + 1, close).toLowerCase();
            String wholeTag = name.substring(open, close + 1);
            System.out.println(tag + " " + wholeTag);
            if (matchFilter(tag)) {
                name = name.replace(wholeTag, "");
            } else {
                name = name.replace(wholeTag, wholeTag.replace("[", "@1").replace("]", "@2"));
            }
        }
        name = name.replace("@1", "[").replace("@2", "]");
        return name;
    }

    private boolean matchFilter(String tag) {
        List<String> wordsToFilter = Arrays.asList("lyric", "official", "video");
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
