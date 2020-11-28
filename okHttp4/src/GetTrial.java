import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.LoggingEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2020/11/9.
 */
public class GetTrial {

//    static private String URL = "http://localhost/cookie.php";
//    static private String URL = "http://localhost/cache.php";
//    static private String URL = "http://localhost/authorization.php";
    static private String URL = "http://localhost/get.php";
//    static private String URL = "http://localhost/post.php";
//    static private String URL = "http://localhost/redirect.php";
//    static private String URL = "http://localhost/retry.php";
//    static private String URL = "https://docs.oracle.com/javase/8/docs/technotes/guides/language/generics.html";

    public static void main(String[] args) {

        GetTrial getTrial = new GetTrial();
//        System.out.println("OkHttp.VERSION is " + OkHttp.VERSION);


//        getTrial.get();//同/异步请求
//        getTrial.getWithAuthorization();//身份认证
//        getTrial.getWithRetry();//失败重试
//        getTrial.getWithRedirect();//重定向
//        getTrial.getWithTimeout();//超时限制
//        getTrial.getWithCookies();//Cookie容器
//        getTrial.getWithCache();//缓存
//        getTrial.getConnectionPool();//连接池
//        getTrial.getDispatcher();//线程池
//        getTrial.getWithEventListener();//生命周期函数
//        getTrial.getWithInterceptors();//拦截器
//        getTrial.logging();//日志

    }


    private void logging() {

        //使用日志拦截器
//        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor()
////                .setLevel(HttpLoggingInterceptor.Level.BASIC);
//                .setLevel(HttpLoggingInterceptor.Level.BODY);
////                .setLevel(HttpLoggingInterceptor.Level.HEADERS);
////                .setLevel(HttpLoggingInterceptor.Level.NONE);
//
//
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(httpLoggingInterceptor)
//                .build();
//
//        Request request = new Request.Builder()
//                .get()
//                .post(new FormBody.Builder().add("xxx", "yyy").build())
//                .header("one", "111")
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



        //使用日志监听器
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .eventListenerFactory(new LoggingEventListener.Factory(HttpLoggingInterceptor.Logger.DEFAULT))
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

    private void getDispatcher() {

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.dispatcher().setIdleCallback(new Runnable() {//线程空闲时触发
            @Override
            public void run() {
                System.out.println("~~idleCallback.run~~");
            }
        });

        Request request = new Request.Builder().get().url(URL).build();
        Call call = okHttpClient.newCall(request);


        //使用同步请求
//        try (Response response = call.execute()) {
//            System.out.println(response.body().string());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //使用异步请求
//        call.enqueue(new CallBack());


        okHttpClient.dispatcher().executorService().shutdown();//关闭线程池
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
//                .connectTimeout(1L, TimeUnit.SECONDS)
//                .readTimeout(1L, TimeUnit.SECONDS)
//                .writeTimeout(L, TimeUnit.SECONDS)
                .callTimeout(1L, TimeUnit.SECONDS)
                .build();


        System.out.println("callTimeoutMillis is " + okHttpClient.callTimeoutMillis());

        Request request = new Request.Builder()
                .get()
                .url(URL)
                .build();

        Call call = okHttpClient.newCall(request);
        System.out.println("timeoutNanos is " + call.timeout().timeoutNanos());


        try (Response response = call.execute()) {
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
//                .authenticator(Authenticator.JAVA_NET_AUTHENTICATOR)//使用内置授权提供器，处理407异常
//                .authenticator(Authenticator.NONE)//使用内置授权提供器，直接返回Null
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

//                        if (responseCount(response) >= 3) { //统计重试次数，防止死循环
//                            return null;
//                        }

                        String credential = Credentials.basic("jesse", "password");
                        System.out.println("credential = " + credential);

                        return response.request().newBuilder()
                                .header("Authorization", credential)
                                .build();
                    }
                })
                .eventListener(new EventListener())
                .build();


