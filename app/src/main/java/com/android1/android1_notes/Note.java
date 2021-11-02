package com.android1.android1_notes;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
// Класс Заметки. Какие ещё пояснения нужны?
    private int noteIndex;
    private String noteName;

    public Note(int noteIndex, String noteName) {
        this.noteIndex = noteIndex;
        this.noteName = noteName;
    }

    protected Note(Parcel in) {
        noteIndex = in.readInt();
        noteName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getNoteIndex());
        dest.writeString(getNoteName());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public int getNoteIndex() {
        return noteIndex;
    }

    public String getNoteName() {
        return noteName;
    }

}
