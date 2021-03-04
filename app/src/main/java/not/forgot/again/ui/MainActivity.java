package not.forgot.again.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import io.reactivex.subjects.BehaviorSubject;
import not.forgot.again.R;
import not.forgot.again.data.db.Db;
import not.forgot.again.data.ds.LocalDS;
import not.forgot.again.data.ds.NetworkDS;
import not.forgot.again.data.impl_repositories.NFARepositoryImpl;
import not.forgot.again.data.network.ApiFactory;
import not.forgot.again.data.GlobalStore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}