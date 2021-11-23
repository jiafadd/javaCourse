package week03.filter;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * Created by jiafa
 * on 2021/11/23 10:30
 */
public class HeaderHttpResponseFilter implements HttpResponseFilter{
    @Override
    public void filter(FullHttpResponse response) {
        response.headers().set("xjava","kimmking");
    }
}
