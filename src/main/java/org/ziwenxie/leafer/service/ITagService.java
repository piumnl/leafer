package org.ziwenxie.leafer.service;

import org.ziwenxie.leafer.model.Tag;

import java.util.List;

public interface ITagService {

    /**
     * 新增一个标签
     * @param tagName 标签的名字
     * @param username 用户名
     * @return Tag对象
     */
    Tag insertOneTag(String tagName, String username);

    /**
     * 删除一个标签
     * @param tagId 标签id
     * @param username 用户名
     * @return 布尔值
     */
    boolean deleteOneTagById(long tagId, String username);

    /**
     * 根据id查询一个标签
     * @param tagId 标签id
     * @return Tag对象
     */
    Tag getOneTagById(long tagId);

    /**
     * 根据用户名和标签名查询一个标签
     * @param tagName 标签名
     * @param username 用户名
     * @return Tag对象
     */
    Tag getOneTagByName(String tagName, String username);

    /**
     * 得到一个用户所有的便签
     * @param username 标签名
     * @return List<Tag>
     */
    List<Tag> getAllTags(String username);
}
