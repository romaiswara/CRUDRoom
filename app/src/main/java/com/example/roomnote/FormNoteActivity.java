package com.example.roomnote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class FormNoteActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.roomnote.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.roomnote.EXTRA_TITLE";
    public static final String EXTRA_DESC = "com.example.roomnote.EXTRA_DESC";
    public static final String EXTRA_PROT = "com.example.roomnote.EXTRA_PROT";

    private EditText etTitle, etDesc;
    private NumberPicker numberPicker;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_note);

        etTitle = findViewById(R.id.etTitle);
        etDesc = findViewById(R.id.etDesc);
        btnSave = findViewById(R.id.btnSave);
        numberPicker = findViewById(R.id.numberPriority);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);

        Intent i = getIntent();
        if (i.hasExtra(EXTRA_ID)){
            btnSave.setText("Update Note");
            etTitle.setText(i.getStringExtra(EXTRA_TITLE));
            etDesc.setText(i.getStringExtra(EXTRA_DESC));
            numberPicker.setValue(i.getIntExtra(EXTRA_PROT, 1));
        } else {
            btnSave.setText("Buat Note");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

    }

    private void saveNote() {
        String title = etTitle.getText().toString();
        String desc = etDesc.getText().toString();
        int priority = numberPicker.getValue();

        if (title.trim().isEmpty() || desc.trim().isEmpty()){
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = new Intent();
        i.putExtra(EXTRA_TITLE, title);
        i.putExtra(EXTRA_DESC, desc);
        i.putExtra(EXTRA_PROT, priority);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1){
            i.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, i);
        finish();
    }
}
