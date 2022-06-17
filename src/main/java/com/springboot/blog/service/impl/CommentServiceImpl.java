package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelMapper mapper;

//    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
//        this.commentRepository = commentRepository;
//        this.postRepository = postRepository;
//    }

    @Override
    public CommentDto createComment(long postId, CommentDto commentDto) {
        // convert to Entity
        Comment comment = mapToEntity(commentDto);

        // find Post by Id
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId));

        // save
        comment.setPost(post);
        Comment newComment = commentRepository.save(comment);

        // convert to Dto
        return mapToDto(newComment);
    }

    @Override
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentRequest) {
        // retrieve post
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId));

        // retrieve comment
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("CommentId", "id", commentId));

        // check comment belong post
        if (!comment.getPost().getId().equals(post.getId())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST,"Comment does not belong to post");
        }

        // set value
        comment.setName(commentRequest.getName());
        comment.setEmail(commentRequest.getEmail());
        comment.setBody(commentRequest.getBody());

        // update
        Comment updateComment = commentRepository.save(comment);

        // return
        return mapToDto(updateComment);
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        // retrieve post
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("PostId", "id", postId));

        // retrieve comment
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("commentId", "id", commentId));

        // check mapping
        if (!comment.getPost().getId().equals(post.getId())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
        }

        // delete
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);

        return comments.stream().map(comment -> mapToDto(comment)).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long postId, Long commentId) {
        // retrieve post
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId));

        // retrieve comment
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("CommentId", "id", commentId));

        // check comment belong post
        if (!comment.getPost().getId().equals(post.getId())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST,"Comment does not belong to post");
        }

        return mapToDto(comment);
    }

    /**
     *
     */
    private CommentDto mapToDto(Comment comment) {
//        CommentDto commentDto = new CommentDto();
//        commentDto.setId(comment.getId());
//        commentDto.setName(comment.getName());
//        commentDto.setEmail(comment.getEmail());
//        commentDto.setBody(comment.getBody());

        CommentDto commentDto = mapper.map(comment, CommentDto.class);
        return commentDto;
    }

    /**
     *
     */
    private Comment mapToEntity(CommentDto commentDto) {
//        Comment comment = new Comment();
//        comment.setName(commentDto.getName());
//        comment.setEmail(commentDto.getEmail());
//        comment.setBody(commentDto.getBody());

        Comment comment = mapper.map(commentDto, Comment.class);
        return comment;
    }
}
