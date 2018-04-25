package edu.temple.foodgo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StatusActivity extends AppCompatActivity {
private String orderNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        orderNum = getIntent().getStringExtra("orderNum");
        TextView textView = (TextView)findViewById(R.id.messageText);
        if(textView != null){
            textView.setText("Your order, number " + orderNum + ", has been placed");
        }
        Button button = (Button)findViewById(R.id.newOrderButton);
        if(button != null){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StatusActivity.this, QRScanActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
