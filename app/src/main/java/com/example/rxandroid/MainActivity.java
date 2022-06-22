package com.example.rxandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private Disposable mdisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observer<User> observer = getObserverUser();
        Observable<User> observable = getObservableUsers();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private Observer<User> getObserverUser(){
        return new Observer<User>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.e("Log", "onSubcribe");
                mdisposable = d;
            }

            @Override
            public void onNext(@NonNull User user) {
                Log.e("Log", "onNext"+user.toString());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("Log", "onError");
            }

            @Override
            public void onComplete() {
                Log.e("Log", "onComplete");
            }
        };
    }

    private Observable<User> getObservableUsers(){
        List<User> listUser = getListUsers();
        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<User> emitter) throws Throwable {
                if (listUser == null || listUser.isEmpty()){
                    if (!emitter.isDisposed()){
                        emitter.onError(new Exception());
                    }
                }
                for (User user: listUser){
                    if (!emitter.isDisposed()){
                        emitter.onNext(user);
                    }

                }
                if (!emitter.isDisposed()){
                    emitter.onComplete();
                }
            }
        });

    }

    private List<User> getListUsers(){
        List<User> list = new ArrayList<>();
        list.add(new User(1, "tuananh1"));
        list.add(new User(2, "tuananh2"));
        list.add(new User(3, "tuananh3"));
        list.add(new User(4, "tuananh4"));
        list.add(new User(5, "tuananh5"));

        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mdisposable != null){
            mdisposable.dispose();
        }
    }
}