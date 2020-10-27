package ab.cd.firstorder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class backService extends Service {

    static class Product implements Serializable {
        String name;
        String store;
        String endTime;
        int count;
        public Product(String name, String store, String endTime, int count) {
            this.name = name;
            this.store = store;
            this.endTime = endTime;
            this.count = count;
        }
    }
    static List<Product> basearr;
    HttpRequest v = new HttpRequest(){
      public void rcvData(String r){
          Log.d("test",r);
          try {
              List<Product> tmpArr = new ArrayList<>();
              JSONArray jArr = new JSONArray(r);
              for(int i=0;i<jArr.length();i++){
                  JSONObject ob = jArr.getJSONObject(i);
                  String name = ob.getString("Name");
                  String store = ob.getString("StoreName");
                  String time = ob.getString("EndTime");
                  int count = ob.getInt("OnlineCount");
                  tmpArr.add(new Product(name,store,time,count));
              }


              for(int i=0;i<tmpArr.size();i++){
                  int chk = 0;
                  for(int j=0;j<basearr.size();j++){
                      Product ori = basearr.get(j);
                      Product t = tmpArr.get(i);
                      if(ori.name.equals(t.name) && ori.store.equals(t.store) && ori.endTime.equals(t.endTime) && ori.count == t.count){
                          chk = 1;
                          break;
                      }
                  }
                  if(chk == 0){
                      //새로운 물품 등장
                      Log.d("test","file save!!");
                      save(mContext,"first.dat", tmpArr);


                      NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                      //Notification 객체를 생성해주는 건축가객체 생성(AlertDialog 와 비슷)
                      NotificationCompat.Builder builder= null;

                      //Oreo 버전(API26 버전)이상에서는 알림시에 NotificationChannel 이라는 개념이 필수 구성요소가 됨.
                      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                          String channelID="channel_01"; //알림채널 식별자
                          String channelName="MyChannel01"; //알림채널의 이름(별명)

                          //알림채널 객체 만들기
                          NotificationChannel channel= new NotificationChannel(channelID,channelName,NotificationManager.IMPORTANCE_DEFAULT);

                          //알림매니저에게 채널 객체의 생성을 요청
                          notificationManager.createNotificationChannel(channel);

                          //알림건축가 객체 생성
                          builder=new NotificationCompat.Builder(mContext, channelID);


                      }else{
                          //알림 건축가 객체 생성
                          builder= new NotificationCompat.Builder(mContext, null);
                      }

                      //건축가에게 원하는 알림의 설정작업
                      builder.setSmallIcon(android.R.drawable.ic_menu_view);

                      Intent intent2 = new Intent(mContext, MainActivity.class);

                      PendingIntent pi = PendingIntent.getActivity(mContext,0,intent2,PendingIntent.FLAG_UPDATE_CURRENT);
                      builder.setContentIntent(pi);
                      //상태바를 드래그하여 아래로 내리면 보이는
                      //알림창(확장 상태바)의 설정
                      builder.setContentTitle("퍼스트오더");//알림창 제목
                      builder.setContentText("새로운 상품이 등록되었습니다.");//알림창 내용
                      builder.setAutoCancel(true);
                      //알림창의 큰 이미지
                      //Bitmap bm= BitmapFactory.decodeResource(getResources(),R.drawable.gametitle_09);
                      //builder.setLargeIcon(bm);//매개변수가 Bitmap을 줘야한다.

                      //건축가에게 알림 객체 생성하도록
                      Notification notification=builder.build();

                      //알림매니저에게 알림(Notify) 요청
                      notificationManager.notify(1, notification);



                      break;
                  }
              }
          }catch(Exception e){

          }


      }
    };
    public static synchronized void save(Context context, String fileName, List<Product> arr)  {
        try {
            File f =new File(context.getFilesDir(), fileName);
            FileOutputStream fout = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(arr);
            basearr =  arr;
            fout.close();
        }catch(Exception e){
            Log.d("test","save fail");
        }
    }

    public static synchronized  void read(Context context, String fileName)  {
        try{
            File f =new File(context.getFilesDir(), fileName);
            FileInputStream fin= new FileInputStream (f);
            ObjectInputStream ois = new ObjectInputStream(fin);
            basearr= (ArrayList<Product>)ois.readObject();
            fin.close();
        }catch(Exception e){
            Log.d("test","load fail");
            basearr = new ArrayList<Product>();
        }
    }



    private Context mContext;
    private Handler handler;
    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        handler = new Handler();
        mContext = this.getApplicationContext();
        read(mContext, "first.dat");
        chk();

        Log.d("test", "서비스의 onCreate");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행
        Log.d("test", "서비스의 onStartCommand");


        return super.onStartCommand(intent, flags, startId);
    }

    private void chk(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //
                v.request("http://192.168.0.100:4100/showProduct/requestProduct");

                chk();
            }
        }, 2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행
        Log.d("test", "서비스의 onDestroy");
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        System.out.println("service in onTaskRemoved");
        long ct = System.currentTimeMillis(); //get current time
        Intent restartService = new Intent(getApplicationContext(),
                backService.class);
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 0, restartService,
                0);

        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.setRepeating(AlarmManager.RTC_WAKEUP, ct, 1 * 1000, restartServicePI);
    }

}
