/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package de.saar.coli.ccgparser;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        WordWithSupertags[][] allTaggedSentences = mapper.readValue(new File("supertags.json"), WordWithSupertags[][].class);

        for( WordWithSupertags[] sentence : allTaggedSentences ) {
            for( WordWithSupertags word : sentence ) {
                int i = 0;
                for( SupertagWithScore tag : word.supertags ) {
                    tag.setPositionInSupertagList(i++);
                }
            }
        }

        Parser parser = new Parser(allTaggedSentences[0]);
        parser.parse();
    }
}
