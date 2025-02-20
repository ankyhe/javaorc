package com.topstonesoftware.javaorc;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.orc.TypeDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TestUnionColumnVector {
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

    private TypeDescription buildSchema() {
        TypeDescription schema = TypeDescription.createStruct();
        TypeDescription union = TypeDescription.createUnion();
        union.addUnionChild( TypeDescription.createInt());
        union.addUnionChild( TypeDescription.createTimestamp());
        union.addUnionChild( TypeDescription.createString());
        schema.addField("union", union);
        return schema;
    }

    private List<TypeDescription> getUnionTypeFields( TypeDescription schema) {
        List<TypeDescription> unionParent = schema.getChildren();
        return unionParent.get(0).getChildren();
    }

    @Test
    void unionColumnVectorTest(@TempDir Path tempDirPath) throws IOException, ORCFileException, InterruptedException {
        Path filePath = tempDirPath.resolve("union_data.orc");
        File tempFile = filePath.toFile();
        String filePathStr = tempFile.getPath();
        TypeDescription schema = buildSchema();
        List<TypeDescription> unionFieldTypes = getUnionTypeFields(schema);
        // convert the text to a single line by removing new lines
        String oneLine = text.replaceAll("[\\t\\n\\r]+"," ");
        // remote punctuation and split into words.
        String[] words = oneLine.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        List<Pair<TypeDescription, Object>> rowValues = new ArrayList<>();
        try (WriteORCFile orcWriter = new WriteORCFile(filePathStr, schema)) {
            List<Object> row = new ArrayList<>();
            for (var word : words) {
                Timestamp timestamp = new Timestamp(new Date().getTime());
                // Write one union value per row, for each of the three union values
                for (var unionFieldType : unionFieldTypes) {
                    row.clear();
                    Pair<TypeDescription, Object> unionPair = createValue(unionFieldType, word, timestamp);
                    row.add(unionPair);
                    rowValues.add(unionPair);
                    orcWriter.writeRow(row);
                }
                Thread.sleep(10);
            }
        }
        try(var orcReader = new ReadORCFile(filePathStr)) {
            String fileSchema = orcReader.getSchema().toString();
            assertThat(fileSchema).isEqualTo(schema.toString());
            long fileRows = orcReader.getNumberOfRows();
            assertThat(fileRows).isEqualTo(rowValues.size());
            List<Object> row;
            int rowNum = 0;
            while ((row = orcReader.readRow()).size() > 0) {
                @SuppressWarnings("unchecked")
                Pair<TypeDescription, Object> rowPair = (Pair<TypeDescription, Object>)row.get(0);
                Pair<TypeDescription, Object> checkPair = rowValues.get(rowNum);
                assertThat(rowPair).isEqualTo(checkPair);
                rowNum++;
            }
        }
    }

    private Pair<TypeDescription, Object> createValue(final TypeDescription unionFieldType, String word, final Timestamp timestamp) throws ORCFileException {
        switch(unionFieldType.getCategory()) {
        case INT: return new ImmutablePair<>(unionFieldType, word.hashCode());
        case TIMESTAMP: return new ImmutablePair<>(unionFieldType, timestamp);
        case STRING: return new ImmutablePair<>(unionFieldType, word);
        default: throw new ORCFileException("Unsupported union type");
        }
    }
}
