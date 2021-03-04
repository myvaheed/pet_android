package not.forgot.again.data.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public final class ApiKeyInterceptor implements Interceptor {

    private final String token;

    private ApiKeyInterceptor(String token) {
        this.token = token;
    }

    public static Interceptor create(String token) {
        return new ApiKeyInterceptor(token);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request()
                .newBuilder()
                .addHeader("Accept", "application/json");
        if (token == null || token.isEmpty()) {
            return chain.proceed(builder.build());
        }
        builder.addHeader("Authorization", String.format("%s %s", "Bearer", token));
        return chain.proceed(builder.build());
    }
}
