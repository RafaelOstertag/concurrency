package ch.guengel.concurrency.workunits;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class HttpCallOkHttpAsync implements UnitOfWork<String> {
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public String result() {
        Request request = new Request.Builder()
                .url("https://yapet.guengel.ch/downloads/yapet-2.3.tar.xz")
                .build();

        CompletableFuture<String> future = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                future.complete(response.body().string());
            }
        });

        return future.join();
    }

    @Override
    public void close() {
        // no impl
    }
}
