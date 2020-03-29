package ch.guengel.concurrency.workunits;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class HttpCallHttpClientPooling implements UnitOfWork<String> {
    private final URI uri;
    private final PoolingHttpClientConnectionManager cm;
    private final CloseableHttpClient httpClient;


    public HttpCallHttpClientPooling() {
        uri = URI.create("https://yapet.guengel.ch/downloads/yapet-2.3.tar.xz");
        cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(20);
        cm.setMaxTotal(20);

        httpClient = HttpClients
                .custom()
                .setConnectionManager(cm)
                .build();
    }

    @Override
    public String result() {
        HttpGet httpGet = new HttpGet(uri);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            return entityToString(entity);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);

            return null;
        }
    }

    private String entityToString(HttpEntity entity) throws IOException {
        StringBuilder stringBuilder = new StringBuilder((int) entity.getContentLength());
        try (InputStream content = entity.getContent()) {
            int b;
            while ((b = content.read()) != -1) {
                stringBuilder.append((char) b);
            }

            return stringBuilder.toString();
        }
    }

    @Override
    public void close() throws IOException {
        cm.close();
        httpClient.close();
    }
}
