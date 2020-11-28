import okio.*;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * Created by Administrator on 2020/11/24 11:26.
 */
public class OkioTrial {

    public static void main(String[] args) {

        OkioTrial okioTrial = new OkioTrial();

        //Buffer读写
//        okioTrial.write();
//        okioTrial.read();

        //数据源
//        okioTrial.sink();
//        okioTrial.souce();

        //拉取和下沉
//        okioTrial.bufferedSource();//Source读操作
//        okioTrial.BufferedSourceAPI();
//        okioTrial.bufferedSink();//Source写操作


        //读写文件
//        okioTrial.file();


        //管道
//        okioTrial.pipe();


        //转发
//        okioTrial.forwardingSink();
//        okioTrial.forwardingSource();

        //压缩解压
        okioTrial.gzip();


        //计算散列
//        okioTrial.hashing();

    }

    private void forwardingSource() {

        File file = new File("okHttp4/res/a.jpg");
        try {
            Source source = Okio.source(file);
            ForwardingSource forwardingSource = new ForwardingSource(source) {
                @Override
                public long read(@NotNull Buffer sink, long byteCount) throws IOException {
                    System.out.println("~~OkioTrial.read~~");
                    System.out.println("sink = " + sink + ", byteCount = " + byteCount);
                    return super.read(sink, byteCount);
                }
            };
            BufferedSource bufferedSource = Okio.buffer(forwardingSource);

            while (!bufferedSource.exhausted())
                bufferedSource.readByte();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void forwardingSink() {

        File file = new File("okHttp4/res/a.jpg");

        //显示输出进度
        ForwardingSink forwardingSink = new ForwardingSink(Okio.blackhole()) {

            long byteCount = 0;

            @Override
            public void write(@NotNull Buffer source, long byteCount) throws IOException {
                System.out.println("~~ForwardingSink.write~~");
                System.out.println("source = " + source + ", byteCount = " + byteCount);
                super.write(source, byteCount);


                double persent = ((double) (this.byteCount += byteCount) / Files.size(file.toPath())) * 100;
                System.out.println("[" + Math.round(persent) + "%]" + this.byteCount + "/" + Files.size(file.toPath()));

            }
        };

        BufferedSink bufferedSink = Okio.buffer(forwardingSink);
        try {
            BufferedSource bufferedSource = Okio.buffer(Okio.source(file));
            while (!bufferedSource.exhausted()) {
                bufferedSink.writeAll(bufferedSource);
            }

            bufferedSink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void souce() {

        try {
            File file = new File("okHttp4/res/abcd");
            Source fileSource = Okio.source(file);

            Socket socket = new Socket("http://localhost/get.php", 80);
            Source socketSource = Okio.source(socket);

            InputStream iputStream = new FileInputStream(file);
            Source iputStreamSource = Okio.source(iputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void sink() {


        try {
            File file = new File("okHttp4/res/abcd");
            Sink fileSink = Okio.sink(file);
            System.out.println(fileSink);

            Socket socket = new Socket("http://localhost/put.php", 80);
            Sink socketSink = Okio.sink(socket);

            OutputStream outputStream = new FileOutputStream(file);
            Sink outputStreamSink = Okio.sink(outputStream);

            Sink sink = Okio.blackhole();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void BufferedSourceAPI() {

        Buffer buffer = new Buffer();
        buffer.write("abcd".getBytes());
        System.out.println(buffer);

        BufferedSource bufferedSource = Okio.buffer((Source) buffer);

        try {

            //掉过字节
//            bufferedSource.skip(1);//跳过1个字节
//            System.out.println(bufferedSource.readUtf8());


            //获取上游副本
//            BufferedSource bufferedSource1 = bufferedSource.peek();//获取上游副本
//            System.out.println(bufferedSource1.readUtf8());//拉取副本中的数据，源上游不会被改变
//            System.out.println(bufferedSource1.getBuffer());
//            System.out.println(bufferedSource.getBuffer());


            //查找消费前缀
//            ByteString byteString1 = new ByteString("a".getBytes());
//            ByteString byteString2 = new ByteString("ab".getBytes());
//
//            Options options = Options.of(byteString1, byteString2);
//            int i = bufferedSource.select(options);
//            System.out.println(i);
//            System.out.println(bufferedSource.getBuffer());


            //判断长度
//            System.out.println(bufferedSource.request(4));
//            System.out.println(bufferedSource.request(8));


            bufferedSource.require(8);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void bufferedSink() {

        //基本使用
        Buffer buffer = new Buffer();
        System.out.println(buffer);


        BufferedSink bufferedSink = Okio.buffer((Sink) buffer);//下游为buffer

        try {
            bufferedSink.write("ddd".getBytes());
            bufferedSink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(buffer);


        //使用多层箱体
//        Buffer buffer = new Buffer();
//        HashingSink hashingSink = new HashingSink(buffer, "SHA-1");//下游为Buffer
//        BufferedSink bufferedSink = Okio.buffer(hashingSink);//下游为HashingSink
//        GzipSink gzipSink = new GzipSink(bufferedSink);//下游为BufferedSink
//
//
//        Buffer data = new Buffer();
//        data.write("go".getBytes());
//        try {
//            gzipSink.write(data, data.size());
//            gzipSink.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(hashingSink.hash());
//        System.out.println(buffer);


    }

    private void bufferedSource() {

        //基本使用
        Buffer buffer = new Buffer();
        buffer.write("abcd".getBytes());
        System.out.println(buffer);

        BufferedSource bufferedSource = Okio.buffer((Source) buffer);

        try {
            byte[] bytes = new byte[5];
            bufferedSource.read(bytes);
            System.out.println(buffer);


            for (int i = 0; i < bytes.length; i++) {
                System.out.print(bytes[i] + ", ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        //使用多层上游
//        Buffer buffer = new Buffer();
//        buffer.write("a".getBytes());
//
//        HashingSource hashingSource = HashingSource.sha1(buffer);
//        BufferedSource bufferedSource = Okio.buffer(hashingSource);
//
//        try {
//            System.out.println(bufferedSource.readUtf8());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(hashingSource.hash());
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

        //计算输入数据hash值
        File file = new File("okHttp4/res/abcd");

        try (HashingSink hashingSink = HashingSink.sha256(Okio.blackhole());
             BufferedSource source = Okio.buffer(Okio.source(file))) {
            source.readAll(hashingSink);
            System.out.println("sha256: " + hashingSink.hash().hex());
        } catch (IOException e) {
            e.printStackTrace();
        }


        //计算写出数据hash值
//        HashingSink hashingSink = HashingSink.sha256(Okio.blackhole());
//        BufferedSink bufferedSink = Okio.buffer(hashingSink);
//
//        try {
//            bufferedSink.writeUtf8("a").flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        ByteString hash = hashingSink.hash();
//        System.out.println("sha256 is " + hash);


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


        //例一：基本使用
//        File file = new File("okHttp4/res/gzip");

        //使用gzip压缩后写入到文件
//        try (Sink sink = Okio.sink(file);
//             GzipSink gzipSink = new GzipSink(sink);
//             BufferedSink bufferedSink = Okio.buffer(gzipSink)) {
//            bufferedSink.writeUtf8("abcd");
//            System.out.println(bufferedSink.getBuffer());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //使用gzip解压，再读取
//        try (Source source = Okio.source(file);
//             GzipSource gzipSource = new GzipSource(source);
//             BufferedSource bufferedSource = Okio.buffer(gzipSource)) {
//            while (!bufferedSource.exhausted())
//                System.out.println(bufferedSource.readUtf8());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



        //例二：基本使用读取源文件，使用gizp压缩后保存
//        File src = new File("okHttp4/res/b.jpg");
//        File des = new File("okHttp4/res/b.jpg.gzip");
        File src = new File("okHttp4/res/a.txt");
        File des = new File("okHttp4/res/a.txt.gzip");

        try (BufferedSource bufferedSource = Okio.buffer(Okio.source(src));
             BufferedSink bufferedGzipSink = Okio.buffer(new GzipSink(Okio.sink(des)))) {

            bufferedSource.readAll(bufferedGzipSink);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void file() {

        //输出数据到文件
//        File file = new File("okHttp4/res/abcd");
////        try(BufferedSink bufferedSink = Okio.buffer(Okio.sink(new File("okHttp4/res/abcd")))) {
//        try (BufferedSink bufferedSink = Okio.buffer(Okio.sink(new File("okHttp4/res/abcd"), true))) {
//            bufferedSink.writeUtf8("aabbccdd");
//            System.out.println(bufferedSink.getBuffer());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //读取文件中的数据
//        try (BufferedSource bufferedSource = Okio.buffer(Okio.source(file))) {
//            while (!bufferedSource.exhausted()) {
//                System.out.println(bufferedSource.readUtf8Line());
////                System.out.println(source.readUtf8LineStrict(1024L));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //读取二进制数据
        File file = new File("okHttp4/res/a.jpg");
        try (BufferedSource bufferedSource = Okio.buffer(Okio.source(file))) {
            while (!bufferedSource.exhausted()) {
                System.out.println(bufferedSource.readByte() + " - " + bufferedSource.getBuffer().size());//Buffer默认尺寸8K，满了就会从上游拉取一批数据
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
