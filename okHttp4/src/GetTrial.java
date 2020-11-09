import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
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
                    List<Cookie> list = new ArrayList<Cookie>();

                    @Override
                    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                        System.out.println("~~saveFromResponse~~");
                        System.out.println("httpUrl is " + httpUrl);
                        System.out.println("list is " + list);

                        System.out.println(list.size());
                        Cookie cookie = Cookie.parse(httpUrl, list.toString());
                        System.out.println(cookie);

//                        Cookie cookie = new Cookie.Builder()
//                                .name(list.get(0).name())
//                                .value("111")
//                                .domain("192.168.0.126")
//                                .path("")
//                                .build();
//
//                        list.add(cookie);
                    }

                    @NotNull
                    @Override
                    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                        System.out.println("~~loadForRequest~~");
                        System.out.println("httpUrl is " + httpUrl);


                        List<Cookie> list = new ArrayList<Cookie>();
//                        list.add(cookie);


//                        List<Cookie> cookies = Cookie.parseAll(url, headers);

                        return list;
                    }
                })
                .build();
        Request request = new Request.Builder()
                .get()
                .url("http://192.168.0.126/index.php")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println("~~first~~");
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        request = new Request.Builder()
                .get()
                .url("http://192.168.0.126/index.php")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println("~~second~~");
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
