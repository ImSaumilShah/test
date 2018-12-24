package skillders.com.booksqlite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddBookActivity extends AppCompatActivity {

    EditText etBookName, etAuthor,etPrice,etYear,etImage;
    Button btnAdd;
    DatabaseHandler dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        etBookName=(EditText)findViewById(R.id.etBookName);
        etAuthor=(EditText)findViewById(R.id.etAuthor);
        etPrice=(EditText)findViewById(R.id.etPrice);
        etYear=(EditText)findViewById(R.id.etYear);
        etImage=(EditText)findViewById(R.id.etImage);

        btnAdd = (Button)findViewById(R.id.btnAddBook);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etBookName.getText().toString();
                String author = etAuthor.getText().toString();
                String price = etPrice.getText().toString();
                String year = etYear.getText().toString();
                String image =  etImage.getText().toString();

                dh =new DatabaseHandler(getApplicationContext()," ",null,1);
                dh.addBook(name,author,price,year,image);

                Intent in = new Intent(AddBookActivity.this,AddActivity.class);
                startActivity(in);
            }
        });


    }
}
