package com.example.rxjavaexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ArrayList<User> users = new ArrayList<>();
        users.add(new User("1", "Hien", "Quan 9"));
        users.add(new User("2", "Hien", "Quan 9"));
        users.add(new User("3", "Hien", "Quan 9"));
        users.add(new User("4", "Hien", "Quan 9"));

        createWithInterval(users);


    }


    private void createObservable(ArrayList<User> list) {
        //emit list to observer
        Observable.just(list)
                .subscribeOn(Schedulers.io())
                .observeOn(Android.mainThread())
                .subscribe(new Observer<ArrayList<User>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ArrayList<User> users) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        ;
    }

    private @NonNull Observable<ArrayList<User>> createObservableEmitEachItem(ArrayList<User> list) {
        //emit each item in list to observer
        return Observable.fromArray(list);
    }

    private @NonNull Observable<ArrayList<User>> createObservableWithDefer(ArrayList<User> list) {
        return Observable.defer(new Supplier<ObservableSource<ArrayList<User>>>() {
            @Override
            public ObservableSource<ArrayList<User>> get() throws Throwable {
                return Observable.just(list);
            }
        });
    }

    private void createWithInterval(ArrayList<User> list) {
        Observable.interval(2, TimeUnit.SECONDS)
                .flatMap(
                    i -> Observable.just(list)
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<User>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ArrayList<User> users) {
                        Log.d("list", users.size() + "");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                })
        ;

    }
}