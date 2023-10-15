package com.topstonesoftware.javaorc;

import org.apache.orc.TypeDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TestStringList extends TestBase {
    private final static String text = "When half way through the journey of our life\n" +
                                       "I found that I was in a gloomy wood,\n" +
                                       "because the path which led aright was lost.\n" +
                                       "And ah, how hard it is to say just what\n" +
                                       "this wild and rough and stubborn woodland was,\n" +
                                       "the very thought of which renews my fear!\n" +
                                       "So bitter ’t is, that death is little worse;\n" +
                                       "but of the good to treat which there I found,\n" +
                                       "I ’ll speak of what I else discovered there.\n" +
                                       "I cannot well say how I entered it,\n" +
                                       "so full of slumber was I at the moment\n" +
                                       "when I forsook the pathway of the truth;\n" +
                                       "but after I had reached a mountain’s foot,\n" +
                                       "where that vale ended which had pierced my heart\n" +
                                       "with fear, I looked on high,\n" +
                                       "and saw its shoulders\n" +
                                       "mantled already with that planet’s rays\n" +
                                       "which leadeth one aright o’er every path.\n" +
                                       "Then quieted a little was the fear,\n" +
                                       "which in the lake-depths of my heart had lasted\n" +
                                       "throughout the night I passed so piteously.\n" +
                                       "And even as he who, from the deep emerged\n" +
                                       "with sorely troubled breath upon the shore,\n" +
                                       "turns round, and gazes at the dangerous water;\n" +
                                       "even so my mind, which still was fleeing on,\n" +
                                       "turned back to look again upon the pass\n" +
                                       "which ne’er permitted any one to live.\n" +
                                       "When I had somewhat eased my weary body,\n" +
                                       "o’er the lone slope I so resumed my way,\n" +
                                       "that e’er the lower was my steady foot.\n" +
                                       "Then lo, not far from where the ascent began,\n" +
                                       "a Leopard which, exceeding light and swift,\n" +
                                       "was covered over with a spotted hide,\n" +
                                       "and from my presence did not move away;\n" +
                                       "nay, rather, she so hindered my advance,\n" +
                                       "that more than once I turned me to go back.\n" +
                                       "Some time had now from early morn elapsed,\n" +
                                       "and with those very stars the sun was rising\n" +
                                       "that in his escort were, when Love Divine\n" +
                                       "in the beginning moved those beauteous things;\n" +
                                       "I therefore had as cause for hoping well\n" +
                                       "of that wild beast with gaily mottled skin,\n" +
                                       "the hour of daytime and the year’s sweet season;\n" +
                                       "but not so, that I should not fear the sight,\n" +
                                       "which next appeared before me, of a Lion,\n" +
                                       "— against me this one seemed to be advancing\n" +
                                       "with head erect and with such raging hunger,\n" +
                                       "that even the air seemed terrified thereby —\n" +
                                       "and of a she-Wolf, which with every lust\n" +
                                       "seemed in her leanness laden, and had caused\n" +
                                       "many ere now to lead unhappy lives.\n" +
                                       "The latter so oppressed me with the fear\n" +
                                       "that issued from her aspect, that I lost\n" +
                                       "the hope I had of winning to the top.\n" +
                                       "And such as he is, who is glad to gain,\n" +
                                       "and who, when times arrive that make him lose,\n" +
                                       "weeps and is saddened in his every thought;\n" +
                                       "such did that peaceless animal make me,\n" +
                                       "which, ’gainst me coming, pushed me, step by step,\n" +
                                       "back to the place where silent is the sun.\n" +
                                       "While toward the lowland I was falling fast,\n" +
                                       "the sight of one was offered to mine eyes,\n" +
                                       "who seemed, through long continued silence, weak.\n" +
                                       "When him in that vast wilderness I saw,\n" +
                                       "“Have pity on me,” I cried out to him,\n" +
                                       "“whate’er thou be, or shade, or very man!”\n" +
                                       "“Not man,” he answered, “I was once a man;\n" +
                                       "and both my parents were of Lombardy,\n" +
                                       "and Mantuans with respect to fatherland.\n" +
                                       "’Neath Julius was I born, though somewhat late,\n" +
                                       "and under good Augustus’ rule I lived\n" +
                                       "in Rome, in days of false and lying gods.\n" +
                                       "I was a poet, and of that just man,\n" +
                                       "Anchises’ son, I sang, who came from Troy\n" +
                                       "after proud Ilion had been consumed.\n" +
                                       "But thou, to such sore trouble why return?\n" +
                                       "Why climbst thou not the Mountain of Delight,\n" +
                                       "which is of every joy the source and cause?”\n" +
                                       "“Art thou that Virgil, then, that fountain-head\n" +
                                       "which poureth forth so broad a stream of speech?”\n" +
                                       "I answered him with shame upon my brow.\n" +
                                       "“O light and glory of the other poets,\n" +
                                       "let the long study, and the ardent love\n" +
                                       "which made me con thy book, avail me now.\n" +
                                       "Thou art my teacher and authority;\n" +
                                       "thou only art the one from whom I took\n" +
                                       "the lovely manner which hath done me honor.\n" +
                                       "Behold the beast on whose account I turned;\n" +
                                       "from her protect me, O thou famous Sage,\n" +
                                       "for she makes both my veins and pulses tremble!”\n" +
                                       "“A different course from this must thou pursue,”\n" +
                                       "he answered, when he saw me shedding tears,\n" +
                                       "“if from this wilderness thou wouldst escape;\n" +
                                       "for this wild beast, on whose account thou criest,\n" +
                                       "alloweth none to pass along her way,\n" +
                                       "but hinders him so greatly, that she kills;\n" +
                                       "and is by nature so malign and guilty,\n" +
                                       "that never doth she sate her greedy lust,\n" +
                                       "but after food is hungrier than before.\n" +
                                       "Many are the animals with which she mates,\n" +
                                       "and still more will there be, until the Hound\n" +
                                       "shall come, and bring her to a painful death.\n" +
                                       "He shall not feed on either land or wealth,\n" +
                                       "but wisdom, love and power shall be his food,\n" +
                                       "and ’tween two Feltros shall his birth take place.\n" +
                                       "Of that low Italy he ’ll be the savior,\n" +
                                       "for which the maid Camilla died of wounds,\n" +
                                       "with Turnus, Nisus and Eurỳalus.\n" +
                                       "And he shall drive her out of every town,\n" +
                                       "till he have put her back again in Hell,\n" +
                                       "from which the earliest envy sent her forth.\n";

    @Test
    void stringListTest(@TempDir Path tempDirPath) throws ORCFileException {
        Path filePath = tempDirPath.resolve("long_list_data.orc");
        File timestampStringFile = filePath.toFile();
        String filePathStr = timestampStringFile.getPath();
        TypeDescription schema = TypeDescription.createStruct();
        TypeDescription stringListType = TypeDescription.createList(TypeDescription.createString());
        schema.addField("string_list", stringListType);
        List<List<Object>> valueList = new ArrayList<>();
        List<String> lines = textToLines(text);
        try(var orcWriter = new WriteORCFile(filePathStr, schema)) {
            List<Object> row = new ArrayList<>();
            for (var line : lines) {
                row.clear();
                List<Object> listVal = textToWords(line);
                row.add(listVal);
                valueList.add(listVal);
                orcWriter.writeRow( row );
            }
        }
        try(var orcReader = new ReadORCFile(filePathStr)) {
            String fileSchema = orcReader.getSchema().toString();
            assertThat(fileSchema).isEqualTo(schema.toString());
            long fileRows = orcReader.getNumberOfRows();
            assertThat(fileRows).isEqualTo(lines.size());
            List<Object> row;
            int rowNum = 0;
            while ((row = orcReader.readRow()).size() > 0) {
                assertThat(row.size()).isEqualTo(1);
                Object rowElem = row.get(0);
                assertThat( rowElem ).isInstanceOf( ArrayList.class );
                @SuppressWarnings("unchecked")
                List<Object> stringList = (ArrayList<Object>)rowElem;
                assertThat(listsAreEqual(stringList, valueList.get( rowNum ))).isTrue();
                rowNum++;
            }
        }
    }

}
