import okio.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Administrator on 2020/11/24 11:26.
 */
public class BufferTrial {

    public static void main(String[] args) {

        BufferTrial bufferTrial = new BufferTrial();

        //内存读写
        bufferTrial.write();
//        bufferTrial.read();


        //读写文件
//        bufferTrial.file();


        //管道
//        bufferTrial.pipe();


        //压缩解压
        bufferTrial.gzip();


        //计算散列
//        bufferTrial.hashing();

    }


    private void pipe() {

        //方式一：读阻塞
//        Pipe pipe = new Pipe(4);
//        Sink sink = pipe.sink();
//        Source source = pipe.source();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try (BufferedSource bufferedSource = Okio.buffer(source)) {
//
//                    Buffer buffer = new Buffer();
//                    bufferedSource.read(buffer, 2);//子线程在写操作之前启动，所以它将阻塞，等待数据写入Pipe
//                    System.out.println(buffer);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//
//        try (BufferedSink bufferedSink = Okio.buffer(sink)) {
//
//            bufferedSink.writeUtf8("ok");//写入数据到Pipe
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        //例二：写阻塞
        Pipe pipe = new Pipe(1);
        Sink sink = pipe.sink();
        Source source = pipe.source();

        try (BufferedSink bufferedSink = Okio.buffer(sink)) {

            bufferedSink.writeUtf8("ok");//写入数据到Pipe

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void hashing() {

//        File file = new File("okHttp4/res/gzip");
//
//        try (HashingSink hashingSink = HashingSink.sha256(Okio.blackhole());
//             BufferedSource source = Okio.buffer(Okio.source(file))) {
//            source.readAll(hashingSink);
//            System.out.println("sha256: " + hashingSink.hash().hex());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        //计算写出数据hash值
        HashingSink hashingSink = HashingSink.sha256(Okio.blackhole());
        BufferedSink bufferedSink = Okio.buffer(hashingSink);

        try {
            bufferedSink.writeUtf8("a").flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteString hash = hashingSink.hash();
        System.out.println("sha256 is " + hash);


        //计算读取数据hash值
//        Buffer buffer = new Buffer();
//        HashingSink hashingSink = HashingSink.sha1(buffer);
//        BufferedSink bufferedSink = Okio.buffer(hashingSink);
//
//        try {
//            bufferedSink.writeUtf8("a").flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        ByteString hash = hashingSink.hash();
//        System.out.println(hash);
//
//
//        ByteString byteString = new ByteString("a".getBytes());
//        System.out.println(byteString.sha1());
//
//        System.out.println(hash.equals(byteString.sha1()));


    }

    private void gzip() {

        File file = new File("okHttp4/res/gzip");

        //gzip压缩
        try (Sink sink = Okio.sink(file);
             GzipSink gzipSink = new GzipSink(sink);
             BufferedSink bufferedSink = Okio.buffer(gzipSink)) {
            bufferedSink.writeUtf8("abcd");
            System.out.println(bufferedSink.getBuffer());
        } catch (IOException e) {
            e.printStackTrace();
        }


        //gzip解压
        try (Source source = Okio.source(file);
             GzipSource gzipSource = new GzipSource(source);
             BufferedSource bufferedSource = Okio.buffer(gzipSource)) {
            while (!bufferedSource.exhausted())
                System.out.println(bufferedSource.readUtf8());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void file() {

        File file = new File("okHttp4/res/abcd");

        //输出数据到文件
////        try(BufferedSink bufferedSink = Okio.buffer(Okio.sink(new File("okHttp4/res/abcd")))) {
//        try (BufferedSink bufferedSink = Okio.buffer(Okio.sink(new File("okHttp4/res/abcd"), true))) {
//            bufferedSink.writeUtf8("aabbccdd");
//            System.out.println(bufferedSink.getBuffer());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //读取文件中的数据
        try (BufferedSource bufferedSource = Okio.buffer(Okio.source(file))) {
            while (!bufferedSource.exhausted()) {
                System.out.println(bufferedSource.readUtf8Line());
//                System.out.println(source.readUtf8LineStrict(1024L));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        //复制文件
//        File src = new File("okHttp4/res/abcd");
//        File des = new File("okHttp4/res/abcd1");
//        try (Source source = Okio.source(src);
//             BufferedSource bufferedSource = Okio.buffer(source);
//             Sink sink = Okio.sink(des, true)) {
//
//            bufferedSource.readAll(sink);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


    private void read() {

        //例一：读数据到字节数组
        Buffer buffer = new Buffer();
        System.out.println(buffer);
        buffer.write("aaaaa".getBytes());
        System.out.println(buffer);

        byte[] bytes = new byte[10];
        buffer.read(bytes);

        for (int i = 0; i < bytes.length; i++) {
            System.out.print(bytes[i] + ",");
        }

        System.out.println(buffer);


        //例二：读数据到Buffer
//        File file = new File("okHttp4/res/abcd");
//
//        try (Source source = Okio.source(file)) {
//
//            Buffer buffer = new Buffer();
//            source.read(buffer, 2);
//
//            System.out.println(buffer);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void write() {

        //方式一：
        Buffer buffer = new Buffer();
        System.out.println(buffer.buffer());
        byte[] bytes = new byte[1024];
//        Arrays.fill(bytes, (byte) 15);//打印出的是十六进制字符
        Arrays.fill(bytes, (byte) 97);//打印出的是UTF-8字符
        buffer.write(bytes);
        buffer.flush();
        System.out.println(buffer.toString());


        //方式二:
//        File file = new File("okHttp4/res/abcd");
//        try (Sink sink = Okio.sink(file)){
//
//            Buffer buffer = new Buffer();
//            buffer.write("xxxxxx".getBytes());
//            System.out.println(buffer);
//
//            sink.write(buffer, 5L);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        File file = new File("okHttp4/res/abcd");
//        try (HashingSink hashingSink = HashingSink.sha256(Okio.blackhole());
//             BufferedSink sink = Okio.buffer(hashingSink);
//             Source source = Okio.source(file)) {
//            sink.writeAll(source);
//            sink.close(); // Emit anything buffered.
//            System.out.println("sha256: " + hashingSink.hash().hex());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }
}
