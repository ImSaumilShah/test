package skillders.com.booksqlite;

import android.provider.BaseColumns;

/**
 * Created by Naisargi on 28-Dec-17.
 */

public class FeedEntry implements BaseColumns {
    public static final String TABLE_NAME = "Book";
    public static final String COLUMN_NAME= "BookName";
    public static final String COLUMN_AUTHOR = "Author";
    public static final String COLUMN_PRICE = "Price";
    public static final String COLUMN_YEAR = "Year";
    public static final String COLUMN_IMAGE = "Image";

}
