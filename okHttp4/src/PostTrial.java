import okhttp3.*;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Created by Administrator on 2020/11/11 18:54.
 */
public class PostTrial {

    public static void main(String[] args) {

        PostTrial postTrial = new PostTrial();

//        postTrial.mediaType();


//        postTrial.postText();
//        postTrial.postFile();
//        postTrial.postForm();
//        postTrial.multipart();
//        postTrial.postWithRequestBody();
//        postTrial.postWithInterceptor();
        postTrial.postProgress();

    }

    private void postProgress() {

        String url = "http://localhost/post.php";

        MediaType contentType = MediaType.parse("image/png");
        File file = new File("okHttp4/res/a.jpg");
        RequestBody fileBody = RequestBody.create(file, contentType);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("xxxx", "ooo")
                .addFormDataPart("AA", "BB.jpg", fileBody)
                .build();

        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();


        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        System.out.println("~~Interceptor.intercept~~");
                        Request req = chain.request();

                        System.out.println("contentLength is " + req.body().contentLength());

                        return chain.proceed(req);
                    }
                })
                .addNetworkInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        System.out.println("~~NetworkInterceptor.intercept~~");
                        Request req = chain.request();
                        System.out.println("contentLength is " + req.body().contentLength());
                        return chain.proceed(req);
                    }
                })
                .build();

        try {
            System.out.println(client.newCall(request).execute().body().string());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void postWithRequestBody() {

        String url = "http://localhost/post.php";

        Request request = new Request.Builder()
                .post(new TheRequestBody("go=ddd".getBytes()))
                .url(url)
                .build();


        OkHttpClient client = new OkHttpClient();
        try {
            System.out.println(client.newCall(request).execute().body().string());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void postWithInterceptor() {


        String url = "http://localhost/post.php";

        RequestBody formBody = new FormBody.Builder()
                .add("username", "admin")
                .add("password", "admin123")
                .add("grant_type", "password")
                .addEncoded("urlEncoded", "!@#$%^&*()_<>")
                .build();

        Request request = new Request.Builder()
                .header("one", "111")
                .post(formBody)
                .url(url)
                .build();


        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        Request req = chain.request();

                        req.newBuilder()
                                .header("Content-Encoding", "gzip")
                                .post(new RequestBody() {
                                    @Nullable
                                    @Override
                                    public MediaType contentType() {
                                        return req.body().contentType();
                                    }

                                    @Override
                                    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
                                        GzipSink gzipSink = new GzipSink(bufferedSink);
                                        BufferedSink bufferGzipSink = Okio.buffer(gzipSink);
                                        req.body().writeTo(bufferGzipSink);
                                    }
                                })
                                .build();

                        return chain.proceed(req);
                    }
                })
//                .addNetworkInterceptor(new Interceptor() {
//                    @NotNull
//                    @Override
//                    public Response intercept(@NotNull Chain chain) throws IOException {
//                        System.out.println(chain.request().headers());
//                        return chain.proceed(chain.request());
//                    }
//                })
                .build();



        try {
            System.out.println(client.newCall(request).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void mediaType() {

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        System.out.println("type is " + mediaType.type());
        System.out.println("subtype is " + mediaType.subtype());
        System.out.println("parameter is " + mediaType.parameter("charset"));


    }

    private void multipart() {

        String url = "http://localhost/post.php";


        MediaType contentType = MediaType.parse("image/png");
        File file = new File("okHttp4/res/a.jpg");
        RequestBody fileBody = RequestBody.create(file, contentType);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "Square Logo")
                .addFormDataPart("AA", "BB.jpg", fileBody)
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "xxx yyy")
                .post(requestBody)
                .url(url)
                .build();


        OkHttpClient client = new OkHttpClient();
        try {

            System.out.println(client.newCall(request).execute().body().string());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void postText() {


        MediaType contentType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

        Request request = new Request.Builder()
                .url("http://localhost/post.php")
                .post(RequestBody.create("abcd", contentType))
                .build();

        try {
            System.out.println(new OkHttpClient().newCall(request).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void postFile() {


        MediaType contentType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

        Request request = new Request.Builder()
                .url("http://localhost/post.php")
                .post(RequestBody.create(new File("okHttp4/res/abcd"), contentType))
                .build();

        try {
            System.out.println(new OkHttpClient().newCall(request).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void postForm() {

        String url = "http://localhost/post.php";

        RequestBody formBody = new FormBody.Builder()
                .add("username", "admin")
                .add("password", "admin123")
                .add("grant_type", "password")
                .addEncoded("urlEncoded", "!@#$%^&*()_<>")
                .build();

        Request request = new Request.Builder()
                .header("one", "111")
                .post(formBody)
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        try {

            System.out.println(client.newCall(request).execute().body().string());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


//    class Interceptor implements okhttp3.Interceptor {
//        String tag;
//
//        public Interceptor(String tag) {
//            this.tag = tag;
//        }
//
//        @NotNull
//        @Override
//        public Response intercept(@NotNull Chain chain) throws IOException {
//            System.out.println("~~" + tag + ".intercept~~");
//            System.out.println("chain is " + chain);
//
//            Request request = chain.request();
//            RequestBody requestBody = request.body();
//            System.out.println("contentLength is " + requestBody.contentLength());
//            System.out.println("contentType is " + requestBody.contentType());
//
//            if (request.body() == null || request.header("Content-Encoding") != null) {
//                return chain.proceed(request);
//            }
//
//            MediaType mediaType = request.body().contentType();
//
//            request = request.newBuilder()
//                    .header("Content-Encoding", "gzip")
//                    .post(new RequestBody() {
//                        @Nullable
//                        @Override
//                        public MediaType contentType() {
//                            System.out.println("~~Interceptor.contentType~~");
//                            return mediaType;
//                        }
//
//                        @Override
//                        public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
//                            System.out.println("~~Interceptor.writeTo~~");
//
//
//                        }
//                    })
//                    .build();
//
//
//
//
//
//
//            Response response = chain.proceed(request);
//
//
//            return response;
//        }
//    }


    class TheRequestBody extends RequestBody {
        byte[] data;

        public TheRequestBody(byte[] data) {
            this.data = data;
        }


        @Nullable
        @Override
        public MediaType contentType() {
            System.out.println("~~RequestBody.contentType~~");
            return MediaType.parse("application/x-www-form-urlencoded");
        }

        @Override
        public long contentLength() throws IOException {
            return data.length;
        }

        @Override
        public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
            System.out.println("~~RequestBody.writeTo~~");
            System.out.println("bufferedSink = " + bufferedSink);

            bufferedSink.write(data);
            bufferedSink.close();

        }
    }
}

