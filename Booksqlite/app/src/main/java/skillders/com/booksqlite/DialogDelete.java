package skillders.com.booksqlite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Naisargi on 01-Feb-18.
 */

public class DialogDelete extends DialogFragment {

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
}
