package javes.nlp;

import java.io.IOException;

import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;

/**
 * This stream wraps a sample stream for an intent, and creates an
 * DocumentSample stream based on it. This stream can be used to feed the
 * OpenNLP training system
 */
public class DocumentSampleStream implements ObjectStream<DocumentSample> {
    String intent;
    ObjectStream<String> sampleStream;

    public DocumentSampleStream(String intent, ObjectStream<String> sampleStream) {
        this.intent = intent;
        this.sampleStream = sampleStream;
    }

    public DocumentSample read() throws IOException {
        String sample = this.sampleStream.read();

        if (sample == null) {
            return null;
        }
        else {
            String[] sampleTokens = WhitespaceTokenizer.INSTANCE.tokenize(sample);

            if (sampleTokens.length > 0) {
                return new DocumentSample(this.intent, sampleTokens);
            }
            else {
                throw new IOException("The read sample for '" + this.intent + "' was empty.");
            }
        }
    }

    public void reset() throws IOException, UnsupportedOperationException {
        this.sampleStream.reset();
    }

    public void close() throws IOException {
        this.sampleStream.close();
    }
}