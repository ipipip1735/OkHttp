import okhttp3.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2020/11/11 18:54.
 */
public class PostTrial {

    public static void main(String[] args) {

        PostTrial postTrial = new PostTrial();

        postTrial.postText();
//        postTrial.postForm();
//        postTrial.postFile();


    }

    private void postText() {


        MediaType contentType = MediaType.parse("text/plain; charset=utf-8");
//        System.out.println(contentType.type());
//        System.out.println(contentType.subtype());
//        System.out.println(contentType.parameter("charset"));



        OkHttpClient client = new OkHttpClient();

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

    private void postFile() {


        MediaType contentType = MediaType.parse("text/plain; charset=utf-8");
//        System.out.println(contentType.type());
//        System.out.println(contentType.subtype());
//        System.out.println(contentType.parameter("charset"));



        OkHttpClient client = new OkHttpClient();

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

//        String url = "http://192.168.1.117:8000/msauthserver/oauth/token";
        String url = "http://localhost/post.php";

        RequestBody formBody = new FormBody.Builder()
                .add("admin", "admin123")
                .add("password", "admin123")
                .add("grant_type", "password")
                .build();

        Request request = new Request.Builder()
                .post(formBody)
                .header("Authorization", "Basic RDgyRjgxMzRFMDFEMTFFOUE0RjM1MDQ2NUQ1NjAxQ0U6OTUyNjNFQTBFMDFFMTFFOUE0RjM1MDQ2NUQ1NjAxQ0U=")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .url(url)
                .build();


        OkHttpClient client = new OkHttpClient();
        try {

            System.out.println(client.newCall(request).execute().body().string());



        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
