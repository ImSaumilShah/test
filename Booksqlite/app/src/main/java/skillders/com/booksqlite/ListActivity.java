package skillders.com.booksqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

/**
 * Created by Naisargi on 28-Dec-17.
 */

public class ListActivity extends AppCompatActivity {

    DatabaseHandler dh;
    ListView lv;
    Cursor cs;
    UserBean ub = new UserBean("","",null,"","");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        lv=(ListView)findViewById(R.id.book_list);

       // ub.getBname();
        //ub.getPrice();
        //ub.getImage();

        dh = new DatabaseHandler(getApplicationContext(),"",null,1);
        cs= dh.getAllBook();

        MyCursorAdapter ca = new MyCursorAdapter(getApplicationContext(),cs,0);
        lv.setAdapter(ca);
    }
}
