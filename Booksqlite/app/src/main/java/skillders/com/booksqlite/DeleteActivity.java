package skillders.com.booksqlite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class DeleteActivity extends AppCompatActivity {

    DatabaseHandler dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        dh = new DatabaseHandler(getApplicationContext(),"",null,1);

        Intent in = getIntent();
        int position=in.getIntExtra("pos",0);

        Log.d("in delete activity",+position+"");

        dh.deleteBook(position);

        in = new Intent(DeleteActivity.this,AddActivity.class);
        startActivity(in);
    }
}
