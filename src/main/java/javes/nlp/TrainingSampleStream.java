package javes.nlp;

import opennlp.tools.util.ObjectStream;

/**
 * This stream wraps a given string-array as stream, which is usable for the
 * OpenNLP API.
 */
public class TrainingSampleStream implements ObjectStream<String> {
    private String[] sampleList;
    private int sampleIndex;
    private boolean open;

    public TrainingSampleStream(String[] trainingSampleList) {
        this.sampleList = trainingSampleList;
        this.sampleIndex = 0;
        this.open = true;
    }

    public String read() {
        if (this.open && this.sampleIndex < this.sampleList.length) {
            return this.sampleList[this.sampleIndex++]
                    // normalize to lowercase
                    .toLowerCase()
                    // preserve OpenNLP markers
                    .replace("<start:", "<START:").replace("<end>", "<END>");
        }

        return null;
    }

    public void reset() {
        this.sampleIndex = 0;
    }

    public void close() {
        this.open = false;
    }
}