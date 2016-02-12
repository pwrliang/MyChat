package com.gl.mychatclient.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gl.mychatclient.R;
import com.gl.mychatclient.activities.ShowPitcureActivity;
import com.gl.mychatclient.apis.models.Message;
import com.gl.mychatclient.utils.LogUtil;

import java.util.List;

/**
 * Created by gl on 2016/2/2.
 */
public class MessageAdapter extends BaseAdapter {
    private List<Message> messageList;
    private LayoutInflater inflater;
    private Bitmap leftPortrait;
    private Bitmap rightPortrait;

    public MessageAdapter(Context context, List<Message> messageList, Bitmap leftPortrait, Bitmap rightPortrait) {
        this.messageList = messageList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.leftPortrait = leftPortrait;
        this.rightPortrait = rightPortrait;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return messageList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        final Message msg = messageList.get(position);
        int type = getItemViewType(position);
        switch (type) {
            case Message.VALUE_LEFT_TEXT:
                ViewHolderLeftText viewHolderLeftText;
                if (convertView == null) {
                    view = inflater.inflate(R.layout.message_item_left_text, null);
                    viewHolderLeftText = new ViewHolderLeftText();
                    viewHolderLeftText.tvLeftText = (TextView) view.findViewById(R.id.milt_tv_left_msg);
                    viewHolderLeftText.ivLeftPortrait = (ImageView) view.findViewById(R.id.milt_iv_left_portrait);
                    view.setTag(viewHolderLeftText);
                } else {
                    view = convertView;
                    viewHolderLeftText = (ViewHolderLeftText) view.getTag();
                }
                viewHolderLeftText.tvLeftText.setText(msg.getText());
                viewHolderLeftText.ivLeftPortrait.setImageBitmap(leftPortrait);
                break;
            case Message.VALUE_LEFT_IMAGE:
                ViewHolderLeftImg viewHolderLeftImg;
                if (convertView == null) {
                    view = inflater.inflate(R.layout.message_item_left_image, null);
                    viewHolderLeftImg = new ViewHolderLeftImg();
                    viewHolderLeftImg.ivLeftImage = (ImageView) view.findViewById(R.id.mili_iv_left_image);
                    viewHolderLeftImg.ivLeftPortrait = (ImageView) view.findViewById(R.id.mili_iv_left_portrait);
                    view.setTag(viewHolderLeftImg);
                } else {
                    view = convertView;
                    viewHolderLeftImg = (ViewHolderLeftImg) view.getTag();
                }
                viewHolderLeftImg.ivLeftImage.setImageBitmap(BitmapFactory.decodeByteArray(msg.getImage(), 0, msg.getImage().length));
                viewHolderLeftImg.ivLeftPortrait.setImageBitmap(leftPortrait);
                break;
            case Message.VALUE_RIGHT_TEXT:
                ViewHolderRightText viewHolderRightText;
                if (convertView == null) {
                    view = inflater.inflate(R.layout.message_item_right_text, null);
                    viewHolderRightText = new ViewHolderRightText();
                    viewHolderRightText.tvRightText = (TextView) view.findViewById(R.id.mirt_tv_right_msg);
                    viewHolderRightText.ivRightPortrait = (ImageView) view.findViewById(R.id.mirt_iv_right_portrait);
                    view.setTag(viewHolderRightText);
                } else {
                    view = convertView;
                    viewHolderRightText = (ViewHolderRightText) view.getTag();
                }
                viewHolderRightText.tvRightText.setText(msg.getText());
                viewHolderRightText.ivRightPortrait.setImageBitmap(rightPortrait);
                break;
            case Message.VALUE_RIGHT_IMAGE:
                ViewHolderRightImg viewHolderRightImg;
                if (convertView == null) {
                    view = inflater.inflate(R.layout.message_item_right_image, null);
                    viewHolderRightImg = new ViewHolderRightImg();
                    viewHolderRightImg.ivRightImage = (ImageView) view.findViewById(R.id.miri_iv_right_image);
                    viewHolderRightImg.ivRightPortrait = (ImageView) view.findViewById(R.id.miri_iv_right_portrait);
                    view.setTag(viewHolderRightImg);
                } else {
                    view = convertView;
                    viewHolderRightImg = (ViewHolderRightImg) view.getTag();
                }
                viewHolderRightImg.ivRightImage.setImageBitmap(BitmapFactory.decodeByteArray(msg.getImage(), 0, msg.getImage().length));
                viewHolderRightImg.ivRightPortrait.setImageBitmap(rightPortrait);
                break;
            default:
                LogUtil.i("Adapter", "其他！！！！！！！！" + msg.getType());
                break;
        }
        if (view != null) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!TextUtils.isEmpty(msg.getText())) {
                        ClipboardManager clipboardManager = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("simple text", msg.getText());
                        clipboardManager.setPrimaryClip(clipData);
                        Toast.makeText(v.getContext(), R.string.copied, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (msg.getImage() != null) {
                        Intent intent = new Intent(v.getContext(), ShowPitcureActivity.class);
                        intent.putExtra("picture", msg.getImage());
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }
        return view;
    }

    /**
     * 根据数据源的position返回需要显示的的layout的type
     * <p/>
     * type的值必须从0开始
     */
    @Override
    public int getItemViewType(int position) {
        Message msg = messageList.get(position);
        int type = msg.getType();
//        Log.e("TYPE:", "" + type);
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }

    class ViewHolderTime {
        private TextView tvTimeTip;// 时间
    }

    class ViewHolderRightText {
        private ImageView ivRightPortrait;// 右边的头像
        private TextView tvRightText;// 右边的文本
    }

    class ViewHolderRightImg {
        private ImageView ivRightPortrait;// 右边的头像
        private ImageView ivRightImage;// 右边的图像
    }

    class ViewHolderRightAudio {
        private ImageView ivRightPortrait;// 右边的头像
        private Button btnRightAudio;// 右边的声音
        private TextView tvRightAudioTime;// 右边的声音时间
    }

    class ViewHolderLeftText {
        private ImageView ivLeftPortrait;// 左边的头像
        private TextView tvLeftText;// 左边的文本
    }

    class ViewHolderLeftImg {
        private ImageView ivLeftPortrait;// 左边的头像
        private ImageView ivLeftImage;// 左边的图像
    }

    class ViewHolderLeftAudio {
        private ImageView ivLeftPortrait;// 左边的头像
        private Button btnLeftAudio;// 左边的声音
        private TextView tvLeftAudioTime;// 左边的声音时间
    }

}
