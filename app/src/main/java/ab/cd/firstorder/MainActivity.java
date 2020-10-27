package ab.cd.firstorder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        permissionCheck();
        ActionBar ab = getSupportActionBar();
        ab.hide();
        setContentView(R.layout.activity_main);
        Handler h =new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Store.class) ;
                startActivity(intent) ;
                finish();
            }
        },1500);

        Intent intent = new Intent(
                getApplicationContext(),//현재제어권자
                backService.class); // 이동할 컴포넌트
        startService(intent); // 서비스 시작

    }

    private void permissionCheck() {
        int c1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int c2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS);
        int c3 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int c4 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(c1 == PackageManager.PERMISSION_GRANTED && c2 == PackageManager.PERMISSION_GRANTED && c3 ==  PackageManager.PERMISSION_GRANTED  && c4 ==  PackageManager.PERMISSION_GRANTED ){
            return;
        }

        // 이 권한을 필요한 이유를 설명해야하는가?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_PHONE_STATE)) {
            new AlertDialog.Builder(this).setTitle("알림").setMessage("권한이 필요합니다").setNegativeButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });


        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE , Manifest.permission.READ_PHONE_NUMBERS , Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE},
                    5);

            // 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다

        }



    }


}
