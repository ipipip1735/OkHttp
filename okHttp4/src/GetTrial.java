import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2020/11/9.
 */
public class GetTrial {

    //    static private String URL = "http://localhost/cookie.php";
//    static private String URL = "http://localhost/cache.php";
//    static private String URL = "http://localhost/authorization.php";
    static private String URL = "http://localhost/get.php";
//    static private String URL = "http://localhost/redirect.php";
//    static private String URL = "http://localhost/retry.php";
//    static private String URL = "https://docs.oracle.com/javase/8/docs/technotes/guides/language/generics.html";

    public static void main(String[] args) {

        GetTrial getTrial = new GetTrial();
//        getTrial.get();
//        getTrial.getWithAuthorization();
//        getTrial.getWithRetry();
//        getTrial.getWithRedirect();
//        getTrial.getWithTimeout();
//        getTrial.getWithCookies();

//        getTrial.getConnectionPool();
//        getTrial.getWithCache();
//        getTrial.getWithEventListener();
        getTrial.getWithInterceptors();


    }

    private void getWithRedirect() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .eventListener(new EventListener())
                .build();

        Request request = new Request.Builder()
                .get()
                .url(URL)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getConnectionPool() {

        String URL = "http://192.168.1.0/retry.php";

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(3, 5000L, TimeUnit.MICROSECONDS))
                .build();


        Request request = new Request.Builder()
                .get()
                .url(URL)
                .build();

        okHttpClient.connectionPool().evictAll();


        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void getWithTimeout() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1000L, TimeUnit.MICROSECONDS)
                .readTimeout(1000L, TimeUnit.MICROSECONDS)
                .writeTimeout(1000L, TimeUnit.MICROSECONDS)
                .callTimeout(1000L, TimeUnit.MICROSECONDS)
                .build();

        Request request = new Request.Builder()
                .get()
                .url(URL)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getWithRetry() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //.retryOnConnectionFailure(false)
                .eventListener(new EventListener())
                .build();

        Request request = new Request.Builder()
                .get()
                .url(URL)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int responseCount(Response response) {
        System.out.println("~~responseCount~~");
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            System.out.println(result + "|response is " + response);
            result++;
        }
        return result;
    }

    private void getWithAuthorization() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Nullable
                    @Override
                    public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
                        System.out.println("~~authenticate~~");
                        System.out.println("route = " + route);
                        System.out.println("response = " + response);

                        if (response.request().header("Authorization") != null) {
                            return null;
                        }

                        System.out.println("Challenges: " + response.challenges());

//                        if (responseCount(response) >= 3) {
//                            return null;
//                        }

                        String credential = Credentials.basic("jesse", "password");

                        return response.request().newBuilder()
                                .header("Authorization", credential)
                                .build();
                    }
                })
                .eventListener(new EventListener())
                .build();


        Request request = new Request.Builder()
                .get()
