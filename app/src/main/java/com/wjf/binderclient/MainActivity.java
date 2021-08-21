package com.wjf.binderclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.View;

import com.wanjf.baseVO.Person;

public class MainActivity extends AppCompatActivity {
    IBinder mRemote;

    private static final String SEND_TOKEN = "token_128379824234356345";
    private static final String REPLY_TOKEN = "token_4529342983402834";

    private final int get_person = 10;
    private final int get_persons = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent();
        intent.setAction("wanjf.intent.action.USER_SERVICE");
        intent.setPackage("com.wjf.binderforaidl");
        bindService(intent, coon, BIND_AUTO_CREATE);
    }

    ServiceConnection coon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("=====================>成功连接服务");
            mRemote = service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("===================>服务连接断开");
        }
    };

    private void getRemotePersonInfo() throws RemoteException {
        // 客户端向服务端发送的数据的载体
        Parcel send = Parcel.obtain();
        //发送给服务端的，服务端会通过这个
        Parcel reply = Parcel.obtain();
        send.writeInterfaceToken(SEND_TOKEN); // 身份验证
        send.writeInt(1);
        send.writeString("");
        mRemote.transact(Binder.INTERFACE_TRANSACTION, send, reply, 0);
        String s = reply.readString();

        mRemote.transact(get_person, send, reply, 0);

        Person result = (Person)reply.readParcelable(Person.class.getClassLoader());
        System.out.println("取到了值:" + result);
        send.recycle();
        reply.recycle();
    }

    public void getPerson(View view) {
        try {
            getRemotePersonInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (coon != null) {
            unbindService(coon);
        }
    }
}