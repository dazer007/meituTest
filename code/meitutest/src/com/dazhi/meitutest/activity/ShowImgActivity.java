package com.dazhi.meitutest.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.dazhi.meitutest.R;
import com.dazhi.meitutest.utils.Utils;
import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.banner.AdViewListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 展示图片
 */
public class ShowImgActivity extends Activity {
    private ImageView imageView;
    private Button save, share;
    private String savePath = null;

    private List<String> data = new ArrayList<String>();
    private ArrayAdapter<String> adapter = null;
    private int selectIndex = 0;
    private List<String> shresSites = new ArrayList<String>(4);

    private SharedPreferences sharedPreferences; // 用xml存放图片的在线url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_activity);

        savePath = getIntent().getStringExtra("savePath");
        imageView = (ImageView) findViewById(R.id.image);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                imageViewOnLongClick();
                return false;
            }
        });


        save = (Button) findViewById(R.id.delete);
        share = (Button) findViewById(R.id.share);

        sharedPreferences = getSharedPreferences("imgUrls", MODE_PRIVATE);

        initImageView(); // 初始化imageView
        adjustSaveButtonText(); // 调节保存按钮的文字，在"保存", "删除"中切换
        initAdapterData(); // 初始化分享弹出框的Adapter
        initShareSites(); // 初始化分享网站的Url
        inityoumiAds(); // 添加有米广告
    }

    private void imageViewOnLongClick() {
        // 1：获取动画对象
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.imageview_anim);
        // 2：启动动画
        this.imageView.startAnimation(animation);
    }

    private void initImageView() {
        if (getIntent().getIntExtra("type", 1) == 1) { // 缓存图片
            File tempFile = new File(MainActivity.tempPath);
            imageView.setImageURI(Uri.fromFile(tempFile));
        } else { // 正式保存路径图片
            File saveFile = new File(savePath);
            imageView.setImageURI(Uri.fromFile(saveFile));
        }
    }


    private void inityoumiAds() {
        // 初始化接口，应用启动的时候调用
        // 参数：appId, appSecret, 调试模式
        AdManager.getInstance(this).init("d264d7b39faf1d50",
                "36f3efddac30d82f", false);

        // 广告条接口调用
        // 将广告条adView添加到需要展示的layout控件中
        LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
        AdView adView = new AdView(this, AdSize.FIT_SCREEN);
        adLayout.addView(adView);
    }


    private void adjustSaveButtonText() {
        File file = new File(savePath);
        if(file.exists()) { // 文件已经存在，直接显示删除
            save.setText("删除");
        } else {
            save.setText("保存");
        }
    }

    private String getLocalImageUrl(){
        String url = getIntent().getStringExtra("online_url");
        int type = getIntent().getIntExtra("type", 1);
        if (type == 2) { // 正式保存路径的图片
            url = sharedPreferences.getString(savePath, "no image");
        }
        return url;
    }

    private void initShareSites() {
        String shareStr = getShareContent();

        shresSites.add("http://www.jiathis.com/send/?webid=tsina" + shareStr); // sina
        shresSites.add("http://www.jiathis.com/send/?webid=qzone" + shareStr); // Tencent
        shresSites.add("http://www.jiathis.com/send/?webid=t163" + shareStr); // 163
        shresSites.add("http://www.jiathis.com/send/?webid=tsohu" + shareStr); // sohu
    }

    private String getShareContent() {
        String username = getIntent().getStringExtra("username");
        String describe = getIntent().getStringExtra("describe");
        String shareStr = "&title=美图大测试Android版 - " + ""
                + username + " " +  describe
                + "。Apk下载地址:&url=http://apk.hiapk.com/html/2011/08/180303.html "
                + "http://as.baidu.com/a/item?docid=2151656&pre=web_am_se"
                + "&pic=" + getLocalImageUrl()
                + "&uid=" + "1898581"
                + "&shortUrl=true"
                + "&summary=分享";
        return shareStr;
    }

    /**
     * 分享按钮事件
     */
    public void shareButtonOnClick(View view) {
        // 分享方式1- jiathis
//        share1();

        // 分享方式2-系统自带分享:
        share2();
    }

    /**
     * 调用加网分享  http://www.jiathis.com/
     */
    private void share1() {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.share_window_layout, null, true);
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(viewGroup)
                .setTitle("选择需要分享的网站")
                .create();
        alertDialog.setCanceledOnTouchOutside(true); // 点击dialog外边也消失
        alertDialog.show();

        ListView listView = (ListView) viewGroup.findViewById(R.id.share_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectIndex = position;
                alertDialog.dismiss();

                // 加网分享
                String shareUri = shresSites.get(selectIndex);
                Uri uri = Uri.parse(shareUri);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        });

    }

    /**
     * 调用系统自带分享
     */
    private void share2() {
        Intent intent = new Intent(Intent.ACTION_SEND);//启动分享发送的属性
        intent.setType("image/*");////分享发送的数据类型
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");//分享的主题
        intent.putExtra(Intent.EXTRA_TEXT, "分享");//分享的内容
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//这个也许是分享列表的背景吧
        String url = getIntent().getStringExtra("online_url");
        intent.putExtra(Intent.EXTRA_STREAM, url); // 分享地址
        intent.putExtra(Intent.EXTRA_STREAM, getLocalImageUrl());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getTitle()));//目标应用选择对话框的标题

    }

    private void initAdapterData() {
        data.add("新浪微博");
        data.add("腾讯微博");
        data.add("网易微博");
        data.add("搜狐微博");
        // public ArrayAdapter (Context context, int resource, int textViewResourceId, List<T> objects)
        adapter = new ArrayAdapter<String>(this,
                R.layout.share_simple_list_item,
                R.id.text1,
                data);

    }

    /**
     * 保存删除按钮事件
     */
    public void saveButtonOnClick(View view) {
        if (save.getText().toString().equals("保存")) {
            if (Utils.copyFile(MainActivity.tempPath, savePath)) {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(savePath, getIntent().getStringExtra("online_url"));
                editor.commit();
            } else {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            }
            save.setText("删除");
        } else { // 删除
            new AlertDialog.Builder(this)
                    .setMessage("是否删除??")
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Utils.deleteFile(savePath);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove(savePath);
                            save.setText("保存");
                            finish(); // 通知系统，销毁当前activity
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
