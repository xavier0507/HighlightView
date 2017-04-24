package com.xy.highlightview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xy.highlightview.R;
import com.xy.highlightview.view.ViewManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button highlightCircleButton;
    private EditText highlightFocusedEditText;
    private Button highlightTestEditTextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.findUI();
        this.addListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_highlight_circle:
                new ViewManager.HighlightEffectBuilder(this)
                        .focusedOn(this.highlightCircleButton)
                        .highlightShape(ViewManager.SHAPE_CIRCLE)
                        .backgroundColor(R.color.color_00FFFFFF)
                        .build()
                        .render();
                break;

            case R.id.btn_highlight_edit_focus:
                new ViewManager.HighlightEffectBuilder(this)
                        .focusedOn(this.highlightFocusedEditText)
                        .highlightShape(ViewManager.SHAPE_RECT)
                        .backgroundColor(R.color.color_BB00DDAA)
                        .build()
                        .render();
                break;
        }
    }

    private void findUI() {
        this.highlightCircleButton = (Button) this.findViewById(R.id.btn_highlight_circle);
        this.highlightFocusedEditText = (EditText) this.findViewById(R.id.edit_focus_testing);
        this.highlightTestEditTextButton = (Button) this.findViewById(R.id.btn_highlight_edit_focus);
    }

    private void addListener() {
        this.highlightCircleButton.setOnClickListener(this);
        this.highlightTestEditTextButton.setOnClickListener(this);
    }
}
