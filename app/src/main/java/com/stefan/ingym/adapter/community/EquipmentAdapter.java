package com.stefan.ingym.adapter.community;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.stefan.ingym.R;

/**
 * @ClassName: EquipmentAdapter
 * @Description: 九宫格数据适配器
 * @Author Stefan
 * @Date 2017/10/6 16:22
 */
public class EquipmentAdapter extends BaseAdapter{

    // 定义数据库对应的类别id
    private String category[] = {"1", "2", "3", "4", "5", "6", "7", "8", ""};

    // 定义标题
    private String labels[] = new String[] {"装备", "健身器", "杠铃", "腹肌板", "拉力绳", "练臂器", "瑜伽垫", "增肌品", "全部"};
    // 定义标题颜色
    private int colors[] = new int[]{R.color.item_0, R.color.item_1, R.color.item_2, R.color.item_3,
            R.color.item_4, R.color.item_5, R.color.item_6, R.color.item_7, R.color.item_8};
    // 定义图标
    private int icons[] = new int[] {
            R.drawable.select_equipment,
            R.drawable.select_sport,
            R.drawable.select_barbell,
            R.drawable.select_abdominal,
            R.drawable.select_rope,
            R.drawable.select_arm,
            R.drawable.select_yoga,
            R.drawable.select_protein,
            R.drawable.select_all
    };

    @Override
    public int getCount() {
        return labels.length;
    }

    /**
     * 返回对应宫格对应分类ID
     * @param position
     * @return
     */
    @Override
    public String getItem(int position) {
        return category[position];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.community_gridview_item, null);
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        /**
            渲染数据
         */
        // 渲染标题
        holder.gridview_item.setText(labels[position]);
        // 渲染文字颜色
        holder.gridview_item.setTextColor(viewGroup.getContext().getResources().getColor(colors[position]));
        // 渲染图标
        Drawable drawable = viewGroup.getContext().getResources().getDrawable(icons[position]);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        holder.gridview_item.setCompoundDrawables(null, drawable, null, null);
        return convertView;
    }

    class ViewHolder {
        @ViewInject(R.id.gridview_item)
        TextView gridview_item;
    }

}
