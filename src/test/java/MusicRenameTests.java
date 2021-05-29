import net.lapismc.musicnamemanager.Processor;
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
                , " Amazing -LRoPz5_oibI.mp3", "Olivia Rodrigo - jealousy, jealousy (Lyric Video)-Z-9gQjUZMm0.mp3"};
        String[] expectedOutputs = {"Alan Walker & Sia - Faded Cheap Thrills Alive Airplanes (feat. Hayley Williams, B.o.B, Sean Paul).mp3",
                "Nightcore - Want To Want Me.mp3",
                "Ghostbusters Theme Song Remix - The Living Tombstone.mp3",
                "Imany - Don't Be So Shy (Filatov & Karas Remix) Official Music Video.mp3",
                "Closer vs. Airplanes (Mashup) - The Chainsmokers, Halsey & B.O.B (By Adrian Mashups).mp3"
                , "Amazing.mp3", "Olivia Rodrigo - jealousy, jealousy.mp4"};
        Processor processor = new Processor();
        for (int i = 0; i < rawNames.length; i++) {
            String input = rawNames[i];
            String output = processor.cleanupYoutubeName(rawNames[i]);
            System.out.println(input + " = " + output + "\n");
            Assert.assertEquals(expectedOutputs[i], output);
        }
    }

}
