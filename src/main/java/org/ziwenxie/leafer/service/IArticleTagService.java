package org.ziwenxie.leafer.service;

import org.ziwenxie.leafer.model.ArticleTag;

public interface IArticleTagService {

    /**
     * 为一篇文章新加入一个标签
     * @param articleTag
     * @param username 用户名
     * @return 布尔值
     */
    boolean insertOneArticleTag(ArticleTag articleTag, String username);

    /**
     * 为一篇文章删除一个标签
     * @param articleTag
     * @param username 用户名
     * @return 布尔值
     */
    boolean deleteOneArticleTag(ArticleTag articleTag, String username);
}