//                .header("Authorization", "bearer d80ee24d-68c5-484b-b560-ddc4aff389fe")
                .url(URL)
                .build();


        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void getWithInterceptors() {

        //例一：基本使用
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addNetworkInterceptor(new Interceptor("NetworkInterceptor"))
//                .addInterceptor(new Interceptor("Interceptor"))
//                .build();
//        Request request = new Request.Builder()
//                .get()
//                .url(URL)
//                .build();
//        try (Response response = okHttpClient.newCall(request).execute()) {
//            System.out.println(response.body().string());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        //例二：拦截器链
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addNetworkInterceptor(new Interceptor("NetworkInterceptorOne"))
//                .addNetworkInterceptor(new Interceptor("NetworkInterceptorTwo"))
//                .build();
//        Request request = new Request.Builder()
//                .get()
//                .url(URL)
//                .build();
//        try (Response response = okHttpClient.newCall(request).execute()) {
//            System.out.println(response.body().string());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        //处理请求主体
        OkHttpClient client = new OkHttpClient.Builder()
//                .post()
//                .addNetworkInterceptor(new okhttp3.Interceptor() {
//                    @NotNull
//                    @Override
//                    public Response intercept(@NotNull Chain chain) throws IOException {
//                        System.out.println("~~intercept~~");
//                        System.out.println(chain);
//
//
//
//
//                        return null;
//                    }
//                })
                .build();

        Request request = new Request.Builder()
                .url("http://www.publicobject.com/helloworld.txt")
                .header("User-Agent", "OkHttp Example")
                .build();

        Response response = client.newCall(request).execute();
        response.body().close();


    }

    private void getWithEventListener() {

        //方式一：使用EventListener
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .eventListener(EventListener.NONE)//使用空监听器
                .eventListener(new EventListener())
                .build();
        Request request = new Request.Builder()
                .get()
                .url(URL)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }


        //方式二：使用监听器工厂
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .eventListenerFactory(new EventListener.Factory() {
//                    @NotNull
//                    @Override
//                    public EventListener create(@NotNull Call call) {
//                        return new TheEventListener();
//                    }
//                })
//                .build();
//        Request request = new Request.Builder()
//                .get()
//                .url(URL)
//                .build();
//        try (Response response = okHttpClient.newCall(request).execute()) {
//            System.out.println(response.body().string());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private void getWithCache() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(new Cache(new File("okHttp4/res"), 50L * 1024L * 1024L))
                .build();
        Request request = new Request.Builder()
                .get()
//                .cacheControl(CacheControl.FORCE_CACHE)
                .url(URL)
                .build();


        try (Response response = okHttpClient.newCall(request).execute()) {

            System.out.println(response.body().string());

            System.out.println("Response 2 response:          " + response);
            System.out.println("Response 2 cache response:    " + response.cacheResponse());
            System.out.println("Response 2 network response:  " + response.networkResponse());


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getWithCookies() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
                    List<Cookie> list = new ArrayList<Cookie>();

                    @Override
                    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                        System.out.println("~~saveFromResponse~~");
                        System.out.println("httpUrl is " + httpUrl);
                        System.out.println("list is " + list);


                        cookieStore.put(httpUrl, list);
                    }

                    @NotNull
                    @Override
                    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                        System.out.println("~~loadForRequest~~");
                        System.out.println("httpUrl is " + httpUrl);

                        List<Cookie> cookies = cookieStore.get(httpUrl);
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();

        Request request = new Request.Builder()
                .get()
                .url(URL)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println("~~first~~");
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }


        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println("~~second~~");
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void get() {

        //同步请求
//        OkHttpClient okHttpClient = new OkHttpClient();
//        Request request = new Request.Builder()
//                .get()
////                .method("GET", null)
//                .header("one", "111")
//                .addHeader("two", "222")
//                .url(URL)
//                .build();
//
//
//        Call call = okHttpClient.newCall(request);
//
//        try (Response response = call.execute()) {
//            System.out.println(response.headers());
//            System.out.println(response.body().string());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        //异步请求
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().get().url(URL).build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("~~onFailure~~");
                System.out.println("call is " + call);
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("~~onResponse~~");
                System.out.println("call is " + call);
                System.out.println("response is " + response);

                System.out.println(response.body().string());
            }
        });


//        okHttpClient.dispatcher().setIdleCallback(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("~~idleCallback.run~~");
//            }
//        });
//        for (int i = 0; i < 3; i++) {
//
//            try {
//                Thread.sleep(1500L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//
//        }


