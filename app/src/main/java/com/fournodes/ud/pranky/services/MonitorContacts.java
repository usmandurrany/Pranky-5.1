package com.fournodes.ud.pranky.services;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import com.fournodes.ud.pranky.Contacts;
import com.fournodes.ud.pranky.interfaces.OnCompleteListener;
import com.fournodes.ud.pranky.network.ContactsAsync;

public class MonitorContacts extends Service implements OnCompleteListener {
    private int mContactCount;

    public MonitorContacts() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContactCount = getContactCount();

        Log.d("Contact Service", mContactCount + "");

        this.getContentResolver().registerContentObserver(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, true, mObserver);
    }

    private int getContactCount() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI, null, null, null,
                    null);
            if (cursor != null) {
                return cursor.getCount();
            } else {
                return 0;
            }
        } catch (Exception ignore) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    private ContentObserver mObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange,null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange);

            Log.e("URI CHANGE",uri.toString());

            final int currentCount = getContactCount();
            if (currentCount < mContactCount) {
                // CONTACT DELETED.

                Log.d("Contact Service", currentCount + "");

            } else if (currentCount == mContactCount) {
                // CONTACT UPDATED.
                Log.d("Contact Service", currentCount+"");
                ContactsAsync async = new ContactsAsync(getApplicationContext());
                async.delegate=MonitorContacts.this;
                Contacts contacts = new Contacts(getApplicationContext());
                async.execute(contacts.getUpdated());

            } else {
                // NEW CONTACT.
                Log.d("Contact Service", currentCount + "");

            }
            mContactCount = currentCount;

            getContentResolver().unregisterContentObserver(mObserver);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    getContentResolver().registerContentObserver(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, true, mObserver);
                }
            }, 60000);



        }

        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getContentResolver().unregisterContentObserver(mObserver);

        }catch (Exception e){}
    }

    @Override
    public void onCompleteSuccess() {

    }

    @Override
    public void conSyncComplete() {

    }

    @Override
    public void onCompleteFailed() {

    }
}
