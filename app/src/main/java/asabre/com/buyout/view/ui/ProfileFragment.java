package asabre.com.buyout.view.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.suke.widget.SwitchButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import asabre.com.buyout.R;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileFragment extends Fragment  {
    private static final String TAG = ProfileFragment.class.getSimpleName();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    static final int PICK_IMAGE_REQUEST = 1;
    public static final String STORAGE_PERMISSION_CODE = "123";
    private Bitmap mBitmap;
    private Uri mFilePath;
    private File mFinalFile;
    String mFinalFileStr;


    // widgets
    CircleImageView mCircleImageView;
    TextView userName;
    SwitchButton shareWishList;
    SwitchButton shareHistory;
    FloatingActionButton addUserImageFAB;
    ExtendedFloatingActionButton editProfileFAB;

    private ViewModelHomeFragment mViewModelHomeFragment;
    TextView uploadUserImage;

    // new entries
    private TextView profileMyAccount;
    private TextView profileEditProfile;
    private TextView profileResetPassword;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        mCircleImageView = view.findViewById(R.id.userImage);
        userName = view.findViewById(R.id.userName);
        shareWishList = view.findViewById(R.id.shareWishList);
        shareHistory = view.findViewById(R.id.shareHistory);
        addUserImageFAB = view.findViewById(R.id.addUserImageFAB);
        editProfileFAB = view.findViewById(R.id.editProfileFAB);
        uploadUserImage = view.findViewById(R.id.uploadUserImage);

        profileMyAccount = view.findViewById(R.id.profileMyAccount);
        profileEditProfile = view.findViewById(R.id.profileEditProfile);
        profileResetPassword = view.findViewById(R.id.profileResetPassword);

        setUserImage();
    }


    private void setUserImage(){
        if(getContext() != null && ViewModelHomeFragment.userEntity != null){
            userName.setText(ViewModelHomeFragment.userEntity.getFirstName());
            Glide.with(getContext())
                    .load(ViewModelHomeFragment.userEntity.getUserImage())
                    .into(mCircleImageView);
        }
    }

    private void setProfileViewModel(){
        if(getActivity() != null){

            // show file size


            // create requestBody instance from file
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse(Objects.requireNonNull(getActivity().getContentResolver().getType(mFilePath))), mFinalFile
            );

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body = MultipartBody
                    .Part
                    .createFormData("file", mFinalFile.getName(), requestFile);

            // add another part within the multipart request
            String customerIdString = ViewModelHomeFragment.userEntity == null ?
                    "" : ViewModelHomeFragment.userEntity.getCustomerId();
            RequestBody customerId = RequestBody.create(
                    MultipartBody.FORM, customerIdString
            );

            // finally execute the request
            mViewModelHomeFragment = ViewModelProviders.of(getActivity()).get(ViewModelHomeFragment.class);
            mViewModelHomeFragment.initCustomerImage(body, customerId);
            mViewModelHomeFragment.setCustomerImage().observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    Log.d(TAG, "onChanged: The http file path is " + s);
                }
            });
        }
    }

    private void imageBrowse() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            mFilePath = data.getData();
            try {
                if (getActivity() != null && getContext() != null) {
                    mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mFilePath);
                    mCircleImageView.setImageBitmap(mBitmap);

                    // extract uri from bitmap
                    Uri tempUri = getImageUri(getContext(), mBitmap);
                    mFinalFileStr = getRealPathFromUri(tempUri);
                    mFinalFile = new File(mFinalFileStr);
                    Log.d(TAG, "onActivityResult: final file " + mFinalFile);



//                    Bitmap bitmap = BitmapFactory.decodeFile(mFinalFile.getPath());
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 4, new FileOutputStream(mFinalFile));
//                    Log.d(TAG, "onActivityResult: size of file is " + );



                    Toast.makeText(getContext(), "hey ", Toast.LENGTH_SHORT).show();
                    uploadUserImage.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        Log.d(TAG, "getImageUri: called");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        byte[] imageInByte = bytes.toByteArray(); // 1 288 128
        long lengthOfBmp = imageInByte.length;
        Log.d(TAG, "image length is " + lengthOfBmp); // 22 573

//        int count = 0;
//        while(lengthOfBmp > 10000){
//            Log.d(TAG, "started loop");
//            count += 1;
//            inImage.compress(Bitmap.CompressFormat.JPEG, 2, bytes);
//            lengthOfBmp /= 2;
//            Log.d(TAG, "index  " + count  + " -> image length is  " + lengthOfBmp);
//        }


//        Log.d(TAG, "getImageUri: image size " + bytes);


        // compress again
