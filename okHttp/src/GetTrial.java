import com.squareup.okhttp.*;

import java.io.*;

/**
 * Created by Administrator on 2020/11/4 11:16.
 */
public class GetTrial {
    public static void main(String[] args) {

        GetTrial getTrial = new GetTrial();

        getTrial.getSync();
//        getTrial.getAsync();

    }

    void getSync() {


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("https://docs.oracle.com/javase/8/docs/technotes/guides/language/generics.html")
                .addHeader("Accept-Encoding", "identity")//禁用gzip压缩
                .build();

        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            System.out.println("response is " + response);
            System.out.println("responseHeaders is " + response.headers());
            System.out.println("responseBody is " + response.body());
            System.out.println("responseBody.contentLength is " + response.body().contentLength());//需要禁用gzip，否则总是-1

//            pull(response.body().byteStream());//处理二进制数据


            System.out.println(response.body().string());//处理文本数据


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    void getAsync() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("https://docs.oracle.com/javase/8/docs/technotes/guides/language/generics.html")
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("~~onFailure~~");
                System.out.println(Thread.currentThread());
                System.out.println("request is " + request);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                System.out.println("~~onResponse~~");
                System.out.println(Thread.currentThread());
                System.out.println("response is " + response);
                System.out.println("response is " + response.headers());
                System.out.println("response is " + response.body());

                pull(response.body().charStream());
            }
        });


    }


    private void pull(Reader reader) {


        try (BufferedReader bufferedReader = new BufferedReader(reader)) {

            String s;
            while ((s = bufferedReader.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void pull(InputStream inputStream) {
        //使用InputStream
//        try (
//                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)
//        ) {
//            System.out.println("size is " + inputStream.available());
//            int len = 0;
//            byte[] bytes = new byte[1024];
//            while ((len = bufferedInputStream.read(bytes)) != -1) {
//                System.out.println(new String(bytes, 0, len));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        //使用InputStreamReader
        try (
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,  "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            //方式一：
//                StringBuilder  builder  =  new  StringBuilder(128);
//                char[]  buffer  =  new char[8192];
//                int  len;
//                while((len=bufferedReader.read(buffer))  != -1  ){
//                    builder.append(buffer,  0,  len);
//                }
//                String result  =  builder.toString();
//                System.out.println(result);


            //方式二：
            String s;
            while((s = bufferedReader.readLine()) != null){
                System.out.println(s);
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
