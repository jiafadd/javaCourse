package week026;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jiafa
 * on 2021/11/13 22:07
 */
public class HttpClient01 {
    
    private static final String GET_URL = "http://localhost:8001";

    private static final String USER_AGENT = "Mozilla/5.0";

    /**
     * 1. 使用帮助类HttpClients创建CloseableHttpClient对象.
     * 2. 基于要发送的HTTP请求类型创建HttpGet或者HttpPost实例.
     * 3. 使用addHeader方法添加请求头部,诸如User-Agent, Accept-Encoding等参数.
     * 4. 通过执行此HttpGet或者HttpPost请求获取CloseableHttpResponse实例
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(GET_URL);
        httpGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = bufferedReader.readLine()) != null){
            response.append(inputLine);
        }
        bufferedReader.close();
        System.out.println(response.toString());
        httpClient.close();
    }
}
