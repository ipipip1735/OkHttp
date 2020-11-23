import okio.Buffer;
import okio.BufferedSink;
import okio.Sink;
import okio.Source;

import java.io.IOException;

/**
 * Created by Administrator on 2020/11/23 18:44.
 */
public class SinkTrial {

    public static void main(String[] args) {
        SinkTrial sinkTrial = new SinkTrial();
        sinkTrial.write();
    }

    private void write() {

        Buffer buffer = new Buffer();

        Sink sink = new Buffer();

        try {
            sink.write(buffer, 1L);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
