package skillders.com.booksqlite;

import android.util.Log;

/**
 * Created by Naisargi on 28-Dec-17.
 */

public class UserBean  {
    String LOG_TAG="in UserBean";

    String bname , author , year,image;
    Float price;

    public UserBean(String bname, String author, Float price, String year, String image) {
        this.bname = bname;
        this.author = author;
        this.price = price;
        this.year = year;
        this.image = image;
    }

    public void printAll(){

        Log.d(LOG_TAG,"book name: "+this.bname);
        Log.d(LOG_TAG,"book Author: "+this.author);
        Log.d(LOG_TAG,"book price: "+this.price);
        Log.d(LOG_TAG,"book year: "+this.year);

    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
