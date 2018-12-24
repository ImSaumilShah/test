package skillders.com.booksqlite;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {
    Cursor cs;
    ListView lv;
    DatabaseHandler dh;
    View cv;
    EditText etBookName, etAuthor,etPrice,etYear,etImage;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Button btnAdd = (Button)findViewById(R.id.btnAdd);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        lv=(ListView)findViewById(R.id.book_list);
        dh = new DatabaseHandler(getApplicationContext(),"",null,1);
        cs= dh.getAllBook();

        MyCursorAdapter ca = new MyCursorAdapter(AddActivity.this,cs,0);
        lv.setAdapter(ca);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
// Retrieve student records
       /* String URL = "content://com.example.naisargi.intent";

        Uri students = Uri.parse(URL);
        Cursor c = managedQuery(students, null, null, null, "name");

        if (c.moveToFirst()) {
            do{
                Toast.makeText(this,
                        c.getString(c.getColumnIndex("_ID")) +
                                ", " +  c.getString(c.getColumnIndex("NAME")) +
                                ", " + c.getString(c.getColumnIndex( "GRADE")),
                        Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }*/
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.parse("content://com.example.naisargi.intent/students");
        String[] projection = {"_ID","NAME","GRADE"};
        Cursor c = cr.query(uri, projection, null, null, null);

        if (c == null) {
            Log.d("Herer....", "Cursor c == null.");
            return;
        }
        while (c.moveToNext()) {
            String column1 = c.getString(0);
            String column2 = c.getString(1);
            String column3 = c.getString(2);
            Toast.makeText(this,
                    c.getString(0) +
                            ", " +  c.getString(1) +
                            ", " + c.getString(2),
                    Toast.LENGTH_SHORT).show();
            Log.d("Herer....", "column1=" + column1 + " column2=" + column2 + " column3=" + column3);
        }
        c.close();
     /*   btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add();
            }
        });*/
    }

    public void refresh(){
        cs= dh.getAllBook();
        MyCursorAdapter ca = new MyCursorAdapter(AddActivity.this,cs,0);
        lv.setAdapter(ca);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.add:
                Add();
                return true;
        }
        return true;
    }

    public void Add() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_add_book, null);
        builder.setView(v);

        etBookName = (EditText) v.findViewById(R.id.etBookName);
        etAuthor = (EditText) v.findViewById(R.id.etAuthor);
        etPrice = (EditText) v.findViewById(R.id.etPrice);
        etYear = (EditText) v.findViewById(R.id.etYear);
        etImage = (EditText) v.findViewById(R.id.etImage);
        cv = v;
        builder.setPositiveButton("add book", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                String name = etBookName.getText().toString();
                String author = etAuthor.getText().toString();
                String price = etPrice.getText().toString();
                String year = etYear.getText().toString();
                String image = etImage.getText().toString();

                dh = new DatabaseHandler(getApplicationContext(), " ", null, 1);
                dh.addBook(name, author, price, year, image);

                // Intent in = new Intent(A.this,AddActivity.class);
                // startActivity(in);

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

        //Intent in = new Intent(this,AddBookActivity.class);
        //startActivity(in);

}
