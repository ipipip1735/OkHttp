import okhttp3.*;
import okhttp3.Authenticator;

import okhttp3.EventListener;
import okhttp3.internal.tls.CertificateChainCleaner;
import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.LoggingEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2020/11/9.
 */
public class GetTrial {

    //    static private String URL = "http://localhost/cookie.php";
//    static private String URL = "http://localhost/cache.php";
//    static private String URL = "http://localhost/authorization.php";
//    static private String URL = "http://localhost/get.php";
//    static private String URL = "http://localhost/post.php";
//    static private String URL = "http://localhost/redirect.php";
//    static private String URL = "http://localhost/retry.php";
    static private String URL = "https://docs.oracle.com/javase/8/docs/technotes/guides/language/generics.html";

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

        //HTTPS连接
//        getTrial.httpsWithConnectionSpec();//指定TSL版本和加密算法
        getTrial.httpsWithCertificatePinner();//绑定公钥摘要
//        getTrial.httpsWithSSLSocket();//自定义SSL连接

    }

    private void httpsWithSSLSocket() {
        String URL = "https://publicobject.com/robots.txt";
//        String URL = "https://www.baidu.com";


        X509TrustManager[] trustManagers = new X509TrustManager[1];
        //方式一：自定义受信管理器
        trustManagers[0] = new X509TrustManager(){
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                System.out.println("~~X509TrustManager.checkClientTrusted~~");
                System.out.println("x509Certificates = " + Arrays.deepToString(x509Certificates) + ", s = " + s);
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                System.out.println("~~X509TrustManager.checkServerTrusted~~");
//                System.out.println("x509Certificates = " + Arrays.deepToString(x509Certificates) + ", s = " + s);
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                System.out.println("~~X509TrustManager.getAcceptedIssuers~~");

                try (InputStream inStream = Files.newInputStream(Path.of("okHttp4/res/ca/root.cer"))) {
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");//创建工厂对象
                    Certificate certificate = cf.generateCertificate(inStream);//获取证书对象集
                    System.out.println("certificate = " + certificate);
                    return new X509Certificate[]{(X509Certificate) certificate};
                } catch (IOException | CertificateException e) {
                    e.printStackTrace();
                }
                return new X509Certificate[0];
            }
        };

        //方式二
        try (InputStream inStream = Files.newInputStream(Path.of("okHttp4/res/ca/root.cer"))) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");//创建工厂对象
            X509Certificate x509Certificate = (X509Certificate) cf.generateCertificate(inStream);//获取证书对象集

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(null, null);
            keyStore.setCertificateEntry("root", x509Certificate);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
            trustManagerFactory.init(keyStore);
            trustManagers[0] = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }




        SSLSocketFactory sslSocketFactory;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManagers[0])
                .eventListener(new TheEventListener())
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

    private void httpsWithCertificatePinner() {
        String URL = "https://publicobject.com/robots.txt";
//        String URL = "https://www.baidu.com";


        //创建证书绑定器
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add("publicobject.com", "sha256/afwiKY3RxoMmLkuRW1l7QsPZTJPwDS2pdDROQjXw8ig=", "sha256/RHF4AjPA0S2CWTzbJ376hUO4YziSi2Tk+iO12TJHj9c=")
                .build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .certificatePinner(certificatePinner)
//                .hostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String s, SSLSession sslSession) {
//                        System.out.println("~~GetTrial.verify~~");
//                        System.out.println("s = " + s + ", sslSession = " + sslSession);
//
//                        return HttpsURLConnection.getDefaultHostnameVerifier().verify(s, sslSession);
//                    }
//                })
                .eventListener(new TheEventListener())
                .build();

        Request request = new Request.Builder()
                .get()
                .url(URL)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
//            System.out.println(response.body().string());

            //打印证书
            System.out.println("peerCertificates() = " + response.handshake().peerCertificates());
//            List l = response.handshake().peerCertificates();
//            Object o = l.get(0);

            //打印证书和摘要
//            for (Certificate certificate : response.handshake().peerCertificates()) {
////                System.out.println("certificate = " + certificate);
//                System.out.println(CertificatePinner.pin(certificate));//打印证书SHA256摘要
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void httpsWithConnectionSpec() {
        //创建标准
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)//创建MODERN_TLS，以此为基础进行修改
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
//                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
                .eventListener(new TheEventListener())
                .build();

        Request request = new Request.Builder()
                .get()
                .url(URL)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println("response.handshake() = " + response.handshake());
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                .eventListenerFactory((EventListener.Factory) new LoggingEventListener.Factory(HttpLoggingInterceptor.Logger.DEFAULT))
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
                .eventListener(new TheEventListener())
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
                .eventListener(new TheEventListener())
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
                .eventListener(new TheEventListener())
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
//                .eventListener(new TheEventListener())
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
                .eventListenerFactory(new TheEventListener.Factory() {
                    @NotNull
                    @Override
                    public EventListener create(@NotNull Call call) {
                        return new TheEventListener();
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
                .cache(new Cache(new File("okHttp4/res"), 50L * 1024L * 1024L))//开启缓存，设置缓存目录
                .build();
        Request request = new Request.Builder()
                .get()
                .cacheControl(CacheControl.FORCE_CACHE)//配置请求时的缓存控制策略
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

        //方式一：自定义CookieJar
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .cookieJar(new CookieJar() {
//                    HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
//
//                    @Override
//                    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
//                        System.out.println("~~saveFromResponse~~");
//                        System.out.println("httpUrl is " + httpUrl);
//                        System.out.println("list is " + list);
//
//
//                        cookieStore.put(httpUrl, list);
//                    }
//
//                    @NotNull
//                    @Override
//                    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
//                        System.out.println("~~loadForRequest~~");
//                        System.out.println("httpUrl is " + httpUrl);
//
//                        List<Cookie> cookies = cookieStore.get(httpUrl);
//
//                        if (cookies != null) {
//                            System.out.println(cookies);
//                            cookies = cookies.stream().filter(cookie -> {
//
//                                System.out.println("domain is " + cookie.domain());
//                                System.out.println("hostOnly is " + cookie.httpOnly());
//                                System.out.println("matches is " + cookie.matches(httpUrl));
//                                System.out.println("hostOnly is " + cookie.hostOnly());
//                                System.out.println("expiresAt is " + cookie.expiresAt() + "|" + System.currentTimeMillis());
//                                System.out.println("persistent is " + cookie.persistent());
//
//                                return true;
//                            }).collect(Collectors.toList());
//                        }
//
//
//                        return cookies != null ? cookies : new ArrayList<Cookie>();
//                    }
//                })
//                .build();

        //方式二：使用CookieManager
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(new CookiePolicy() {
            @Override
            public boolean shouldAccept(URI uri, HttpCookie cookie) {
                System.out.println("~~GetTrial.shouldAccept~~");
                System.out.println("uri = " + uri + ", cookie = " + cookie);
                return true;
            }
        });
        CookieHandler.setDefault(cookieManager);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(CookieHandler.getDefault()))
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
            Thread.sleep(3000L);
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
