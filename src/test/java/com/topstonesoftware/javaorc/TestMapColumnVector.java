package com.topstonesoftware.javaorc;

import org.apache.orc.TypeDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TestMapColumnVector {
    private final static String text = "Do not go gentle into that good night,\n" +
                                       "Old age should burn and rave at close of day;\n" +
                                       "Rage, rage against the dying of the light.\n" +
                                       "Though wise men at their end know dark is right,\n" +
                                       "Because their words had forked no lightning they\n" +
                                       "Do not go gentle into that good night.\n" +
                                       "Good men, the last wave by, crying how bright\n" +
                                       "Their frail deeds might have danced in a green bay,\n" +
                                       "Rage, rage against the dying of the light.\n" +
                                       "Wild men who caught and sang the sun in flight,\n" +
                                       "And learn, too late, they grieved it on its way,\n" +
                                       "Do not go gentle into that good night.\n" +
                                       "Grave men, near death, who see with blinding sight\n" +
                                       "Blind eyes could blaze like meteors and be gay,\n" +
                                       "Rage, rage against the dying of the light.\n" +
                                       "And you, my father, there on the sad height,\n" +
                                       "Curse, bless, me now with your fierce tears, I pray.\n" +
                                       "Do not go gentle into that good night.\n" +
                                       "Rage, rage against the dying of the light.\n";

    private TypeDescription buildMapSchema() {
        TypeDescription schema = TypeDescription.createStruct();
        TypeDescription map = TypeDescription.createMap(TypeDescription.createString(), TypeDescription.createInt());
        schema.addField("word_freq", map);
        return schema;
    }


    @Test
    void testMapColumnVector(@TempDir Path tempDirPath) throws ORCFileException {
        Path filePath = tempDirPath.resolve("map_data.orc");
        File longVectorFile = filePath.toFile();
        String filePathStr = longVectorFile.getPath();
        TypeDescription schema = buildMapSchema();
        HashMap<String, Integer> wordFreqMap = new HashMap<>();
        // convert the text to a single line by removing new lines
        String oneLine = text.replaceAll("[\\t\\n\\r]+"," ");
        // remote punctuation and split into words.
        String[] words = oneLine.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        for (var word : words) {
            int freq = 1;
            if (wordFreqMap.containsKey( word)) {
                freq = wordFreqMap.get( word ) + 1;
            }
            wordFreqMap.put(word, freq);
        }
        try (var orcWriter = new WriteORCFile(filePathStr, schema)) {
            List<Object> row = new ArrayList<>();
            row.add( wordFreqMap);
            orcWriter.writeRow( row );
        }
        try(var orcReader = new ReadORCFile(filePathStr)) {
            String fileSchema = orcReader.getSchema().toString();
            assertThat(fileSchema).isEqualTo(schema.toString());
            long fileRows = orcReader.getNumberOfRows();
            assertThat(fileRows).isEqualTo(1);
            List<Object> row;
            while ((row = orcReader.readRow()).size() > 0) {
                assertThat(row.size()).isEqualTo(1);
                Object hashObj = row.get(0);
                assertThat( hashObj ).isInstanceOf( HashMap.class );
                @SuppressWarnings("unchecked")
                HashMap<String, Integer> elementMap = (HashMap<String, Integer>)hashObj;
                assertThat(elementMap).isEqualTo(wordFreqMap);
            }
        }
    }
    
}
