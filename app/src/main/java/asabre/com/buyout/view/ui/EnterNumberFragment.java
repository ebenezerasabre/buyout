package asabre.com.buyout.view.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import asabre.com.buyout.R;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;


public class EnterNumberFragment extends Fragment implements BaseFragment {
    private static final String TAG = EnterNumberFragment.class.getSimpleName();

    private TextInputEditText enterNumber;
    private MaterialButton enterNumberContinue;
    private ProgressDialog mProgressDialog;
    private String mPhoneNumber = "";
    private TextView enterNumberTopWord;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_number, container, false);
        init(view);
        setSmsListener();
        return view;
    }

    private void init(View view) {
        enterNumber = view.findViewById(R.id.enterNumber);
        enterNumberContinue = view.findViewById(R.id.enterNumberContinue);
        enterNumberTopWord = view.findViewById(R.id.enterNumberTopWord);
    }

    private void setSmsListener() {
        enterNumberContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode();
            }
        });
    }

    private boolean fieldIsNotEmpty() {
        return !enterNumber.getText().toString().isEmpty();
    }

    private void sendVerificationCode() {
        if (smsGranted() && getActivity() != null) {
            if (fieldIsNotEmpty()) {
                showProgressDialog();
                ReadSMS readSMS = new ReadSMS();
                readSMS.execute();  // call AsynTask
            } else {
                Toast.makeText(getContext(), "Field can't be empty", Toast.LENGTH_SHORT).show();
            }

        } else {
            dismissProgressDialog();
            grantSMSPermission();
        }
    }

    private String getRandom() {
        int min = 50;
        int max = 100;
        double randomDouble = Math.random() * (max - min + 1) + min;
        randomDouble *= 25;
        int ran = (int) randomDouble;
        return String.valueOf(ran);
    }


    private String readSMS() {
        String code = "";
        if (getActivity() != null) {
            SystemClock.sleep(5000);

            Uri uri = Uri.parse("content://sms/");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                int hasReadSmsPermission = getActivity().checkSelfPermission(Manifest.permission.READ_SMS);
                if (hasReadSmsPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_SMS}, AppConstants.SMS_PERMISSION);
                    return "";
                }
            }

            int count = 0;
            ContentResolver resolver = getActivity().getContentResolver();
            Cursor cursor = resolver.query(uri, new String[]{"_id", "address", "type", "body", "date"}, null, null, null);
            List<HashMap<String, String>> smsInfos = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext() && count <= 2) {
                    count += 1;
                    if (count == 1) {
                        int _id = cursor.getInt(0);
                        String address = cursor.getString(1);
                        int type = cursor.getInt(2);
                        String body = cursor.getString(3);
                        long date = cursor.getLong(4);
                        HashMap<String, String> smsInfo = new HashMap<>();
                        smsInfo.put("_id", String.valueOf(_id));
                        smsInfo.put("address", address);
                        smsInfo.put("type", String.valueOf(type));
                        smsInfo.put("body", body);
                        smsInfo.put("date", String.valueOf(date));

                        smsInfos.add(smsInfo);
                    }
                }
                cursor.close();
            }

            String body = "";
            for (int x = 0; x < smsInfos.size(); x++) {
                body = smsInfos.get(x).get("body") + "\n";
                Log.d(TAG, "body is : " + body);
            }
            code = body;
        }
        return code;
    }

    private class ReadSMS extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            sendTextMessage();
            return readSMS();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // set phoneNumber
            ViewModelHomeFragment.createObject.put("phoneNumber", mPhoneNumber);

            Log.d(TAG, "onPostExecute: the string " + s);
            dismissProgressDialog();
            loadEnterEmailFragment();
        }
    }

    private void sendTextMessage() {
        mPhoneNumber = enterNumber.getText().toString();
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mPhoneNumber, null, getRandom(), null, null);
    }

    private boolean smsGranted() {
        int smsPermission = 0;
        if (getContext() != null) {
            smsPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS);
            return smsPermission == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void grantSMSPermission() {
        if (getActivity() != null) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, AppConstants.SMS_PERMISSION);
        }
    }


    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Verifying number");
        mProgressDialog.setMessage("Please wait.");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    private void loadEnterEmailFragment() {
        EnterEmailFragment enterEmailFragment = new EnterEmailFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.containerProducts, enterEmailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadSignInFragment(){
        SignInFragment signInFragment = new SignInFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.containerProducts, signInFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private View.OnClickListener goBackListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadSignInFragment();
            }
        };
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: of enterNumber fragment called");
    }

    private void checkPermission(){
        int permissionSendSms = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS);
        int permissionReadSms = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS);

        List<String> listPermissionsNeed = new ArrayList<>();

        if(permissionSendSms != PackageManager.PERMISSION_GRANTED){ listPermissionsNeed.add(Manifest.permission.SEND_SMS); }
        if(permissionReadSms != PackageManager.PERMISSION_GRANTED){ listPermissionsNeed.add(Manifest.permission.READ_SMS); }

        if(!listPermissionsNeed.isEmpty()){
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeed.toArray(new String[listPermissionsNeed.size()]), AppConstants.SMS_PERMISSION);
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: number called");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: number called");
        enterNumberTopWord.setOnClickListener(goBackListener());
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: number called");
    }


    @Override
    public void onBackPressed() {
        if(MainActivity.mHomeTrack == MainActivity.HomeTrack.PRODUCTS){
            loadSignInFragment();
        }
    }
}
