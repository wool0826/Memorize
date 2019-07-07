package com.example.memorize;

import io.realm.RealmObject;

public class RecyclerData extends RealmObject {
    private String word;
    private String meaning;
    private Integer successCount;
    private Integer failureCount;

    public RecyclerData(){
        this.word = null;
        this.meaning = null;
        this.successCount = null;
        this.failureCount = null;
    }
    public RecyclerData(String word, String meaning, Integer successCount, Integer failureCount){
        this.word = word;
        this.meaning = meaning;
        this.successCount = successCount;
        this.failureCount = failureCount;
    }

    void setWord(String word){
        this.word = word;
    }
    String getWord(){
        return this.word;
    }

    void setMeaning(String meaning){
        this.meaning = meaning;
    }
    String getMeaning(){
        return this.meaning;
    }

    void setSuccessCount(Integer successCount){
        this.successCount = successCount;
    }
    Integer getSuccessCount(){
        return this.successCount;
    }

    void setFailureCount(Integer failureCount){
        this.failureCount = failureCount;
    }
    Integer getFailureCount(){
        return this.failureCount;
    }
}