//        if(lengthOfBmp > 40000){
//            inImage.compress(Bitmap.CompressFormat.JPEG, 4, bytes);
//        }



        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromUri(Uri uri) {
        String str = "";
        if (getActivity() != null) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                str = cursor.getString(idx);
                cursor.close();
            }
        }
        return str;
    }

    private View.OnClickListener addingImageListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userIsSignedIn()){ cFPerm(); }
                else { showSignIn(); }
            }
        };
    }



    private void loadEditProfile(){
        ProfileEditFragment profileEditFragment = new ProfileEditFragment();
        loadProfileDetailsFrameLayout(profileEditFragment);
    }

    private void loadMyAccountFragment(){
        MyAccountFragment myAccountFragment = new MyAccountFragment();
        loadProfileDetailsFrameLayout(myAccountFragment);
    }

    private void resetPassWordFragment(){
        ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
        loadProfileDetailsFrameLayout(resetPasswordFragment);
    }


    private View.OnClickListener editProfileListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userIsSignedIn()){ loadEditProfile(); }
                else { showSignIn(); }
            }
        };
    }

    private View.OnClickListener myAccountListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userIsSignedIn()){ loadMyAccountFragment(); }
                else { showSignIn(); }
            }
        };
    }

    private View.OnClickListener resetPasswordListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userIsSignedIn()){ resetPassWordFragment(); }
                else { showSignIn(); }
            }
        };
    }


    private void loadProfileDetailsFrameLayout(Fragment fragment){
        removePreviousProfileDetailsFragment();
        if(fragment != null && getActivity() != null){

            getActivity().findViewById(R.id.containerProfile).setVisibility(View.GONE);
            getActivity().findViewById(R.id.containerProfileDetails).setVisibility(View.VISIBLE);
            MainActivity.mProfileTrack = MainActivity.ProfileTrack.PROFILE_DETAIL;

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerProfileDetails, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
    }


    private void removePreviousProfileDetailsFragment(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerProfileDetails);
            if(fragment != null){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }


    private boolean userIsSignedIn(){
        return ViewModelHomeFragment.userEntity != null && !ViewModelHomeFragment.userEntity.getCustomerId().isEmpty();
    }

    private void showSignIn(){
        // load signInFragment
        SignInFragment signInFragment = new SignInFragment();
        MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCTS;
        loadProductsFragment(signInFragment);
    }

    private void loadProductsFragment(Fragment fragment){
        removePreviousProductsFameLayoutData();

        if(fragment != null && getActivity() != null){
            findProfileFrameLayout().setVisibility(View.GONE);
            findProductFrameLayout().setVisibility(View.VISIBLE);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerProducts, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void removePreviousProductsFameLayoutData(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerProducts);
            if(fragment != null){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }

    private View.OnClickListener uploadUserImageListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Setting", Toast.LENGTH_LONG).show();
                findProgressBar().setVisibility(View.VISIBLE);
                setProfileViewModel();
                uploadUserImage.setVisibility(View.GONE);
            }
        };
    }



    @Override
    public void onStart() {
        super.onStart();
        addUserImageFAB.setOnClickListener(addingImageListener());
        editProfileFAB.setOnClickListener(editProfileListener());
        uploadUserImage.setOnClickListener(uploadUserImageListener());
        profileMyAccount.setOnClickListener(myAccountListener());
        profileEditProfile.setOnClickListener(editProfileListener());
        profileResetPassword.setOnClickListener(resetPasswordListener());

    }

    @Override
    public void onStop() {
        super.onStop();
        addUserImageFAB.setOnClickListener(null);
        uploadUserImage.setOnClickListener(null);
        editProfileFAB.setOnClickListener(null);
        profileMyAccount.setOnClickListener(null);
        profileEditProfile.setOnClickListener(null);
        profileResetPassword.setOnClickListener(null);
    }


    private void cFPerm(){
        if(getContext() != null){
            List<String> listPermissionsNeeded = new ArrayList<>();
            int permissionReadExternalStorage = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            int permissionWriteExternalStorage = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(permissionReadExternalStorage == PackageManager.PERMISSION_GRANTED){
                if(permissionWriteExternalStorage == PackageManager.PERMISSION_GRANTED){
                    imageBrowse();
                } else {
                    listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                }
            } else {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }

        }
    }

    private View findProgressBar(){return Objects.requireNonNull(getActivity()).findViewById(R.id.indeterminateProfile); }
    private View findProfileFrameLayout(){ return getActivity().findViewById(R.id.containerProfile); }
    private View findProfileDetailsFrameLayout(){return getActivity().findViewById(R.id.containerProfileDetails); }
    private View  findProductFrameLayout(){ return getActivity().findViewById(R.id.containerProducts); }



}



//kwame adu law consultant at
//Anderson and kay law firm
//Before we begin legal action
//practicing
//we provide legal council to address issues
//client mary nsiah brought a case
//before we take up a case we try to settle it at home

