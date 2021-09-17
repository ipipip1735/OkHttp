import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.cert.Certificate;
import java.util.List;

/**
 * Created by Administrator on 2021/8/30.
 */
public class TheEventListener extends okhttp3.EventListener {

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
        System.out.println("call = " + call + ", handshake = " + handshake);
        super.secureConnectEnd(call, handshake);

        //打印证书链
//        for (Certificate certificate : handshake.peerCertificates()) {
//            System.out.println("certificate = " + certificate);
//        }

    }

    @Override
    public void secureConnectStart(@NotNull Call call) {
        System.out.println("~~EventListener.secureConnectStart~~");
        System.out.println("call = " + call);
        super.secureConnectStart(call);
    }

}