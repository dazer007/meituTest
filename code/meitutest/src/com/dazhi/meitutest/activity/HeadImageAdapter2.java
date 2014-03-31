package com.dazhi.meitutest.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.dazhi.meitutest.R;
import com.dazhi.meitutest.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图像画廊的Adapter,这种方法不用写布局Xml
 */
public class HeadImageAdapter2 extends BaseAdapter {

    private List<File> datas = new ArrayList<File>();
    private Activity activity;

    /**
     * 构造器
     * @param picDirctory 存放图片的路径
     */
    public HeadImageAdapter2(String picDirctory, Activity activity) {
        this.activity = activity;
        this.datas = Utils.traverseSingle(picDirctory);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView = new ImageView(this.activity);

        File file = (File) getItem(position);
        String filePath = file.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        float density = this.activity.getResources().getDisplayMetrics().density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        imageView.setLayoutParams(new Gallery.LayoutParams((int)(100*density),(int)(100*density)));
        imageView.setBackgroundColor(0x00000000);
        return imageView;
    }
}
