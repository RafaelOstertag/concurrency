package ch.guengel.concurrency.workunits;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;

public class HttpCallHttpClient implements UnitOfWork<String> {
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Override
    public String result() {
        HttpGet httpGet = new HttpGet("http://localhost/image.png");
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
        httpClient.close();
    }
}
