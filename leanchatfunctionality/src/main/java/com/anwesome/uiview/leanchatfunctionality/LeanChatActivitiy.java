package com.anwesome.uiview.leanchatfunctionality;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by anweshmishra on 10/12/16.
 */
public class LeanChatActivitiy extends AppCompatActivity {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int w,h;
    private LinearLayout messageArea;
    private Button send;
    private EditText messageText;
    private LeanMessageView prevMessageView;
    private float total_y = 0;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);
        paint.setTextSize(AppContants.CHAT_FONT_SIZE);
        setDimensions();
        initUi();
        handleUserMessage();
    }
    private void initUi() {
        messageArea = (LinearLayout)findViewById(R.id.message_area);
        messageText = (EditText)findViewById(R.id.message_text);
        send = (Button) findViewById(R.id.send);
    }
    private void handleUserMessage() {
        send.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {
                createUserTextMessageView(messageText.getText().toString());
                messageText.setText("");
            }
        });
    }
    private void setDimensions() {
        DisplayManager displayManager = (DisplayManager)getSystemService(Context.DISPLAY_SERVICE);
        Display display = displayManager.getDisplay(0);
        Point size = new Point();
        display.getRealSize(size);
        w = size.x;
        h = size.y;
        total_y = h/100;
    }
    private void createUserTextMessageView(String text) {
        if(prevMessageView!=null) {
            prevMessageView.setCurrent(false);
            prevMessageView.invalidate();
        }
        float textWidth = paint.measureText(text);
        float textHeight = AppContants.CHAT_FONT_SIZE;
        float x = 0,y = total_y;
        String tokens[] = text.split(" ");
        String msg = "";
        int m_h = 1;
        for(int i=0;i<tokens.length;i++) {
            if(paint.measureText(msg+tokens[i]) > 9*w/10) {
                m_h++;
                msg = "";
            }

            msg += tokens[i];
        }
        if(m_h>1) {
            textWidth = 9*w/10;
            x = 0;
        }
        else {
            textWidth = paint.measureText(text)+w/20;
            x = w-textWidth;
        }
        textHeight = m_h*(AppContants.CHAT_FONT_SIZE-1)+h/60;

        total_y = total_y+textHeight+h/15;
        float gap = h/100;
        if(m_h>1) {
            gap = h/20;
        }
        LeanMessageView leanMessageView = new LeanMessageView(this,text,textWidth,textHeight+gap);
        leanMessageView.setX(x);
        leanMessageView.setY(y);

        messageArea.addView(leanMessageView,new ViewGroup.LayoutParams((int)(textWidth),(int)(textHeight+h/15)));

        prevMessageView = leanMessageView;
    }
    private class LeanMessageView extends View {
        private String message;
        private float mWidth,mHeight;
        private boolean current = true;
        public LeanMessageView(Context context,String message,float messageWidth,float messageHeight) {
            super(context);
            this.mHeight = messageHeight;
            this.message = message;
            this.mWidth = messageWidth;
        }
        public void setCurrent(boolean current) {
            this.current = current;
        }
        private void drawMessageBody(Canvas canvas,Paint paint) {
            paint.setColor(AppContants.MESSAGE_COLOR);
            int tw = canvas.getWidth();
            int th = (int)mHeight;
            //canvas.drawRoundRect(new RectF(0,0,mWidth,mHeight),mWidth/5,mHeight/5,paint);

            paint.setTextSize(AppContants.CHAT_FONT_SIZE-1);
            String tokens[] = message.split("");

            String msg = "";
            int xh = tw/5,yi = 0, yh = AppContants.CHAT_FONT_SIZE-1;
            for(int i=0;i<tokens.length;i++) {
                if(paint.measureText(msg+tokens[i]) > 7*tw/10) {
                    canvas.drawText(msg,xh,yh,paint);
                    msg = ""+tokens[i];
                    yh = yh+AppContants.CHAT_FONT_SIZE-1+tw/40;
                    yi++;
                }
                else {
                  msg = msg+tokens[i];
                }
            }
            float endY = th+yh-(AppContants.CHAT_FONT_SIZE-1);
            canvas.drawRoundRect(new RectF(0,0,tw,th+yh),tw/5,tw/5,paint);
            th = canvas.getHeight();
            if(current) {
                Path path = new Path();
                path.moveTo(tw*0.8f,th);
                path.lineTo(tw*0.6f,endY);
                path.lineTo(tw*0.8f,endY);
                path.lineTo(tw*0.8f,th);
                canvas.drawPath(path,paint);
            }
            paint.setColor(Color.WHITE);
            canvas.drawText(msg,xh,yh,paint);
        }
        public void onDraw(Canvas canvas){
            drawMessageBody(canvas,paint);
        }
    }
}
