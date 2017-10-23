package com.stefan.ingym.ui.fragment.mine.memorandum.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.stefan.ingym.R;
import com.stefan.ingym.ui.fragment.mine.memorandum.model.Note;
import com.stefan.ingym.ui.fragment.mine.memorandum.util.StringUtil;

import java.util.List;

/**
 * 主菜单适配器
 */
public class MainSwipeAdapter extends BaseAdapter {

    private List<Note> mData ;
    private Context mContext;

    public MainSwipeAdapter(Context mContext, List<Note> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(null == convertView) {
            convertView = View.inflate(mContext, R.layout.item_main, null);
        }

        Note note = ((Note)getItem(position));

        TextView txt = (TextView) convertView.findViewById(R.id.title_item);
        TextView date = (TextView) convertView.findViewById(R.id.date_item);
        TextView content=(TextView) convertView.findViewById(R.id.content_item);

        txt.setText(note.getName());
        date.setText(note.getDate().getHours() + ":" + note.getDate().getMinutes() + "");

        TextView month = (TextView) convertView.findViewById(R.id.month_item);
        TextView day = (TextView) convertView.findViewById(R.id.day_item);

        month.setText(note.getDate().getMonth() + "");
        day.setText(note.getDate().getDay() + "");

        StringBuilder sb= new StringBuilder();
        if(StringUtil.isEmpty(note.getText())){
            sb.append(" ");
        }else{
            String clearContent = StringUtil.clearHtml(note.getText());
            content.setVisibility(View.VISIBLE);


            if(clearContent.length()<32){
                sb.append(clearContent);
            }
            else if(note.getText().length() > 32){
                sb.append(clearContent.substring(0,31));
                sb.append("...");
            }
        }

        content.setText(StringUtil.clearEnter(sb.toString()));

        View view = convertView.findViewById(R.id.level_item);

        if(note.getLevel() == Note.GRE_LEVEL)
            view.setBackgroundResource(R.color.green);
        else if (note.getLevel() == Note.ORA_LEVEL)
            view.setBackgroundResource(R.color.orange);
        else
            view.setBackgroundResource(R.color.red);

        return convertView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
