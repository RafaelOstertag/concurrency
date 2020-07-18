package ch.guengel.concurrency.workunits;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class HttpCallOkHttpBlocking implements UnitOfWork<String> {
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public String result() {
        Request request = new Request.Builder()
                .url("http://localhost/image.png")
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);

            return null;
        }
    }

    @Override
    public void close() {
        // no impl
    }
}
