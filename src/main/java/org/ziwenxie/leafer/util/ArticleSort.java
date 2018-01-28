package org.ziwenxie.leafer.util;

import org.ziwenxie.leafer.model.Article;

import java.util.List;

public class ArticleSort {

    public List<Article> sortByCreateTime(List<Article> articles) {
        articles.sort((a1, a2) -> a1.getCreatedTime().compareTo(a2.getCreatedTime()));
        return articles;
    }

    public List<Article> sortByModifyTime(List<Article> articles) {
        articles.sort((a1, a2) -> a1.getModifiedTime().compareTo(a2.getModifiedTime()));
        return articles;
    }
}
