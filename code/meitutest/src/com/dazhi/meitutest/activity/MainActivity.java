package com.dazhi.meitutest.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.Toast;
import com.dazhi.meitutest.R;
import com.dazhi.meitutest.utils.Utils;
import com.dazhi.meitutest.vo.MyUrlObj;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 主测试界面
 */
public class MainActivity extends Activity {
    private EditTextWithDel usernameEditText;
    private ImageButton[] apps = new ImageButton[9];
    private GalleryFlow gallery;
    public static final String TAG = "MeituTestApps";
    private String username = "";
    private JSONObject jsonObject;
    private String resultJsonStr = "";
    private int curIndex; // 标记当前点击的功能
    private File curSelectFile; // 标记Galley当前选中的图片
    private ProgressDialog dialog;
    public static String tempPath = null; // 临时文件目录
    public static String BASE_SRC = null; // 基目录
    public static String SAVE_DIRECTORY = "img"; // 保存正式文件的文件加名称
    private String savePath = null; // 保存正式文件的文件路径
    private String imgUrl = null; // 生成图片网络URI
    private boolean isNewImage = true;

    static {
        BASE_SRC = Utils.getSDPath().getPath() + File.separator + "MeituTest";
        tempPath = MainActivity.BASE_SRC + File.separator + "temp" + File.separator + "temp.jpg";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (!Utils.isNetWork(this)) { // 判断是否有网络访问
            //this.showToast();
        }

        this.initLocalJSON(); // 解析本地app.json文件
        this.initComponent(); // 初始化View组件,并添加事件
    }

