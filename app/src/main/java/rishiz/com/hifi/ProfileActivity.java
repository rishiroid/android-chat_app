package rishiz.com.hifi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {
    public Button btnLogOut,btnUplodaImg;
    public ImageView profilePic;
    private Uri imgPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        btnLogOut = findViewById(R.id.btnLogOut);
        profilePic=findViewById(R.id.profile_img);
        btnUplodaImg=findViewById(R.id.btnUploadImg);
        btnLogOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
        profilePic.setOnClickListener(v->{
            Intent photIntent=new Intent(Intent.ACTION_PICK);
            photIntent.setType("image/*");
            startActivityForResult(photIntent,1);
        });
        btnUplodaImg.setOnClickListener(v->{
            uploadImg();
        });
    }

    private void uploadImg() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading..");
        progressDialog.show();

        FirebaseStorage.getInstance().getReference("images/"+ UUID.randomUUID().toString()).putFile(imgPath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isComplete()){
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            updateProfilePicture(task.getResult().toString());
                        }
                    });
                    Toast.makeText(ProfileActivity.this,"Image Uploaded!",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ProfileActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress=100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded "+(int)progress + "%");
            }
        });
    }

    private void updateProfilePicture(String url) {
        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profilePicture").setValue(url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data !=null){
            imgPath=data.getData();
            getImageInImageView();
        }
    }

    private void getImageInImageView()  {
        Bitmap bitmap=null;
        try {
             bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgPath);
        }catch (IOException e){
            e.printStackTrace();
        }
        profilePic.setImageBitmap(bitmap);

    }
}