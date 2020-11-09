import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2020/11/9.
 */
public class GetTrial {
    public static void main(String[] args) {

        GetTrial getTrial = new GetTrial();
//        getTrial.get();
        getTrial.getWithCookies();

    }

    private void getWithCookies() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                        System.out.println("~~saveFromResponse~~");
                        System.out.println("httpUrl is " + httpUrl);
                        System.out.println("list is " + list);
                    }

                    @NotNull
                    @Override
                    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                        System.out.println("~~loadForRequest~~");
                        System.out.println("httpUrl is " + httpUrl);
                        return null;
                    }
                })
                .build();
        Request request = new Request.Builder()
                .get()
                .url("http://192.168.0.126/index.php")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void get() {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("https://docs.oracle.com/javase/8/docs/technotes/guides/language/generics.html")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
