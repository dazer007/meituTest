package com.dazhi.meitutest.activity;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dazhi.meitutest.R;
import com.dazhi.meitutest.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图像画廊的Adapter
 */
public class HeadImageAdapter extends BaseAdapter {

    private List<File> datas = new ArrayList<File>();
    private Activity activity;

    /**
     * 构造器
     * @param picDirctory 存放图片的路径
     */
    public HeadImageAdapter(String picDirctory, Activity activity) {
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
        LayoutInflater inflater = this.activity.getLayoutInflater();
        LinearLayout linearLayout = (LinearLayout) inflater.
                inflate(R.layout.gallay_layout, parent, true);
        ImageView imageView = (ImageView) linearLayout.findViewById(R.id.gallery_imageView);

        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        File file = (File) getItem(position);
        imageView.setImageURI(Uri.fromFile(file));
        return linearLayout;
    }
}
