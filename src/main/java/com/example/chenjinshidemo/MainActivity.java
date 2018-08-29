package com.example.chenjinshidemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.normal_chenjinshi).setOnClickListener(this);
        findViewById(R.id.custom_chenjinshi).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.normal_chenjinshi:
                Intent intent = new Intent(this, NormalChenjinshiActivity.class);
                startActivity(intent);
                break;
            case R.id.custom_chenjinshi:
                Intent intent1 = new Intent(this, CustomChenjinshiActivity.class);
                startActivity(intent1);
                break;
        }
    }


}
