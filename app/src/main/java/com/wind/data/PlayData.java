package com.wind.data;

import com.wind.interfaces.PlayInfoListener;

/**
 * Created by w010003593 on 2018/4/10.
 */

public class PlayData{

    public PlayInfoListener listener;

    public PlayData(){}

    public void setListener(PlayInfoListener listener){
        this.listener = listener;
    }

    public void setPlayData(String playAlbum,String playArtist,String playTrack){
        listener.doSomething(playAlbum,playArtist,playTrack);
    }

}