//        okHttpClient.dispatcher().executorService().shutdown();

    }


    class EventListener extends okhttp3.EventListener {


        @Override
        public void cacheConditionalHit(@NotNull Call call, @NotNull Response cachedResponse) {
            System.out.println("~~EventListener.cacheConditionalHit~~");
            super.cacheConditionalHit(call, cachedResponse);
        }

        @Override
        public void cacheHit(@NotNull Call call, @NotNull Response response) {
            System.out.println("~~EventListener.cacheHit~~");
            super.cacheHit(call, response);
        }

        @Override
        public void cacheMiss(@NotNull Call call) {
            System.out.println("~~EventListener.cacheMiss~~");
            super.cacheMiss(call);
        }

        @Override
        public void callEnd(@NotNull Call call) {
            System.out.println("~~EventListener.callEnd~~");
            super.callEnd(call);
        }

        @Override
        public void callFailed(@NotNull Call call, @NotNull IOException ioe) {
            System.out.println("~~EventListener.callFailed~~");
            super.callFailed(call, ioe);
        }

        @Override
        public void callStart(@NotNull Call call) {
            System.out.println("~~EventListener.callStart~~");
            super.callStart(call);
        }

        @Override
        public void canceled(@NotNull Call call) {
            System.out.println("~~EventListener.canceled~~");
            super.canceled(call);
        }

        @Override
        public void connectEnd(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress, @NotNull Proxy proxy, @Nullable Protocol protocol) {
            System.out.println("~~EventListener.connectEnd~~");
            super.connectEnd(call, inetSocketAddress, proxy, protocol);
        }

        @Override
        public void connectFailed(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress, @NotNull Proxy proxy, @Nullable Protocol protocol, @NotNull IOException ioe) {
            System.out.println("~~EventListener.connectFailed~~");
            System.out.println("call = " + call + ", inetSocketAddress = " + inetSocketAddress + ", proxy = " + proxy + ", protocol = " + protocol + ", ioe = " + ioe);
            super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
        }

        @Override
        public void connectStart(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress, @NotNull Proxy proxy) {
            System.out.println("~~EventListener.connectStart~~");
            super.connectStart(call, inetSocketAddress, proxy);
        }

        @Override
        public void connectionAcquired(@NotNull Call call, @NotNull Connection connection) {
            System.out.println("~~EventListener.connectionAcquired~~");
            super.connectionAcquired(call, connection);
        }

        @Override
        public void connectionReleased(@NotNull Call call, @NotNull Connection connection) {
            System.out.println("~~EventListener.connectionReleased~~");
            super.connectionReleased(call, connection);
        }

        @Override
        public void dnsEnd(@NotNull Call call, @NotNull String domainName, @NotNull List<InetAddress> inetAddressList) {
            System.out.println("~~EventListener.dnsEnd~~");
            super.dnsEnd(call, domainName, inetAddressList);
        }

        @Override
        public void dnsStart(@NotNull Call call, @NotNull String domainName) {
            System.out.println("~~EventListener.dnsStart~~");
            super.dnsStart(call, domainName);
        }

        @Override
        public void proxySelectEnd(@NotNull Call call, @NotNull HttpUrl url, @NotNull List<Proxy> proxies) {
            System.out.println("~~EventListener.proxySelectEnd~~");
            super.proxySelectEnd(call, url, proxies);
        }

        @Override
        public void proxySelectStart(@NotNull Call call, @NotNull HttpUrl url) {
            System.out.println("~~EventListener.proxySelectStart~~");
            super.proxySelectStart(call, url);
        }

        @Override
        public void requestBodyEnd(@NotNull Call call, long byteCount) {
            System.out.println("~~EventListener.requestBodyEnd~~");
            super.requestBodyEnd(call, byteCount);
        }

        @Override
        public void requestBodyStart(@NotNull Call call) {
            System.out.println("~~EventListener.requestBodyStart~~");
            super.requestBodyStart(call);
        }

        @Override
        public void requestFailed(@NotNull Call call, @NotNull IOException ioe) {
            System.out.println("~~EventListener.requestFailed~~");
            super.requestFailed(call, ioe);
        }

        @Override
        public void requestHeadersEnd(@NotNull Call call, @NotNull Request request) {
            System.out.println("~~EventListener.requestHeadersEnd~~");
            super.requestHeadersEnd(call, request);
        }

        @Override
        public void requestHeadersStart(@NotNull Call call) {
            System.out.println("~~EventListener.requestHeadersStart~~");
            super.requestHeadersStart(call);
        }

        @Override
        public void responseBodyEnd(@NotNull Call call, long byteCount) {
            System.out.println("~~EventListener.responseBodyEnd~~");
            super.responseBodyEnd(call, byteCount);
        }

        @Override
        public void responseBodyStart(@NotNull Call call) {
            System.out.println("~~EventListener.responseBodyStart~~");
            super.responseBodyStart(call);
        }

        @Override
        public void responseFailed(@NotNull Call call, @NotNull IOException ioe) {
            System.out.println("~~EventListener.responseFailed~~");
            super.responseFailed(call, ioe);
        }

        @Override
        public void responseHeadersEnd(@NotNull Call call, @NotNull Response response) {
            System.out.println("~~EventListener.responseHeadersEnd~~");
            super.responseHeadersEnd(call, response);
        }

        @Override
        public void responseHeadersStart(@NotNull Call call) {
            System.out.println("~~EventListener.responseHeadersStart~~");
            super.responseHeadersStart(call);
        }

        @Override
        public void satisfactionFailure(@NotNull Call call, @NotNull Response response) {
            System.out.println("~~EventListener.satisfactionFailure~~");
            super.satisfactionFailure(call, response);
        }

        @Override
        public void secureConnectEnd(@NotNull Call call, @Nullable Handshake handshake) {
            System.out.println("~~EventListener.secureConnectEnd~~");
            super.secureConnectEnd(call, handshake);
        }

        @Override
        public void secureConnectStart(@NotNull Call call) {
            System.out.println("~~EventListener.secureConnectStart~~");
            super.secureConnectStart(call);
        }

    }


    class CallBack implements Callback {

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println("~~CallBack.onFailure~~");
            System.out.println("call = " + call + ", e = " + e);
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            System.out.println("~~CallBack.onResponse~~");
            System.out.println("call = " + call + ", response = " + response);

        }
    }

    class Interceptor implements okhttp3.Interceptor {
        String tag;

        public Interceptor(String tag) {
            this.tag = tag;
        }

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            System.out.println("~~" + tag + ".intercept~~");
            System.out.println("chain is " + chain);

            Request request = chain.request();
            System.out.println(tag + "-1|chain.connection is " + chain.connection());
            System.out.println(tag + "-1|request.url is " + request.url());
            System.out.println(tag + "-1|request.headers is " + request.headers());
            System.out.println(tag + "-1|request.body is " + request.body());
            request = request.newBuilder().url("http://localhost/retry.php").build();


            Response response = chain.proceed(request);
            System.out.println(tag + "-2|chain.connection is " + chain.connection());
            System.out.println(tag + "-2|request.url is " + request.url());
            System.out.println(tag + "-2|request.headers is " + request.headers());
            System.out.println(tag + "-2|request.body is " + request.body());
            System.out.println(tag + "-2|response.body is " + response.body());


            return response;
        }
    }
}