    @Override
    protected void onResume() {

        // 设置新保存的图片被选中
        if (isNewImage && savePath != null) { //如果是刚生成的图片，就把新创建的图片选中
            File newSaveFile = new File(savePath);
            if (newSaveFile.exists()) {
                curSelectFile = newSaveFile;
            }
        }

        // 1:重新创建Gallery
        createGallery();

        super.onResume(); // 必须调用
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if(item.getItemId() == R.id.menuItem1){
            Intent intent = new Intent();
            intent.setAction("com.dazhi.Landscape");
            startActivity(intent);
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initComponent() {
        usernameEditText = (EditTextWithDel) findViewById(R.id.username);

        addImageButton(R.id.app1, 0);
        addImageButton(R.id.app2, 1);
        addImageButton(R.id.app3, 2);
        addImageButton(R.id.app4, 3);
        addImageButton(R.id.app5, 4);
        addImageButton(R.id.app6, 5);
        addImageButton(R.id.app7, 6);
        addImageButton(R.id.app8, 7);
        addImageButton(R.id.app9, 8);

        gallery = (GalleryFlow) findViewById(R.id.gallery);
        createGallery();
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 获取保存正式路径的所有图片
                List<File> files = Utils.traverseSingle(BASE_SRC + File.separator + SAVE_DIRECTORY);
                // 获取当前选中的图片
                curSelectFile = files.get(i);
                // 跳转activity
                savePath = curSelectFile.getPath();
                startShowActivity(2);
                isNewImage = false;
            }
        });
    }


    private void createGallery() {
        HeadImageAdapter2 adapter = new HeadImageAdapter2(BASE_SRC + File.separator + SAVE_DIRECTORY, this);
        gallery.setAdapter(adapter);

        // 设置Gallery默认选中项

        // 获取保存正式路径的所有图片
        List<File> files = Utils.traverseSingle(BASE_SRC + File.separator + SAVE_DIRECTORY);
        // 设置默认选中的Galley图片
        if (files.contains(curSelectFile)) {
            // 获取curSelectFile在files中的索引
            int index = getCurFileIndex(files, curSelectFile);
            // 设置默认选中
            gallery.setSelection(index);
        }
    }

    private int getCurFileIndex(List<File> files, File curFile) {
        int index = 0;
        try {
            for (int i = 0; i < files.size(); ++i) {
                File file = files.get(i);
                if (file.getPath().equals(curFile.getPath())) {
                    index = i;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return index;
    }


    /**
     * 获取ImageButton,并添加事件
     *
     * @param viewId
     * @param index
     */
    private void addImageButton(int viewId, int index) {
        apps[index] = (ImageButton) findViewById(viewId);
        apps[index].setOnClickListener(new OnClickListenerImpl(apps[index]));
    }

    private class OnClickListenerImpl implements View.OnClickListener {
        private ImageButton imageButton;

        public OnClickListenerImpl(ImageButton imageButton) {
            this.imageButton = imageButton;
        }

        @Override
        public void onClick(View view) {
            isNewImage = true;

            switch (view.getId()) {
                case R.id.app1:
                    curIndex = 0;
                    break;
                case R.id.app2:
                    curIndex = 1;
                    break;
                case R.id.app3:
                    curIndex = 2;
                    break;
                case R.id.app4:
                    curIndex = 3;
                    break;
                case R.id.app5:
                    curIndex = 4;
                    break;
                case R.id.app6:
                    curIndex = 5;
                    break;
                case R.id.app7:
                    curIndex = 6;
                    break;
                case R.id.app8:
                    curIndex = 7;
                    break;
                case R.id.app9:
                    curIndex = 8;
                    break;
                default:
                    curIndex = 0;
            }

            ImageButtonOnCliCK(curIndex);
        }
    }

    private void ImageButtonOnCliCK(int i) {
        if (apps[i] instanceof ImageButton) {
            // 1:获取username
            username = usernameEditText.getText().toString();
            if (null != username && !"".equals(username)) {
                // 2:解析本地app.json文件，获取请求信息
                MyUrlObj myUrlObj = getUrlObj(i);
                Log.d(TAG, myUrlObj.toString());
                // 3:初始化HttpClinet组件，访问网络，并读取返回数据
                final DefaultHttpClient httpClient = initHttpClient(myUrlObj);
                final HttpPost request = initHttpPost(myUrlObj);
                // 4:获取的网络图片路径
                // 参数，请求的url,下载当前量，返回状态
                if (httpClient != null && request != null) {
                    // 5:获取服务器图片地址
                    // 6:缓存图片到本地SDCARD
                    MainActivity.this.requestDataByAsyncTask(httpClient, request, myUrlObj.getRequestURL());
                } else {
                    MainActivity.this.showToast(getUrlObj(curIndex).getAppName() + "请检查请求服务器的路径");
                }
                // 7:给ImageView设置图片
            } else { // 测试姓名不能为空
                usernameIsEmpty();
            }
        }
    }

    private void usernameIsEmpty() {
        final AlertDialog alert = new AlertDialog.Builder(this)
                .setMessage("测试姓名不能为空")
                .create();
        alert.setCancelable(true); // 点击任意空白部分或者返回键都会取消
        alert.setCanceledOnTouchOutside(true); // 设置点击对话框空白位置也会取消
        alert.show();


        //android定时器
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() { // 在主线程执行过多上时间执行
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        }, 3000);
    }

    /**
     * 请求网络数据，返回数据
     *
     * @param httpClient
     * @param request
     * @param url        请求地址
     */
    private void requestDataByAsyncTask(final DefaultHttpClient httpClient, final HttpPost request, String url) {
        dialog = new ProgressDialog(this);

        AsyncTask<String, Void, Integer> asyncTask = new AsyncTask<String, Void, Integer>() {

            @Override
            protected void onPreExecute() { // 在doInBackgroud之前执行，用来初始化一些数据
                dialog.setCancelable(false); // 是否消失dialog,在点击返回键或者屏幕以外空白区域（false即点击啥都不会取消）
                //dialog.setCanceledOnTouchOutside(false); // 是否取消，当点击屏幕以外空间的时候，false:即只有点击返回键才能取消
                dialog.setMessage("正在努力加载，请稍等..");
                dialog.show();
            }

            @Override
            protected Integer doInBackground(String... strings) {
                Integer state = -1; // 状态标示，0正常
                if (strings != null && strings.length > 0) {
                    try {
                        HttpResponse response = httpClient.execute(request);
                        if (response.getStatusLine().getStatusCode() == 200) { // 服务器正常返回
                            resultJsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                            Log.d(TAG, "resultJsonStr = " + resultJsonStr);
                            state = 200; // 正常
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                        state = -1; // IO异常
                    }
                }
                return state;
            }

            @Override
            protected void onPostExecute(Integer state) {// 退回主线程修改Ui
                if (state == 200) { // 正常获取url
                    MyUrlObj myUrlObj = null;
                    if (resultJsonStr.contains("http")) { // 排除是无效的url，如"数据查询错误！"
                        myUrlObj = getUrlObj(curIndex);

                        if (MyUrlObj.RESULT_TYPE_JSON.equals(myUrlObj.getResultType())) { // json数据
                            try {
                                Log.d(TAG, "resultJsonStr = " + resultJsonStr);
                                JSONObject jsonObj = new JSONObject(resultJsonStr);
                                imgUrl = jsonObj.get("url").toString();
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                        } else if (MyUrlObj.RESULT_TYPE_IMAGE_URI.equals(myUrlObj.getResultType())) { // imgUrl
                            imgUrl = resultJsonStr;
                        }

                        // 根据imgUrl下载数据
                        if (!imgUrl.isEmpty()) {
                            downloadImageByAsyncTask(imgUrl);
                        } else {
                            MainActivity.this.showToast("服务器暂时没有测试成功，正在抢修！！ + url" + imgUrl);
                            dialog.dismiss();
                        }
                    } else {  // 无效的返回结果
                        dialog.dismiss();
                        // MainActivity.this.showToast("数据查询错误！ - 给您带来不便请谅解！！");
                        forbiddenFuncton();
                    }
                } else if (state == -1) { // 错误
                    MainActivity.this.showToast("信号是否不好，请检查！");
                    dialog.dismiss();
                }
            }
        };

        asyncTask.execute(url);
    }

    /**
     * 禁用功能
     */
    private void forbiddenFuncton() {
        final ImageButton view = apps[curIndex];
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage("该功能服务器出现故障，是否暂时禁用该功能")
                .setPositiveButton("禁用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        view.setEnabled(false);
                        view.setImageResource(R.drawable.app_6_press);
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        alertDialog.show();
    }

    /**
     * 缓存图片，下载图片
     *
     * @param imgUrl
     */
    private void downloadImageByAsyncTask(String imgUrl) {

        // 处理的下载的异步任务
        AsyncTask<String, Double, Integer> asyncTask = new AsyncTask<String, Double, Integer>() {

            @Override
            protected Integer doInBackground(String... imageUrls) {
                Integer downFlag = 0;   // 下载标示,0：表示下载成功
                HttpGet request = new HttpGet(imageUrls[0]);
                HttpClient httpClient = new DefaultHttpClient();
                try {
                    HttpResponse response = httpClient.execute(request);
                    InputStream in = response.getEntity().getContent();

                    File sdcardFile = Utils.getSDPath();
                    savePath = MainActivity.BASE_SRC + File.separator + SAVE_DIRECTORY + File.separator + username + "_" + curIndex + ".jpg";
                    if (sdcardFile != null) { // SDCARD 存在
                        Utils.writeDataToFile(tempPath, in);
                    } else {
                        downFlag = -1; // sdcard不存在
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                    downFlag = -2; // 数据读取失败
                }
                return downFlag;
            }

            @Override
            protected void onPostExecute(Integer downStateCode) {
                dialog.dismiss();

                switch (downStateCode) {
                    case 0:
                        //下载成功,跳转
                        Log.d(TAG, "文件" + tempPath + "下载成功");
                        startShowActivity(1);
                        break;
                    case -1:
                        //sdcard不存在
                        Log.d(TAG, "sdcard不存在");
                        MainActivity.this.showToast("sdcard不存在，文件缓存失败");
                        break;
                    case -2:
                        // 数据读取失败
                        MainActivity.this.showToast("数据读取失败，请检查网络或者检查磁盘空间");
                        break;
                    default:
                        ;
                }
            }
        };

        asyncTask.execute(imgUrl);
    }

    /**
     * 启动Activity
     *
     * @param type 类型，1:缓存图片， 2：正式保存路径的图片
     */
    private void startShowActivity(int type) {
        Intent intent = new Intent(MainActivity.this, ShowImgActivity.class);
        intent.putExtra("savePath", savePath);
        intent.putExtra("online_url", imgUrl);
        intent.putExtra("username", username);
        intent.putExtra("describe", MainActivity.this.getUrlObj(curIndex).getDescribe());
        intent.putExtra("type", type);
        startActivity(intent);
    }

    /**
     * 初始化访问网络组件，并设置超时参数
     *
     * @param myUrlObj
     * @return
     */
    private DefaultHttpClient initHttpClient(MyUrlObj myUrlObj) {
        int timeout = 30 * 1000;
        // httpClient参数设置，设置超时时间
        HttpParams httpParameters = new BasicHttpParams();
        // Sets the timeout until a connection is etablished.
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
        // Sets the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(httpParameters, timeout);

        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.setParams(httpParameters);
        return httpClient;
    }

    /**
     * 初始化HttpPost
     *
     * @param myUrlObj
     * @return
     */
    private HttpPost initHttpPost(MyUrlObj myUrlObj) {
        HttpPost request = null;
        try {
            request = new HttpPost(myUrlObj.getRequestURL());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair(myUrlObj.getFormData(), username));
        Log.d(TAG, "" + myUrlObj.getFormData() + "xxxxxxxxxxxxxx:" + username);

        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(postParams, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        request.setEntity(entity);
        return request;
    }

    /**
     * 获取本地assets目录中的json数据
     *
     * @return
     * @throws org.json.JSONException
     */
    private JSONObject initLocalJSON() {
        // 1：读取Assets的文本数据
        String jsonStr = "";
        AssetManager assetManager = getAssets();
        try {
            InputStream in = assetManager.open("app.json");
            jsonStr = Utils.readServiceText(in);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        // 2 :获取JSONObject
        jsonObject = null;
        try {
            if (!jsonStr.isEmpty()) {
                jsonObject = new JSONObject(jsonStr);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage() + "获取JSON数据失败", e);
        }
        return jsonObject;
    }

    /**
     * 获取访问网络的参数对象
     *
     * @param index
     * @return
     */
    private MyUrlObj getUrlObj(int index) {
        MyUrlObj obj = null;
        if (jsonObject != null) {
            try {
                obj = new MyUrlObj();
                JSONObject json = (JSONObject) jsonObject.getJSONArray("apps").get(index);
                obj.setRequestURL(json.get("RequestURL").toString());
                obj.setFormData(json.get("FormData").toString());
                obj.setResultType(json.get("ResultType").toString());
                obj.setAppName(json.get("AppName").toString());
                obj.setDescribe(json.get("describe").toString());
                Log.d(TAG, "xxxxxx 当前本地json对象 xxxxxxxx:" + json);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                obj = null;
            }
        }
        return obj;
    }

    private void showToast(String... strs) {
        if (strs != null && strs.length > 0) {
            Toast.makeText(this, strs[0], Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "网络范围失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }


    // 再按一次退出系统=====================
    long waitTime = 2000;
    long touchTime = 0;

    //如果你需要同时重写这两个方法，可能要注意一些问题啦！系统先是onKeyDown，如果return true了，就不会onBackPressed啦！
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - touchTime) >= waitTime) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            touchTime = currentTime;
        } else {
            // 完全退出系统
            android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
            System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
        }
    }

}
