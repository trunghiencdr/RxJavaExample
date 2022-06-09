package com.example.rxjavainkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var users = arrayListOf<User>(
            User("1", "Hien", "Quan 9"),
            User("2", "Hien", "Quan 9"),
            User("3", "Hien", "Quan 9"),
            User("4", "Hien", "Quan 9")
        )

//        createWithInterval(users)
//        createWithToObserver(users)
//        createWithToObserver2(users)
//        operators(users)
//        createWithPublishSubject(users)
        operatorFilter(users)
    }

    fun createWithInterval(list: ArrayList<User>) {
        Observable.interval(2, TimeUnit.SECONDS)
            .flatMap {
                Observable.just(list)    // or call api
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ArrayList<User>> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: ArrayList<User>) {
                    Log.d("list", t.size.toString())
                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })
    }

    fun createWithToObserver(list: ArrayList<User>) {
        Observable.just(list)
            .flatMap { it ->
                Observable.fromIterable(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { user ->
                    Log.d("user", user.name.toString())
                },
                onError = { error ->
                    Log.d("error", error.message.toString())
                },
                onComplete = {
                    Log.d("complete", "complete")
                }
            )
    }

    fun createWithToObserver2(list: ArrayList<User>) {
        // emit each item of array
        list.toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { user ->
                    Log.d("user2", user.name.toString())
                },
                onError = { error ->
                    Log.d("error", error.message.toString())
                },
                onComplete = {
                    Log.d("complete", "complete")
                }
            )
    }

    fun createWithlist(list: ArrayList<User>) {
        // emit each item of array
        Observable
//            .create<ArrayList<User>> {
//                it.onNext(list) // emit list user to subscribers
//                it.onComplete()
//            }
            .create(object : ObservableOnSubscribe<ArrayList<User>> {
                override fun subscribe(emitter: ObservableEmitter<ArrayList<User>>) {
                    Log.d("on subcribe", "")
                    if (!emitter.isDisposed)
                        emitter.onNext(list)
                    emitter.onComplete()

                }

            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { users ->
                    Log.d("user2", users.size.toString())
                },
                onError = { error ->
                    Log.d("error", error.message.toString())
                },
                onComplete = {
                    Log.d("complete", "complete")
                }
            )
    }

    fun operators(list: ArrayList<User>) {
        handleObserver(Observable.just(list),
            { users -> Log.d("onNext", users.size.toString()) },
            { throwable -> Log.d("onErrer", throwable.localizedMessage) },
            { Log.d("onComplete", "") })
    }

    fun <T : Any> handleObserver(
        observable: Observable<T>,
        onNext: (T) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit
    ) {
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = onNext,
                onError = onError,
                onComplete = onComplete
            )
    }

    fun createWithPublishSubject(list: ArrayList<User>) {
        val publishSubject = PublishSubject.create<User>()
        publishSubject.subscribeBy(
            onNext = { user ->
                Log.d("PublishSubject", "observer1: ${user.id}")
            }
        )
        publishSubject.onNext(list[0])
        publishSubject.onNext(list[1])
        publishSubject.onNext(list[2])
        publishSubject.subscribeBy(
            onNext = { user ->
                Log.d("PublishSubject", "observer2: ${user.id}")
            }
        )
        publishSubject.onNext(list[3])
        publishSubject.onComplete()

        // tương tự với replaysubject: không quan tâm vào lúc nào, vẫn nghe được hết từ đầu đến cuối
        // async subject: Chỉ nghe được thằng cuối cùng và onComplete
        // behavior subject: nghe được trước nó 1 thằng và sau nó 1 thằng và onComplete
    }

    fun operatorFilter(list: ArrayList<User>){
        var observable = Observable.just(list)
            .flatMap { users -> Observable.fromIterable(users) }
            .filter { user -> user.id.toInt() >= 2 }

        handleObserver(observable,
            {user -> Log.d("Filter", user.id)}, {}, {})
    }


}