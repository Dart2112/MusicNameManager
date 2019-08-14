import net.lapismc.musicnamemanager.Processor;
import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.junit.Assert;
import org.junit.Test;

public class MusicRenameTests {

    @Test
    public void YoutubeRenameTest() {
        String[] rawNames = {"Alan Walker & Sia - Faded_Cheap Thrills_Alive_Airplanes (feat. Hayley Williams, B.o.B, Sean Paul)-uryVw968ZMM.mp3",
                "Nightcore - Want To Want Me-DNxMbst8m10.mp3",
                "Ghostbusters Theme Song Remix [Music Video] - The Living Tombstone-vWltUi1zXWM.mp3",
                "Imany - Don't Be So Shy (Filatov & Karas Remix) _ Official Music Video-b1_B-IKEufg.mp3",
                "Closer vs. Airplanes (Mashup) - The Chainsmokers, Halsey & B.O.B (By Adrian Mashups)-pjrmF7--JZg.mp3"
                , " Amazing -LRoPz5_oibI.mp3"};
        String[] expectedOutputs = {"Alan Walker & Sia - Faded Cheap Thrills Alive Airplanes.mp3",
                "Nightcore - Want To Want Me.mp3",
                "Ghostbusters Theme Song Remix - The Living Tombstone.mp3",
                "Imany - Don't Be So Shy Official Music Video.mp3",
                "Closer vs. Airplanes - The Chainsmokers, Halsey & B.O.B.mp3"
                , "Amazing.mp3"};
        Processor processor = new Processor();
        for (int i = 0; i < rawNames.length; i++) {
            Assert.assertEquals(expectedOutputs[i], processor.cleanupYoutubeName(rawNames[i]));
        }
    }

    @Test
    public void AudioFileRenameTest() {
        Processor processor = new Processor();
        try {
            AudioFile audioFile = AudioFileIO.read(FileUtils.toFile(this.getClass().getResource("The Best Song.mp3")));
            Tag tag = audioFile.createDefaultTag();
            tag.setField(FieldKey.TITLE, " The Best Song ");
            audioFile.setTag(tag);
            Assert.assertEquals("The Best Song", processor.generateAudioFileName(audioFile));
            tag.setField(FieldKey.ARTIST, "Me ");
            audioFile.setTag(tag);
            Assert.assertEquals("The Best Song - Me", processor.generateAudioFileName(audioFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
