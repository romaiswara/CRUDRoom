package com.example.roomnote;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int NEW_REQUEST_CODE = 1;
    public static final int UPDATE_REQUEST_CODE = 2;


    private NoteViewModel noteViewModel;
    private MainAdapter adapter;
    private RecyclerView rvNote;
    private FloatingActionButton fab;
    private Button btnDeleteAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        rvNote = findViewById(R.id.rvNotes);
        rvNote.setLayoutManager(new LinearLayoutManager(this));
        rvNote.setHasFixedSize(true);

        adapter = new MainAdapter();
        rvNote.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                adapter.setNotes(notes);

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, FormNoteActivity.class), NEW_REQUEST_CODE);
            }
        });

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteViewModel.deleteAllNotes();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Hapus Note", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(rvNote);

        adapter.setOnItemClickListener(new MainAdapter.onClickListener() {
            @Override
            public void onItemClick(Note note) {
                startActivityForResult(new Intent(MainActivity.this, FormNoteActivity.class)
                        .putExtra(FormNoteActivity.EXTRA_ID, note.getId())
                        .putExtra(FormNoteActivity.EXTRA_TITLE, note.getTitle())
                        .putExtra(FormNoteActivity.EXTRA_DESC, note.getDesc())
                        .putExtra(FormNoteActivity.EXTRA_PROT, note.getPriority()), UPDATE_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_REQUEST_CODE && resultCode == RESULT_OK){
            String title = data.getStringExtra(FormNoteActivity.EXTRA_TITLE);
            String desc = data.getStringExtra(FormNoteActivity.EXTRA_DESC);
            int priority = data.getIntExtra(FormNoteActivity.EXTRA_PROT, 1);

            Note note = new Note(title, desc, priority);
            noteViewModel.insert(note);

            Toast.makeText(this, "Simpan Berhasil", Toast.LENGTH_SHORT).show();

        } else if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK){
            int id = data.getIntExtra(FormNoteActivity.EXTRA_ID, -1);
            String title = data.getStringExtra(FormNoteActivity.EXTRA_TITLE);
            String desc = data.getStringExtra(FormNoteActivity.EXTRA_DESC);
            int priority = data.getIntExtra(FormNoteActivity.EXTRA_PROT, 1);

            if (id == -1){
                Toast.makeText(this, "Tidak bisa diupdate", Toast.LENGTH_SHORT).show();
                return;
            }
            Note note = new Note(title, desc, priority);
            note.setId(id);
            noteViewModel.update(note);
            Toast.makeText(this, "Update Berhasil", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal Menyimpan", Toast.LENGTH_SHORT).show();
        }
    }
}
