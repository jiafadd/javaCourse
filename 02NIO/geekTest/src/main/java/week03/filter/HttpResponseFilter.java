package week03.filter;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * Created by jiafa
 * on 2021/11/23 10:28
 */
public interface HttpResponseFilter {
    void filter(FullHttpResponse response);
}
