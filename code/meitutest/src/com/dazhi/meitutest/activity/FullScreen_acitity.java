package com.dazhi.meitutest.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.dazhi.meitutest.R;
import com.dazhi.meitutest.utils.Utils;

import java.io.File;
import java.util.List;

/**
 * 全屏浏览的Activity
 */
public class FullScreen_acitity extends Activity implements View.OnClickListener{
    private ImageSwitcher imageSwitcher;
    private ImageButton pre, next;
    private List<File> allFile;
    private int root = 0; // 资源图片ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landscape_activity);


        initImageResource();

        imageSwitcher = (ImageSwitcher) findViewById(R.id.image_switcher);
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        imageSwitcher.setFactory(new ViewFactoryImpl());
        this.setImageSwitcherImageUri(0);

        pre = (ImageButton) findViewById(R.id.btn_pre);
        pre.setOnClickListener(this);

        next = (ImageButton) findViewById(R.id.btn_next);
        next.setOnClickListener(this);
    }

    /**
     * 获取指定目录的所有图片
     */
    private void initImageResource() {
        // 获取保存正式路径的所有图片
        allFile = Utils.traverseSingle(MainActivity.BASE_SRC + File.separator + MainActivity.SAVE_DIRECTORY);
    }

    /**
     * 设置ImageSwitcher的图片
     * @param i
     */
    private void setImageSwitcherImageUri(int i) {
        if(getImageUri(i) != null) {
            imageSwitcher.setImageURI(getImageUri(i));
        }
    }

    /**
     * 获取指定下标的文件的Uri
     * @param index
     * @return
     */
    private Uri getImageUri(int index) {
        Uri uri = null;
        if(index < allFile.size()) {
            uri = Uri.fromFile(allFile.get(index));
        }
        return uri;
    }

    private class ViewFactoryImpl implements ViewSwitcher.ViewFactory {

        @Override
        public View makeView() {
            ImageView img = new ImageView(FullScreen_acitity.this);	// 实例化图片显示
            img.setBackgroundColor(0xFF000000);			// 设置背景颜色
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);		// 居中显示
            img.setLayoutParams(new ImageSwitcher.LayoutParams(		// 自适应图片大小
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));	// 定义组件
            return img;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pre:
                root--;
                if(root < 0){
                    root = allFile.size();
                }
                break;
            case R.id.btn_next:
                root++;
                if(root >= allFile.size()){
                    root = 0;
                }
                break;
        }
        this.setImageSwitcherImageUri(root);
    }
}
