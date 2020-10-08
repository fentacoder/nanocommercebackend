package com.infotechnano.nanocommerce.dao;

import com.infotechnano.nanocommerce.models.Comment;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface CommentDao {
    public HashMap<String,String> getCommentNum(UUID postId);
    public HashMap<String,Object> retrieveComments(UUID postId);
    List<Comment> paginate(Integer currentPage, boolean earlier, boolean lastPage, Integer skipped, Integer idxBound);
    public String commentAuthor(UUID authorId);
    public Comment addComment(Comment comment);
}
