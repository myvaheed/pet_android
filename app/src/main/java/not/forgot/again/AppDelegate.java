package not.forgot.again;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import io.reactivex.subjects.BehaviorSubject;
import not.forgot.again.data.GlobalStore;
import not.forgot.again.data.db.Db;
import not.forgot.again.data.ds.LocalDS;
import not.forgot.again.data.ds.NetworkDS;
import not.forgot.again.data.impl_repositories.NFARepositoryImpl;
import not.forgot.again.data.network.ApiFactory;
import not.forgot.again.model.repositories.NFARepository;

public class AppDelegate extends Application {
    private BehaviorSubject<Boolean> networkObserver = BehaviorSubject.createDefault(true);
    private static GlobalStore globalStore;

    @Override
    public void onCreate() {
        super.onCreate();
        NFARepository nfaRepository = new NFARepositoryImpl(new LocalDS(Db.getInstance(this)), new NetworkDS(ApiFactory
                .getNFAService()), networkObserver);
        globalStore = new GlobalStore(nfaRepository);
    }

    public static GlobalStore getGlobalStore() {
        return globalStore;
    }
}
