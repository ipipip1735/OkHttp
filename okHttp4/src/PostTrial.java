import okhttp3.*;

import java.io.File;
import java.io.IOException;

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
        postTrial.multipart();


    }

    private void mediaType() {

        MediaType contentType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        System.out.println(contentType.type());
        System.out.println(contentType.subtype());
        System.out.println(contentType.parameter("charset"));
    }

    private void multipart() {


//        String url = "http://192.168.1.117:8000/msauthserver/oauth/token";
        String url = "http://192.168.0.126/post.php";


//        MediaType contentType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        MediaType contentType = MediaType.parse("image/png");

        File file = new File("okHttp4/res/a.jpg");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "Square Logo")
                .addFormDataPart("image", file.getName(), RequestBody.create(file, contentType))
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "xxx tttt")
                .url(url)
                .post(requestBody)
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
                .url("http://192.168.0.126/post.php")
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
                .url("http://192.168.0.126/post.php")
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
        String url = "http://192.168.0.126/post.php";

        RequestBody formBody = new FormBody.Builder()
                .add("admin", "admin123")
                .add("password", "admin123")
                .add("grant_type", "password")
                .addEncoded("urlEncoded", "!@#$%^&*()_<>")
                .build();

        Request request = new Request.Builder()
                .post(formBody)
//                .header("Authorization", "Basic RDgyRjgxMzRFMDFEMTFFOUE0RjM1MDQ2NUQ1NjAxQ0U6OTUyNjNFQTBFMDFFMTFFOUE0RjM1MDQ2NUQ1NjAxQ0U=")
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