        Request request = new Request.Builder()
                .get()
//                .header("Authorization", Credentials.basic("jesse", "password"))//不提供授权头字段
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
//        OkHttpClient client = new OkHttpClient.Builder()
////                .post()
////                .addNetworkInterceptor(new okhttp3.Interceptor() {
////                    @NotNull
////                    @Override
////                    public Response intercept(@NotNull Chain chain) throws IOException {
////                        System.out.println("~~intercept~~");
////                        System.out.println(chain);
////
////
////
////
////                        return null;
////                    }
////                })
//                .build();
//
//        Request request = new Request.Builder()
//                .url("http://www.publicobject.com/helloworld.txt")
//                .header("User-Agent", "OkHttp Example")
//                .build();
//
//        Response response = client.newCall(request).execute();
//        response.body().close();


    }

    private void getWithEventListener() {

        //方式一：使用EventListener
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
////                .eventListener(EventListener.NONE)//使用空监听器
//                .eventListener(new EventListener())
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


        //方式二：使用监听器工厂
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .eventListenerFactory(new EventListener.Factory() {
                    @NotNull
                    @Override
                    public EventListener create(@NotNull Call call) {
                        return new EventListener();
                    }
                })
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

                        if (cookies != null) {
                            System.out.println(cookies);
                            cookies = cookies.stream().filter(cookie -> {

                                System.out.println("matches is " + cookie.matches(httpUrl));
                                System.out.println("hostOnly is " + cookie.hostOnly());
                                System.out.println("expiresAt is " + cookie.expiresAt() + "|" + System.currentTimeMillis());
                                System.out.println("persistent is " + cookie.persistent());

                                return true;
                            }).collect(Collectors.toList());
                        }


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

        try {
            Thread.sleep(6000L);
        } catch (InterruptedException e) {
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
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
//                .method("GET", null)
                .header("one", "111")
                .addHeader("two", "222")
                .url(URL)
                .build();


        Call call = okHttpClient.newCall(request);

        try (Response response = call.execute()) {
            System.out.println(response.headers());
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }


        //异步请求
//        OkHttpClient okHttpClient = new OkHttpClient();
//        Request request = new Request.Builder().get().url(URL).build();
//
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                System.out.println("~~onFailure~~");
//                System.out.println("call is " + call);
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                System.out.println("~~onResponse~~");
//                System.out.println("call is " + call);
//                System.out.println("response is " + response);
//
//                System.out.println(response.body().string());
//            }
//        });


        //打印二进制数据
//        OkHttpClient okHttpClient = new OkHttpClient();
//        Request request = new Request.Builder()
//                .get()
//                .url("http://localhost/a.jpg")
//                .build();
//
//        Call call = okHttpClient.newCall(request);
//
//        try (Response response = call.execute()) {
//            ByteString byteString = response.body().byteString();
//
//            System.out.println("size is " + byteString.size());
//            System.out.println("base64 is " + byteString.base64());
//            System.out.println("md5 is " + byteString.md5());
//            System.out.println("hmacSha256 is " + byteString.hmacSha256(byteString));
//
////            byteString.write(new FileOutputStream("res/b.jpg"));
////            byteString.toByteArray();
////            ByteBuffer byteBuffer = byteString.asByteBuffer();
////            System.out.println("byteBuffer = " + byteBuffer);
//
//
//
//            ByteString bs = new ByteString("abcdabcd".getBytes());
//            System.out.println("startsWith is " + bs.startsWith("bcd".getBytes()));
//            System.out.println("endsWith is " + bs.endsWith("bcd".getBytes()));
//            System.out.println("string is " + bs.string(UTF_8));
//            System.out.println("utf8 is " + bs.utf8());
//            System.out.println("hex is " + bs.hex());
//
//            System.out.println("substring is " + bs.substring(0, bs.size()/2));
//            System.out.println("getByte is " + bs.getByte(bs.size()/2));
//            System.out.println("indexOf is " + bs.indexOf("bc".getBytes()));
//            System.out.println("lastIndexOf is " + bs.lastIndexOf("bc".getBytes()));
//
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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

            System.out.println(response.body().string());

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
