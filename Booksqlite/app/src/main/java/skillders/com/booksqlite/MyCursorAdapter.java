package skillders.com.booksqlite;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Naisargi on 28-Dec-17.
 */

public class MyCursorAdapter extends CursorAdapter {

    private LayoutInflater cursorInflater;
    ImageView iv;
    TextView tvname, tvprice;
    Button edit, delete;
    int idx = 0;
    int deleteidx;
    Context context;
    View v;
    Bundle bundle;

    public MyCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.simple_row, null);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        tvname = (TextView) view.findViewById(R.id.tvName);
        tvprice = (TextView) view.findViewById(R.id.tvPrice);
        iv = (ImageView) view.findViewById(R.id.imageView);
        edit = (Button) view.findViewById(R.id.btnEdit);
        idx = cursor.getInt(0);
        delete = (Button) view.findViewById(R.id.btndelete);
        edit.setTag(idx);
        delete.setTag(idx);

        tvname.setText(cursor.getString(1));
        tvprice.setText(cursor.getString(3));
        new DownloadImage(iv).execute(cursor.getString(5));

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clicked tag number : ", v.getTag().toString());
                edit(Integer.parseInt(v.getTag().toString()));

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  delete(Integer.parseInt(v.getTag().toString()));
            }
        });

    }

    public void edit(int idx) {

        Intent in = new Intent(context, EditActivity.class);
        in.putExtra("pos", idx);
        context.startActivity(in);

    }

    public void delete(int idx) {
        deleteidx = idx;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Are You Sure, You Want to Delete? ");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int idx) {
                Intent in=new Intent(context,DeleteActivity.class);
                in.putExtra("pos",deleteidx);
                context.startActivity(in);
                // delete(Integer.parseInt(v.getTag().toString()));
               // Toast.makeText(context,"Yes clicked",Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }



  /*  public static class dialogDelete extends DialogFragment{

        Context context;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Delete");
            builder.setMessage("Are You Sure, You Want to Delete? ");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int idx) {
                    Intent in=new Intent(context,DeleteActivity.class);
                    in.putExtra("pos",idx);
                    startActivity(in);
                  // delete(Integer.parseInt(v.getTag().toString()));
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog alert=null;
                    alert.dismiss();

                }
            });

            AlertDialog dialog = builder.create();
            return dialog;
        }
    }*/


   /* public void dialogDelete(final View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Are You Sure, You Want to Delete? ");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete(Integer.parseInt(v.getTag().toString()));
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog alert=null;
                alert.dismiss();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }*/

}



