package com.example.portal.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Chapter {
    private Long  chapterId;
    private Long  seriesId;
    private String  seriesName;
    private String seriesDescription;
    private Integer chapterCount;
    private String chapterTitle;
    private String author;
    private Integer seq;
    private List<String> tags = new ArrayList<>();

    private String content;
    private Integer length;
    private String publishTime;

    public Chapter addTag(String tag){
        tags.add(tag);
        return this;
    }
}
