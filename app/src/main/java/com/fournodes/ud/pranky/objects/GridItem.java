package com.fournodes.ud.pranky.objects;

        import android.os.Parcel;
        import android.os.Parcelable;

public class GridItem implements Parcelable {

    public String item; //Item NAME or LOCATION fetched from the database
    public int itemID; //Item position
    public int itemResID; //Item resource number generated by android fetched from database
    public int itemVolume; //Item volume fetched from database
    public int itemRepeatCount; //Item repeat value fetched from database

    // For non-sound item i.e add sound etc
    public GridItem(int itemID, int itemResID, String item) {
        this.itemID = itemID;
        this.itemResID = itemResID;
        this.item = item;
        this.itemRepeatCount = 0;
        this.itemVolume = 0;
    }
    // For sound playback items i.e all pre included sounds and custom sounds
    public GridItem(int itemID, int itemResID, String item, int itemRepeatCount, int itemVolume) {
        this.itemID = itemID;
        this.itemResID = itemResID;
        this.item = item;
        this.itemRepeatCount = itemRepeatCount;
        this.itemVolume = itemVolume;
    }

    protected GridItem(Parcel in) {
        item = in.readString();
        itemID = in.readInt();
        itemResID = in.readInt();
        itemRepeatCount = in.readInt();
        itemVolume = in.readInt();
    }

    public static final Creator<GridItem> CREATOR = new Creator<GridItem>() {
        @Override
        public GridItem createFromParcel(Parcel in) {
            return new GridItem(in);
        }

        @Override
        public GridItem[] newArray(int size) {
            return new GridItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(item);
        parcel.writeInt(itemID);
        parcel.writeInt(itemResID);
        parcel.writeInt(itemRepeatCount);
        parcel.writeInt(itemVolume);
    }


}