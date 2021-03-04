package not.forgot.again.data.network;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import not.forgot.again.data.utils.BooleanJsonMapper;
import not.forgot.again.data.utils.TaskJsonMapper;
import not.forgot.again.model.entities.Task;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiFactory {

    private static OkHttpClient sClient;

    private static volatile NFAService sService;

    private ApiFactory() {
    }

    @NonNull
    public static NFAService getNFAService() {
        NFAService service = sService;
        if (service == null) {
            synchronized (ApiFactory.class) {
                service = sService;
                if (service == null) {
                    service = sService = buildRetrofit().create(NFAService.class);
                }
            }
        }
        return service;
    }

    public static NFAService recreate(String token) {
        sClient = null;
        sClient = getClient(token);
        sService = buildRetrofit().create(NFAService.class);
        return sService;
    }

    @NonNull
    private static Retrofit buildRetrofit() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Boolean.class, new BooleanJsonMapper())
                .registerTypeAdapter(Task.class, new TaskJsonMapper());

        Gson gson = builder.create();

        return new Retrofit.Builder().baseUrl("http://practice.mobile.kreosoft.ru/api/")
                .client(getClient(""))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @NonNull
    private static OkHttpClient getClient(String token) {
        OkHttpClient client = sClient;
        if (client == null) {
            synchronized (ApiFactory.class) {
                client = sClient;
                if (client == null) {
                    client = sClient = buildClient(token);
                }
            }
        }
        return client;
    }

    @NonNull
    private static OkHttpClient buildClient(String token) {
        return new OkHttpClient.Builder().addInterceptor(ApiKeyInterceptor.create(token)).build();
    }
}

